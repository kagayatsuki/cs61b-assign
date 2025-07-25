package bstmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private class Node {
        K key;
        V value;
        Node left, right;
        int size;

        Node(K k, V v) {
            key = k;
            value = v;
            size = 1;
        }
    }//key代表BST节点在树中的位置，左子树键 < 当前节点键 < 右子树键
    private Node root;
    private int size;

    public BSTMap() {
    }

    /** Removes all mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if(key == null) throw new NullPointerException("key is null");
        return get(key) != null;
    }

    /** Returns the value to which the specified key is mapped, or null if no mapping exists. */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        return get(root, key);
    }
    private V get(Node node ,K key){
        if(node == null){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if(cmp <0) return get(node.left,key);
        else if(cmp > 0) return get(node.right,key);
        else return node.value;
    }
    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }
    private int size(Node node) {
        if (node == null) return 0;
        return node.size;
    }
    /** Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        root = put(root, key, value);
        size++;
    }
    private Node put(Node node,K key, V value){
        if(node == null){
            return new Node(key,value);
        }
        int cmp = key.compareTo(node.key);
        if(cmp <0) node.left = put(node.left,key,value);
        else if(cmp > 0) node.right = put(node.right,key,value);
        else node.value = value;
        node.size=1+size(node.left)+size(node.right);
        return node;
    }
    /** Returns a Set view of the keys in this map. Not required for Lab 7. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("keySet() not implemented for Lab 7");
    }

    /** Removes the mapping for the specified key if present. Not required for Lab 7. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("not implemented for Lab 7");


    }

    /** Removes the mapping for the key if it maps to the specified value. Not required for Lab 7. */
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("not implemented for Lab 7");
    }

    /** Returns an iterator over the keys in this map. Not required for Lab 7. */
    @Override
    public Iterator<K> iterator() {
        return new BSTIterator();
    }

    private class BSTIterator implements Iterator<K> {
        private int index;
        private ArrayList<K> keys;
        BSTIterator() {
            index = 0;
            keys = new ArrayList<>();
        }
        public boolean hasNext(){
            return index < size();
        }
        public K next(){
            return keys.get(index++);
        }
        public void remove(){
            throw new UnsupportedOperationException("iterator() not implemented for Lab 7");
        }
    }
    public void printInOrder() {
        printInOrder(root);
    }
    private void printInOrder(Node node) {
        if(node == null) return;
        printInOrder(node.left);
        System.out.print(node.key + " ");
        printInOrder(node.right);

    }
}