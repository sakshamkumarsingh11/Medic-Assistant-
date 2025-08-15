import javax.swing.*;
import javax.swing.border.EmptyBorder;                                              //for spacing the border
import java.awt.*;                                                                  // for font style etc
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;                                                  // window closing
import java.awt.geom.RoundRectangle2D;                                              // round borders
import java.net.URL;                                                                // image insertion
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;                                                 //hashset mapping

public class DoctorAssistantApp extends JFrame {
    // ui components
    private JTextField symptomInput;
    private JTextPane resultArea;        //result(html supporting)
    private JButton searchButton;
    private Image backgroundImage;

    // --- inner classes ---
    private static class SymptomInfo {
        //data holding but sirf single symptoms par hai
        String name;
        String description;
        String severity;
        SymptomInfo(String name, String description, String severity){
            this.name=name;
            this.description=description;
            this.severity=severity;
        }
        @Override       //html formated representation
        public String toString() {
            return String.format("<b>%s:</b> %s (Severity: %s)<br>",
                    htmlEscape(name),                                                            // used  htmlescape to avoid breakage of the window due to special characters
                    htmlEscape(description != null ? description : "No description"),            // if true= descrip or  no descrip
                    htmlEscape(severity != null ? severity : "N/A")); }
    }

    private static class MedicineInfo {
        //holds data the same way as SymptomInfo
        String name;
        String description;
        String dosage; String sideEffects;
        String contraindications;

        MedicineInfo(String n, String de, String d, String s, String c){
            name=n;
            description=de;
            dosage=d;
            sideEffects=s;
            contraindications=c;
        }
        @Override
        public String toString() {      //samee for html formated
            return String.format("<br><b><u>%s</u></b><br>"+"<b>Description:</b> %s<br>"+"<b>Dosage:</b> %s<br>"+"<b>Side Effects:</b> %s<br>"+"<b>Contraindications:</b> %s<br>",      // bold underline and line break
                    htmlEscape(name),
                    htmlEscape(description!=null?description:"N/A"),
                    htmlEscape(dosage!=null?dosage:"N/A"),
                    htmlEscape(sideEffects!=null?sideEffects:"N/A"),
                    htmlEscape(contraindications!=null?contraindications:"N/A"));
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;     // comparing it self
            if (o == null || getClass() != o.getClass()) return false;
            MedicineInfo that = (MedicineInfo) o;
            return name != null ? name.equals(that.name) : that.name == null;
        }
        @Override
        public int hashCode() {         // ensure same name objects  has same hash code
            return name != null ? name.hashCode() : 0;
        }
    }
    private static String htmlEscape(String input) {
        if (input == null) return "";
        return input.replace("&", "&").replace("<", "<").replace(">", ">");
    }
    // --- end of inner classes ---
//------------------------------------------------------------------------------------------------------------------------------------------------
    // Ui(Vivek)

    public DoctorAssistantApp() {
        super("Doctor Assistant");
        loadBackgroundImage();
        initComponents();
        setupLayout();
        setupListeners();

        setSize(900, 650);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }


