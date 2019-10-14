

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class to implement a TreapMap.
 * @param <K> Key value.
 * @param <V> Value held.
 */
public class TreapMap<K extends Comparable<? super K>, V>
        implements OrderedMap<K, V> {

    /**
     * Class to construct the
     * new node and its fields.
     */
    private class Node {

        Node right;
        Node left;
        K key;
        V value;
        int priority;
        Random rd = new Random();

        /**
         * Constructor for node for testing
         * purposes, to insert our own
         * priority.
         * @param pri priority entered.
         * @param k Key entered.
         * @param v Value entered.
         */
        Node(int pri, K k, V v) {
            this.key = k;
            this.value = v;
            this.priority = pri;
            this.left = null;
            this.right = null;
        }

        /**
         * Regular constructor for new
         * node.
         * @param k Key value.
         * @param v Value held.
         */
        Node(K k, V v) {
            this.key = k;
            this.value = v;
            this.left = null;
            this.right = null;
            this.priority = rd.nextInt();
        }

        /**
         * For debugging purposes.
         * @return String of map.
         */
        public String toString() {
            return "Node<key" + this.key + "; value:" + this.value + ">";
        }

    }

    private Node root;
    private int size;
    private StringBuilder stringBuilder;

    /**
     * The size of the map.
     * @return int the size of map.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Find method to look for node with
     * specific key.
     * @param k key being searched for.
     * @return The node with that key.
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
     * Returns whether a node with the
     * given key exists.
     * @param k The key.
     * @return True if node exists, false otherwise.
     */
    @Override
    public boolean has(K k) {
        if (k == null) {
            return false;
        }
        return this.find(k) != null;
    }

    /**
     * Method to make sure that the key exists
     * in the map before other methods are executed.
     * @param k Key being searched for.
     * @return The node with that key if it exists.
     */
    private Node findForSure(K k) {
        Node n = this.find(k);
        if (n == null) {
            throw new IllegalArgumentException("cannot find key " + k);
        }
        return n;
    }

    /**
     * Method to put a new value in a existing node.
     * @param k The key.
     * @param v The value to be associated with k.
     * @throws IllegalArgumentException if key is null/doesn't exist.
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
     * Method to get a value associated with
     * a key.
     * @param k The key.
     * @return The value in the node found.
     * @throws IllegalArgumentException if key is null/doesn't exist.
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
     * Method to insert a new node.
     * @param k The key.
     * @param v The value to be associated with k.
     * @throws IllegalArgumentException If key is null
     * or already exists.
     */
    @Override
    public void insert(K k, V v) throws IllegalArgumentException {
        //System.out.println("Before if");
        if (k == null) {
            //System.out.println("Inside if");
            throw new IllegalArgumentException("cannot handle null key");
        }
        if (this.has(k)) {
            throw new IllegalArgumentException("Key already in map");
        }
        this.root = this.insertCheck(this.root, k, v);
        this.size += 1;
    }

    /**
     * Method to check where to insert node.
     * @param n The starting node (root).
     * @param k Key being entered.
     * @param v Value being entered.
     * @return the new node and nodes above.
     */
    private Node insertCheck(Node n, K k, V v) {
        //System.out.println("inside private insert");
        if (n == null) {
            return new Node(k, v);
        }

        int cmp = k.compareTo(n.key);
        if (cmp < 0) {
            n.left = this.insertCheck(n.left, k, v);
            n = balanceFix(n);
        } else if (cmp > 0) {
            n.right = this.insertCheck(n.right, k, v);
            n = balanceFix(n);
        } else {
            n.value = v;
            return n;
        }

        return balanceFix(n);
    }

    /**
     * Method to fix the balance of the map
     * based on priorities.
     * @param n Node being entered.
     * @return Node after being balanced.
     */
    private Node balanceFix(Node n) {
        if (n.left != null && n.left.priority > n.priority) {
            n = rightRotate(n);
        } else if (n.right != null && n.right.priority > n.priority) {
            n = leftRotate(n);
        }
        return n;
    }


    /**
     * Find the largest node in left
     * subtree of given node, for
     * removing a node.
     * @param n node to be removed.
     * @return largest node in left subtree.
     */
    private Node maxTree(Node n) {
        while (n.right != null) {
            n = n.right;
        }
        return n;
    }

    /**
     * Left rotation of node.
     * @param n node to be rotated.
     * @return the new node.
     */
    private Node leftRotate(Node n) {
        Node temp = n.right;
        n.right = temp.left;
        temp.left = n;
        return temp;
    }

    /**
     * Right rotation of node.
     * @param n node to be rotated.
     * @return the new node.
     */
    private Node rightRotate(Node n) {
        Node temp = n.left;
        n.left = temp.right;
        temp.right = n;
        return temp;
    }

    /**
     * Finding node to remove.
     * @param n node to start from (root)
     * @param k key of node being removed.
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
        }
        if (n != null) {
            n = this.balanceFix(n);
        }
        return n;
    }

    /**
     * Method to remove the node.
     * @param n Node to be removed.
     * @return New node in that position.
     */
    private Node removeNode(Node n) {

        // 0 and 1 child
        if (n.left == null) {
            return n.right;

        }
        if (n.right == null) {
            return n.left;

        }

        // 2 children
        Node temp = n;
        n = this.maxTree(n.left);
        n.left = removeMax(temp.left);
        n.right = temp.right;               //This part different
        return balanceFix(n);
    }

    /**
     * Swapping node with max node in subtree.
     * @param n node at head of subtree.
     * @return max node.
     */
    private Node removeMax(Node n) {
        if (n.right == null) {
            return n.left;
        }
        n.right = removeMax(n.right);       //Till here
        return balanceFix(n);

    }

    /**
     * Method to reomve node.
     * @param k The key.
     * @return Value of removed node.
     */
    @Override
    public V remove(K k) {
        // Need this additional find() for the V return value, because the
        // private remove() method cannot return that in addition to the new
        // root. If we had been smarter and used a void return type, we would
        // not need to do this extra work.
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

    //Return new iterator of arraylist.
    @Override
    public Iterator<K> iterator() {
        List<K> keys = new ArrayList<K>();
        this.iteratorHelper(this.root, keys);
        return keys.iterator();
    }

    // If we don't have a StringBuilder yet, make one;
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
     * Convert treap to string.
     * @return String representation of Treap.
     */
    @Override
    public String toString() {
        this.setupStringBuilder();
        this.stringBuilder.append("{");

        this.toStringHelper(this.root, this.stringBuilder);

        int length = this.stringBuilder.length();
        if (length > 1) {
            // If anything was appended at all, get rid of
            // the last ", " the toStringHelper put in.
            this.stringBuilder.setLength(length - 2);
        }
        this.stringBuilder.append("}");

        return this.stringBuilder.toString();
    }

    /**
     * Method to find correct place to insert node
     * based on the key. For testing purposes.
     * @param n The node to start comparing from (root).
     * @param k Key being inserted.
     * @param v Value being inserted.
     * @param pri Priority being inserted, for testing.
     * @return The new node and nodes above it.
     */
    private Node insertPriCheck(Node n, K k, V v, int pri) {
        if (n == null) {
            return new Node(pri, k, v);
        }

        int cmp = k.compareTo(n.key);
        if (cmp < 0) {
            n.left = this.insertPriCheck(n.left, k, v, pri);
            n = this.balanceFix(n);
        } else if (cmp > 0) {
            n.right = this.insertPriCheck(n.right, k, v, pri);
            n = this.balanceFix(n);
        } else {
            n.value = v;
            return n;
        }

        return balanceFix(n);
    }


    /**
     * Method to insert new node, for testing.
     * @param pri Priority being given, for testing.
     * @param k Key being inserted.
     * @param v Value being inserted.
     * @throws IllegalArgumentException If node with that key
     * exists or key is null.
     */
    public void insertPri(int pri, K k, V v) throws IllegalArgumentException {
        //System.out.println("Before if");
        if (k == null) {
            //System.out.println("Inside if");
            throw new IllegalArgumentException("cannot handle null key");
        }
        if (this.has(k)) {
            throw new IllegalArgumentException("Key already in map");
        }
        this.root = this.insertPriCheck(this.root, k, v, pri);
        this.size += 1;
    }

    /**
     * Gives priority of left child
     * of node (for testing).
     * @param k Key of parent.
     * @return priority of left child.
     */
    public int leftPriority(K k) {
        Node n = this.findForSure(k);
        if (n.left != null) {
            return n.left.priority;
        }
        else {
            throw new IllegalArgumentException("Left does not exist");
        }
    }

    /**
     * Gives priority of right child
     * of node (for testing).
     * @param k key of parent.
     * @return priority of right child.
     */
    public int rightPriority(K k) {
        Node n = this.findForSure(k);
        if (n.right != null) {
            return n.right.priority;
        }
        else {
            throw new IllegalArgumentException("Right does not exist");
        }
    }

}