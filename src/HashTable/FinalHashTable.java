

// The test harness will belong to the following package; the hash table
// implementation will belong to it as well.  In addition, the hash table
// implementation will specify package access for all data members in order 
// that the test harness may have access to them.
//
package HashTable;

import java.io.FileWriter;
import java.io.IOException;

import HashTable.slotState;

public class FinalHashTable<T extends Hashable> {

    T[]         Table;        // stores data objects
    slotState[] Status;       // stores corresponding status values
    int         Size;         // dimension of Table
    int         Usage;        // # of data objects stored in Table
    FileWriter  Log;          // target of logged output
    boolean     loggingOn;    // write output iff this is true

    // Construct hash table with specified size.
    // Pre:
    //      Sz is a positive integer of the form 2^k.
    // Post:
    //      this.Table is an array of dimension Sz; all entries are null
    //      this.Status is an array of dimension Sz; all entries are EMPTY
    //      this.Usage == 0
    //      this.Log == null
    //      this.loggingOn == false
    //      
    @SuppressWarnings("unchecked")
	public FinalHashTable(int Sz) { 
    	Size = Sz;
    	Table = (T[]) new Hashable[Sz];
    	Status = new slotState[Sz];
    	for (int i = 0; i < Sz; i++) {
    		this.Table[i] = null;
    	}
    	for (int i = 0; i < Sz; i++) {
    		this.Status[i] = slotState.EMPTY;
    	}
    	this.Usage = 0;
    	this.Log = null;
    	this.loggingOn = false;
    }
    // Attempt insertion of Elem. 
    // Pre:
    //      Elem is a proper object of type T
    // Post:
    //      If Elem already occurs in Table (according to equals()):
    //         this.Table is unchanged
    //         this.Usage is unchanged
    //      Otherwise:
    //         Elem is added to Table
    //         this.Usage is incremented
    //      If loggingOn == true:
    //         indices accessed during search are written to Log and
    //         success/failure message is written to Log
    // Return: reference to inserted object or null if insertion fails
    //
    public T Insert(T Elem) throws IOException { 
    	try {
    		int capacity = (int) (Size * .50);
    		if (Usage >= capacity) {
    			rehash();
    		}
    		if (loggingOn == true) {
    			return InsertLog(Elem);
    		}
    		boolean duplicate = false;
   			int k = 1;
   			//marker for how many tombstones have been passed over
   			//while searching to see if the element is already in the table
   			int stones = 0;
   			//a number to represent the home slot, found by hashing mod the table size
   			int homeSlot = Math.abs(Elem.Hash()) % Size;
   			int currentPos = homeSlot;
   			//initializing the position for a potential tombstone to be inserted with 
   			//the new element
    		int tombPos = 0;
    		while (Status[currentPos] != slotState.EMPTY && !duplicate) {
    			//search to see if the element is already in the table
    			if (Table[currentPos] != null && Table[currentPos].equals(Elem)) {
    					duplicate = true;
    				}
    			else if (Status[currentPos] == slotState.TOMBSTONE) {
    				//every time a tombstone is "seen", the marker increases
    				stones++;
    				//the first tombstone will be the position Elem is
    				//inserted into if it isn't a duplicate
    				if (stones == 1) {
    					tombPos = currentPos;
    				}
    				//quadratic probing
        			currentPos = (homeSlot + (k^2 + k)/2) % Size;
        			k++;
    			}
    			else {
    				//quadratic probing
    				currentPos = (homeSlot + (k^2 + k)/2) % Size;
    				k++;
    				}
    		}
    		//if it's not a duplicate and at least one tombstone
    		//has been "seen", then the position of that tombstone
    		//is where Elem will be inserted
    		if (stones > 0 && duplicate == false) {
    			//the table now has Elem's value at this position
    			Table[tombPos] = Elem;
    			//this position is now FULL instead of a tombstone
    			Status[tombPos] = slotState.FULL;
    			//usage has increased
    			this.Usage++;
    			return Elem;
    		}
    		//if Elem is a duplicate it can't be inserted
    		else if (duplicate == true) {
    			return null;
    		}
    		else {
    			//If it doesn't find a tombstone, then Elem goes
    			//into the first empty slot it finds during probing
    			Table[currentPos] = Elem;
    			Status[currentPos] = slotState.FULL;
    			this.Usage++;
    			return Elem;
    		}
    		
    	}
    	catch (IOException e) {
    		Log.write("Something went wrong with insertion");
    	}
		return Elem;
    }
    
