package allusecases;

import java.util.ArrayList;

/**
 * UseCase3InventorySetup
 * 
 * This class demonstrates inventory setup using a custom Room class.
 * It improves design by using objects instead of plain strings.
 * 
 * Each room has attributes like room number and type.
 * 
 * @author Srividya
 * @version 3.0
 */

public class UseCase3InventorySetup {

    // Room class (Custom Data Structure)
    static class Room {
        int roomNumber;
        String roomType;

        // Constructor
        Room(int roomNumber, String roomType) {
            this.roomNumber = roomNumber;
            this.roomType = roomType;
        }

        // Method to display room details
        void displayRoom() {
            System.out.println("Room " + roomNumber + " - " + roomType);
        }
    }

    public static void main(String[] args) {

        // Step 1: Create Inventory (ArrayList of Room objects)
        ArrayList<Room> roomList = new ArrayList<>();

        // Step 2: Add Room Objects
        roomList.add(new Room(101, "Single"));
        roomList.add(new Room(102, "Double"));
        roomList.add(new Room(103, "Deluxe"));
        roomList.add(new Room(104, "Suite"));
        roomList.add(new Room(105, "Premium"));

        // Step 3: Display Inventory
        System.out.println("=======================================");
        System.out.println("     Book My Stay - Inventory Setup");
        System.out.println("=======================================");

        System.out.println("\nAvailable Rooms:");
        System.out.println("---------------------------------------");

        for (Room room : roomList) {
            room.displayRoom();
        }

        // Step 4: Total Rooms
        System.out.println("---------------------------------------");
        System.out.println("Total Rooms: " + roomList.size());

        System.out.println("=======================================");
        System.out.println("Inventory Setup Completed Successfully!");
    }
}
