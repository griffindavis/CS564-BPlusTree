import java.util.ArrayList;
import java.util.List;

/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
class BTree {

    /**
     * Pointer to the root node.
     */
    private BTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    private int t;

    BTree(int t) {
        this.root = null;
        this.t = t;
    }

    long search(long studentId) {
    	//start at root
    	BTreeNode curNode = root;
    	//init check that root is not null
    	if (curNode == null) {
    		return -1;
    	}
    	int i = 0;
    	//traverse all the non-leaves until studentID is less than key
    	while ((!(curNode.leaf))) { //&& curNode.next != null) {
    		for (i = 0; i < curNode.n; i++) {
    			if (studentId < curNode.keys[i]) {
    				break;
    			}
    		}
    		//grab children value
    		curNode = curNode.children[i];
    	}
    	return curNode.getValue(studentId);
        //return -1;
    }

    BTree insert(Student student) {
        /**
         * TODO: update student.csv
         */

         // find the correct leaf node
        BTreeNode node = root;

        if (node == null) { // this is if this is the firts entry
            root = insertRecursive(node, student);
            return this;
        }

        BTreeNode newNode = insertRecursive(node, student);
        if (newNode == null) { // if we inserted without a split
            return this;
        }
        // this will be the case if we have split the root and need to add a level to the tree
        this.root = new BTreeNode(this.t, false);
        this.root.children[0] = node;
        this.root.keys[0] = newNode.keys[0];
        this.root.children[1] = newNode;
        this.root.n++;
        
        return this;
    }

    /**
     * Recursive insert function to traverse the tree and add the new value where appropriate
     * @param node - the node to try the insert on
     * @param entry - the new entry
     * @return null if there is no split, the new node if we have created a new node that needs to be added to the parent.
     */
    private BTreeNode insertRecursive(BTreeNode node, Student entry) {
        if (node == null) { // the leaf node has not been created yet
            node = new BTreeNode(this.t, true);
            node.keys[0] = entry.studentId;
            node.values[0] = entry.recordId;
            node.n++;
            return node;
        }
        if (node.leaf) { // we found an appropriate leaf node
            if (node.hasKey(entry.studentId)) { // prevent the addition of duplicate keys
                return null;
            }
            if (node.hasSpace()) { // we can just put the key here
                for (int i = node.n; i >= 0; i--) { // loop backwards to shift all values right assuming that the entry will be stored somewhere to the left
                    if (entry.studentId < node.keys[i]) { // entry must be stored to the right of this key
                        node.keys[i+1] = node.keys[i];
                        node.values[i+1] = node.values[i];
                    }
                    else { // only hit this when the inserted value is the lowest in the node
                        node.keys[i] = entry.studentId;
                        node.values[i] = entry.recordId;
                        node.n++;
                        break; // TODO: make sure this is safe and we don't need to update the parent key in any circumstances
                    }
                }
            }
            else { // split
                return splitLeafNode(node, entry);
            }
        }
        else { // this is an internal node
            for (int i = 0; i < node.n; i++) {
                if (entry.studentId < node.keys[i]) {
                    BTreeNode newNode = insertRecursive(node.children[i], entry);
                    if (newNode == null) {
                        return null;
                    }
                    else {
                        // handle new internal node addition
                        newNode = handleAddInternalNode(node, newNode);
                        return newNode;
                    }
                }
                else if (i == node.n - 1) { // if we're here, we're looking at the outermost key in the node
                    BTreeNode newNode = insertRecursive(node.children[i+1], entry);
                    return (newNode == null) ? null : handleAddInternalNode(node, newNode);
                }
            }
        }
        return null;
    }

    /**
     * Takes the current internal node and adds a new internal node as a child
     * @param currNode - the node we are adding to
     * @param newNode - the new node that needs to be added
     * @return null if we don't need an additional split, otherwise a new node
     */
    private BTreeNode handleAddInternalNode(BTreeNode currNode, BTreeNode newNode) {
        long newKey = newNode.keys[0];
        if (currNode.n < currNode.maxKeys()) { // if there's space in the node
            for (int i = currNode.n; i >= 0; i--) { // shift keys
                if (newKey < currNode.keys[0]) {
                    currNode.keys[i+1] = currNode.keys[i];
                    currNode.children[i+1] = currNode.children[i];
                }
                else { // new key is greater than
                    currNode.keys[i] = newKey;
                    currNode.children[i + 1] = newNode;
                    currNode.n++;
                    newNode.next = currNode.children[i+1]; // update pointers
                    if (i > 0) { currNode.children[i - 1].next = newNode; }
                    break;
                }
            }
        }
        else { // no space, requires a split
            BTreeNode n2 = splitInternalNode(currNode);
            if (currNode == root) { // special handling for splitting the root
                this.root = new BTreeNode(this.t, false);
                this.root.keys[0] = n2.keys[0];
                this.root.children[0] = currNode;
                this.root.children[1] = n2;
                this.root.n++;
                return handleAddInternalNode(n2, newNode);
            }
            else { // if not the root, we can handle this normally with a recurrsive call
                handleAddInternalNode(n2, newNode);
                return handleAddInternalNode(currNode, n2);
            }
        } 
        return null;
    }

