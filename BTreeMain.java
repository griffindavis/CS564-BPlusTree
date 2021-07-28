import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Main Application.
 */
public class BTreeMain {

    public static void main(String[] args) {
    	
        /** Read the input file -- input.txt */
        Scanner scan = null;
        try {
            scan = new Scanner(new File("src/input.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        /** Read the minimum degree of B+Tree first */

        int degree = scan.nextInt();

        BTree bTree = new BTree(degree);

        /** Reading the database student.csv into B+Tree Node*/
        List<Student> studentsDB = getStudents();
        
        for (Student s : studentsDB) {
            bTree.insert(s);
        }

        /** Start reading the operations now from input file*/
        try {
            while (scan.hasNextLine()) {
                Scanner s2 = new Scanner(scan.nextLine());

                while (s2.hasNext()) {

                    String operation = s2.next();

                    switch (operation) {
                        case "insert": {

                            long studentId = Long.parseLong(s2.next());
                            String studentName = s2.next() + " " + s2.next();
                            String major = s2.next();
                            String level = s2.next();
                            int age = Integer.parseInt(s2.next());
                            
                            /*Write a logic to generate recordID*/
                            boolean rIDFound = false;
                            
                            Random r = new Random(); 
                            long recordID;
                            //loop to make sure we don't happen to use a record ID already in use
                            do {
                            	recordID = (long) r.nextInt(1000000000 - 1) + 1;  //up to 10 digit number
                            	
                            	for (Student stud : studentsDB) {
                            		//System.out.println("random: " + recordID + " student rID: " + stud.recordId);
                            		if (recordID == stud.recordId) {
                            			rIDFound = true;
                            		}
                            	}
                            } while (rIDFound);

                            Student s = new Student(studentId, age, studentName, major, level, recordID);
                            bTree.insert(s);

                            break;
                        }
                        case "delete": {
                            long studentId = Long.parseLong(s2.next());
                            boolean result = bTree.delete(studentId);
                            if (result)
                                System.out.println("Student deleted successfully.");
                            else
                                System.out.println("Student deletion failed.");

                            break;
                        }
                        case "search": {
                            long studentId = Long.parseLong(s2.next());
                            long recordID = bTree.search(studentId);
                            if (recordID != -1)
                                System.out.println("Student exists in the database at " + recordID);
                            else
                                System.out.println("Student does not exist.");
                            break;
                        }
                        case "print": {
                            List<Long> listOfRecordID = new ArrayList<>();
                            listOfRecordID = bTree.print();
                            System.out.println("List of recordIDs in B+Tree " + listOfRecordID.toString());
                            break;
                        }
                        default:
                            System.out.println("Wrong Operation");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Student> getStudents() {
        List<Student> studentList = new ArrayList<>();
        
        long sID;  //student ID
        long rID;  //record ID
        int age;   
        String name;
        String major;
        String level;
        
        String line;
        String[] lineSplit;
        //open scanner, try to read file
        Scanner sc = null;
        try {
        	sc = new Scanner(new File("src/Student.csv"));
        } catch (FileNotFoundException e) {
        	System.out.println("File not found.");
        }
        
        String delim = ",";  //set delimeter
        
        while (sc.hasNextLine()) {  			//grab each line
        	sc.useDelimiter(delim);				//use comma as delimeter
        	line = sc.nextLine();
        	System.out.println(line);
        	
        	lineSplit = line.split(delim);		//split up each line of CSV file
        	
        	sID = Long.parseLong(lineSplit[0]);	//convert to long and assign to sID
        	name = lineSplit[1];				//assign to name
        	major = lineSplit[2];				//assign to major
        	level = lineSplit[3];				//assign to level
        	age = Integer.parseInt(lineSplit[4]); //convert to Integer and assign to age
        	rID = Integer.parseInt(lineSplit[5]);
        	
        	//create a new student and add to student list arrayList
        	studentList.add(new Student(sID, age, name, major, level, rID));        	
        }
        
        //printList(studentList);       
               
        sc.close();  //close scanner
        return studentList;
    }
    
    /*
     * print out each student in the list to confirm getStudents is parsing correctly
     */
    private static void printList(List<Student> list) {
    	
    	for (Student stu : list) {
    		System.out.println(stu.studentId + "|" + stu.studentName + "|" + stu.major + "|" + stu.level + "|" + stu.age + "|" + stu.recordId);
    	}
    	
    }
}