    public T InsertLog(T Elem) throws IOException {
    	try {
   			boolean duplicate = false;
   			int k = 1;
   			int stones = 0;
   			int homeSlot = Math.abs(Elem.Hash()) % Size;
   			int currentPos = homeSlot;
    		Log.write("\t" + homeSlot + "" + "\t");
    		int tombPos = 0;
    		while (Status[currentPos] != slotState.EMPTY && !duplicate) {
    			if (Table[currentPos] != null && Table[currentPos].equals(Elem)) {
    					duplicate = true;
    				}
    			else if (Status[currentPos] == slotState.TOMBSTONE) {
    				stones++;
    				if (stones == 1) {
    					tombPos = currentPos;
    				}
        			currentPos = (homeSlot + (k^2 + k)/2) % Size;
        			Log.write("" + currentPos + "\t" );
        			k++;
    			}
    			else {
    				currentPos = (homeSlot + (k^2 + k)/2) % Size;
    				Log.write("" + currentPos + "\t" );
    				k++;
    				}
    		}
    		if (stones > 0 && duplicate == false) {
    			Table[tombPos] = Elem;
    			Status[tombPos] = slotState.FULL;
    			this.Usage++;
    			Log.write("inserted");
    			Log.flush();
    			return Elem;
    		}
    		else if (duplicate == true) {
    			Log.write("duplicate");
    			Log.flush();
    			return null;
    		}
    		else {
    			Table[currentPos] = Elem;
    			Status[currentPos] = slotState.FULL;
    			Log.write("inserted");
    			this.Usage++;
    			Log.flush();
    			return Elem;
    		}
    		
    	}
    	catch (IOException e) {
    		Log.write("Something went wrong with insertion");
    		Log.flush();
    		return null;
    	}
    }
    
    // Search Table for match to Elem (according to equals()).
    // Pre:
    //      Elem is a proper object of type T
    // Post:
    //      No member of the hash table object is changed.
    //      If loggingOn == true:
    //         indices accessed during search are written to Log and
    //         success/failure message is written to Log
    // Return reference to matching data object, or null if no match 
    //      is found.
    public T Find(T Elem) throws IOException { 
    	try {
    		//modified from code on textbook page 186
    		if (loggingOn == true) {
    			return FindLog(Elem);
    		}
    		boolean found = false;
    		int k = 1;
    		int homeSlot = Math.abs(Elem.Hash()) % Size;
    		int currentPos = homeSlot;
    		while (Status[currentPos] != slotState.EMPTY && !found)  { 				
    			if (Table[currentPos] != null && Table[currentPos].equals(Elem)) {
    				found = true;
    			} 
   				else {
   					currentPos = (homeSlot + (k^2 + k)/2) % Size;
   					k++;
    			}
    		}
    		if (found) {
    			return Table[currentPos];
    		}	
    		else {
    			return null;
    		}
    	}
    	
    	catch (IOException e) {
    		Log.write("There was an error with the Find() method");
    		Log.flush();
    		return null;
    	}
	
    }
    
    public T FindLog(T Elem) throws IOException {
    	try {
    		boolean found = false;
    		int k = 1;
    		int homeSlot = Math.abs(Elem.Hash()) % Size;
    		Log.write("\t" + homeSlot + "" + "\t");
    		int currentPos = homeSlot;
    		while (Status[currentPos] != slotState.EMPTY && !found ) {
				if (Table[currentPos] != null && Table[currentPos].equals(Elem)) {
					found = true;
				} 
			else {
				//quadratic probing if not found
					currentPos = (homeSlot + (k^2 + k)/2) % Size;
					Log.write(currentPos + "\t" + "");
    				k++;
				}
    		}
    		if (found) {
    			Log.write("found");
        		Log.flush();
    			return Table[currentPos];
    		}
    		else {
    			Log.write("record not found");
        		Log.flush();
    			return null;
    		}
    	}
    	catch (IOException e) {
    		Log.write("There was a problem with the Find() method");
    		Log.flush();
    		return null;
    	}
    }
    
    // Delete data object that matches Elem.
    // Pre:
    //      Elem is a proper object of type t
    // Post:
    //      If Elem does not occur in Table (according to equals()):
    //         this.Table is unchanged
    //         this.Usage is unchanged
    //      Otherwise:
    //         matching reference in Table is null
    //         this.Usage is decremented
    //      If loggingOn == true:
    //         indices accessed during search are written to Log and
    //         success/failure message is written to Log
    // Return reference to deleted object, or null if not found.
    public T Delete(T Elem) throws IOException { 
    	try 
    	{
    		if (loggingOn == true) {
    			return deleteLog(Elem);
    		}
    		boolean found = false;
    		int k = 1;
    		//this hashes the function (mod table size) and returns an integer
    		//to be used as the home slot for probing
    		int homeSlot = Math.abs(Elem.Hash()) % Size;
    		int currentPos = homeSlot;
    		while (Status[currentPos] != slotState.EMPTY && !found) {
    			//a null, nonempty slot could be a tombstone
    			if (Table[currentPos] != null && Table[currentPos].equals(Elem)){
    				found = true;
    			} 
   				else {
   				//continue quadratic probe to find it
   					currentPos = (homeSlot + (k^2 + k)/2) % Size;
   					k++;
    			}
    		}
    		
    		if (found) {
    			//resets the slot to null and to be a tombstone
    			Table[currentPos] = null;
    			Status[currentPos] = slotState.TOMBSTONE;
    			//decreases the usage
    			this.Usage--;
    			return Elem;
    		}
    		else {
    			//it wasn't found, so it can't be deleted
    			return null;
    		}
   
    	}
    	
    	catch (IOException e) {
    		Log.write("Something went wrong with Delete()");
    		Log.flush();
    		return null;
    	}
    	
    }
    
