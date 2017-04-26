import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import AVL.Tree.AVLstring;
import AVL.Tree.avl;
import AVL.Tree.avl.AVLNode;
import HashTable.FinalHashTable;
import HashTable.HashPlayer;
import Parser.CommandType;


public class Run {
	static RandomAccessFile db;
	static FileReader commands;
	static FileWriter log;
	static BufferedReader reader;
	private static avl<AVLstring> Tree;
	private static FinalHashTable<HashPlayer> hash;
	private static String[] debut;
	private static String lahman, playerID;
	private static String birthDate, birthCountry, birthState, birthCity;
	private static String deathDate, deathCountry, deathState, deathCity;
	private static String firstName;
	private static String lastName;
	private static String weight, height, batting, throwing;
	private static String firstAppear, lastAppear;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		
		if (new File(args[0]) == null) {
			System.out.println("There is no name given "
					+ "for the database file\n");
			System.exit(0);
		}
		if (new File(args[1]) == null) {
			System.out.println("There is no script"
					+ " file name given\n");
			System.exit(0);
		}
		if (new File(args[2]) == null) {
			System.out.println("There is no name given"
					+ " for the log file");
			System.exit(0);	
		}
		File dbFile = new File(args[0]);
		db = new RandomAccessFile(dbFile, "rw");
		File commandsFile = new File(args[1]);
		commands = new FileReader(commandsFile);
		BufferedReader reader = new BufferedReader(commands);
		File logFile = new File(args[2]);
		log = new FileWriter(logFile);

