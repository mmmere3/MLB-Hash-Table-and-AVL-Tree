package HashTable;

public enum slotState {
	//slots that have nothing in them
	EMPTY,
	//slots that have data in them currently
	FULL, 
	//slots that used to have data 
	//in them but are now empty
	TOMBSTONE,
	;

}
