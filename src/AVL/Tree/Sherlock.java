package AVL.Tree;

import java.io.FileWriter; 
import java.io.IOException;
import java.util.Random;


public class Sherlock {
	private boolean    profMode = false;
	private Random     Source = null;
	
	public Sherlock(boolean profMode, long randSeed ) {
		this.profMode = profMode;
		Source = new Random(randSeed);
	}
		
	public boolean checkAVLTreeInitialization( ) {
	   
	   boolean Passed = true;
	   String Notes = new String();
	   Notes += writePoints(0) + "checkTreeInitialization results:\n";
	   Notes += writePoints(0) + "   Creating a new AVL using default constructor.\n";
	   
	   avl<Integer> Tree = new avl<Integer>();
	   Notes += writePoints(10); 
	   if ( Tree.root != null ) {
		   Notes += "   Error:  AVL root was NOT null.\n";
		   Passed = false;
      }
      else {
	      Notes += "   AVL root was OK.\n";
      }
      
      try {
          String logName = "TestAVLTreeInitialization.txt";
          if ( profMode )
    		 logName = "prof" + logName;
          FileWriter Log = new FileWriter(logName);
	      Log.write(Notes);
	      Log.close();
      }
      catch ( IOException e ) {
		   System.out.println("Error writing notes to log file in Sherlock.checkTreeInitialization().");
      }
      return Passed;
   }
	 public boolean checkAVLInsertion() throws IOException {

	      boolean Passed = true;
	      String logName = "TestAVLInsertion.txt";
	      if ( profMode )
			 logName = "prof" + logName;
	      FileWriter Log = new FileWriter(logName);
			
	      String Notes = new String();
	      Notes += writePoints(0) + "Begin test of AVL insertion.\n";
	      //////////////////////////////////////////////////////////////////////////// insertion of value into empty tree
	      Notes += writePoints(0) + "Checking insertion of value to empty tree...\n";
		  avl<Integer> T1 = new avl<Integer>();
		  avl<String> stringTree = new avl<String>();
		  String data = new String("jamerle01, 986");
		  stringTree.insert(data);
		  if (stringTree.root == null) {
			  Notes += "the String was not added to the AVL";
			  Log.write(Notes);
			  Log.close();
			  return false;
		  }
		  else {
			  String x = stringTree.find(data);
			  Notes += "The AVL tree found value " + x;
		  }
		  int rootValueKey = Source.nextInt() % 1000;
	      T1.insert( new Integer(rootValueKey) );   // tree with root only
	      if ( T1.root == null ) {
	         Notes += writePoints(80) + "   Error:  null root pointer.\n";
	         Notes += writePoints( 0) + "   Aborting insertion test.\n";
	         Log.write(Notes);
	         Log.close();
	         return false;
	      }
	      else {
	          Notes += writePoints(10) + "   Root pointer is not null.\n";
	          if ( !T1.root.element.equals( rootValueKey ) ) {
	        	 Notes += writePoints(70) + "   Error: root node contains wrong data value.\n";
	             Notes += writePoints( 0) + "   Aborting insertion test.\n";
	             Log.write(Notes);
	             Log.close();
	             return false;
	          }
	          else {
	         	 Notes += writePoints(10) + "   Root node contains correct data value.\n";
	          }
	          if ( T1.root.left != null || T1.root.right != null ) { 
	        	 Notes += writePoints(60) + "   Error: root node contains one or more non-null pointers.\n";
	             Notes += writePoints( 0) + "   Aborting insertion test.\n";
	             Log.write(Notes);
	             Log.close();
	             return false;
	          }
	          else {
	        	 Notes += writePoints(10) + "   Root node pointers are both null.\n";
	          }
	      }
	      //////////////////////////////////////////////////////////////////////////// creation of full two-level BST
	      Notes += writePoints(10) + "Checking insertion of two children of the current root node...\n";
	      int leftValue  = T1.root.element - 10;
	      int rightValue = T1.root.element + 10;
	      T1.insert( new Integer(leftValue) );   // insert left child
	      T1.insert( new Integer(rightValue) );  // insert right child
	      avl<Integer>.AVLNode rootAddress = T1.root;
	      avl<Integer>.AVLNode leftChildAddress  = T1.root.left;
	      avl<Integer>.AVLNode rightChildAddress = T1.root.right;
	      try {
	         if ( rootAddress != T1.root ) {
	            Notes += writePoints(70) + "   Error:  insertion messed up root address.\n";
	            Notes += writePoints( 0) + "   Aborting insertion test.\n";
	            Log.write(Notes);
	            Log.close();
	            return false;
	         }
	         else {
	            Notes += writePoints(10) + "   Insertion did not change root address.\n";
	         }
	         if ( leftChildAddress == null || rightChildAddress == null ) {
	            Notes += writePoints(60) + "   Error:  root node has one or more null child pointers.\n";
	            Notes += writePoints( 0) + "   Aborting insertion test.\n";
	            Log.write(Notes);
	            Log.close();
	            return false;
	         }
	         else {
	            Notes += writePoints(10) + "   Root node has two non-null child pointers.\n";
	         }
	         if ( !leftChildAddress.element.equals( leftValue ) ) {
	            Notes += writePoints(10) + "   Error:  left child stores wrong value.\n";
	            Passed = false;
	         }
	         else {
	            Notes += writePoints(10) + "   Left child stores correct value.\n";
	          }
	         if ( !rightChildAddress.element.equals( rightValue ) ) {
	            Notes += writePoints(10) + "   Error:  right child stores wrong value.\n";
	            Passed = false;
	         }
	         else {
	            Notes += writePoints(10) + "   Right child stores correct value.\n";
	         }
	      }
	      catch ( Exception e ) {
	         Notes += writePoints(70) + "   Bad pointer!  Caught an exception while checking structure!\n";
	         Notes += writePoints( 0) + "   Aborting insertion test.\n";
	         Log.write(Notes);
	         Log.close();
	         T1.root = null;  // mask bad pointer
	         Log.close();
	         return false;
	      }
	      Notes += writePoints(0) + "\n";
	      //////////////////////////////////////////////////////////////////////////// test of mass insertion
	      Notes += writePoints(0) + "Beginning general insertion test with a new, empty tree...\n";
	      avl<Integer> T2 = new avl<Integer>();
	      int treeSize = 50 + Math.abs(Source.nextInt()) % 51;
	      try {
	         String fillNotes = fillAVL(T2, treeSize, true);
	         Notes += fillNotes;
	         Notes += writePoints(0) + "   Resulting tree follows.  If the log ends suddenly, it's likely a pointer exception occurred.\n";
	         Notes += Display(T2, 2);
	      }
	      catch ( Exception e ) {
	    	 Notes += writePoints(0) + "   Bad pointer!  Caught an exception while inserting into the tree!\n";
	         Notes += writePoints(0) + "   Aborting insertion test.\n";
	         Log.write(Notes);
	         Log.close();
	         T2.root = null;  // mask bad pointer
	         Log.close();
	         return false;
	      }
	      Notes += writePoints(0) + "\n";
	      //////////////////////////////////////////////////////////////////////////// report final results
	      Log.write(Notes);
	      Log.close();
		   
	      return Passed;
	   }
	 public String fillAVL(avl<Integer> T, int Size, boolean showIt) {

		   String Notes = new String(writePoints(0) + "   Filling AVL with " + Size + " random integer values.\n");
		   if ( showIt ) {
			  Notes += writePoints(0) + "Inserted values: ";
		   }
		   for (int Count = 1; Count <= Size; Count++) {
			  int keyValue  = Source.nextInt() % 1000;
			  Integer toInsert = new Integer(keyValue);
			  
			  if ( showIt ) {
			     Notes += "  " + toInsert.toString();
		      }
		      T.insert( new Integer(keyValue) );
		   }
		   if ( showIt )
		      Notes += "\n";
		   return Notes;
		}
	 
	 public String Display(avl<Integer> Tree, int ptsPerLine) throws IOException {

		   if ( Tree.root == null ) {
			  return new String(writePoints(0) + "   Tree is empty.\n");
		   }
		   return DisplayHelper(Tree.root, 0, ptsPerLine);
		}
	 
	 public String DisplayHelper(avl<Integer>.AVLNode sRoot, int Level, int ptsPerLine) throws IOException {

			   String Notes = new String();
			   if (sRoot == null) return Notes;

			   Notes += DisplayHelper(sRoot.left, Level + 1, ptsPerLine);

			   Notes += writePoints(ptsPerLine);
		       for (int i = 0; i < Level; i++) {
		    	   Notes += "---";
		       }
		       if ( Level > 0 )
		    	  Notes += " ";
			   Notes += sRoot.element + "\n";

			   Notes += DisplayHelper(sRoot.right, Level + 1, ptsPerLine);
			   return Notes;
			}
	 
	    public String writePoints(int Pts) {

	        String S;
	        if ( profMode ) {
	     	   if ( Pts < 10 )
	     	      S = new String("[ " + Pts + "]");
	     	   else
	     		  S = new String("[" + Pts + "]");
	        }
	        else
	     	  S = new String("");
	        
	        return S;
	     }
}
