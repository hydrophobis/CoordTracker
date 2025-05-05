import coords.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main extends JFrame {
    // Data models
    private Map<String, Group> groups = new HashMap<>();
    private Group currentGroup = null;
    private DefaultTableModel coordTableModel;
    
    // Main UI components
    private JTabbedPane mainTabPane;
    private JPanel groupPanel, coordPanel, aboutPanel;
    private JComboBox<String> groupSelector;
    private JTextArea groupDescription;
    private JTextArea statsArea;
    private JLabel statusBar;
    private JTable coordTable;
    
    // Colors and styling
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color BG_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    
    public Main() {
        setTitle("Coordinate Group Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setBackground(BG_COLOR);
        
        initComponents();
        layoutComponents();
        setupListeners();
        
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
        
        // Welcome message
        JOptionPane.showMessageDialog(this, 
            "Welcome to the Coordinate Group Manager!\n\n" +
            "Create or load a group to get started.", 
            "Welcome", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void initComponents() {
        // Initialize main container
        mainTabPane = new JTabbedPane();
        mainTabPane.setFont(HEADER_FONT);
        mainTabPane.setBackground(BG_COLOR);
        
        // Initialize panels
        groupPanel = createGroupPanel();
        coordPanel = createCoordinatePanel();
        aboutPanel = createAboutPanel();
        
        // Add tabs
        mainTabPane.addTab("Groups", new ImageIcon(), groupPanel, "Manage coordinate groups");
        mainTabPane.addTab("Coordinates", new ImageIcon(), coordPanel, "View and edit coordinates");
        mainTabPane.addTab("About", new ImageIcon(), aboutPanel, "About this application");
        
        // Status bar
        statusBar = new JLabel(" Ready");
        statusBar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)));
        statusBar.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private JPanel createGroupPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(BG_COLOR);
        
        // Top controls - group selection
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        topPanel.setBackground(BG_COLOR);
        
        JLabel groupLabel = new JLabel("Current Group:");
        groupLabel.setFont(HEADER_FONT);
        groupLabel.setForeground(TEXT_COLOR);
        
        groupSelector = new JComboBox<>();
        groupSelector.setFont(NORMAL_FONT);
        groupSelector.setBackground(Color.WHITE);
        groupSelector.setPreferredSize(new Dimension(200, 30));
        
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.setBackground(BG_COLOR);
        selectPanel.add(groupLabel);
        selectPanel.add(groupSelector);
        
        // Group description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(HEADER_FONT);
        descLabel.setForeground(TEXT_COLOR);
        
        groupDescription = new JTextArea(3, 20);
        groupDescription.setFont(NORMAL_FONT);
        groupDescription.setLineWrap(true);
        groupDescription.setWrapStyleWord(true);
        groupDescription.setEditable(false);
        JScrollPane descScroll = new JScrollPane(groupDescription);
        descScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Top panel assembly
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBackground(BG_COLOR);
        descPanel.add(descLabel, BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        
        topPanel.add(selectPanel, BorderLayout.NORTH);
        topPanel.add(descPanel, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton createBtn = createStyledButton("Create Group", "create");
        JButton switchBtn = createStyledButton("Switch Group", "switch");
        JButton saveBtn = createStyledButton("Save Group", "save");
        JButton loadBtn = createStyledButton("Load Group", "load");
        JButton listBtn = createStyledButton("List Groups", "list");
        JButton deleteBtn = createStyledButton("Delete Group", "delete");
        
        buttonPanel.add(createBtn);
        buttonPanel.add(switchBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(listBtn);
        buttonPanel.add(deleteBtn);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new BorderLayout(5, 5));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(10, 10, 10, 10)));
        
        JLabel statsLabel = new JLabel("Group Statistics");
        statsLabel.setFont(HEADER_FONT);
        statsLabel.setForeground(PRIMARY_COLOR);
        
        JTextArea statsArea = new JTextArea(5, 20);
        statsArea.setFont(NORMAL_FONT);
        statsArea.setEditable(false);
        statsArea.setText("No group selected");
        
        statsPanel.add(statsLabel, BorderLayout.NORTH);
        statsPanel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        
        // Store reference to statsArea for updates
        this.statsArea = statsArea;
        
        // Main assembly
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(statsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCoordinatePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(BG_COLOR);
        
        // Table model setup
        String[] columnNames = {"Name", "X", "Y", "Z", "Description"};
        coordTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        coordTable = new JTable(coordTableModel);
        coordTable.setFont(NORMAL_FONT);
        coordTable.setRowHeight(25);
        coordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coordTable.getTableHeader().setFont(HEADER_FONT);
        coordTable.getTableHeader().setBackground(PRIMARY_COLOR);
        coordTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane tableScroll = new JScrollPane(coordTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton addBtn = createStyledButton("Add Coordinate", "add");
        JButton editBtn = createStyledButton("Edit Coordinate", "edit");
        JButton deleteBtn = createStyledButton("Delete Coordinate", "delete_coord");
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        // Assembly
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Dimensional");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JTextArea aboutText = new JTextArea();
        aboutText.setFont(NORMAL_FONT);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setEditable(false);
        aboutText.setBackground(Color.WHITE);
        aboutText.setText(
            "Minecraft coordinate manager\n\n" +
            "Features:\n" +
            "- Create, save, and load coordinate groups\n" +
            "- Add/edit/delete 3D coordinates with descriptions\n" +
            "- Organize coordinates into logical groups\n" +
            "- Export and import data to/from files\n\n" +
            "Version: 2.0\n" +
            "Last Updated: May 2025"
        );
        
        JScrollPane textScroll = new JScrollPane(aboutText);
        textScroll.setBorder(null);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(textScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void layoutComponents() {
        // Main layout
        setLayout(new BorderLayout());
        add(mainTabPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text, String actionCommand) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setActionCommand(actionCommand);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    private void setupListeners() {
        // Find all buttons and add action listeners
        addButtonListeners(groupPanel);
        addButtonListeners(coordPanel);
        
        // Group selector listener
        groupSelector.addActionListener(e -> {
            String selected = (String) groupSelector.getSelectedItem();
            if (selected != null && groups.containsKey(selected)) {
                currentGroup = groups.get(selected);
                updateGroupInfo();
                updateCoordinateTable();
                setStatus("Switched to group: " + selected);
            }
        });
    }
    
    private void addButtonListeners(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.addActionListener(e -> handleButtonAction(e.getActionCommand()));
            } else if (comp instanceof Container) {
                addButtonListeners((Container) comp);
            }
        }
    }
    
    private void handleButtonAction(String action) {
        switch (action) {
            case "create": createGroup(); break;
            case "switch": switchGroup(); break;
            case "add": addCoordinate(); break;
            case "edit": editCoordinate(); break;
            case "delete_coord": deleteCoordinate(); break;
            case "save": saveGroup(); break;
            case "load": loadGroup(); break;
            case "list": listGroups(); break;
            case "delete": deleteGroup(); break;
            default: setStatus("Action not implemented: " + action);
        }
    }
    
    private void createGroup() {
        JTextField nameField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Group Name:"), BorderLayout.NORTH);
        panel.add(nameField, BorderLayout.CENTER);
        
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        
        JPanel mainPanel = new JPanel(new BorderLayout(5, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(descPanel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, mainPanel, "Create New Group", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String desc = descArea.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Group name cannot be empty.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (groups.containsKey(name)) {
                JOptionPane.showMessageDialog(this, "A group with this name already exists.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Group g = new Group(name, desc, new Coordinate[0]);
            groups.put(name, g);
            currentGroup = g;
            
            // Update UI
            groupSelector.addItem(name);
            groupSelector.setSelectedItem(name);
            updateGroupInfo();
            updateCoordinateTable();
            mainTabPane.setSelectedIndex(0); // Switch to Groups tab
            
            setStatus("Created new group: " + name);
        }
    }
    
    private void switchGroup() {
        if (groups.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No groups available.", 
                                         "Switch Group", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] groupNames = groups.keySet().toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(this, 
                                                          "Select a group to switch to:",
                                                          "Switch Group", 
                                                          JOptionPane.QUESTION_MESSAGE,
                                                          null,
                                                          groupNames,
                                                          currentGroup != null ? currentGroup.name : groupNames[0]);
        
        if (selected != null) {
            currentGroup = groups.get(selected);
            groupSelector.setSelectedItem(selected);
            updateGroupInfo();
            updateCoordinateTable();
            setStatus("Switched to group: " + selected);
        }
    }
    
    private void addCoordinate() {
        if (currentGroup == null) {
            JOptionPane.showMessageDialog(this, "Please select or create a group first.", 
                                         "No Group Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JTextField nameField = new JTextField();
        JTextField xField = new JTextField();
        JTextField yField = new JTextField();
        JTextField zField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("X Coordinate:"));
        panel.add(xField);
        panel.add(new JLabel("Y Coordinate:"));
        panel.add(yField);
        panel.add(new JLabel("Z Coordinate:"));
        panel.add(zField);
        
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        
        JPanel mainPanel = new JPanel(new BorderLayout(5, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(descPanel, BorderLayout.SOUTH);
        
        int result = JOptionPane.showConfirmDialog(this, mainPanel, "Add New Coordinate", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                int z = Integer.parseInt(zField.getText().trim());
                String desc = descArea.getText().trim();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Coordinate name cannot be empty.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Coordinate c = new Coordinate(name, x, y, z, desc);
                currentGroup.addCoordinate(c);
                
                updateCoordinateTable();
                mainTabPane.setSelectedIndex(1); // Switch to Coordinates tab
                setStatus("Added coordinate: " + name + " to group " + currentGroup.name);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Coordinates must be valid integers.", 
                                             "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editCoordinate() {
        if (currentGroup == null || currentGroup.coords.length == 0) {
            JOptionPane.showMessageDialog(this, "No coordinates available to edit.", 
                                         "Edit Coordinate", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = coordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a coordinate to edit.", 
                                         "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Coordinate c = currentGroup.coords[selectedRow];
        
        JTextField nameField = new JTextField(c.name);
        JTextField xField = new JTextField(String.valueOf(c.x));
        JTextField yField = new JTextField(String.valueOf(c.y));
        JTextField zField = new JTextField(String.valueOf(c.z));
        JTextArea descArea = new JTextArea(c.description, 3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("X Coordinate:"));
        panel.add(xField);
        panel.add(new JLabel("Y Coordinate:"));
        panel.add(yField);
        panel.add(new JLabel("Z Coordinate:"));
        panel.add(zField);
        
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        
        JPanel mainPanel = new JPanel(new BorderLayout(5, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(descPanel, BorderLayout.SOUTH);
        
        int result = JOptionPane.showConfirmDialog(this, mainPanel, "Edit Coordinate", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                int z = Integer.parseInt(zField.getText().trim());
                String desc = descArea.getText().trim();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Coordinate name cannot be empty.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                c.name = name;
                c.x = x;
                c.y = y;
                c.z = z;
                c.description = desc;
                
                updateCoordinateTable();
                setStatus("Updated coordinate: " + name);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Coordinates must be valid integers.", 
                                             "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteCoordinate() {
        if (currentGroup == null || currentGroup.coords.length == 0) {
            JOptionPane.showMessageDialog(this, "No coordinates available to delete.", 
                                         "Delete Coordinate", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = coordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a coordinate to delete.", 
                                         "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Coordinate c = currentGroup.coords[selectedRow];
        int result = JOptionPane.showConfirmDialog(this, 
                                                "Are you sure you want to delete coordinate '" + c.name + "'?", 
                                                "Confirm Delete", 
                                                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Create new array without selected coordinate
            Coordinate[] newCoords = new Coordinate[currentGroup.coords.length - 1];
            int newIndex = 0;
            
            for (int i = 0; i < currentGroup.coords.length; i++) {
                if (i != selectedRow) {
                    newCoords[newIndex++] = currentGroup.coords[i];
                }
            }
            
            currentGroup.coords = newCoords;
            updateCoordinateTable();
            setStatus("Deleted coordinate: " + c.name);
        }
    }
    
    private void saveGroup() {
        if (currentGroup == null) {
            JOptionPane.showMessageDialog(this, "No group selected.", 
                                         "Save Group", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            
            // Add .cgrp extension if not specified
            if (!f.getName().toLowerCase().endsWith(".cgrp")) {
                f = new File(f.getAbsolutePath() + ".cgrp");
            }
            
            try (PrintWriter out = new PrintWriter(f)) {
                out.println(currentGroup.name);
                out.println(currentGroup.description);
                for (Coordinate c : currentGroup.coords) {
                    out.println(c.toFileString());
                }
                setStatus("Group saved to " + f.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), 
                                             "File Error", JOptionPane.ERROR_MESSAGE);
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
                List<Coordinate> coords = new ArrayList<>();
                String line;
                
                while ((line = in.readLine()) != null) {
                    coords.add(Coordinate.fromFileString(line));
                }
                
                Group g = new Group(name, desc, coords.toArray(new Coordinate[0]));
                
                // Check if group with same name exists
                if (groups.containsKey(name)) {
                    int result = JOptionPane.showConfirmDialog(this, 
                        "A group with the name '" + name + "' already exists. Overwrite?", 
                        "Group Already Exists", JOptionPane.YES_NO_OPTION);
                    
                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    groupSelector.addItem(name);
                }
                
                groups.put(name, g);
                currentGroup = g;
                groupSelector.setSelectedItem(name);
                updateGroupInfo();
                updateCoordinateTable();
                setStatus("Loaded group: " + name + " (" + coords.size() + " coordinates)");
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), 
                                             "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void listGroups() {
        if (groups.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No groups available.", 
                                         "Group List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Available Groups:\n\n");
        
        for (Group g : groups.values()) {
            sb.append("• ").append(g.name).append(" (").append(g.coords.length).append(" coordinates)\n");
            if (!g.description.isEmpty()) {
                sb.append("  Description: ").append(g.description).append("\n");
            }
            sb.append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(NORMAL_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Group List", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteGroup() {
        if (currentGroup == null) {
            JOptionPane.showMessageDialog(this, "No group selected.", 
                                         "Delete Group", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
                                                "Are you sure you want to delete group '" + currentGroup.name + "'?", 
                                                "Confirm Delete", 
                                                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            String name = currentGroup.name;
            groups.remove(name);
            groupSelector.removeItem(name);
            
            if (groups.isEmpty()) {
                currentGroup = null;
                updateGroupInfo();
                updateCoordinateTable();
            } else {
                String firstGroup = groups.keySet().iterator().next();
                currentGroup = groups.get(firstGroup);
                groupSelector.setSelectedItem(firstGroup);
            }
            
            setStatus("Deleted group: " + name);
        }
    }
    
    private void updateGroupInfo() {
        if (currentGroup != null) {
            // Update description
            groupDescription.setText(currentGroup.description);
            
            // Update statistics
            StringBuilder stats = new StringBuilder();
            stats.append("Group: ").append(currentGroup.name).append("\n");
            stats.append("Total coordinates: ").append(currentGroup.coords.length).append("\n");
            
            // Calculate bounding box if coordinates exist
            if (currentGroup.coords.length > 0) {
                int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
                int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
                int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
                
                for (Coordinate c : currentGroup.coords) {
                    minX = Math.min(minX, c.x);
                    maxX = Math.max(maxX, c.x);
                    minY = Math.min(minY, c.y);
                    maxY = Math.max(maxY, c.y);
                    minZ = Math.min(minZ, c.z);
                    maxZ = Math.max(maxZ, c.z);
                }
                
                stats.append("\nBounding Box:\n");
                stats.append("X: ").append(minX).append(" to ").append(maxX).append("\n");
                stats.append("Y: ").append(minY).append(" to ").append(maxY).append("\n");
                stats.append("Z: ").append(minZ).append(" to ").append(maxZ).append("\n");
                
                // Calculate centroid
                double centerX = (minX + maxX) / 2.0;
                double centerY = (minY + maxY) / 2.0;
                double centerZ = (minZ + maxZ) / 2.0;
                
                stats.append("\nCenter point: (");
                stats.append(String.format("%.2f, %.2f, %.2f", centerX, centerY, centerZ));
                stats.append(")");
            } else {
                stats.append("\nNo coordinates in this group yet.");
            }
            
            statsArea.setText(stats.toString());
        } else {
            groupDescription.setText("No group selected");
            statsArea.setText("No group selected");
        }
    }
    
    private void updateCoordinateTable() {
        coordTableModel.setRowCount(0); // Clear existing data
        
        if (currentGroup != null && currentGroup.coords.length > 0) {
            for (Coordinate c : currentGroup.coords) {
                coordTableModel.addRow(new Object[]{c.name, c.x, c.y, c.z, c.description});
            }
        }
    }
    
    private void setStatus(String message) {
        statusBar.setText(" " + message);
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
        }
        
        SwingUtilities.invokeLater(Main::new);
    }
}