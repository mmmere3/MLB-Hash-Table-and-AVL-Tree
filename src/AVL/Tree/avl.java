package AVL.Tree;


public class avl<T extends Comparable<? super T>> {
	//This constructs the AVL tree
	public class AVLNode {
	       // Initialize a childless AVL node.
	       // Pre:   elem is not null
	       // Post:  (in the new node)
	       //        element == elem
	       //        left == right == null 
	       public AVLNode( T elem ) { 
	    	   element = elem;
	    	   left = right = null;
	    	   
	       }
	       // Initialize a AVL node with children.
	       // Pre:   elem is not null
	       // Post:  (in the new node)
	       //        element == elem
	       //        left == lt, right == rt 
	       public AVLNode(T elem, AVLNode lt, AVLNode rt ) { 
	    	   element = elem;
	    	   left = lt;
	    	   right = rt;
	    	   height = 2;
	       }

	       public T       element;  // the data in the node
	       public AVLNode left;     // pointer to the left child
	       public AVLNode right;    // pointer to the right child
	       public int 	   height; 	 // the height of the AVL
	    }
	    public AVLNode left;     // pointer to the left child
        public AVLNode right;    // pointer to the right child
	    public AVLNode root;        // pointer to root node, if present
	    boolean duplicate;
	    boolean exist;
	    
	    // modified from code on page 114 of textbook
	    // Return true iff AVL tree contains no nodes.
	    // Pre:   none
	    // Post:  the AVL tree is unchanged
	    public boolean isEmpty( ) { 
	    	if (root == null) {
	    		return true;
	    	}
	    	else
	    		return false;
	    }
	    
		public T find( T x ) { 
	    	if (x == null) {
	    		return null;
	    	}
	    	return findHelper(x, root);
	    }
	    
	    private T findHelper(T x, AVLNode sroot) {
	    	if (sroot == null) {
	    		return null;
	    	}
	    	int compare = x.compareTo(sroot.element);
	    	if (compare < 0 ) {
	    		return findHelper(x, sroot.left);
	    	}
	    	else if (compare > 0) {
	    		return findHelper(x, sroot.right);
	    	}
	    	else
	    		return sroot.element;
	    }
	    // modified from page 133 in the textbook, this returns
	    // the height of the node t, or, if t is null,
	    // it returns 0.
	    public int height(AVLNode t) {
	    	if (t == null) {
	    		return 0;
	    	}
	    	else {
	    		return t.height;
	    	}
	    }
	    
	    public boolean insert(T x) {
	    	duplicate = false;
	    	if (x == null) {
	    		return false;
	    	}
	    	root = insertHelper(x, root);
	    	return !duplicate;
	    }
	    // this inserts a new node into the AVL tree, modified
	    // from my BST method as well as the code on page 134 
	    // of the textbook
	    private AVLNode insertHelper(T x, AVLNode sroot) {
	    	AVLNode newNode = new AVLNode(x, null, null);
	    	if (sroot == null) {
	    		sroot = newNode;
	    	}
	    	int compare = x.compareTo(sroot.element);
	    	if (compare < 0 ) {
	    		sroot.left = insertHelper(x, sroot.left);
	        }
	    	else if (compare > 0) {
	    		sroot.right = insertHelper(x, sroot.right);
	    	}
	    	else {
				duplicate = true;
			}
	    	return balance(sroot);
	    }
	    
	    // this checks the balance of the AVL so it can determine where
	    // the new node should be inserted. This assumes that sroot is 
	    // balanced or within one of being balanced
	    private AVLNode balance(AVLNode sroot) {
	    	if (sroot == null) {
	    		return sroot;
	    	}
	    	if (height(sroot.left) - height(sroot.right) > 1) {
	    		if (height(sroot.left.left) >= height(sroot.left.right)) {
	    			sroot = rotateWithLeftChild(sroot);
	    		}
	    		else {
	    			sroot = doubleWithLeftChild(sroot);
	    		}
	    	}
	    	else {
	    		if (height(sroot.right) - height(sroot.left) > 1) {
	    			if (height(sroot.right.right) >= height(sroot.right.left)) {
	    				sroot = rotateWithRightChild(sroot);
	    			}
	    			else {
	    				sroot = doubleWithRightChild(sroot);
	    			}
	    		}
	    	}
	    	sroot.height = Math.max(height(sroot.left), height(sroot.right)) + 1;
	    	return sroot;
	    }
	    
