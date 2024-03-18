import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class SchoolAppMainWindow {
    private static DefaultTableModel courseTableModel; // Declare courseTableModel at the class level
    private static boolean isEditClicked = false; // Flag to track whether the "Edit Student" button is clicked

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("School Management App");
        mainFrame.setSize(700, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("School Management Application"));

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Status: Ready"));

        // Create the JTabbedPane for central content
        JTabbedPane tabbedPane = new JTabbedPane();

        // Student panel
        JPanel studentPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2)); // Reduced to 5 rows
        // Remove ID field
        formPanel.add(new JLabel("First Name:"));
        JTextField firstNameField = new JTextField();
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        JTextField lastNameField = new JTextField();
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Date of Birth:"));
        JTextField dobField = new JTextField();
        formPanel.add(dobField);
        formPanel.add(new JLabel("Address:"));
        JTextField addressField = new JTextField();
        formPanel.add(addressField);
        String[] genders = {"Male", "Female", "Other"};
        JComboBox genderComboBox = new JComboBox(genders);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderComboBox);

        //Button Panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Student");
        JButton editButton = new JButton("Edit Student");
        JButton deleteButton = new JButton("Delete Student");

        buttonPanel.add(saveButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);


        String[] columnNames = {"ID", "First Name", "Last Name", "Gender", "Address", "Date of Birth"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable studentTable = new JTable(tableModel);
        studentTable.setDefaultEditor(Object.class, null); // Disable row editing

        // Add double-click listener for student table
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click detected
                    int row = studentTable.rowAtPoint(evt.getPoint());
                    if (row >= 0 && row < studentTable.getRowCount()) {
                        int studentId = (int) studentTable.getValueAt(row, 0);
                        String firstName = (String) studentTable.getValueAt(row, 1);
                        String lastName = (String) studentTable.getValueAt(row, 2);
                        String gender = (String) studentTable.getValueAt(row, 3);
                        String address = (String) studentTable.getValueAt(row, 4);
                        String dateOfBirth = (String) studentTable.getValueAt(row, 5);

                        List<Grade> grades;
                        try {
                            grades = Course.getGradesForStudent(studentId);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return;
                        }

                        // Create a new JFrame for displaying student details
                        JFrame studentDetailsFrame = new JFrame("Student Details: " + firstName + " " + lastName);
                        studentDetailsFrame.setSize(600, 400);

                        // Create a panel to hold student details and grades
                        JPanel studentDetailsPanel = new JPanel();
                        studentDetailsPanel.setLayout(new BorderLayout());

                        // Panel for displaying student information
                        JPanel studentInfoPanel = new JPanel();
                        studentInfoPanel.setLayout(new GridLayout(6, 1));
                        studentInfoPanel.add(new JLabel("Student ID: " + studentId));
                        studentInfoPanel.add(new JLabel("First Name: " + firstName));
                        studentInfoPanel.add(new JLabel("Last Name: " + lastName));
                        studentInfoPanel.add(new JLabel("Gender: " + gender));
                        studentInfoPanel.add(new JLabel("Address: " + address));
                        studentInfoPanel.add(new JLabel("Date of Birth: " + dateOfBirth));

                        studentDetailsPanel.add(studentInfoPanel, BorderLayout.NORTH);

                        // Panel for displaying grades
                        JPanel gradesPanel = new JPanel();
                        gradesPanel.setLayout(new BorderLayout());

                        // Create a table model for grades
                        String[] columnNames = {"Course", "Grade"};
                        DefaultTableModel gradeTableModel = new DefaultTableModel(columnNames, 0);

                        // Add grades to the table model
                        for (Grade grade : grades) {
                            gradeTableModel.addRow(new Object[]{grade.getCourse().getTitle(), grade.getGradeValue()});
                        }

                        // Print the value obtained from the table model for debugging
                        for (int i = 0; i < gradeTableModel.getRowCount(); i++) {
                            System.out.println("Value from table model: " + gradeTableModel.getValueAt(i, 1));
                        }

                        // Create a JTable with the table model
                        JTable gradeTable = new JTable(gradeTableModel);
                        JScrollPane scrollPane = new JScrollPane(gradeTable);

                        gradesPanel.add(scrollPane, BorderLayout.CENTER);

                        // Add the grades panel to the student details panel
                        studentDetailsPanel.add(gradesPanel, BorderLayout.CENTER);

                        // Button to add course to student
                        JButton addCourseToStudentButton = new JButton("Add Course to Student");
                        addCourseToStudentButton.addActionListener(e -> {
                            // Create a dropdown menu (combo box) with existing courses
                            JComboBox<String> courseComboBox = new JComboBox<>();
                            for (int i = 0; i < courseTableModel.getRowCount(); i++) {
                                String courseTitle = (String) courseTableModel.getValueAt(i, 1);
                                courseComboBox.addItem(courseTitle);
                            }

                            // Display a dialog with the dropdown menu
                            int option = JOptionPane.showConfirmDialog(null, courseComboBox, "Select Course", JOptionPane.OK_CANCEL_OPTION);

                            // Check if the user selected a course
                            if (option == JOptionPane.OK_OPTION) {
                                // Get the selected course from the combo box
                                String selectedCourseTitle = (String) courseComboBox.getSelectedItem();

                                // Find the courseId based on the selected course title
                                int courseId = -1; // Default value
                                for (int i = 0; i < courseTableModel.getRowCount(); i++) {
                                    if (selectedCourseTitle.equals(courseTableModel.getValueAt(i, 1))) {
                                        courseId = (int) courseTableModel.getValueAt(i, 0);
                                        break;
                                    }
                                }

                                if (courseId != -1) {
                                    // Associate the courseId with the studentId in the student_courses table
                                    try {
                                        Course.addCourseToStudent(studentId, courseId);
                                        JOptionPane.showMessageDialog(null, "Course added to student successfully.");

                                        // Reload courses and update the table model
                                        courseTableModel.setRowCount(0); // Clear the table
                                        Course.loadCoursesFromDatabase(courseTableModel); // Reload courses
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(null, "Failed to add course to student.");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Selected course not found.");
                                }
                            }
                        });
                        studentDetailsPanel.add(addCourseToStudentButton, BorderLayout.NORTH);

                        // Button to delete course from student
                        JButton deleteCourseFromStudentButton = new JButton("Delete Course from Student");
                        deleteCourseFromStudentButton.addActionListener(e -> {
                            int selectedRow = gradeTable.getSelectedRow();
                            if (selectedRow == -1) {
                                JOptionPane.showMessageDialog(null, "Please select a course to delete.");
                                return;
                            }

                            String selectedCourseTitle = (String) gradeTableModel.getValueAt(selectedRow, 0);
                            int courseId = -1;

                            // Find the courseId based on the selected course title
                            for (Grade grade : grades) {
                                if (selectedCourseTitle.equals(grade.getCourse().getTitle())) {
                                    courseId = grade.getCourse().getCourseId(); // Adjusted here
                                    break;
                                }
                            }

                            if (courseId != -1) {
                                // Delete the association between the student and the course
                                try {
                                    Course.deleteCourseFromStudent(studentId, courseId);
                                    JOptionPane.showMessageDialog(null, "Course deleted from student successfully.");

                                    // Reload courses and update the table model
                                    courseTableModel.setRowCount(0); // Clear the table
                                    Course.loadCoursesFromDatabase(courseTableModel); // Reload courses
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Failed to delete course from student.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Selected course not found.");
                            }
                        });
                        studentDetailsPanel.add(deleteCourseFromStudentButton, BorderLayout.SOUTH);

                        JButton saveGradesButton = new JButton("Save Grades");
                        saveGradesButton.addActionListener(e -> {
                            // Iterate through the table model and update grades in the database
                            for (int i = 0; i < gradeTableModel.getRowCount(); i++) {
                                String courseTitle = (String) gradeTableModel.getValueAt(i, 0);
                                Object gradeValueObject = gradeTableModel.getValueAt(i, 1);

                                // Debugging statement to check the type of gradeValueObject
                                System.out.println("Type of gradeValueObject: " + gradeValueObject.getClass().getName());

                                // Convert gradeValueObject to double
                                try {
                                    double gradeValue = Double.parseDouble(gradeValueObject.toString());
                                    Course.updateGrade(studentId, courseTitle, gradeValue);
                                } catch (NumberFormatException ex) {
                                    // Handle the case where gradeValueObject is not a valid double
                                    System.err.println("Error: Grade value is not a valid double.");
                                    continue; // Skip this iteration and proceed with the next one
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Failed to save grades.");
                                    return;
                                }
                            }

                            JOptionPane.showMessageDialog(null, "Grades saved successfully.");
                        });

                        studentDetailsPanel.add(saveGradesButton, BorderLayout.EAST);

                        // Add the student details panel to the frame
                        studentDetailsFrame.add(studentDetailsPanel);
                        studentDetailsFrame.setVisible(true);
                    }
                }
            }
        });



        // Add to studentPanel (order changed for positioning)
        studentPanel.add(buttonPanel, BorderLayout.NORTH); // Position buttons at the top
        studentPanel.add(formPanel, BorderLayout.CENTER);
        studentPanel.add(new JScrollPane(studentTable), BorderLayout.SOUTH); // Position table below

        // Action Listeners
        saveButton.addActionListener(e -> {
            if (isEditClicked) {
                editStudent(tableModel, studentTable, firstNameField, lastNameField, dobField, addressField, genderComboBox);
            } else {
                saveStudent(tableModel, studentTable, firstNameField, lastNameField, dobField, addressField, genderComboBox);
            }
        });
        editButton.addActionListener(e -> {
            isEditClicked = true;
            editStudent(tableModel, studentTable, firstNameField, lastNameField, dobField, addressField, genderComboBox);
        });
        deleteButton.addActionListener(e -> deleteStudent(tableModel, studentTable));

        // Initial Load of Student Data
        try {
            Student.loadStudentsFromDatabase(tableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Course panel
        JPanel coursePanel = new JPanel(new BorderLayout());

        // Form panel for adding/editing courses
        JPanel courseFormPanel = new JPanel(new GridLayout(1, 2));
        courseFormPanel.add(new JLabel("Course Title:"));
        JTextField courseTitleField = new JTextField();
        courseFormPanel.add(courseTitleField);

        // Button panel for course management
        JPanel courseButtonPanel = new JPanel();
        JButton addCourseButton = new JButton("Add Course");
        JButton editCourseButton = new JButton("Edit Course");
        JButton deleteCourseButton = new JButton("Delete Course");
        courseButtonPanel.add(addCourseButton);
        courseButtonPanel.add(editCourseButton);
        courseButtonPanel.add(deleteCourseButton);

        // Table to display courses
        String[] courseColumnNames = {"ID", "Course Title"};
        courseTableModel = new DefaultTableModel(courseColumnNames, 0);
        JTable courseTable = new JTable(courseTableModel);
        courseTable.setDefaultEditor(Object.class, null); // Disable row editing
        JScrollPane courseScrollPane = new JScrollPane(courseTable);
        coursePanel.add(courseScrollPane, BorderLayout.CENTER);

        // Add to coursePanel
        coursePanel.add(courseButtonPanel, BorderLayout.NORTH);
        coursePanel.add(courseFormPanel, BorderLayout.CENTER);
        coursePanel.add(courseScrollPane, BorderLayout.SOUTH);

        // Load initial courses from the database
        try {
            Course.loadCoursesFromDatabase(courseTableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Action Listeners for course buttons
        addCourseButton.addActionListener(e -> {
            String title = courseTitleField.getText();

            try {
                Course.addCourse(title);
                courseTableModel.setRowCount(0); // Clear the table
                Course.loadCoursesFromDatabase(courseTableModel); // Reload courses
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle exception
            }
        });

        editCourseButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a course to edit.");
                return;
            }

            int courseId = (int) courseTableModel.getValueAt(selectedRow, 0);
            String newTitle = courseTitleField.getText();

            try {
                Course.editCourse(courseId, newTitle);
                courseTableModel.setRowCount(0); // Clear the table
                Course.loadCoursesFromDatabase(courseTableModel); // Reload courses
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle exception
            }
        });

        deleteCourseButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a course to delete.");
                return;
            }

            int courseId = (int) courseTableModel.getValueAt(selectedRow, 0);

            try {
                Course.deleteCourse(courseId);
                courseTableModel.setRowCount(0); // Clear the table
                Course.loadCoursesFromDatabase(courseTableModel); // Reload courses
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle exception
            }
        });



        // Add tabs to the pane
        tabbedPane.addTab("Student Management", studentPanel);
        tabbedPane.addTab("Course Management", coursePanel);
        //tabbedPane.addTab("Grades Management", gradesPanel);

        // Add components to the main frame
        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(tabbedPane, BorderLayout.CENTER); // Use the tabbedPane in the center
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
    }

    private static void deleteStudent(DefaultTableModel tableModel, JTable studentTable) {
        int selectedRow = studentTable.getSelectedRow(); // Get the selected row
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to delete.");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0); // Retrieve the student ID
        try {
            // Delete the student from the database
            Student.deleteFromDatabase(studentId);
            System.out.println("Student deleted successfully.");

            // Remove the corresponding row from the table model
            tableModel.removeRow(selectedRow);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Failed to delete student.");
        }
    }

    public static void saveStudent(DefaultTableModel tableModel, JTable studentTable, JTextField firstNameField,
                                   JTextField lastNameField, JTextField dobField, JTextField addressField,
                                   JComboBox genderComboBox) {
        // Collect data from form fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String dateOfBirth = dobField.getText();
        String address = addressField.getText();
        String gender = (String) genderComboBox.getSelectedItem();

        // Check if a row is selected
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            // No row selected, so it's a new student
            try {
                // Insert the new student into the database
                Student newStudent = new Student(firstName, lastName, gender, address, dateOfBirth);
                newStudent.saveToDatabase();
                System.out.println("New student added successfully.");

                // Update the table model with the new student
                tableModel.addRow(new Object[]{newStudent.getStudentId(), firstName, lastName, gender, address, dateOfBirth});
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.err.println("Failed to add new student.");
            }
        } else {
            // Row selected, so it's an existing student being edited
            int studentId = (int) tableModel.getValueAt(selectedRow, 0);

            // Update existing student
            Student updatedStudent = new Student(studentId, firstName, lastName, gender, address, dateOfBirth);
            try {
                updatedStudent.updateStudentInDatabase();
                System.out.println("Student updated successfully.");

                // Update the table model with the edited information
                tableModel.setValueAt(firstName, selectedRow, 1);
                tableModel.setValueAt(lastName, selectedRow, 2);
                tableModel.setValueAt(gender, selectedRow, 3);
                tableModel.setValueAt(address, selectedRow, 4);
                tableModel.setValueAt(dateOfBirth, selectedRow, 5);
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.err.println("Failed to update student.");
            }
        }
    }

    public static void editStudent(DefaultTableModel tableModel, JTable studentTable, JTextField firstNameField,
                                   JTextField lastNameField, JTextField dobField, JTextField addressField,
                                   JComboBox genderComboBox) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(null, "Please select a student to edit.");
            return;
        }

        // Retrieve data from the selected row in the table model.
        String firstName = (String) tableModel.getValueAt(selectedRow, 1);
        String lastName = (String) tableModel.getValueAt(selectedRow, 2);
        String gender = (String) tableModel.getValueAt(selectedRow, 3);
        String address = (String) tableModel.getValueAt(selectedRow, 4);
        String dateOfBirth = tableModel.getValueAt(selectedRow, 5) != null ? (String) tableModel.getValueAt(selectedRow, 5) : "";


        // Set the retrieved data to the form fields
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        genderComboBox.setSelectedItem(gender);
        addressField.setText(address);

        // Check if the date of birth value is not null or empty before setting it to the dobField
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            dobField.setText(dateOfBirth);
        } else {
            dobField.setText(""); // Set an empty string if the date of birth is null or empty
        }
    }
}
