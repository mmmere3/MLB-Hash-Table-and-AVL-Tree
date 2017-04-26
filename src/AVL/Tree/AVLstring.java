package AVL.Tree;

public class AVLstring implements Comparable<AVLstring> {
	String s;
	long l;
	
	public AVLstring(String s, long l){
		this.s = s;
		this.l = l;
	}
	
	public String toString() {
		return (s.toString() + ", " + l);
	}
	
	public String getS() {
		return s;
	}

	public long getL() {
		return l;
	}
	
	public int compareTo(AVLstring o) {
		return this.s.compareTo(((AVLstring) o).getS());
	}
}