		//128 is 2^7, so if the the table is less than 
		//half full, my insert method will always work
		hash = new FinalHashTable<HashPlayer>(128);
		hash.logOff();
		//146 is because that is the number of years the data
		//could range.
		debut = new String[146];
		//to replace the initial "null" values
		java.util.Arrays.fill(debut,"");
		Tree = new avl<AVLstring>();
		log.write("MLBPlayer Database\n\n");
		log.write("dbFile:  " + dbFile.getName() + "\n");
		log.write("Script:  " + commandsFile.getName() + "\n");
		log.write("Log:     " + logFile.getName() + "\n");
		log.write("------------------------------------------\n");
		String line = null;
		line = reader.readLine();
		while (line != null) {
			//to ignore the commented lines
			if (!line.startsWith(";")) {
				log.write("Command: " + line + "\n\n");
				log.write("------------------------------------------\n");
			}
				line = reader.readLine();	
			}
		reader.close();
		log.flush();
		db.close();
		commands.close();		
	}
	
	//This method reads the given commands and decides
	//how to process them. The different command types are 
	//located in the Parser package
	public static void ProcessCommand(String cmd, FileWriter logFile) 
			throws IOException {
		Scanner In = new Scanner(cmd);
		In.useDelimiter("\t");
		String Command = In.next();
		switch (Classify(Command)) {
			case IMPORT: handleImport(cmd, logFile);
				break;
			case IDENTIFY: handleIdentify(In, logFile);
				break;
			case STATS: handleStats(In, logFile);
				break;
			case DEBUTS: handleDebuts(In, logFile);
				break;
			case INDEX: handleIndex(In, logFile);
				break;
			case EXIT: logFile.write("Exiting now ...\n");
				return;
			default: logFile.write("Unrecognized command: " + 
				Command + "\n");
				break;
		}
	}
	
	private static void handleImport(String com, FileWriter Out) 
		throws IOException {
		//numOfFiles will report at the end the number
		//of lines that have been imported into the database
		int numOfFiles = 0;
		Scanner In = new Scanner(com);
		In.useDelimiter("\t");
		In.next();
		//data holds the file name, i.e. "MiniData.txt"
		String data = In.next();
		File dataFile = new File(data);
		Scanner s = new Scanner(dataFile);	
		String next = s.nextLine();
		long offset = 0;
		while (s.hasNextLine()) {
			String[] parts = next.split("\t");
			//this searches for the PlayerId, which is unique
			//Therefore, if the same ID has already been imported,
			//this should skip to the next line instead of importing
			//again
			if (Tree.find(new AVLstring(parts[1], 0)) != null) {
				next = s.nextLine();
			}
			//to hold the values of the different offsets if 
			//the same last name is imported more than once
			offset = db.getFilePointer();
			ArrayList<Long> off = new ArrayList<Long>();
			off.add(offset);
			//db.seek(offset);
			//to get over my no-line-separation issue
			db.writeChars(next + "\n");
			//the position of the first appearance
			if (!(parts[16].length() == 1)) {
				Scanner year = new Scanner(parts[16]);
				//breaking up the date to just get the year
				year.useDelimiter("/");
				year.nextInt();
				year.nextInt();
				int firstYear = year.nextInt();
			//simpler to start the array at 0 
			//and just add the difference later
				int index = (firstYear - 1869);
				String currentIDs = debut[index];
				debut[index] = (currentIDs + parts[1] + ", ");
			}
			//so the AVL will holds the playerID and offset
			Tree.insert(new AVLstring(parts[1], offset));
			//Hash table will hold the last name and an array list
			//of offsets that hold a line with that last name
			hash.Insert(new HashPlayer(parts[11], off));
			numOfFiles++;
//			offset = offset + next.length() + 1;
			next = s.nextLine();
		}
		String[] parts = next.split("\t");
		//this searches for the PlayerId, which is unique
		//Therefore, if the same ID has already been imported,
		//this should skip to the next line instead of importing
		//again
		if (Tree.find(new AVLstring(parts[1], 0)) != null) {
			Out.flush();
		}
		//this is the same thing as above to grab the last
		//line in the data file
		offset = db.getFilePointer();
		ArrayList<Long> off = new ArrayList<Long>();
		off.add(offset);
		db.writeChars(next + "\n");
		Scanner year = new Scanner(parts[16]);
		year.useDelimiter("/");
		year.nextInt();
		year.nextInt();
		int firstYear = year.nextInt();
		int index = (firstYear - 1869);
		String currentIDs = debut[index];
		if (!(parts[1].length() == 1)) {
			debut[index] = (currentIDs + parts[1]);
		}
		Tree.insert(new AVLstring(parts[1], offset));
		hash.Insert(new HashPlayer(parts[11], off));
		Out.write("\tImported " + numOfFiles + " records from " +
				data + "\n");
		Out.flush();	
	}
	
	private static void handleIdentify(Scanner In, FileWriter Out) 
			throws IOException {
		int numOfRecords = 0;
		In.useDelimiter("\t");
		//name holds the last name given
		String name = In.next();
		if (hash.Find(new HashPlayer(name, null)) == null) {
			Out.write("\tCouldn't find a record for " + name +
					"\n");
		}
		else {
			//this finds all players in the HashTable that have the 
			//same last name, and then creates an Array List of all 
			//the offsets, as getL() returns the offsets associated 
			//with that name
			ArrayList<Long> offs = hash.Find(new HashPlayer(name, null)).getL();
			//The number of records is the number of offsets found
			numOfRecords = offs.size();
			Out.write("\tFound " + numOfRecords + " record(s):\n\n");
			//This is supposed to go through that array, use the 
			//offset value to seek for the info in the data base, 
			//read that line, then split it up to return the
			//first and last name of the player at that offset
			for (int i = 0; i < numOfRecords; i++) {
				db.seek(offs.get(i));
				String line = db.readLine();
				String[] parts = line.split("\t");
				Out.write("\t" + offs.get(i) + ":  " +
						parts[10] + " " + parts[11] + "\n");
			}
		}
		Out.flush();
	}
	private static void handleStats(Scanner In, FileWriter Out) 
			throws IOException {
		In.useDelimiter("\t");
		String data = In.next();
		AVLstring node = Tree.find(new AVLstring(data, 0));
		//if the playerID isn't in the AVL, 
		//there is no data on that player
		if (node == null){
			Out.write("\tThere is no record of " + data + " stored\n");
		}
		else {
			//this gets the long value of the offset
			//associated with this playerID in the AVL
			long off = Tree.find(new AVLstring(data, 0)).getL();
			reportStatsAt(off, Out);
		}
		Out.flush();
	}
	
	private static void reportStatsAt(long offs, FileWriter Out) 
		throws IOException {
		try {
			stats(offs, Out);
			Out.write("\tFound record at offset " + offs + ":\n\n");
			Out.write("Lahman ID:   " + lahman + "\n");
			Out.write("Player ID:   " + playerID + "\n");
			Out.write("Name:        " + firstName + " " + 
					lastName + "\n");
			Out.write("Born:        " + birthDate + " " + birthCity +
					" " + birthState + ", " + birthCountry + "\n");
			Out.write("Died:        " + deathDate + " " + deathCity +
					" " + deathState + ", " + deathCountry + "\n");
			Out.write("Weight:      " + weight + "\n");
			Out.write("Height:      " + height + "\n");
			Out.write("Throws:      " + throwing + "\n");
			Out.write("Bats:        " + batting + "\n");
			Out.write("First game:  " + firstAppear + "\n");
			Out.write("Last game:   " + lastAppear + "\n");
			Out.flush();
		}
		catch (IOException e) {
			Out.write("Writing error: " + e);
			Out.flush();
		}
	}
	
	public static void stats(long offs, FileWriter Out) 
		throws IOException{
		
		try {
			//for the most part, empty values have a 
			//length of 1, except for batting and throwing,
			//which have a length of three when fulls
			db.seek(offs);
			String line = db.readLine();
			String[] parts = line.split("\t");
			if (!(parts[0].length() == 1)) {
				lahman = parts[0];
			}
			else {
				lahman = "??";
			}
			playerID = parts[1];
			if (!(parts[2].length() == 1)) {
				birthDate = parts[2];
			}
			else {
				birthDate = "??";
			}
			if (!(parts[3].length() == 1)) {
				birthCountry = parts[3];
			}
			else {
				birthCountry = "??";
			}
			if (!(parts[4].length() == 1)) {
				birthState = parts[4];
			}
			else {
				birthState = "??";
			}
			if (!(parts[5].length() == 1)) {
				birthCity = parts[5];
			}
			else {
				birthCity= "??";
			}
			if (!(parts[6].length() == 1)) {
				deathDate = parts[6];
			}
			else {
				deathDate = "??";
			}
			if (!(parts[7].length() == 1)){
				deathCountry = parts[7];
			}
			else {
				deathCountry = "??";
			}
			if (!(parts[8].length() == 1)) {
				deathState = parts[8];
			}
			else {
				deathState = "??";
			}
			if (!(parts[9].length() == 1)) {
				deathCity = parts[9];
			}
			else {
				deathCity = "??";
			}
			if (!(parts[10].length() == 1)) {
				firstName = parts[10];
			}
			else {
				firstName = "??";
			}
			lastName = parts[11];
			if (!(parts[12].length() == 1)) {
				weight = parts[12] + " lbs";
			}
			else {
				weight = "??";
			}
			if (!(parts[13].length() == 1)) {
				height = parts[13] + " inches";
			}
			else {
				height = "??";
			}
			if (!(parts[14].length() == 3)) {
				batting = "??";
			}
			else {
				batting = parts[14];
			}
			if (!(parts[15].length() == 3)) {
				throwing = "??";
			}
			else {
				throwing = parts[15];
			}
			if (!(parts[16].length() == 1)) {
				firstAppear = parts[16];
			}
			else {
				firstAppear = "??";
			}
			if (!(parts[17].length() == 1)) {
				lastAppear = parts[17];
			}
			else {
				lastAppear = "??";
			}
		}
		catch (IOException e) {
			Out.write("Writing error: " + e);
			Out.flush();
		}
		
	}
	
	private static void handleDebuts(Scanner s, FileWriter Out) 
		throws IOException {
		if (debut.length == 0) {
			Out.write("\tThere is no data at all in the debut array\n");
		}
		s.useDelimiter("\t");
		int records = 0;
		String d = s.next();
		//this gets the String value for the year,
		//converts it into an integer, then 
		//subtracts 1869 to find the index value
		//for debut[]
		int index = Integer.parseInt(d) - 1869;
		String debutData = debut[index];
		if (debutData.isEmpty()) {
			Out.write("\tThere is no data for this year\n");
		}
		else {
			String[] debutIDs = debutData.split(", ");
			records = debutIDs.length;
			Out.write("\tFound " + records + " record(s):\n\n");
			for (int i = 0; i < records; i++) {
				String treeData = Tree.find(new AVLstring(debutIDs[i], 0)).getS();
				//returns the offset for the playerID
				long treeID = Tree.find(new AVLstring(debutIDs[i], 0)).getL();
				if (treeData != null){
					//finds the offset in the database
					db.seek(treeID);
					String line = db.readLine();
					String[] pos = line.split("\t");
					//returns the first and last name at the offset
					Out.write("\t" + treeID + ":  " + pos[10] +
							" " + pos[11] + "\n");
				}
				else {
					Out.write("\tSomehow this data isn't in the AVL tree"
							+ " so something is very wrong");
				}
		}
		}
		Out.flush();
	}
	
	private static void handleIndex(Scanner s, FileWriter Out) 
			throws IOException {
		s.useDelimiter("\t");
		//the three different options for show_index_for
		String d = s.next();
		if (d.equals("PlayerID")) {
			Out.write(DisplayHelper(Tree.root, 0));;
		}
		else if (d.equals("PlayerName")) {
			hash.Display(Out);
		}
		else if (d.equals("PlayerDebut")) {
			for (int i = 0; i < 146; i++) {
				if (!(debut[i].equals(""))) {
					int year = i + 1869;
					Out.write(year + ":\t[" + debut[i] + "]\n");
				}
			}
			
		}
		else {
			Out.write("\tThere is not an index for that command\n");
		}
		Out.flush();
	}
	

	private static CommandType Classify(String Command) {

		if (Command.equals("import"))            
			return CommandType.IMPORT;
		if (Command.equals("identify_by_name"))  
			return CommandType.IDENTIFY;
		if (Command.equals("show_stats_for"))    
			return CommandType.STATS;
		if (Command.equals("show_debuts_for"))           
			return CommandType.DEBUTS;
		if (Command.equals("show_index_for")) 
			return CommandType.INDEX;
		if (Command.equals("exit"))      
			return CommandType.EXIT;
		return CommandType.UNKNOWN;
	} 

	private static String DisplayHelper(@SuppressWarnings("rawtypes") AVLNode root, int Level) throws IOException {

		   String Notes = new String();
		   if (root == null) { return Notes; }
		   Notes += DisplayHelper(root.left, Level + 1);
		   
	       for (int i = 0; i < Level; i++) {
	    	   Notes += "---";
	       }
	       if ( Level > 0 ) {
	    	  Notes += " ";
	       }
	       //determines if the node is left high, right high, 
	       //or equal height
    	   if (height(root.left) > height(root.right)) {
    		   Notes += "LEFTHI: ";
    	   }
    	   else if (height(root.left) < height(root.right)) {
    		   Notes += "RIGHTHI: ";
    	   }
    	   else {
    		   Notes += "EQUALHT: ";
    	   }
		   Notes += "(" + root.element + ")\n";

		   Notes += DisplayHelper(root.right, Level + 1);
		   return Notes;
		}

	//gets the height of an AVL node
	private static int height(@SuppressWarnings("rawtypes") AVLNode t) {
    	if (t == null) {
    		return 0;
    	}
    	else {
    		return t.height;
    	}
    }
	
}

