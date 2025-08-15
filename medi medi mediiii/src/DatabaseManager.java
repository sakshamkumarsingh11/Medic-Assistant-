import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {



    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_data_db";     //db details hai yeh
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sys123";

    private static Connection connection = null;        // ek bar me use to make app light

    // Static block to load  driver once if any error then it is displayed.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            }
        catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found!");
            e.printStackTrace(); // prints the place fo error.
            System.exit(1);
        }
    }

    // Private constructor taki objects na banane pade(no instantiation)
    private DatabaseManager() {}
    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {      // If connection is closed or null, create a new one
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection successful!");
            } catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    // Method to close the connection (last me use hoga-connection close and program seh exit)
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed."); // For debugging
                }
            } catch (SQLException e) {
                System.err.println("Error closing the database connection: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection = null; // Ensure it's reset
            }
        }
    }
}