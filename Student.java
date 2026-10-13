// Student.java
// Data structure representing an individual student record.

public class Student {
    // Unique identifier for the student (must be a positive integer)
    private int roll;
    // Full name of the student
    private String name;
    // Academic branch or department (e.g. CSE, ECE, MECH)
    private String branch;
    // Cumulative Grade Point Average (ranging from 0.0 to 10.0)
    private double cgpa;

    // Constructor to initialize a complete Student instance
    public Student(int roll, String name, String branch, double cgpa) {
        this.roll = roll;
        this.name = name;
        this.branch = branch;
        this.cgpa = cgpa;
    }

    // Returns the roll number
    public int getRoll() {
        return roll;
    }

    // Returns the student's name
    public String getName() {
        return name;
    }

    // Updates the student's name
    public void setName(String name) {
        this.name = name;
    }

    // Returns the branch
    public String getBranch() {
        return branch;
    }

    // Updates the branch
    public void setBranch(String branch) {
        this.branch = branch;
    }

    // Returns the CGPA
    public double getCgpa() {
        return cgpa;
    }

    // Updates the CGPA
    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    // Converts student details to JSON format string
    public String toJson() {
        return "{\"roll\":" + roll +
               ",\"name\":\"" + escapeJson(name) + "\"" +
               ",\"branch\":\"" + escapeJson(branch) + "\"" +
               ",\"cgpa\":" + cgpa + "}";
    }

    // Helper method to escape special characters for JSON encoding
    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '"') {
                sb.append("\\\"");
            } else if (ch == '\\') {
                sb.append("\\\\");
            } else if (ch == '\b') {
                sb.append("\\b");
            } else if (ch == '\f') {
                sb.append("\\f");
            } else if (ch == '\n') {
                sb.append("\\n");
            } else if (ch == '\r') {
                sb.append("\\r");
            } else if (ch == '\t') {
                sb.append("\\t");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
