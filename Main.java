import java.util.*;
import java.io.*;
import coords.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Group> groups = new HashMap<>();
        Group currentGroup = null;

        while (true) {
            System.out.println("\n=== Group Manager ===");
            System.out.println("1. Create new group");
            System.out.println("2. Switch group");
            System.out.println("3. Add coordinate to current group");
            System.out.println("4. Show current group");
            System.out.println("5. Save current group to file");
            System.out.println("6. Load group from file");
            System.out.println("7. List all groups");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": {
                    System.out.print("Group name: ");
                    String name = scanner.nextLine();
                    if (groups.containsKey(name)) {
                        System.out.println("Group already exists.");
                        break;
                    }
                    System.out.print("Group description: ");
                    String description = scanner.nextLine();
                    Group newGroup = new Group(name, description, new Coordinate[0]);
                    groups.put(name, newGroup);
                    currentGroup = newGroup;
                    System.out.println("Group created and selected.");
                    break;
                }

                case "2": {
                    if (groups.isEmpty()) {
                        System.out.println("No groups available.");
                        break;
                    }
                    System.out.print("Available groups: ");
                    for (String name : groups.keySet()) {
                        System.out.print(name + " ");
                    }
                    System.out.print("\nEnter group name to switch to: ");
                    String name = scanner.nextLine();
                    if (groups.containsKey(name)) {
                        currentGroup = groups.get(name);
                        System.out.println("Switched to group: " + name);
                    } else {
                        System.out.println("Group not found.");
                    }
                    break;
                }

                case "3": {
                    if (currentGroup == null) {
                        System.out.println("No group selected.");
                        break;
                    }
                    System.out.print("Coordinate name: ");
                    String cname = scanner.nextLine();
                    System.out.print("X: ");
                    int x = Integer.parseInt(scanner.nextLine());
                    System.out.print("Y: ");
                    int y = Integer.parseInt(scanner.nextLine());
                    System.out.print("Z: ");
                    int z = Integer.parseInt(scanner.nextLine());
                    System.out.print("Description: ");
                    String desc = scanner.nextLine();

                    // Expand array manually
                    Coordinate[] old = currentGroup.coords;
                    Coordinate[] updated = Arrays.copyOf(old, old.length + 1);
                    updated[old.length] = new Coordinate(cname, x, y, z, desc);
                    currentGroup.coords = updated;
                    System.out.println("Coordinate added to group: " + currentGroup.name);
                    break;
                }

                case "4": {
                    if (currentGroup == null) {
                        System.out.println("No group selected.");
                    } else {
                        System.out.println(currentGroup);
                        for (Coordinate c : currentGroup.coords) {
                            System.out.println("  - " + c);
                        }
                    }
                    break;
                }

                case "5": {
                    if (currentGroup == null) {
                        System.out.println("No group selected.");
                        break;
                    }
                    System.out.print("Save to filename: ");
                    String filename = scanner.nextLine();
                    try (PrintWriter out = new PrintWriter(filename)) {
                        out.println(currentGroup.name);
                        out.println(currentGroup.description);
                        for (Coordinate c : currentGroup.coords) {
                            out.println(c.toFileString());
                        }
                        System.out.println("Group saved.");
                    } catch (IOException e) {
                        System.out.println("Error saving file.");
                    }
                    break;
                }

                case "6": {
                    System.out.print("Load from filename: ");
                    String filename = scanner.nextLine();
                    try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
                        String name = in.readLine();
                        String desc = in.readLine();
                        List<Coordinate> coordList = new ArrayList<>();
                        String line;
                        while ((line = in.readLine()) != null) {
                            coordList.add(Coordinate.fromFileString(line));
                        }
                        Group loaded = new Group(name, desc, coordList.toArray(new Coordinate[0]));
                        groups.put(name, loaded);
                        currentGroup = loaded;
                        System.out.println("Group loaded and selected.");
                    } catch (IOException e) {
                        System.out.println("Error loading file.");
                    }
                    break;
                }

                case "7": {
                    if (groups.isEmpty()) {
                        System.out.println("No groups created yet.");
                    } else {
                        System.out.println("Groups:");
                        for (String name : groups.keySet()) {
                            System.out.println(" - " + name);
                        }
                    }
                    break;
                }

                case "0": {
                    System.out.println("Exiting.");
                    return;
                }

                default: System.out.println("Invalid choice.");
            }
        }
    }
}
