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
    private Collection<Node>[] buckets;//链表的集合
    // You should probably define some more!
    private int size;
    private int initialSize;
    private double maxSize;
    private static final int capacity=16;
    private static final double loadFactor = 0.75;
    private Set<K> keySet;
    /** Constructors */
    public MyHashMap() {
        this(capacity,loadFactor);
    }

    public MyHashMap(int initialSize) {
        this(initialSize,loadFactor);
    }

    /**
     * MyHashMap constructor that creates a backing array initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.buckets=createTable(initialSize);
        this.size=0;
        this.maxSize=maxLoad;
        this.keySet=new HashSet<>();
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
    private LinkedList<Node>[] createTable(int tableSize) {
       LinkedList<Node>[] buckets = new LinkedList[tableSize];
       for (int i = 0; i < tableSize; i++) {
           buckets[i] =(LinkedList<Node>) createBucket();//创建多个链表
       }
       return buckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    public void clear(){
        buckets=createTable(capacity);
        size=0;
        keySet=new HashSet<>();

    };

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        if(key==null)return false;
        int index =getIndex(key);
        for(Node node : buckets[index]){
            if(node.key.equals(key)){
                return true;
            }
        }
        return false;
    };

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        if(key==null)return null;
        int index = getIndex(key);
        for(Node node : buckets[index]){
            if(node.key.equals(key)){
                return node.value;
            }
        }
        return null;
    };

    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    };

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        if(key==null) return;
        int index = getIndex(key);
        for(Node node : buckets[index]){
            if(node.key.equals(key)){
                node.value = value;
                return;
            }
        }
        buckets[index].add(createNode(key,value));
        size++;
        keySet.add(key);
        if(size/maxSize>maxSize){
            resize(buckets.length*2);
        }
    }
    private void resize(int i) {
        Collection<Node>[] oldBuckets = this.buckets;
        this.buckets = createTable(i);
        size=0;
        keySet =new HashSet<>();
        for (Collection<Node> bucket : oldBuckets) {
            for (Node node : bucket) {
                put(node.key, node.value);
            }
        }
    }
    ;

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet(){
        return keySet;
    };

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key){
        throw new UnsupportedOperationException("no such an operation!");
    };

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value){
        throw new UnsupportedOperationException("no such an operation!");
    };
    private int getIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }//固定用法
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
