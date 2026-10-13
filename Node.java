// Node.java
// Node structure for linked list chaining in the hash table.

public class Node {
    // The student object stored in this bucket chain node
    public Student student;
    // Reference pointer to the next node in the chain
    public Node next;

    // Constructor to initialize node with student record and link to next node
    public Node(Student student, Node next) {
        this.student = student;
        this.next = next;
    }
}
