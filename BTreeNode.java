class BTreeNode {

    /**
     * Array of the keys stored in the node.
     */
    long[] keys;
    /**
     * Array of the values[recordID] stored in the node. This will only be filled when the node is a leaf node.
     */
    long[] values;
    /**
     * Minimum degree (defines the range for number of keys)
     **/
    int t;
    /**
     * Pointers to the children, if this node is not a leaf.  If
     * this node is a leaf, then null.
     */
    BTreeNode[] children;
    /**
     * number of key-value pairs in the B-tree
     */
    int n;
    /**
     * true when node is leaf. Otherwise false
     */
    boolean leaf;

    /**
     * point to other next node when it is a leaf node. Otherwise null
     */
    BTreeNode next;

    // Constructor
    BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.keys = new long[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.n = 0;
        this.next = null;
        this.values = new long[2 * t - 1];
    }

    public boolean hasSpace() {
        return this.n < this.maxKeys();
    }

    public int maxKeys() {
        return this.t * 2 - 1;
    }

    public int getMidpointIndex() {
        return this.t;
    }

    /**
     * Determines whether this node has the key in question
     * @param key - the key we're looking for
     * @return true if the key already exists in the node
     */
    public boolean hasKey(long key) {
        for (int i = 0; i < this.n; i++) {
            if (key == this.keys[i]) { // use less than or equal since the node will be sorted
                return true;
            }
        }
        return false;
    }
    
    public long getValue ( long id) {
    	int j=0;
    	for (j = 0; j < n; j++) {
            if (keys[j] == id) {
            	return values[j];
            }
        }
        return -1;
    }
    boolean removeValue(long k) {
        boolean flag=false;
        for (int i=0; i<n; i++) {
            if (keys[i]==k) {
                for (int j =i+1; j<n; j++) {
                    keys[j-1] = keys[j];
                    values[j-1] = values[j];
                }
                keys[n-1] = 0;
                values[n-1] = 0;
                this.n = n-1;
                flag = true;
                break;
            }
        }
        return flag;
    }
    boolean hasMinPopulation () {
    	if (this.n<= ((this.t)/2)) {
    		return false;
    	}
    	return true;
    }
    public int keyForValue (long value) {
    	
    	for (int i=0; i<n; i++) {
    		if (this.keys[i]==value) {
    			return i;
    		}
    	}
    	return -1;
    }
    public void clearNode () {
    	
    	for (int i=0; i<n; i++) {
    		this.keys[i]=0;
    		this.values[i]=0;
    	}
    	this.n=0;
    }
}
