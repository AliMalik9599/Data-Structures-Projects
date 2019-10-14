

import DS.Map;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


/**
 * Class to implement an AVL Tree Map.
 * @param <K> Key values
 * @param <V> Values held.
 */
public class AvlTreeMap<K extends Comparable<? super K>, V>
        implements OrderedMap<K, V> {

    /**
     * Node class to create node.
     */
    private class Node {

        Node right;
        Node left;
        K key;
        V value;
        int height;

        /**
         * Construct new node.
         * @param k key to enter.
         * @param v value to enter.
         */
        Node(K k, V v) {
            this.height = 0;
            this.key = k;
            this.value = v;
            this.left = null;
            this.right = null;
        }

        //For debugging purposes.
        public String toString() {
            return "Node<key" + this.key + "; value:" + this.value + ">";
        }

    }

    private Node root;
    private int size;
    private StringBuilder stringBuilder;

    /**
     * Default constructor.
     */
    public AvlTreeMap() {
        this.root = null;
        this.size = 0;
    }


    //@Override
    public void insert(String s, ArrayList<String> strings) throws IllegalArgumentException {

    }

    //@Override
    public ArrayList<String> remove(String s) throws IllegalArgumentException {
        return null;
    }

    //@Override
    public void put(String s, ArrayList<String> strings) throws IllegalArgumentException {

    }

    //@Override
    public ArrayList<String> get(String s) throws IllegalArgumentException {
        return null;
    }

    //@Override
    public boolean has(String s) {
        return false;
    }

    /**
     * Size of map.
     * @return
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Balance factor of node.
     * @param n node to find balance of.
     * @return balance.
     */
    private int balance(Node n) {
        if (n == null) {
            throw new IllegalArgumentException("Cannot find key");
        }

        return height(n.left) - height(n.right);

    }

    /**
     * Find the node with that key.
     * @param k key to look for.
     * @return Node with that key.
     */
    private Node find(K k) {
        if (k == null) {
            throw new IllegalArgumentException("cannot handle null key.");
        }

        Node n = this.root;

        while (n != null) {
            int cmp = k.compareTo(n.key);
            if (cmp < 0) {
                n = n.left;
            } else if (cmp > 0) {
                n = n.right;
            } else {
                return n;
            }
        }
        return null;
    }

    /**
     * Check if map has node with key.
     * @param k The key.
     * @return true if found, false otherwise.
     */
    @Override
    public boolean has(K k) {
        if (k == null) {
            return false;
        }
        return this.find(k) != null;
    }

    /**
     * Find the node with this key.
     * @param k key to search for.
     * @return Node with that key.
     */
    private Node findForSure(K k) {
        Node n = this.find(k);
        if (n == null) {
            throw new IllegalArgumentException("cannot find key " + k);
        }
        return n;
    }

    /**
     * Find largest node in subtree
     * @param n head of subtree.
     * @return largest node.
     */
    private Node maxTree(Node n) {
        while (n.right != null) {
            n = n.right;
        }
        return n;
    }

    /**
     * Replace value of existing node.
     * @param k The key.
     * @param v The value to be associated with k.
     * @throws IllegalArgumentException
     */
    @Override
    public void put(K k, V v) throws IllegalArgumentException {
        if (k == null) {
            throw new IllegalArgumentException("Cannot handle null key");
        }
        Node n = this.findForSure(k);
        n.value = v;
    }

    /**
     * Get Value of node.
     * @param k The key.
     * @return Value of node.
     * @throws IllegalArgumentException
     */
    @Override
    public V get(K k) throws IllegalArgumentException {
        if (k == null) {
            throw new IllegalArgumentException("Cannot handle null key");
        }
        Node n = this.findForSure(k);
        return n.value;
    }

    /**
     * Method to add node.
     * @param k The key.
     * @param v The value to be associated with k.
     * @throws IllegalArgumentException
     */
    @Override
    public void insert(K k, V v) throws IllegalArgumentException {

        if (k == null) {

            throw new IllegalArgumentException("cannot handle null key");
        }
        if (this.has(k)) {
            throw new IllegalArgumentException("Key already in map");
        }
        this.root = this.insert(this.root, k, v);
        this.size += 1;
    }

    /**
     * Find where to insert node.
     * @param n node at start (root).
     * @param k key to enter.
     * @param v value to enter.
     * @return new node and nodes above.
     */
    private Node insert(Node n, K k, V v) {

        if (n == null) {
            return new Node(k, v);
        }

        int cmp = k.compareTo(n.key);
        if (cmp < 0) {
            n.left = this.insert(n.left, k, v);
        } else if (cmp > 0) {
            n.right = this.insert(n.right, k, v);
        } else {
            throw new IllegalArgumentException("duplicate key " + k);
        }

        n.height = 1 + max(n);

        return balanceFix(n);
    }

    /**
     * Fix balance of tree.
     * @param n node to check balance of.
     * @return new node position.
     */
    private Node balanceFix(Node n) {

        if (this.balance(n) > 1) {
            if (this.balance(n.left) < 0) {
                n.left = rotateLeft(n.left);
            }
            n = this.rotateRight(n);

        } else if (this.balance(n) < -1) {
            if (this.balance(n.right) > 0) {
                n.right = rotateRight(n.right);
            }

            n = this.rotateLeft(n);

        }
        n.height = 1 + max(n);
        return n;
    }

    /**
     * Find the max height of node
     * @param n node to find height of.
     * @return height.
     */
    private int max(Node n) {
        if (n.left == null && n.right == null) {
            return -1;
        }

        if (height(n.left) > height(n.right)) {
            return height(n.left);
        }
        else {
            return height(n.right);
        }

    }

    /**
     * Find height of node.
     * @param n node to find height of.
     * @return height.
     */
    private int height(Node n) {
        if (n == null) {
            return -1;
        }
        return n.height;
    }

    /**
     * Rotate tree right.
     * @param n node to rotate from.
     * @return new node in that position.
     */
    private Node rotateRight(Node n) {
        Node temp = n.left;
        n.left = temp.right;
        temp.right = n;
        n.height = 1 + max(n); //1 + max(n.left, n.right);
        temp.height = 1 + max(temp);
        return temp;
    }

    /**
     * Rotate tree left
     * @param n node to rotate from.
     * @return the new node in that position.
     */
    private Node rotateLeft(Node n) {
        Node temp = n.right;
        n.right = temp.left;
        temp.left = n;
        n.height = 1 + max(n);
        temp.height = 1 + max(temp);
        return temp;
    }

    /**
     * Find the node to be removed.
     * @param n Node to start from (root)
     * @param k key to find.
     * @return Nodes above.
     * @throws IllegalArgumentException
     */
    private Node removeNow(Node n, K k) throws IllegalArgumentException {
        if (n == null) {
            throw new IllegalArgumentException("cannot find key");
        }

        int cmp = k.compareTo(n.key);
        if (cmp < 0) {
            n.left = this.removeNow(n.left, k);
        } else if (cmp > 0) {
            n.right = this.removeNow(n.right, k);
        } else {
            n = this.removeNode(n);
            return n;
        }

        n.height = 1 + max(n);

        n = this.balanceFix(n);
        return n;
    }

    /**
     * Remove the current node.
     * @param n node to be removed.
     * @return new node in that position.
     */
    private Node removeNode(Node n) {

        if (n.left == null) {
            return n.right;

        }
        if (n.right == null) {
            return n.left;

        }

        Node temp = n;
        n = this.maxTree(n.left);
        n.left = removeMax(temp.left);
        n.right = temp.right;
        n.height = 1 + max(n);
        return balanceFix(n);
    }

    /**
     * Shift the maximum tree in the subtree.
     * @param n Node head of subtree.
     * @return balanced nodes in subtree.
     */
    private Node removeMax(Node n) {
        if (n.right == null) {
            return n.left;
        }
        n.right = removeMax(n.right);
        n.height = 1 + max(n);
        return balanceFix(n);

    }


    @Override
    public V remove(K k) {

        V v = this.findForSure(k).value;
        this.root = this.removeNow(this.root, k);
        this.size -= 1;
        return v;
    }

    // Recursively add keys from subtree rooted at given node
    // into the given list.
    private void iteratorHelper(Node n, List<K> keys) {
        if (n == null) {
            return;
        }
        this.iteratorHelper(n.left, keys);
        keys.add(n.key);
        this.iteratorHelper(n.right, keys);
    }

    //Create iterator for arraylist
    @Override
    public Iterator<K> iterator() {
        List<K> keys = new ArrayList<K>();
        this.iteratorHelper(this.root, keys);
        return keys.iterator();
    }

    //If we don't have a StringBuilder yet, make one;
    // otherwise just reset it back to a clean slate.
    private void setupStringBuilder() {
        if (this.stringBuilder == null) {
            this.stringBuilder = new StringBuilder();
        } else {
            this.stringBuilder.setLength(0);
        }
    }

    // Recursively append string representations of keys and
    // values from subtree rooted at given node.
    private void toStringHelper(Node n, StringBuilder s) {
        if (n == null) {
            return;
        }
        this.toStringHelper(n.left, s);
        s.append(n.key);
        s.append(": ");
        s.append(n.value);
        s.append(", ");
        this.toStringHelper(n.right, s);
    }

    /**
     * Convert Tree to string.
     * @return string version of tree.
     */
    @Override
    public String toString() {
        this.setupStringBuilder();
        this.stringBuilder.append("{");

        this.toStringHelper(this.root, this.stringBuilder);

        int length = this.stringBuilder.length();
        if (length > 1) {

            this.stringBuilder.setLength(length - 2);
        }
        this.stringBuilder.append("}");

        return this.stringBuilder.toString();
    }


    /**
     * Find balance of node (testing).
     * @param k key of node.
     * @return balance factor.
     */
    public int getBalance(K k) {
        Node n = this.findForSure(k);
        return balance(n);
    }

    /**
     * Get the height of the node (testing).
     * @param k key of node.
     * @return value of height.
     */
    public int getHeight(K k) {
        Node n = this.findForSure(k);
        return 1 + max(n);
    }


}






