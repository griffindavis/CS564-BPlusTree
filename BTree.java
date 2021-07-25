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
        /**
         * TODO:
         * Implement this function to search in the B+Tree.
         * Return recordID for the given StudentID.
         * Otherwise, print out a message that the given studentId has not been found in the table and return -1.
         */
        return -1;
    }

    BTree insert(Student student) {
        /**
         * TODO:
         * Implement this function to insert in the B+Tree.
         * Also, insert in student.csv after inserting in B+Tree.
         */

         // find the correct leaf node
        BTreeNode node = root;

        if (node == null) {
            root = insertRecursive(node, student);
            return this;
        }

        BTreeNode newNode = insertRecursive(node, student);
        if (newNode == null) {
            return this;
        }
        this.root = new BTreeNode(this.t, false);
        this.root.children[0] = node;
        this.root.keys[0] = newNode.keys[0];
        this.root.children[1] = newNode;
        this.root.n++;
        
        return this;
    }

    private BTreeNode insertRecursive(BTreeNode node, Student entry) {
        if (node == null) { // the leaf node has not been created yet
            node = new BTreeNode(this.t, true);
            node.keys[0] = entry.studentId;
            node.values[0] = entry.recordId;
            node.n++;
            return node;
        }
        if (node.leaf) { // we found an appropriate leaf node
            if (node.hasSpace()) { // we can just put the key here
                for (int i = node.n; i >= 0; i--) {
                    if (entry.studentId < node.keys[i]) {
                        node.keys[i+1] = node.keys[i];
                        node.values[i+1] = node.values[i];
                    }
                    else {
                        node.keys[i] = entry.studentId;
                        node.values[i] = entry.recordId;
                        node.n++;
                        break;
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
                        // handle new node addition
                        newNode = handleAddNewNode(node, newNode);
                        return newNode;
                    }
                }
                else if (i == node.n - 1) {
                    BTreeNode newNode = insertRecursive(node.children[i+1], entry);
                    if (newNode == null) {
                        return null;
                    }
                    else {
                        // handle new node addition
                        newNode = handleAddNewNode(node, newNode);
                        return newNode;
                    }
                }
            }
        }

        return null;
    }

    private BTreeNode handleAddNewNode(BTreeNode currNode, BTreeNode newNode) {
        long newKey = newNode.keys[0];
        if (currNode.n < currNode.maxKeys()) {
            for (int i = currNode.n; i >= 0; i--) {
                if (newKey < currNode.keys[0]) {
                    currNode.keys[i+1] = currNode.keys[i];
                    currNode.children[i+1] = currNode.children[i];
                }
                else { // new key is greater than
                    currNode.keys[i] = newKey;
                    currNode.children[i + 1] = newNode;
                    currNode.n++;
                    newNode.next = currNode.children[i+1];
                    if (i > 0) { currNode.children[i - 1].next = newNode; }
                    break;
                }
            }
        }
        else {
            newNode = splitInternalNode(currNode);
            if (currNode == root) {
                this.root = new BTreeNode(this.t, false);
                this.root.keys[0] = newNode.keys[0];
                this.root.children[0] = currNode;
                this.root.children[1] = newNode;
                this.root.n++;
            }
            else {
                return handleAddNewNode(currNode, newNode);
            }
        }
        
        return null;
    }

    private BTreeNode splitInternalNode(BTreeNode currNode) {
        BTreeNode newNode = new BTreeNode(this.t, false);
        int newIndex = 0;
        for (int i = currNode.getMidpointIndex(); i < currNode.maxKeys(); i++) {
            newIndex = i - currNode.getMidpointIndex();
            newNode.keys[newIndex] = currNode.keys[i];
            newNode.children[newIndex] = currNode.children[i];
            
            newNode.n++; // increment new node
            
            // remove values from current node
            currNode.keys[i] = 0;
            currNode.children[i] = null;
            currNode.n--; // decrement current node
        }
        return newNode;
    }
    
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
        newNode.next = node.next == null ? null : node.next.next;
        node.next = newNode;

        if (entry != null) {
            newNode.keys[newNode.n] = entry.studentId;
            newNode.values[newNode.n] = entry.recordId;
            newNode.n++;
        }
        
        return newNode;
    }

    boolean delete(long studentId) {
        /**
         * TODO:
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
        return true;
    }

    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        BTreeNode node = root;
        while (!node.leaf) {
            node = node.children[0];
        }

        while (node != null) {
            for (int i = 0; i < node.n; i++) {
                listOfRecordID.add(node.values[i]);
            }
            node = node.next;
        };
        return listOfRecordID;
    }

    /**
     * For testing purposes
     * 

     * TODO: Delete Me
     */
    void printTree() {
        printInternal(root);
        System.out.println();
    }
    void printInternal(BTreeNode node) {
        if (node == null) return;
        if (node.leaf){ 
            printLeaf(node);
            return;
        }
        String out = "[";
        for (int i = 0; i < node.maxKeys(); i++) {
            out += node.keys[i] + ", ";
        }
        System.out.println(out + "]");
        for (int i = 0; i <= node.maxKeys(); i++) {
            printInternal(node.children[i]);
        }
        System.out.println();
    }
    void printLeaf(BTreeNode node) {
        String out = "[";
        for (int i = 0; i < node.maxKeys(); i++) {
            out += "(" + node.keys[i] + ")";
        }
        System.out.print(out + "]");
    }

    void printBetter() {
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
    String printNode(BTreeNode node) {
        if (node == null) { return " - "; }
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
