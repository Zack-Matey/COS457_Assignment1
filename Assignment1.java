import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Assignment1 {

    /* ################### GLOBAL FINAL VARIABLES ################### */

    final static String EOL_PATTERN = "##|#";
    final static String DELIM_PATTERN = "\\||;|#";
    final static String DELETE_MEMBER_ATTRIBUTE = "memberid";
    final static String BOOK_FILE_NAME = "Books.txt";
    final static String MEMBER_FILE_NAME = "Members.txt";

    /* ################### MAIN METHODS ################### */

    public static int number_of_record(String filename) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        s.useDelimiter(EOL_PATTERN);
        int num_records;
        for (num_records = -1; s.hasNext(); num_records++)
            s.next();
        return num_records;
    }

    public static int number_of_duplicates(String filename) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        s.useDelimiter(EOL_PATTERN);
        ArrayList<String> entities = new ArrayList<>();
        HashSet<String> duplicates = new HashSet<String>(entities);
        String currentEntity;
        while (s.hasNext()) {
            currentEntity = s.next();
            // hashset will not accept duplicate values
            // so only one entry per repeated entity
            if (entities.contains(currentEntity))
                duplicates.add(currentEntity);
            else
                entities.add(s.next());
        }

        return duplicates.size();
    }

    // note - print_publishers, print_member_name_family, and avg_age slightly sacrifice
    // execution time in favor of readability, however, either way I wrote this, with a
    // separate helper method or all in one method, yields an execution time of O(n), the
    // difference is a constant factor c = 2, such that execution time = O(cn) = O(2n)

    public static void print_publishers() throws FileNotFoundException {
        String[] publishers = getAttributesUnique(BOOK_FILE_NAME, "publisher");
        for (int i = 0; i < publishers.length; i++) {
            System.out.println(publishers[i]);
        }
    }

    public static void print_member_name_family() throws FileNotFoundException {
        String[] family = getAttributesUnique(MEMBER_FILE_NAME, "family");
        for (int i = 0; i < family.length; i++) {
            System.out.println(family[i]);
        }
    }

    public static double avg_age() throws FileNotFoundException {
        String[] ages = getAttributes(MEMBER_FILE_NAME, "memberage");
        double total = 0;
        for (int i = 0; i < ages.length; i++) {
            total += Double.parseDouble(ages[i]);
        }
        return total / ages.length;
    }

    public static void delete_member(String filename, int memberid) throws IOException {
        // note - this only functions for Members.txt, as the method signature
        // restricts functionality somewhat - only Members.txt has memberid

        File database = new File(filename);
        File tempFile = File.createTempFile(filename, ".txt", new File(System.getProperty("user.dir")));
        FileWriter tempFileWrite = new FileWriter(tempFile);

        Scanner s = new Scanner(new File(filename));
        s.useDelimiter(EOL_PATTERN);

        String prototype = s.next();
        int attributePos = getAttributeIndex(prototype, DELETE_MEMBER_ATTRIBUTE);
        tempFileWrite.write(prototype);
        tempFileWrite.flush();

        String currentEntity;
        while (s.hasNext()) {
            currentEntity = s.next();
            if (Integer.parseInt(parseLine(currentEntity, attributePos)) != memberid)
                tempFileWrite.append(currentEntity + "\n");
            tempFileWrite.flush();
        }

        // if another program accesses these files while this is running
        // this will hang until it can access the files
        // this avoids a race condition
        s.close();
        while (!database.delete());
        tempFileWrite.close();
        while (!tempFile.renameTo(database));
    }

    /* ################### HELPER METHODS ################### */

    // returns unique attribute values of a file for a given attribute name and file name
    public static String[] getAttributesUnique(String filename, String attributeName) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        s.useDelimiter(EOL_PATTERN);
        String prototype = s.next();
        int attributeIndex = getAttributeIndex(prototype, attributeName);
        HashSet<String> attributes = new HashSet<String>();
        String currentEntity;
        while (s.hasNext()) {
            currentEntity = s.next();
            attributes.add(parseLine(currentEntity, attributeIndex));
        }
        return (Arrays.stream(attributes.toArray()).toArray(String[]::new));
    }

    // returns all attribute values for a given attribute name and file name
    public static String[] getAttributes(String filename, String attributeName) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        s.useDelimiter(EOL_PATTERN);
        String prototype = s.nextLine();
        int attributeIndex = getAttributeIndex(prototype, attributeName);
        ArrayList<String> attributes = new ArrayList<String>();
        String currentEntity;
        while (s.hasNextLine()) {
            currentEntity = s.nextLine();
            attributes.add(parseLine(currentEntity, attributeIndex));
        }
        return (Arrays.stream(attributes.toArray()).toArray(String[]::new));
    }

    // returns a specific attribute from an entity, given a prototype, entity, and attribute name
    public static String parseLine(String prototype, String entity, String attribute) {
        return parseLine(entity, getAttributeIndex(prototype, attribute));
    }

    // returns the index of an attribute in an entity, given a prototype and attribute name
    public static int getAttributeIndex(String prototype, String attributeName) {
        String prototypeSplit[] = prototype.split(DELIM_PATTERN);
        int i = 0;
        for (i = 0; i < prototypeSplit.length && (prototypeSplit[i].compareTo(attributeName) != 0); i++);
        return i;
    }

    // returns a specific attribute from an entity, given an entity and attribute index
    public static String parseLine(String entity, int attributeIndex) {
        String entityAttributes[] = entity.split(DELIM_PATTERN);
        return entityAttributes[attributeIndex];
    }

}