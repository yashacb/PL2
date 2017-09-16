package server.gui;

public class Student {
    /**
     * rollNo - The roll number of the student.
     * name - The name of the student.
     */
    String rollNo, name ;

    /**
     * @param rollNo - Roll number of the student.
     * @param name - Name of the student.
     */
    public Student(String rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "rollNo='" + rollNo + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
