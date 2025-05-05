import java.io.*;
import java.util.*;

import coords.Coordinate;

public class App {
    private static final String FILE_NAME = "coords.txt";
    private static final List<Coordinate> coords = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadCoords();
        while (true) {
            System.out.println("\n== Minecraft Coordinates ==");
            System.out.println("1. Add coordinate");
            System.out.println("2. List coordinates");
            System.out.println("3. Delete coordinate");
            System.out.println("4. Save and exit");

            System.out.print("> ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> addCoord();
                case "2" -> listCoords();
                case "3" -> deleteCoord();
                case "4" -> {
                    saveCoords();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void addCoord() {
        System.out.print("Name: ");
        String name = scanner.nextLine();

        int x = getInt("X: ");
        int y = getInt("Y: ");
        int z = getInt("Z: ");

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        coords.add(new Coordinate(name, x, y, z, desc));
        System.out.println("Coordinate added.");
    }

    private static int getInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private static void listCoords() {
        if (coords.isEmpty()) {
            System.out.println("No coordinates saved.");
            return;
        }
        for (int i = 0; i < coords.size(); i++) {
            System.out.println((i + 1) + ". " + coords.get(i));
        }
    }

    private static void deleteCoord() {
        listCoords();
        if (coords.isEmpty()) return;

        System.out.print("Enter number to delete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < coords.size()) {
                coords.remove(index);
                System.out.println("Deleted.");
            } else {
                System.out.println("Invalid index.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private static void loadCoords() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                coords.add(Coordinate.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Failed to load coords.");
        }
    }

    private static void saveCoords() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Coordinate coord : coords) {
                writer.println(coord.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Failed to save coords.");
        }
    }
}
