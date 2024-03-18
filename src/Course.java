import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private int courseId;
    private String title;

    // Constructor
    public Course(int courseId, String title) {
        this.courseId = courseId;
        this.title = title;
    }



    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //methods

    public static List<Grade> getGradesForStudent(int studentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();

        // Query to fetch grades associated with the student from the database
        String query = "SELECT sg.studentId, sg.courseId, sg.gradeValue, c.title\n" +
                "FROM student_grades sg\n" +
                "INNER JOIN courses c ON sg.courseId = c.courseId\n" +
                "WHERE sg.studentId = ?\n";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, studentId);
            resultSet = preparedStatement.executeQuery();

            // Iterate over the result set and add grades to the list
            while (resultSet.next()) {
                int courseId = resultSet.getInt("courseId");
                double gradeValue = resultSet.getDouble("gradeValue");
                String courseTitle = resultSet.getString("title");
                Course course = new Course(courseId, courseTitle);
                Grade grade = new Grade(studentId, courseId, course, gradeValue); // Corrected this line
                grades.add(grade);
            }
        } finally {
            // Close resources in the finally block
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }

        return grades;
    }

    public static void addCourseToStudent(int studentId, int courseId) throws SQLException {
        String query = "INSERT INTO student_grades (studentId, courseId) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);
            preparedStatement.executeUpdate();
        }
    }

    public static void updateGrade(int studentId, String courseTitle, double gradeValue) throws SQLException {
        String query = "UPDATE student_grades sg INNER JOIN courses c ON sg.courseId = c.courseId " +
                "SET sg.gradeValue = ? " +
                "WHERE sg.studentId = ? AND c.title = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, gradeValue);
            preparedStatement.setInt(2, studentId);
            preparedStatement.setString(3, courseTitle);
            preparedStatement.executeUpdate();
        }
    }



    // Add a new course to the database
    public static void addCourse(String title) throws SQLException {
        String query = "INSERT INTO courses (title) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.executeUpdate();
        }
    }

    // Update an existing course in the database
    public static void editCourse(int courseId, String title) throws SQLException {
        String query = "UPDATE courses SET title=? WHERE courseId=?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, courseId);
            preparedStatement.executeUpdate();
        }
    }

    // Delete a course from the database
    public static void deleteCourse(int courseId) throws SQLException {
        String query = "DELETE FROM courses WHERE courseId=?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, courseId);
            preparedStatement.executeUpdate();
        }
    }

    // Delete a course from a specific student
    public static void deleteCourseFromStudent(int studentId, int courseId ) throws SQLException {
        String query = "DELETE FROM student_grades WHERE courseId = ? AND studentId = ?";
        System.out.println("Deleting course from student: courseId=" + courseId + ", studentId=" + studentId);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, courseId);
            preparedStatement.setInt(2, studentId);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load courses from the database into the table model
    public static void loadCoursesFromDatabase(DefaultTableModel tableModel) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            String sql = "SELECT courseId, title FROM courses";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int courseId = resultSet.getInt("courseId");
                String title = resultSet.getString("title");
                tableModel.addRow(new Object[]{courseId, title});
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }
}
