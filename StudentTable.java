// StudentTable.java
// Hand-written hash table implementing separate chaining to store Student records.

public class StudentTable {
    // Array of buckets for separate chaining collision resolution (capacity 7)
    private Node[] buckets;
    // Total count of student records currently stored in the hash table
    private int size;

    // Helper class to encapsulate the result of a search operation
    public static class SearchResult {
        // True if matching student was found, false otherwise
        public boolean found;
        // The bucket index where search took place (roll % 7)
        public int bucket;
        // Number of nodes inspected in the linked list chain
        public int steps;
        // Found Student object, or null if missing
        public Student student;

        // Constructor for SearchResult
        public SearchResult(boolean found, int bucket, int steps, Student student) {
            this.found = found;
            this.bucket = bucket;
            this.steps = steps;
            this.student = student;
        }

        // Serializes search result to JSON string
        public String toJson() {
            if (found && student != null) {
                return "{\"found\":true,\"bucket\":" + bucket +
                       ",\"steps\":" + steps +
                       ",\"student\":" + student.toJson() + "}";
            } else {
                return "{\"found\":false,\"bucket\":" + bucket +
                       ",\"steps\":" + steps + "}";
            }
        }
    }

    // Constructor: initializes hash table with 7 empty buckets and size 0
    public StudentTable() {
        this.buckets = new Node[7];
        this.size = 0;
    }

    // Hash function: computes bucket index from positive roll number
    public int hash(int roll) {
        return roll % buckets.length;
    }

    // Inserts a new student or updates existing record if roll number already exists
    public void put(Student student) {
        // Calculate target bucket index using hash function
        int index = hash(student.getRoll());

        // Walk the chain at buckets[index] to check if roll already exists
        Node current = buckets[index];
        while (current != null) {
            if (current.student.getRoll() == student.getRoll()) {
                // Roll number found; update existing record attributes
                current.student.setName(student.getName());
                current.student.setBranch(student.getBranch());
                current.student.setCgpa(student.getCgpa());
                return;
            }
            current = current.next;
        }

        // Roll number not found; create new Node and place at FRONT of bucket chain
        Node newNode = new Node(student, buckets[index]);
        buckets[index] = newNode;
        size++;
    }

    // Searches for a student by roll number and returns SearchResult object
    public SearchResult get(int roll) {
        // Calculate target bucket index
        int index = hash(roll);
        int steps = 0;

        Node current = buckets[index];
        // Walk the linked list chain and count steps taken
        while (current != null) {
            steps++;
            if (current.student.getRoll() == roll) {
                // Found matching student in chain
                return new SearchResult(true, index, steps, current.student);
            }
            current = current.next;
        }

        // Student not found in chain
        return new SearchResult(false, index, steps, null);
    }

    // Removes student record by roll number; returns true if unlinked, false if not found
    public boolean remove(int roll) {
        int index = hash(roll);

        Node current = buckets[index];
        Node previous = null;

        // Walk the chain looking for matching roll number
        while (current != null) {
            if (current.student.getRoll() == roll) {
                if (previous == null) {
                    // Node to remove is the head (first node) of the chain
                    buckets[index] = current.next;
                } else {
                    // Node to remove is in middle or end of the chain
                    previous.next = current.next;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }

        // Roll number was not found in bucket chain
        return false;
    }

    // Returns current number of students stored
    public int getSize() {
        return size;
    }

    // Returns capacity of buckets array (7)
    public int getCapacity() {
        return buckets.length;
    }

    // Serializes the entire table into JSON representation
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"capacity\":").append(buckets.length);
        sb.append(",\"size\":").append(size);
        sb.append(",\"buckets\":[");

        for (int i = 0; i < buckets.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("[");
            Node current = buckets[i];
            boolean firstNode = true;
            while (current != null) {
                if (!firstNode) {
                    sb.append(",");
                }
                sb.append(current.student.toJson());
                firstNode = false;
                current = current.next;
            }
            sb.append("]");
        }
        sb.append("]}");

        return sb.toString();
    }
}
