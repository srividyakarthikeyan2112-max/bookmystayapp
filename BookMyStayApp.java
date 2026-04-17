import java.util.*;
import java.io.*;

/**
 * UseCase12DataPersistenceRecovery
 *
 * Use Case 1: Application Entry
 * Use Case 2: Room Domain Modeling
 * Use Case 3: Centralized Room Inventory
 * Use Case 4: Room Search (Read-only access)
 * Use Case 5: Booking Request (FIFO Intake)
 * Use Case 6: Reservation Confirmation & Room Allocation
 * Use Case 7: Add-On Service Selection
 * Use Case 8: Booking History & Reporting
 * Use Case 9: Error Handling & Validation
 * Use Case 10: Booking Cancellation & Inventory Rollback
 * Use Case 11: Concurrent Booking Simulation (Thread Safety)
 * Use Case 12: Data Persistence & System Recovery
 *
 * @author Eshan Pankaj Joshi
 * @version 12.0
 */
public class BookMyStayApp {

    public static void main(String[] args) throws Exception {

        // =============================
        // Use Case 1: Application Entry
        // =============================
        System.out.println("Welcome to Hotel Booking System\n");

        // ===================================
        // Use Case 2: Room Domain Modeling
        // ===================================
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // ===================================
        // Use Case 3: Centralized Inventory
        // ===================================
        RoomInventory inventory = new RoomInventory();
        inventory.setAvailability(singleRoom.getRoomType(), 2);
        inventory.setAvailability(doubleRoom.getRoomType(), 1);
        inventory.setAvailability(suiteRoom.getRoomType(), 1);

        // ===================================
        // Use Case 5: Booking Queue
        // ===================================
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.enqueueRequest(new Reservation("R1", "Alice", "Suite Room"));
        queue.enqueueRequest(new Reservation("R2", "Bob", "Single Room"));

        // ===================================
        // Use Case 6: Allocation
        // ===================================
        RoomAllocationService allocationService = new RoomAllocationService(inventory);
        Map<String, Reservation> confirmedBookings = new HashMap<>();

        System.out.println("\n--- Confirming Bookings ---");

        while (queue.hasPendingRequests()) {

            Reservation res = queue.dequeueRequest();

            if (allocationService.processAllocation(res)) {
                confirmedBookings.put(res.getReservationId(), res);
            }
        }

        // ===================================
        // Use Case 8: Booking History
        // ===================================
        BookingHistory history = new BookingHistory();

        for (Reservation res : confirmedBookings.values()) {
            history.addReservation(res);
        }

        System.out.println("\n--- Booking History ---");
        for (Reservation res : history.getReservations()) {
            System.out.println(res);
        }

        // ===================================
        // Use Case 10: Cancellation
        // ===================================
        CancellationService cancellationService = new CancellationService(inventory);

        System.out.println("\n--- Cancellation ---");
        cancellationService.cancelBooking("R1", confirmedBookings, history);

        System.out.println("\n--- Updated History ---");
        for (Reservation res : history.getReservations()) {
            System.out.println(res);
        }

        // ===================================
        // Use Case 11: Concurrency
        // ===================================
        System.out.println("\n--- Concurrent Booking ---");

        BookingRequestQueue concurrentQueue = new BookingRequestQueue();

        concurrentQueue.enqueueRequest(new Reservation("R3", "Dave", "Single Room"));
        concurrentQueue.enqueueRequest(new Reservation("R4", "Eva", "Single Room"));

        ConcurrentBookingProcessor processor =
                new ConcurrentBookingProcessor(concurrentQueue, inventory);

        Thread t1 = new Thread(processor);
        Thread t2 = new Thread(processor);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // ===================================
        // Use Case 12: Persistence
        // ===================================
        PersistenceService persistenceService = new PersistenceService();

        System.out.println("\n--- Saving State ---");
        persistenceService.saveState(inventory, confirmedBookings);

        System.out.println("\n--- Loading State ---");

        RoomInventory restoredInventory = new RoomInventory();
        Map<String, Reservation> restoredBookings = new HashMap<>();

        persistenceService.loadState(restoredInventory, restoredBookings);

        System.out.println("Restored Inventory:");
        System.out.println("Single: " + restoredInventory.getAvailability("Single Room"));
        System.out.println("Double: " + restoredInventory.getAvailability("Double Room"));
        System.out.println("Suite: " + restoredInventory.getAvailability("Suite Room"));

        System.out.println("\nRestored Bookings:");
        for (Reservation r : restoredBookings.values()) {
            System.out.println(r);
        }
    }
}

