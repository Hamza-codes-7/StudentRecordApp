# AGENTS.md — Student Record Management using Hashing

## Project goal
A college data-structures project, presented to a teacher. A Java backend
implements a HAND-WRITTEN hash table with chaining. A plain web page lets the
user add, search, update and delete student records and SEE the buckets.
The student must be able to understand and explain every line, so keep the
code simple and heavily commented.

## Hard rules (never break these)
1. Backend is Java only. It must compile with plain `javac *.java`.
   No Maven, no Gradle, no Spring, no external libraries.
2. The hash table is my own. Do NOT use HashMap, HashSet, Hashtable,
   TreeMap or java.util.LinkedList anywhere for storing students.
   Use an array of buckets, each bucket a chain of my own Node objects.
3. Server uses com.sun.net.httpserver.HttpServer on port 8081.
   Port 8080 is used by Jenkins on this machine. Never use 8080.
4. Frontend is ONE file, web/index.html, with plain HTML + CSS + JavaScript.
   No frameworks, no CDNs, no external fonts, no images from the internet.
5. Do not add features that are not listed here. Do not create extra files.

## Folder layout
```
Student.java
Node.java
StudentTable.java
Server.java
web/index.html
AGENTS.md
```

## Data model
Student: roll (int), name (String), branch (String), cgpa (double).
Node: holds one Student and a `next` pointer (for chaining).

## Hash table spec (StudentTable.java)
- `Node[] buckets = new Node[7];` and `int size`.
- Hash function: `roll % buckets.length`.
- `put(Student s)`: if the roll already exists in its chain, update it;
  otherwise insert the new node at the FRONT of the chain. size++ on insert.
- `get(int roll)`: go to the bucket, walk the chain, return the Student or null.
  Also provide a way to know how many nodes were checked (steps).
- `remove(int roll)`: unlink the node (handle first node and middle node).
  size-- on success. Return true/false.
- `toJson()`: whole table as JSON (format below).
- Each method and each important line has a beginner-friendly comment.

## API (Server.java). All responses are JSON, UTF-8.
| Endpoint | What it does |
|---|---|
| `/api/table` | returns the full table |
| `/api/add?roll=&name=&branch=&cgpa=` | add or update, returns full table |
| `/api/delete?roll=` | delete, returns full table |
| `/api/get?roll=` | search one student |
| `/` | serves web/index.html |

Table JSON (buckets is an array of 7 arrays, newest node first):
```
{"capacity":7,"size":2,"buckets":[[],[],[],
 [{"roll":108,"name":"Ravi","branch":"ECE","cgpa":7.9},
  {"roll":101,"name":"Asha","branch":"CSE","cgpa":8.7}],[],[],[]]}
```
Search JSON when found:
`{"found":true,"bucket":3,"steps":2,"student":{"roll":101,...}}`
Search JSON when missing: `{"found":false,"bucket":3,"steps":2}`
Errors: `{"error":"Roll number must be a positive integer"}`

## Validation (never crash on bad input)
- roll: positive integer. name and branch: not empty. cgpa: number from 0 to 10.
- Missing or malformed parameters return an {"error": "..."} message.
- Escape quotes and backslashes in strings when writing JSON.

## UI spec (web/index.html)
Look: a simple, tidy college project. LIGHT theme, human-made look.
- Page background #f5f6f8, panels white, text #222, ONE accent color #2b6cb0.
- System font stack (Segoe UI, Arial, sans-serif).
- 1px #ddd borders, 4px radius. NO gradients, NO shadows, NO glass effects,
  NO emojis, NO neon colors, NO animations except a plain background
  highlight on the bucket just used.
Layout:
- Title "Student Record Management using Hashing", subtitle:
  "Bucket = roll number % 7. Collisions are handled by chaining."
- Left panel: form (Roll No, Name, Branch, CGPA, Add / Update button),
  Search by Roll No (shows "Found in bucket 3 after checking 2 nodes"),
  Delete by Roll No.
- Right panel: "Hash table (7 buckets)", one row per bucket:
  `3 | 115 -> 108 -> 101`, empty buckets show "empty". Highlight the
  bucket just used with a light blue background.
- Bottom: plain table of all students (Roll, Name, Branch, CGPA, Bucket).
- Errors appear as small red text under the form. Never use alert().
- Use fetch() and redraw from the JSON. Use textContent, not innerHTML.
- Responsive: the two panels stack on narrow screens.

## Code style
- Simple, beginner-level Java. Short methods. Clear names.
- Comment every method and every important line.
- No clever tricks, no lambdas outside Server.java.

## Definition of done (check before finishing)
1. `javac *.java` compiles with no errors.
2. `java Server` prints: Server running at http://localhost:8081
3. Test with curl and show me the output:
   - add roll 101, 108, 115 (all land in bucket 3)
   - search roll 108 (bucket 3, steps 2)
   - delete roll 108, then show the table
4. Search all .java files for HashMap/HashSet/LinkedList and confirm none used.
5. Tell me the commands to run it and the address to open in the browser.