import coords.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class GroupManagerGUI extends JFrame {
    private Map<String, Group> groups = new HashMap<>();
    private Group currentGroup = null;

    private JComboBox<String> groupSelector = new JComboBox<>();
    private JTextArea output = new JTextArea(10, 40);

    public GroupManagerGUI() {
        setTitle("Group Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel();
        JButton createBtn = new JButton("Create Group");
        JButton switchBtn = new JButton("Switch Group");
        JButton addCoordBtn = new JButton("Add Coordinate");
        JButton saveBtn = new JButton("Save Group");
        JButton loadBtn = new JButton("Load Group");
        JButton listBtn = new JButton("List Groups");

        topPanel.add(createBtn);
        topPanel.add(switchBtn);
        topPanel.add(addCoordBtn);
        topPanel.add(saveBtn);
        topPanel.add(loadBtn);
        topPanel.add(listBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        // Action listeners
        createBtn.addActionListener(e -> createGroup());
        switchBtn.addActionListener(e -> switchGroup());
        addCoordBtn.addActionListener(e -> addCoordinate());
        saveBtn.addActionListener(e -> saveGroup());
        loadBtn.addActionListener(e -> loadGroup());
        listBtn.addActionListener(e -> listGroups());

        pack();
        setVisible(true);
    }

    private void createGroup() {
        String name = JOptionPane.showInputDialog(this, "Group name:");
        if (name == null || name.isEmpty()) return;
        if (groups.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "Group already exists.");
            return;
        }

        String desc = JOptionPane.showInputDialog(this, "Group description:");
        Group g = new Group(name, desc, new Coordinate[0]);
        groups.put(name, g);
        currentGroup = g;
        groupSelector.addItem(name);
        output.append("Created and selected group: " + name + "\n");
    }

    private void switchGroup() {
        if (groups.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No groups available.");
            return;
        }

        String name = (String) JOptionPane.showInputDialog(this, "Select group:", "Switch Group",
                JOptionPane.PLAIN_MESSAGE, null, groups.keySet().toArray(), null);
        if (name != null && groups.containsKey(name)) {
            currentGroup = groups.get(name);
            output.append("Switched to group: " + name + "\n");
        }
    }

    private void addCoordinate() {
        if (currentGroup == null) {
            JOptionPane.showMessageDialog(this, "No group selected.");
            return;
        }

        JTextField nameField = new JTextField();
        JTextField xField = new JTextField();
        JTextField yField = new JTextField();
        JTextField zField = new JTextField();
        JTextField descField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("X:")); panel.add(xField);
        panel.add(new JLabel("Y:")); panel.add(yField);
        panel.add(new JLabel("Z:")); panel.add(zField);
        panel.add(new JLabel("Description:")); panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Coordinate", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Coordinate c = new Coordinate(
                        nameField.getText(),
                        Integer.parseInt(xField.getText()),
                        Integer.parseInt(yField.getText()),
                        Integer.parseInt(zField.getText()),
                        descField.getText()
                );

                Coordinate[] old = currentGroup.coords;
                Coordinate[] updated = Arrays.copyOf(old, old.length + 1);
                updated[old.length] = c;
                currentGroup.coords = updated;

                output.append("Added: " + c + " to group " + currentGroup.name + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void saveGroup() {
        if (currentGroup == null) {
            JOptionPane.showMessageDialog(this, "No group selected.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try (PrintWriter out = new PrintWriter(f)) {
                out.println(currentGroup.name);
                out.println(currentGroup.description);
                for (Coordinate c : currentGroup.coords) {
                    out.println(c.toFileString());
                }
                output.append("Group saved to " + f.getName() + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        }
    }

    private void loadGroup() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try (BufferedReader in = new BufferedReader(new FileReader(f))) {
                String name = in.readLine();
                String desc = in.readLine();
                java.util.List<Coordinate> coords = new ArrayList<>();
                String line;
                while ((line = in.readLine()) != null) {
                    coords.add(Coordinate.fromFileString(line));
                }
                Group g = new Group(name, desc, coords.toArray(new Coordinate[0]));
                groups.put(name, g);
                currentGroup = g;
                groupSelector.addItem(name);
                output.append("Loaded group: " + name + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file.");
            }
        }
    }

    private void listGroups() {
        if (groups.isEmpty()) {
            output.append("No groups created yet.\n");
            return;
        }
        output.append("Groups:\n");
        for (String name : groups.keySet()) {
            output.append(" - " + name + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GroupManagerGUI::new);
    }
}
