package HashTable;

import java.util.ArrayList;


public class HashPlayer implements Hashable{
	String s;
	ArrayList<Long> l;
	
	public HashPlayer(String s, ArrayList<Long> l) {
		this.s = s;
		this.l = l;
	}
	
	public int Hash() {
		return s.hashCode();
	}
	
	public String toString() {
		return (s.toString() + ", " + l);
	}
	
	public String getS() {
		return s;
	}
	
	public ArrayList<Long> getL() {
		return l;
	}
	
	public boolean equals(Object str) {
		if (str == null) {
			return false;
		}
		if (!this.getClass().equals(str.getClass())) {
			return false;
		}
		if (s.equals(((HashPlayer)str).getS())) {
			if (((HashPlayer)str).getL() == null) {
				return true;
			}
			l.addAll(((HashPlayer)str).getL());
			return true;
		}
		return false;
	}
}