	    // modified from the code on page 135, this is a single
	    // rotation on the AVL tree, with the left child. Then 
	    // this updates the heights and returns a new root.
	    private AVLNode rotateWithLeftChild(AVLNode swap) {
	    	AVLNode lefty = swap.left;
	    	swap.left = lefty.right;
	    	lefty.right = swap;
	    	swap.height = Math.max(height(swap.left), height(swap.right)) + 1;
	    	lefty.height = Math.max(height(lefty.left), height(lefty.right)) + 1;
	    	return lefty;
	    }
	    
	    // this is the opposite of rotateWithLeftChild to 
	    // perform a single rotation of a right child
	    private AVLNode rotateWithRightChild(AVLNode swap) {
	    	AVLNode righty = swap.right;
	    	swap.right = righty.left;
	    	righty.left = swap;
	    	swap.height = Math.max(height(swap.left), height(swap.right)) + 1;
	    	righty.height = Math.max(height(righty.left), height(righty.right)) + 1;
	    	return righty;
	    }
	    
	    // modified from code on page 136, this is a double rotation
	    // that rotates first left child with its right child, then sroot
	    // with its new left child. It updates the heights and returns
	    // the new root.
	    private AVLNode doubleWithLeftChild(AVLNode toDouble) {
	    	toDouble.left = rotateWithRightChild(toDouble.left);
	    	return rotateWithLeftChild(toDouble);
	    }
	    
	    // the opposite of doubleWithLeftChild, does the same thing
	    // with first right child with its left child, then sroot
	    // with its new right child.
	    private AVLNode doubleWithRightChild(AVLNode toDouble) {
	    	toDouble.right = rotateWithLeftChild(toDouble.right);
	    	return rotateWithRightChild(toDouble);
	    }
	    
	    // Return true iff other is a AVL tree that has the same physical structure
	    // and stores equal data values in corresponding nodes.  "Equal" should
	    // be tested using the data object's equals() method.
	    // Pre:   other is null or points to a valid avl<> object, instantiated
	    //           on the same data type as the tree on which equals() is invoked
	    // Post:  both AVL trees are unchanged
	    public boolean equals(Object other) { 
	    	if (other == null) {
	    		return false;
	    	}
	    	if (!this.getClass().equals(other.getClass())) {
	    		return false;
	    	}
	    	@SuppressWarnings("unchecked")
			avl<T> otherNode = (avl<T>) other;
	    	return equalsHelper(otherNode.root, root);
	    }
	    // also modified from the BST project
	    private boolean equalsHelper(AVLNode x, AVLNode sroot) {

	    	if (x == null && sroot == null) {
	    		return true;
	    	}
	    	if (x == null ^ sroot == null)  {
	    		return false;
	    	}
	    	if (!sroot.element.equals(x.element)) {
	    		return false;
	    	}

	    	return equalsHelper(x.left, sroot.left) &&
	    			equalsHelper(x.right, sroot.right);
	    	
	    }
	    
	    //modified from the remove method for my BST project
	    public boolean remove(T x) { 
	    	if ( x == null ) {
	    		return false;
	    	}
	    	exist = true;
	    	root = removeHelper(x, root);
	    	return exist;
	    }
	    
	    // modified from BST code and from code on page 136.
	    private AVLNode removeHelper(T x, AVLNode sroot) {
	    	if (sroot == null) {
	    		exist = false;
	    		return sroot;
	    	}
	    	int compare = x.compareTo(sroot.element);
	    	if (compare < 0) {
	    		sroot.left = removeHelper(x, sroot.left);
	    	}
	    	else if (compare > 0) {
	    		sroot.right = removeHelper(x, sroot.right);
	    	}
	    	// if sroot has two non-null children
	    	else if (sroot.left != null && sroot.right != null) {
	    		sroot.element = findMin(sroot.right).element;
	    		sroot.right = removeHelper(sroot.element, sroot.right);
	    	}
	    	else {
	    		if (sroot.left != null) {
	    			sroot = sroot.left; 
	    		}
	    		else {
	    			sroot = sroot.right;
	    		}
	    	}
	    	return balance(sroot);
	    }
	    
	    // modified from code on page 117 for finding
	    // the smallest item in a subtree in a BST
	    private AVLNode findMin(AVLNode sroot) {
	    	if (sroot == null) {
	    		return null;
	    	}
	    	else if (sroot.left == null) {
	    		return sroot;
	    	}
	    	return findMin(sroot.left);
	    }
	    
	    // Return the tree to an empty state.
	    // Pre:   none
	    // Post:  the AVL tree contains no elements
	    public void clear( ) { 
	    	root = null;
	    }
}