    private void loadBackgroundImage() {
        String imageName = "image.png";
        URL imgUrl = null;
        try {
            imgUrl = getClass().getResource("/" + imageName);
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
                System.out.println("BG img '" + imageName + "' loaded (classpath)."); return;
            }else {
                System.err.println("BG img '/" + imageName + "' not found in classpath!");
            }
        } catch (Exception e) {
            System.err.println("Error loading BG from classpath: " + e);
        }
        if (backgroundImage == null) {
            try {
                backgroundImage = new ImageIcon(imageName).getImage();
                System.out.println("BG img '" + imageName + "' loaded (file system).");
            }
            catch (Exception e) { System.err.println("Error loading BG from file system: " + e);
                backgroundImage = null; }
        }
        if (backgroundImage == null) {
            System.err.println("BG IMAGE FAILED TO LOAD.");
        }
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private class ForegroundPanel extends JPanel {
        private Color overlayBackgroundColor = new Color(0, 80, 150, 190);
        private int cornerRadius = 30;
        ForegroundPanel() { setOpaque(false);
            setLayout(new BorderLayout(10, 15));
            setBorder(new EmptyBorder(25, 30, 25, 30));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(overlayBackgroundColor);
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            g2d.dispose(); super.paintComponent(g);
        }
    }
    private void initComponents() {
        symptomInput = new JTextField(25);
        symptomInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        symptomInput.setToolTipText("Enter symptoms separated by commas");
        symptomInput.setMargin(new Insets(2, 5, 2, 5));
        searchButton = new JButton("Find Recommendation for Medicine");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchButton.setForeground(UIManager.getColor("Button.foreground"));
        searchButton.setBackground(UIManager.getColor("Button.background"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setMargin(new Insets(5, 15, 5, 15));
        searchButton.setFocusPainted(true); searchButton.setBorderPainted(true);
        resultArea = new JTextPane();
        resultArea.setContentType("text/html");
        resultArea.setEditable(false);
        resultArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultArea.setOpaque(false);
        resultArea.setForeground(Color.BLACK);
        resultArea.setText("<html><body>Enter symptoms...</body></html>");
        resultArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    }

    private void setupLayout() {
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout()); // Use GridBagLayout
        ForegroundPanel foregroundPanel = new ForegroundPanel();
        JPanel inputSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputSubPanel.setOpaque(false);
        inputSubPanel.add(symptomInput);
        inputSubPanel.add(searchButton);
         JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        foregroundPanel.add(inputSubPanel, BorderLayout.NORTH);
        foregroundPanel.add(scrollPane, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(30, 40, 30, 40);
        backgroundPanel.add(foregroundPanel, gbc);
        setContentPane(backgroundPanel);
    }
    //work done by vivek till here
    // ----------------------------------------------------------------------------------------------------------------------------------


    // user interaction part
    private void setupListeners() {
        searchButton.addActionListener(e -> performSearch());        // calls when the user clicks the button.                                                                   // lambda function used to perform the event
        symptomInput.addActionListener(e -> performSearch());        // same it also calls
        addWindowListener(new WindowAdapter() {             // disconnects the sql connectivity
            @Override
            public void windowClosing(WindowEvent e) {      // while closing
                DatabaseManager.closeConnection();
                System.out.println("Application exiting.");
                System.exit(0);
            }});
    }

   //--------------------------------------------------------------------------------------------------------------------------------------
   // ---------------------------------------------------------------PERFORM SEARCH--------------------------------------------------------
   //---------------------------------------------------------------------------------------------------------------------------------------
    private void performSearch() {
        String bodyStyle = "color: black; font-family: SansSerif; font-size: 10pt;";
        resultArea.setText("<html><body style='" + bodyStyle + "'><i>Searching...</i></body></html>");
        String symptomsText = symptomInput.getText().trim();
        if (symptomsText.isEmpty()) {
            resultArea.setText("<html><body style='" + bodyStyle + "'><i style='color: #555555;'>Please enter symptoms.</i></body></html>");
            return;
        } List<String> inputSymptomNames = Arrays.stream(symptomsText.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(String::toLowerCase).collect(Collectors.toList());
        if (inputSymptomNames.isEmpty()) {
            resultArea.setText("<html><body style='" + bodyStyle + "'><i style='color: #555555;'>Please enter valid symptoms.</i></body></html>");
            return;
        } try { findSymptomInfoAndMedicines(inputSymptomNames);
        } catch (SQLException ex) {
            resultArea.setText(String.format("<html><body style='" + bodyStyle + "'><b style='color: #CC0000;'>DB Error:</b><br>%s</body></html>", htmlEscape(ex.getMessage())));
            ex.printStackTrace();
        } catch (Exception ex) {
            resultArea.setText(String.format("<html><body style='" + bodyStyle + "'><b style='color: #CC0000;'>Error:</b><br>%s</body></html>", htmlEscape(ex.getMessage())));
            ex.printStackTrace();
        }
    }


    // findSymptomInfoAndMedicines - uses black HTML text, unchanged otherwise
    private void findSymptomInfoAndMedicines(List<String> symptomNames) throws SQLException {
        String bodyStyle = "color: black; font-family: SansSerif; font-size: 10pt;";
        StringBuilder htmlOutput = new StringBuilder("<html><body style='" + bodyStyle + "'>");
        List<SymptomInfo> foundSymptoms;
        Set<MedicineInfo> potentialMedicines = new HashSet<>();
        Connection conn = null;
        try { conn = DatabaseManager.getConnection();
            foundSymptoms = getSymptomsDetails(conn, symptomNames);
            htmlOutput.append("<h3 style='color: #333333;'>Symptom Information</h3>");
            if (foundSymptoms.isEmpty()) { htmlOutput.append("<i>None found.</i><br>");
            } else { foundSymptoms.forEach(s -> htmlOutput.append(s.toString()));
                List<String> keywords = foundSymptoms.stream().map(s -> s.name.toLowerCase()).collect(Collectors.toList());
                if (!keywords.isEmpty()) potentialMedicines = getMedicinesByKeyword(conn, keywords);
            } htmlOutput.append("<h3 style='color: #333333;'>Recommended Medicine</h3>");
            htmlOutput.append("<i style='color: #AA5500;'>*** General Suggestions ONLY. Consult a doctor. ***</i><br>");
            if (potentialMedicines.isEmpty()) { htmlOutput.append("<br><i>None found.</i>");
            } else { potentialMedicines.forEach(m -> htmlOutput.append(m.toString()));
            } htmlOutput.append("<br><hr style='border-color: #AAAAAA;'><p style='color: #666666; font-size: 9pt;'>").append("<b>Disclaimer:</b> ... (Always consult a qualified doctor before taking any medication. This system is only meant to assist, not diagnose.) ...").append("</p>"); htmlOutput.append("</body></html>"); resultArea.setText(htmlOutput.toString()); resultArea.setCaretPosition(0);
        } finally {  }
    }

    // Helper DB methods
    private List<SymptomInfo> getSymptomsDetails(Connection conn, List<String> symptomNames) throws SQLException {
        List<SymptomInfo> details = new ArrayList<>();
        if (symptomNames == null || symptomNames.isEmpty())
            return details;
        String placeholders = String.join(",", java.util.Collections.nCopies(symptomNames.size(), "?")); String sql = "SELECT name, description, severity_level FROM symptoms WHERE LOWER(name) IN (" + placeholders + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < symptomNames.size(); i++) ps.setString(i + 1, symptomNames.get(i).toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { details.add(new SymptomInfo(rs.getString("name"), rs.getString("description"), rs.getString("severity_level")));
                }
            }
        }
        return details;
    }
    private Set<MedicineInfo> getMedicinesByKeyword(Connection conn, List<String> keywords) throws SQLException {
        Set<MedicineInfo> medicines = new HashSet<>();
        if (keywords == null || keywords.isEmpty()) return medicines;
        String sql = "SELECT name, description, dosage_instructions, side_effects, contraindications FROM medicines WHERE ";
        List<String> conditions = new ArrayList<>();
        for (String keyword : keywords) {
            conditions.add("LOWER(name) LIKE ?");
            conditions.add("LOWER(description) LIKE ?");
        }
        sql += String.join(" OR ", conditions);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1; for (String keyword : keywords) { String searchPattern = "%" + keyword.toLowerCase() + "%"; ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            } try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicines.add(new MedicineInfo(rs.getString("name"), rs.getString("description"), rs.getString("dosage_instructions"), rs.getString("side_effects"), rs.getString("contraindications")));
                }
            }
        }
        return medicines;
    }

    // --------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------Main-----------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        try {       // finding nimbus style
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals( info.getName() )) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;      //found and now break
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus L&F not set: " + e);     //if not found
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
              throw new RuntimeException(ex);
            }
        }
            new DoctorAssistantApp().setVisible(true);
        ;           // if not true then program will run but no display
    }
}

//public static void main(String[] args) {
//    SwingUtilities.invokeLater(() -> {
//    DoctorAssistantApp appWindow = new DoctorAssistantApp();
//    appWindow.setVisible(true);
//    });

//}
//}