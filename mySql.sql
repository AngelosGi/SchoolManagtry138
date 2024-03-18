-- Drop the database if it already exists
DROP DATABASE IF EXISTS school_management;

-- Create the database
CREATE DATABASE school_management;

-- Use the database
USE school_management;

-- Create the 'students' table
CREATE TABLE students (
    studentId INT PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL, -- Consider using an ENUM for gender in the future
    address VARCHAR(100),
    dateOfBirth DATE
);

-- Create the 'courses' table
CREATE TABLE courses (
    courseId INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL
);

-- Create the 'grades' table (linking students and courses)
CREATE TABLE grades (
    gradeId INT PRIMARY KEY AUTO_INCREMENT,
    studentId INT NOT NULL,
    courseId INT NOT NULL,
    gradeValue DECIMAL(4,2), -- Example: Store grades with up to 2 decimal places (e.g., 9.50)
    FOREIGN KEY (studentId) REFERENCES students(studentId),
    FOREIGN KEY (courseId) REFERENCES courses(courseId)
);
