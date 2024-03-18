import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String dateOfBirth;

//Constructors


    public Student(String firstName, String lastName, String gender, String address, String dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public Student(int studentId, String firstName, String lastName, String gender, String address, String dateOfBirth) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    //Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    //Methods
    public static void loadStudentsFromDatabase(DefaultTableModel tableModel) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT studentId, firstName, lastName, gender, address, dateOfBirth FROM students";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int studentId = resultSet.getInt("studentId");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String gender = resultSet.getString("gender");
                String address = resultSet.getString("address");
                String dateOfBirth = resultSet.getString("dateOfBirth");
                tableModel.addRow(new Object[]{studentId, firstName, lastName, gender, address, dateOfBirth});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveToDatabase() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO students (firstName, lastName, gender, address, dateOfBirth) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, this.firstName);
            stmt.setString(2, this.lastName);
            stmt.setString(3, this.gender);
            stmt.setString(4, this.address);
            stmt.setString(5, this.dateOfBirth);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.studentId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating student failed, no ID obtained.");
                }
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


    //    // Method to update an existing student in the database
    public void updateStudentInDatabase() throws SQLException {
        String query = "UPDATE students SET firstName=?, lastName=?, gender=?, address=?, dateOfBirth=? WHERE studentId=?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, dateOfBirth);
            preparedStatement.setInt(6, studentId);

            preparedStatement.executeUpdate();
        }
    }
    public static void deleteFromDatabase(int studentId) throws SQLException {
        String query = "DELETE FROM students WHERE studentId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting student failed, no rows affected.");
            }
        }
    }

}


