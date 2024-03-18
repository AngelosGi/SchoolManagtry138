public class Grade {
    private int gradeId;
    private int studentId; // Change the type to int
    private Course course;
    private double gradeValue;

    // Constructor
    public Grade(int gradeId, int studentId, Course course, double gradeValue) {
        this.gradeId = gradeId;
        this.studentId = studentId; // Assign the studentId parameter
        this.course = course;
        this.gradeValue = gradeValue;
    }


    //Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }


    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public double getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(double gradeValue) {
        this.gradeValue = gradeValue;
    }
}