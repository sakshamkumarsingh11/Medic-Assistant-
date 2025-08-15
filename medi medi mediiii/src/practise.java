// this is basic program trial made  till first evaluation further enhancment are been added

//import javax.swing.*;
//import java.awt.*;                                      //abstract window toolkit - layout
//import java.awt.event.ActionEvent;                      //action performed (button)
//import java.awt.event.ActionListener;
//import java.awt.event.WindowAdapter;                    //close, max, min krne me
//import java.awt.event.WindowEvent;
//import java.sql.*;                                      // sql connect update fetch
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.HashSet;                                // To store unique medicine names
//import java.util.Set;                                    // To store unique medicine names
//import java.util.stream.Collectors;                      //for filtering of set (set-list)
//
//public class DoctorAssistantApp extends JFrame {
////forms the window.
//    private JTextField symptomInput;
//    private JTextArea resultArea;
//    private JButton searchButton;
//
//
//    // Inner class(data holder).
//    private static class SymptomInfo {
//        String name;
//        String description;
//        String severity;
//
//        SymptomInfo(String name, String description, String severity) {
//            this.name = name;
//            this.description = description;
//            this.severity = severity;
//        }
//
//        @Override
//        public String toString() {                  // used to string to give the data rather than memory address.
//            return String.format("- %s: %s (Severity: %s)",
//                    name,
//                    (description != null ? description : "No description"),
//                    (severity != null ? severity : "N/A"));
//        }
////easy version for the code  from line 34-39
////public String toString() {
////    String finalDescription;
////    if (description != null) {
////        finalDescription = description;
////    } else {
////        finalDescription = "No description";
////    }
////
////    String finalSeverity;
////    if (severity != null) {
////        finalSeverity = severity;
////    } else {
////        finalSeverity = "N/A";
////    }
////
////    return "- " + name + ": " + finalDescription + " (Severity: " + finalSeverity + ")";
////}
//
//    }
//
//    // Inner class(Med data holder)
//    private static class MedicineInfo {
//        String name;
//        String description;
//        String dosage;
//        String sideEffects;
//        String contraindications;
//
//        MedicineInfo(String name, String description, String dosage, String sideEffects, String contraindications) {
//            this.name = name;
//            this.description = description;
//            this.dosage = dosage;
//            this.sideEffects = sideEffects;
//            this.contraindications = contraindications;
//        }
//
//        @Override
//        public String toString() {
//            //same format 34-39
//            return String.format("- %s: %s\n    Dosage: %s\n    Side Effects: %s\n    Contraindications: %s",
//                    name,
//                    description != null ? description : "N/A",
//                    dosage != null ? dosage : "N/A",
//                    sideEffects != null ? sideEffects : "N/A",
//                    contraindications != null ? contraindications : "N/A");
//        }
//
//        // need equals n hashCode to use set for uniqueness based on name
//        @Override
//        public boolean equals(Object o) {
//            // check if the med given in input is same or not.
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            MedicineInfo that = (MedicineInfo) o;
//            return name != null ? name.equals(that.name) : that.name == null;
//        }
//
//        @Override
//        public int hashCode() {//compares-search
//            return name != null ? name.hashCode() : 0;
//        }
//    }
////---------------------------------------------------------------------------------------------------------------------
//// gui setup
//    public DoctorAssistantApp() {
//        super("Doctor Assistant");          //calls jframe to give tittle
//        initComponents();                       //in this method  buttons, text fields are made.
//        setupLayout();                           // set layout
//        setupListeners();                        // buttons commands
//        setSize(700, 600);
//        setLocationRelativeTo(null);            // screen at centre
//        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//    }
//
//    private void initComponents() {
//        symptomInput = new JTextField(30);
//        searchButton = new JButton("Find Recommendation for Medicines");
//        resultArea = new JTextArea(20, 50);             // Made text area larger
//        resultArea.setEditable(false);
//        resultArea.setLineWrap(true);
//        resultArea.setWrapStyleWord(true);
//    }
//
//    private void setupLayout() {
//        setLayout(new BorderLayout(10, 10));
//
//        // Input Panel
//        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//        inputPanel.add(new JLabel("Enter Symptoms (comma-separated):"));
//        inputPanel.add(symptomInput);
//        // Removed checkbox add
//        inputPanel.add(searchButton);
//
//        // Result Panel
//        JScrollPane scrollPane = new JScrollPane(resultArea);
//
//        // Disclaimer Panel( changed on 24-04-25)
//        JLabel disclaimerLabel = new JLabel("<html><b>Disclaimer:</b> This application is intended solely to assist in reducing medical workload and providing general guidance. It is not a substitute for professional medical advice, diagnosis, or treatment. Always consult a qualified healthcare provider before making any medical decisions based on the information provided by this app.</html>");
//        disclaimerLabel.setForeground(Color.RED);
//        JPanel disclaimerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        disclaimerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//        disclaimerPanel.add(disclaimerLabel);
//
//        // Add panels
//        add(inputPanel, BorderLayout.NORTH);
//        add(scrollPane, BorderLayout.CENTER);
//        add(disclaimerPanel, BorderLayout.SOUTH);
//
//        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//    }
////---------------------------------------------------------------------------------------------------------------------------------------------------------------------
//
//
//    private void setupListeners() {
//        searchButton.addActionListener(e -> performSearch());
//        symptomInput.addActionListener(e -> performSearch()); // allows search from enter key
//
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                DatabaseManager.closeConnection();
//                System.out.println("Application exiting.");
//                System.exit(0);
//                //finally closes the program after close button is been clicked and database connection is removed.
//            }
//        });
//    }
//
//
//    private void performSearch() {
//        String symptomsText = symptomInput.getText().trim();
//        resultArea.setText("");// cleans the result area
//
//
//        if (symptomsText.isEmpty()) {
//            resultArea.setText("Please enter some symptoms.");
//            return;
//        }
//
//        List<String> inputSymptomNames = Arrays.stream(symptomsText.split(","))// seprates by comma
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .map(String::toLowerCase) //  lowercase for searching
//                .collect(Collectors.toList());
//
//        if (inputSymptomNames.isEmpty()) {
//            resultArea.setText("Please enter valid symptoms separated by commas.");
//            return;
//        }
//
//        try {
//            findSymptomInfoAndMedicines(inputSymptomNames);
//        } catch (SQLException ex) {
//            resultArea.setText("Error connecting to or querying the database: \n" + ex.getMessage());
//            ex.printStackTrace();
//        } catch (Exception ex) {
//            resultArea.setText("An unexpected error occurred: \n" + ex.getMessage());
//            ex.printStackTrace();
//        }
//    }
//
//    // Renamed and re-logic'd method
//    private void findSymptomInfoAndMedicines(List<String> symptomNames) throws SQLException {
//        StringBuilder output = new StringBuilder();
//        List<SymptomInfo> foundSymptoms = new ArrayList<>();
//        Set<MedicineInfo> potentialMedicines = new HashSet<>();             // used Set to avoid duplicates
//
//        Connection conn = null;
//
//        try {
//            conn = DatabaseManager.getConnection();
//
//            // 1.details for entered symptoms
//            foundSymptoms = getSymptomsDetails(conn, symptomNames);
//
//            output.append("--- Found Symptom Information ---\n");
//            if (foundSymptoms.isEmpty()) {
//                output.append("None of the entered symptoms were found in the database.\n");
//            } else {
//                foundSymptoms.forEach(s -> output.append(s.toString()).append("\n"));
//
//                // Extract just the names of symptoms that were actually found
//                List<String> validSymptomKeywords = foundSymptoms.stream()
//                        .map(s -> s.name.toLowerCase())
//                        .collect(Collectors.toList());
//
//                //  find potential medi based on keywords from found symptoms
//                if (!validSymptomKeywords.isEmpty()) {
//                    potentialMedicines = getMedicinesByKeyword(conn, validSymptomKeywords);
//                }
//            }
//
//            output.append("\n--- Potential Medicines (General Suggestions based on Keywords) ---\n");
//            output.append("    ***This is NOT a diagnosis or prescription. Consult a doctor.***\n\n");
//            if (potentialMedicines.isEmpty()) {
//                output.append("No specific medicine suggestions found based on symptom keywords.\n");
//            } else {
//                potentialMedicines.forEach(m -> output.append(m.toString()).append("\n\n"));
//            }
//
//            resultArea.setText(output.toString());
//
//        } finally {
//            // Connection is managed by DatabaseManager and closed on exit
//        }
//    }
//
//    // Helper to get full symptom details from names (case-insensitive)
//    private List<SymptomInfo> getSymptomsDetails(Connection conn, List<String> symptomNames) throws SQLException {
//        List<SymptomInfo> details = new ArrayList<>();
//        if (symptomNames == null || symptomNames.isEmpty()) {
//            return details;
//        }
//
//        String placeholders = String.join(",", java.util.Collections.nCopies(symptomNames.size(), "?"));
//        // creates a comma-separated string of placeholders
//        // Fetch all relevant columns from the symptoms table
//        String sql = "SELECT name, description, severity_level FROM symptoms WHERE LOWER(name) IN (" + placeholders + ")";
//        //prepares sql statement
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            for (int i = 0; i < symptomNames.size(); i++) {
//                ps.setString(i + 1, symptomNames.get(i).toLowerCase());
//            }
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    details.add(new SymptomInfo(
//                            rs.getString("name"),
//                            rs.getString("description"),
//                            rs.getString("severity_level")
//                    ));
//                }
//            }
//        }
//        return details;
//    }
//
//
//    // Helper to find medicines potentially related to symptom keywords
//    private Set<MedicineInfo> getMedicinesByKeyword(Connection conn, List<String> keywords) throws SQLException {
//        Set<MedicineInfo> medicines = new HashSet<>(); // Set for uniqueness
//        if (keywords == null || keywords.isEmpty()) {
//            return medicines;
//        }
//
//        // Base query - adjust columns as needed
//        String sql = "SELECT name, description, dosage_instructions, side_effects, contraindications FROM medicines WHERE ";
//
//        // Build OR clauses for each keyword against relevant columns
//        List<String> conditions = new ArrayList<>();
//        for (String keyword : keywords) {
//            // Search in name and description (adjust as needed)
//            conditions.add("LOWER(name) LIKE ?");
//            conditions.add("LOWER(description) LIKE ?");
//            // Add other columns if you want to search them too, e.g., contraindications
//            // conditions.add("LOWER(contraindications) LIKE ?");
//        }
//        sql += String.join(" OR ", conditions);
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            int paramIndex = 1;
//            for (String keyword : keywords) {
//                String searchPattern = "%" + keyword.toLowerCase() + "%";
//                ps.setString(paramIndex++, searchPattern); // For name
//                ps.setString(paramIndex++, searchPattern); // For description
//
//            }
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    medicines.add(new MedicineInfo(
//                            rs.getString("name"),
//                            rs.getString("description"),
//                            rs.getString("dosage_instructions"),
//                            rs.getString("side_effects"),
//                            rs.getString("contraindications")
//                    ));
//                }
//            }                   //try-with-resources statement is used, which means that rs the ResultSet will be automatically closed once the block of code finishes executing.
//        }
//        return medicines;
//    }
//
//
//    // Main
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e) {
//                System.err.println("Couldn't set system Look and Feel.");
//            }
//            new DoctorAssistantApp().setVisible(true);// if not true to program toh chalega but dikhega nhi
//        });
//    }
//}