package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    private static final int DEFAULT_CAPACITY = 16;

    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private int n; // Numbers of nodes.

    private int m; // Size of hash table.

    private double loadFactor;

    private HashSet<K> keys;

    /** Constructors */
    public MyHashMap() {
        this.m = DEFAULT_CAPACITY;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.buckets = createTable(this.m);
        this.keys = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        this.m = initialSize;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.buckets = createTable(this.m);
        this.keys = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.m = initialSize;
        this.loadFactor = maxLoad;
        this.buckets = createTable(this.m);
        this.keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] buckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    private int hash(K key) {
        int hashCode = key.hashCode();
        return ((hashCode % m) + m) % m;
    }

    private void resize(int newCapacity) {
        MyHashMap<K, V> temp = new MyHashMap<>(newCapacity);
        for (int i = 0; i < buckets.length; i++) {
            for (Node node : buckets[i]) {
                K key = node.key;
                V value = node.value;
                temp.put(key, value);
            }
        }
        this.n = temp.n;
        this.m = temp.m;
        this.buckets = temp.buckets;
    }
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        this.buckets = createTable(this.m);
        this.n = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        int index = hash(key);
        Collection<Node> bucket = buckets[index];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new NullPointerException();
        }

        if (n > m * loadFactor) {
            resize(m * 2);
        }

        int index = hash(key);
        if (this.containsKey(key)) {
            Collection<Node> bucket = buckets[index];
            for (Node node : bucket) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }
        } else {
            Node newNode = createNode(key, value);
            Collection<Node> bucket = buckets[index];
            bucket.add(newNode);
            n++;
        }
        this.keys.add(key);
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (!containsKey(key)) {
            return null;
        }
        V res = get(key);
        int index = hash(key);
        Collection<Node> bucket = buckets[index];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                bucket.remove(node);
            }
        }
        this.n--;
        keys.remove(key);
        return res;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }

}