/* =============================
   UC12: Persistence Service
   ============================= */
class PersistenceService {

    private static final String FILE = "booking_data.ser";

    public void saveState(RoomInventory inventory,
                          Map<String, Reservation> bookings) {

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(inventory);
            out.writeObject(bookings);
        } catch (Exception e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadState(RoomInventory inventory,
                          Map<String, Reservation> bookings) {

        File f = new File(FILE);

        if (!f.exists()) {
            System.out.println("No saved data.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))) {

            RoomInventory savedInv = (RoomInventory) in.readObject();
            Map<String, Reservation> savedBookings =
                    (Map<String, Reservation>) in.readObject();

            inventory.restore(savedInv);
            bookings.clear();
            bookings.putAll(savedBookings);

        } catch (Exception e) {
            System.out.println("Load error. Starting fresh.");
        }
    }
}

/* =============================
   UC11: Concurrency Processor
   ============================= */
class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;

    public ConcurrentBookingProcessor(BookingRequestQueue q, RoomInventory i) {
        queue = q;
        inventory = i;
    }

    public void run() {
        while (true) {
            Reservation r = queue.dequeueRequest();
            if (r == null) break;

            synchronized (inventory) {
                int stock = inventory.getAvailability(r.getRoomType());

                if (stock > 0) {
                    inventory.updateAvailability(r.getRoomType(), stock - 1);
                    System.out.println(Thread.currentThread().getName() +
                            " CONFIRMED: " + r.getGuestName());
                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " FAILED: " + r.getGuestName());
                }
            }
        }
    }
}

/* =============================
   Cancellation (UC10)
   ============================= */
class CancellationService {

    private RoomInventory inventory;

    public CancellationService(RoomInventory i) {
        inventory = i;
    }

    public void cancelBooking(String id,
                              Map<String, Reservation> confirmed,
                              BookingHistory history) {

        if (!confirmed.containsKey(id)) {
            System.out.println("Invalid cancellation");
            return;
        }

        Reservation r = confirmed.get(id);

        int stock = inventory.getAvailability(r.getRoomType());
        inventory.updateAvailability(r.getRoomType(), stock + 1);

        confirmed.remove(id);

        history.addReservation(new Reservation(
                id + "-CANCELLED",
                r.getGuestName(),
                r.getRoomType()));

        System.out.println("Cancelled: " + r.getGuestName());
    }
}

/* =============================
   Booking History (UC8)
   ============================= */
class BookingHistory {

    private List<Reservation> list = new ArrayList<>();

    public void addReservation(Reservation r) {
        list.add(r);
    }

    public List<Reservation> getReservations() {
        return list;
    }
}

/* =============================
   Allocation (UC6)
   ============================= */
class RoomAllocationService {

    private RoomInventory inventory;

    public RoomAllocationService(RoomInventory i) {
        inventory = i;
    }

    public boolean processAllocation(Reservation r) {

        int stock = inventory.getAvailability(r.getRoomType());

        if (stock > 0) {
            inventory.updateAvailability(r.getRoomType(), stock - 1);
            System.out.println("CONFIRMED: " + r.getGuestName());
            return true;
        }

        return false;
    }
}

/* =============================
   Queue (UC5)
   ============================= */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void enqueueRequest(Reservation r) {
        queue.add(r);
    }

    public synchronized Reservation dequeueRequest() {
        return queue.poll();
    }

    public boolean hasPendingRequests() {
        return !queue.isEmpty();
    }
}

/* =============================
   Reservation
   ============================= */
class Reservation implements Serializable {

    private String id, name, type;

    public Reservation(String i, String n, String t) {
        id = i; name = n; type = t;
    }

    public String getReservationId() { return id; }
    public String getGuestName() { return name; }
    public String getRoomType() { return type; }

    public String toString() {
        return id + " | " + name + " | " + type;
    }
}

/* =============================
   Inventory (UC3)
   ============================= */
class RoomInventory implements Serializable {

    private Map<String, Integer> map = new HashMap<>();

    public void setAvailability(String r, int c) {
        map.put(r, c);
    }

    public int getAvailability(String r) {
        return map.getOrDefault(r, 0);
    }

    public void updateAvailability(String r, int c) {
        map.put(r, c);
    }

    public void restore(RoomInventory other) {
        map.clear();
        map.putAll(other.map);
    }
}

/* =============================
   Room Model (UC2)
   ============================= */
abstract class Room {
    public abstract String getRoomType();
}

class SingleRoom extends Room {
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public String getRoomType() { return "Suite Room"; }
}