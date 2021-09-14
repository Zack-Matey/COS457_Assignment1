import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Assignment1 {
    
    public static void main(String[] args) throws IOException {
        System.out.println(parseLine("name;firstauthor;isbn;publisher;borrowed;memberid##","Deep Learning with PyTorch;Eli Stevens;1617295264;Maning;0;null##","isbn"));
        System.out.println(number_of_duplicates("Books.txt"));
        System.out.println(avg_age());
        print_publishers();
        print_member_name_family();
        //delete_member("Members.txt", 0);
        //delete_member("Members.txt", 114);
    }

    public static void print_publishers() throws FileNotFoundException {
        String[] publishers = getAttributesUnique("Books.txt","publisher");
        for (int i = 0; i<publishers.length; i++) {
            System.out.println(publishers[i]);
        }
    }

    public static void print_member_name_family() throws FileNotFoundException {
        String[] family = getAttributesUnique("Members.txt","family");
        for (int i = 0; i<family.length; i++) {
            System.out.println(family[i]);
        }
    }

    public static double avg_age() throws FileNotFoundException {
        String[] ages = getAttributes("Members.txt","memberage");
        double total = 0;
        for (int i = 0; i<ages.length; i++) {
            total += Double.parseDouble(ages[i]);
        }
        return total/ages.length;
    }

    public static void delete_member(String filename, int memberid) throws IOException {
        // note - this only functions for Members.txt, as the method signature
        // restricts functionality somewhat
        
        String tempFilename = "%%"+filename+"%%";
        File database = new File(filename);
        File tempFile = File.createTempFile(tempFilename, ".txt", new File(System.getProperty("user.dir")));
        FileWriter tempWrite = new FileWriter(tempFile);
        
        Scanner s = new Scanner(new File(filename));

        String prototype = s.nextLine();
        int attributePos = getAttributeIndex(prototype, "memberid");
        tempWrite.write(prototype);
        tempWrite.flush();


        String currentEntity;
        while (s.hasNextLine()) {
            currentEntity = s.nextLine();
            if (Integer.parseInt(parseLine(currentEntity, attributePos)) != memberid)
                tempWrite.append(currentEntity+"\n");
            tempWrite.flush();
        }
        
        // if another program accesses these files while this is running
        // this will hang until it can access the files
        // this avoids a race condition
        s.close();
        while (!database.delete());
        tempWrite.close();
        while(!tempFile.renameTo(database));
    }

    public static int number_of_duplicates(String filename) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        ArrayList<String> entities = new ArrayList<>();
        HashSet<String> duplicates = new HashSet<String>(entities);
        String currentEntity;
        while (s.hasNextLine()) {
            currentEntity = s.nextLine();
            // hashset will not accept duplicate values
            // so only one entry per repeated entity
            if (entities.contains(currentEntity))
                duplicates.add(currentEntity);
            else
                entities.add(s.nextLine());
        }
        
        return duplicates.size();
        
    }

    /*################### HELPER METHODS ###################*/

    public static String[] getAttributesUnique(String filename, String attributeName) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        String prototype = s.nextLine();
        int attributeIndex = getAttributeIndex(prototype,attributeName);
        HashSet<String> attributes = new HashSet<String>();
        String currentEntity;
        while (s.hasNextLine()) {
            currentEntity = s.nextLine();
            attributes.add(parseLine(currentEntity,attributeIndex));
        }
        return (Arrays.stream(attributes.toArray()).toArray(String[]::new));
    }

    public static String[] getAttributes(String filename, String attributeName) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        String prototype = s.nextLine();
        int attributeIndex = getAttributeIndex(prototype,attributeName);
        ArrayList<String> attributes = new ArrayList<String>();
        String currentEntity;
        while (s.hasNextLine()) {
            currentEntity = s.nextLine();
            attributes.add(parseLine(currentEntity,attributeIndex));
        }
        return (Arrays.stream(attributes.toArray()).toArray(String[]::new));
    }

    public static String parseLine(String prototype, String entity, String attribute) {
        return parseLine(entity, getAttributeIndex(prototype, attribute));
    }
    
    public static int getAttributeIndex(String prototype, String attributeName) {
        String prototypeSplit[] = prototype.split("\\||;|#");
        int i = 0;
        for(i=0;i<prototypeSplit.length && (prototypeSplit[i].compareTo(attributeName) != 0);i++);
        return i;
    }

    public static String parseLine(String entity, int attributeIndex) {
        String entityAttributes[] = entity.split("\\||;|#");
        return entityAttributes[attributeIndex];
    }

}