    public T deleteLog(T Elem) throws IOException {
    	try {
    		boolean found = false;
    		int k = 1;
    		//this hashes the function (mod table size) and returns an integer
    		//to be used as the home slot for probing
    		int homeSlot = Math.abs(Elem.Hash()) % Size;
    		Log.write("\t" + homeSlot + "" + "\t");
    		int currentPos = homeSlot;
    		while (Status[currentPos] != slotState.EMPTY && !found) {
    			//a null, nonempty slot could be a tombstone
    			if (Table[currentPos] != null && Table[currentPos].equals(Elem)) {
    				found = true;
    			} 
    			else {
    				//continue quadratic probe to find it
    				currentPos = (homeSlot + (k^2 + k)/2) % Size;
    				Log.write(currentPos + "\t" + "");
    				k++;
    			}
    		}
    		if (found) {
    			//resets the slot to null and to be a tombstone
    			Table[currentPos] = null;
    			Status[currentPos] = slotState.TOMBSTONE;
    			//decreases the usage
    			this.Usage--;
    			Log.write("deleted");
    			Log.flush();
    			return Elem;
    		}
    		else {
    			//it wasn't found, so it can't be deleted
    			Log.write("not found");
    			Log.flush();
    			return null;
    		}
    	}
    	catch (IOException e) {
    		Log.write("Something went wrong with the DeleteLog() method");
    		Log.flush();
    		return null;
    	}
    	
    }
    
    // Reset hash table to (almost) same state as when first constructed.
    // Post:
    //      this.Table is an array of dimension Sz; all entries are null
    //      this.Status is an array of dimension Sz; all entries are
    //           EMPTY
    //      this.Opt is unchanged
    //      this.Usage == 0
    //      this.Log  is unchanged
    //      this.loggingOn is unchanged
    // 
    public void Clear() { 
    	for (int i = 0; i < Size; i++) {
    		Table[i] = null;
    	}
    	for (int i = 0; i < Size; i++) {
    		Status[i] = slotState.EMPTY;
    	}
    	this.Usage = 0;
    }
    

    // Reset hash table's log output target.
    // Pre:
    //      Log is an open on a file, or is null
    // Post:
    //      If Log is null, no changes are made.
    //      Otherwise: this.Log == Log
    // Return true iff this.Log has been changed and is not null.
    //
    public boolean setLog(FileWriter Log) { 
    	if (Log == null) {
    		return false;
    	}
    	this.Log = Log;
   		return true;
    }
    
    // Turn internal logging on.
    // Post:
    //      If this.Log is not null, loggingOn == true
    //      Otherwise, loggingOn == false
    // Returns final value of loggingOn.
    //
    public boolean logOn() { 
    	loggingOn = false;
    	if (this.Log != null) {
    		loggingOn = true;
    	}
    	return loggingOn;
    }
    
    // Turn internal logging off.
    // Post:
    //      loggingOn == false
    // Returns final value of loggingOn.
    //
    public boolean logOff() { 
    	loggingOn = false;
    	return loggingOn;
    }

    
 // Provides a useful display of the buffer pool's contents.
 	// Pre:
 	//       Log is open on an output file
 	// Post:
 	//       HashTable object is unchanged
 	public void Display(FileWriter Log) throws IOException {
 		
 		if ( Usage == 0 ) {
 			Log.write("Hash table is empty.\n");
 			return;
 		}
 		
 		for (int pos = 0; pos < Size; pos++) {
 			if ( Status[pos] == slotState.FULL ) {
 		        Log.write(pos + ":  " + Table[pos] + "\n");
 			}
// 			else if ( Status[pos] == slotState.TOMBSTONE ) {
// 				Log.write(pos + ":  tombstone" + "\n");
// 			}
// 			else {
// 				Log.write(pos + ":  empty" + "\n");
// 			}
 		}
 		Log.flush();
 	}
 	
 	// this rehashing comes from the code in the textbook
 	// for rehashing for quadratic probing
 	private void rehash() throws IOException {
 		try {
 			int oldSize = Size;
 			int newSize = (2*Size);
 			@SuppressWarnings("unchecked")
			T[] newTable = (T[]) new Hashable[newSize];
 			slotState[] newStatus = new slotState[newSize];
 			for (int i = 0; i < newSize; i++) {
 	    		newTable[i] = null;
 	    		newStatus[i] = slotState.EMPTY;
 	    	}
 			T[] oldTable = Table;
 			slotState[] oldStatus = Status;
 			Table = newTable;
 			Status = newStatus;
 			Usage = 0; 
 			Size = newSize;
 			for (int i = 0; i < oldSize; i++) {
 				if (oldStatus[i] == slotState.FULL) {
 					this.Insert(oldTable[i]);
 				}
 			}
 		}
 		catch (IOException e) {
 			Log.write("There was exception " + e + " with rehashing" );
 		}
 	}
}
