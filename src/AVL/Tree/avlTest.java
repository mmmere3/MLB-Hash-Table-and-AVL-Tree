package AVL.Tree;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import AVL.Tree.Sherlock;
public class avlTest {

public static void main( String[] args ) throws IOException {
		
		String logName = new String("Summary.txt");
		boolean profMode = false;
		if ( args.length > 0 )
			profMode = true;
		if ( profMode ) {
			logName = "prof" + logName;
		}
		FileWriter Log;
		try {
			Log = new FileWriter(logName);
		}
		catch ( IOException e ) {
			System.out.println("Error creating log file: " + logName);
			System.out.println("Exiting...");
			return;
		}
		
		long randSeed = 0;
		if ( profMode ) {
			randSeed = System.currentTimeMillis();
			FileOutputStream f = new FileOutputStream("AVLSeed.txt");
			DataOutputStream Seed = new DataOutputStream(f);
			Seed.writeLong(randSeed);
			Seed.close();
		}
		else {
            try {
			   FileInputStream Seed = new FileInputStream("AVLSeed.txt");
			   DataInputStream Sd = new DataInputStream(Seed);
			   randSeed = Sd.readLong();
			   Seed.close();
            }
            catch ( FileNotFoundException e ) {
               System.out.println("Error: no seed file found.");
               System.out.println("Execute first with a command-line parameter to generate a seed file.");
               Log.close();
               return;
            }
		}
		Sherlock S = new Sherlock( profMode, randSeed);
   	    String Prefix = null;
	    Prefix = S.writePoints(0);
	 // wrap each test call in a try block to catch bad pointer exceptions locally
	 		try {
	 		   try {
	 	          if ( S.checkAVLTreeInitialization() ) {
	 			     Log.write(Prefix + "Passed:  check of AVL initialization.\n");
	 		      }
	 		      else {
	 			     Log.write(Prefix + "Failed:  check of AVL initialization.\n");
	 			     Log.write(Prefix + "Aborting remainder of AVL testing due to this error.\n");
	 				 Log.close();
	 		      }
	 		   }
	 		   catch ( Exception e ) {
	 			  Log.write(Prefix + "Unexpected exception caught during test of AVL initialization.\n");
	 			  Log.write(Prefix + "Likely cause would be a bad pointer.\n");
	 			  Log.write(Prefix + "Aborting remainder of AVL testing due to this error.\n");
	 			  Log.close();
	 			  return;
	 		   }
	 		   
	 		   try {
	 		      if ( S.checkAVLInsertion() ) {
	 			     Log.write(Prefix + "Passed:  check of AVL insertion.\n");
	 		      }
	 		      else {
	 			     Log.write(Prefix + "Failed:  check of AVL insertion.\n");
	 			     Log.write(Prefix + "Aborting remainder of AVL testing due to this error.\n");
	 				 Log.close();
	 		      }
	 		   }
	 		   catch ( Exception e ) {
	 				  Log.write(Prefix + "Unexpected exception caught during test of AVL insertion.\n");
	 				  Log.write(Prefix + "Likely cause would be a bad pointer.\n");
	 				  Log.write(Prefix + "Aborting remainder of AVL testing due to this error.\n");
	 				  Log.close();
	 				  return;
	 		   }		
	 		}
	 		catch ( IOException e ) {
	 			System.out.println("IOException occurred during testing.\n");
	 			return;
	 		}
	 		catch ( Exception e ) {
	 			System.out.println("Exception occurred during testing.\n");
	 			return;
	 		}
	 		

	 		try {
	 			Log.close();
	 		}
	 		catch ( IOException e ) {
	 			System.out.println("Error closing log file.");
	 		}
	 	}
}
