import java.util.ArrayList;

/**
 * UseCase2RoomInitialization
 * 
 * This class demonstrates initialization of room inventory
 * using ArrayList in the Book My Stay application.
 * 
 * It represents a refactored version where room data is
 * dynamically stored and managed using a data structure.
 * 
 * @author Srividya
 * @version 2.0
 */

public class UseCase2RoomInitialization {

    public static void main(String[] args) {

        // Step 1: Create Room List (Inventory)
        ArrayList<String> rooms = new ArrayList<>();

        // Step 2: Add Rooms to Inventory
        rooms.add("Room 101 - Single");
        rooms.add("Room 102 - Double");
        rooms.add("Room 103 - Deluxe");
        rooms.add("Room 104 - Suite");
        rooms.add("Room 105 - Premium");

        // Step 3: Display Initialized Rooms
        System.out.println("=======================================");
        System.out.println("     Book My Stay - Room Initialization");
        System.out.println("=======================================");

        System.out.println("\nAvailable Rooms:");
        System.out.println("---------------------------------------");

        for (int i = 0; i < rooms.size(); i++) {
            System.out.println("Room " + (i + 1) + ": " + rooms.get(i));
        }

        // Step 4: Display Total Count
        System.out.println("---------------------------------------");
        System.out.println("Total Rooms Initialized: " + rooms.size());

        System.out.println("=======================================");
        System.out.println("Room Initialization Completed Successfully!");
    }
}