    /**
     * Handle splitting an internal node
     * @param currNode - the node that needs to be split
     * @return a reference to the new node that was created
     */
    private BTreeNode splitInternalNode(BTreeNode currNode) {
        BTreeNode newNode = new BTreeNode(this.t, false);
        int newIndex = 0; // just instantiating this for use later
        for (int i = currNode.getMidpointIndex(); i < currNode.maxKeys(); i++) {
            // new index should start at 0 so the difference between the max and  the current second half index
            newIndex = i - currNode.getMidpointIndex(); 
            newNode.keys[newIndex] = currNode.keys[i]; // move second half into first half of the new node
            newNode.children[newIndex] = currNode.children[i]; // move second half into first half of the new node
            if (i == currNode.maxKeys() - 1) {
                newNode.children[newIndex + 1] = currNode.children[i + 1];
            }
            
            newNode.n++; // increment new node
            
            // remove values from current node
            currNode.keys[i] = 0;
            currNode.children[i] = null;
            currNode.n--; // decrement current node
        }
        return newNode;
    }
  
    /**
     * Handle the splitting of a leaf node
     * @param node - the node to split
     * @param entry - the entry to add to the new split
     * @return a reference to the new node that is created
     */
    private BTreeNode splitLeafNode(BTreeNode node, Student entry) {
        BTreeNode newNode = new BTreeNode(this.t, true);
        int newIndex = 0;
        for (int i = node.getMidpointIndex(); i < node.maxKeys(); i++) {
            newIndex = i - node.getMidpointIndex();
            newNode.keys[newIndex] = node.keys[i];
            newNode.values[newIndex] = node.values[i];
            
            newNode.n++; // increment new node
            
            // remove values from current node
            node.keys[i] = 0;
            node.values[i] = 0;
            node.n--; // decrement current node
        }
        // move sibling pointers
        newNode.next = (node.next == null) ? null : node.next.next;
        node.next = newNode;

        // insert the new value
        newNode.keys[newNode.n] = entry.studentId;
        newNode.values[newNode.n] = entry.recordId;
        newNode.n++;
        
        return newNode;
    }

    boolean delete(long studentId) {
        /**
         * TODO: Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
        return true;
    }

    /**
     * Returns a list of the record IDs in the leaf nodes of the tree
     * @return - a list of the record IDs from left to right in the tree
     */
    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        BTreeNode node = root;
        while (!node.leaf) { // find the left most leaf node
            node = node.children[0];
        }
        
        while (node != null) { // travers the linked-list like structure of the leaf nodes
            for (int i = 0; i < node.n; i++) {
                listOfRecordID.add(node.values[i]);
            }
            node = node.next;
        };
        return listOfRecordID;
    }

    /**
     * For testing purposes
     * Prints the tree nicely for debugging
     *
     * TODO: Delete Me
     */
    void printTree() {
        ArrayList<BTreeNode> nodeList = new ArrayList<BTreeNode>();
        nodeList.add(this.root);
        while (!nodeList.isEmpty()) {
            // print keys of the whole list
            String out = "";
            for (int i = 0; i < nodeList.size(); i++) {
                out += printNode(nodeList.get(i));
            }
            // add all children of the keys
            int size = nodeList.size();
            for (int j = 0; j < size; j++) {
                BTreeNode node = nodeList.get(0);
                for (int k = 0; k <= node.n; k++) {
                    if (node.children[k] != null) {
                        nodeList.add(node.children[k]);
                    }
                }
                nodeList.remove(node);
            }
            if (nodeList.size() > 0) {
                for (int i = 0; i < 10 - nodeList.size(); i++) {
                    out = "\t" + out;
                }
            }
            System.out.println(out);
        }
    }

    /**
     * For testing purposes, returns a string of the node
     * @param node - node to print
     * @return String representing the node to print
     * 
     * TODO: Delete Me
     */
    String printNode(BTreeNode node) {
        String out = "";
        if (node.leaf) {
            for (int i = 0; i < node.maxKeys(); i++) {
                if (node.keys[i] == 0) { break; }
                out += "(" + node.keys[i] + ")";
            }
        }
        else {
            for (int i = 0; i < node.maxKeys(); i++) {
                if (node.keys[i] == 0) { break; }
                out += node.keys[i] + (i < node.n - 1 ? " " : "");
            }
        }
        return "[" + out + "] ";
    }
}
