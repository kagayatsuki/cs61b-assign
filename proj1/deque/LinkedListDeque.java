package deque;

import  java.util.Iterator;

public class LinkedListDeque<T> implements Iterable <T>, Deque<T> {
    private class Node{
        T item;
        Node next;
        Node prev;
        Node(){};
        Node(T val){
            this.item = val;
        }
        Node(T val,Node next,Node prev){
            this.item = val;
            this.next = next;
            this.prev = prev;
        }
    };
    private Node sentinel; //catalpa
    private int size;
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }//注意到sentinel的prev是尾部，它的next是头部，充当桥梁

    public int size(){
        return size;
    }
    public void addFirst(T item){
        Node newNode = new Node(item,sentinel.next,sentinel);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size++;
    }
    public void addLast(T item) {
        Node newNode = new Node(item, sentinel, sentinel.prev);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size++;
    }//之前有bug
    public void printDeque() {
        System.out.println("Debug: size = " + size);
        Node current = sentinel.next;
        int count = 0;
        while (current != sentinel && count < size + 1) {
            System.out.print(current.item + " ");
            current = current.next;
            count++;
        }
        if (count >= size + 1) {
            System.out.println("Error: Possible infinite loop detected");
        }
        System.out.println();
    }
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        T item = sentinel.next.item;
        Node first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        size--;
        first.prev=null;
        first.next=null;
        first.item=null;
        return item;
    }
    public T removeLast() {
    if(isEmpty()){
        return null;
    }
    Node last = sentinel.prev;
    T item = last.item;
    sentinel.prev=last.prev;
    last.prev.next=sentinel;
    size--;
    last.item=null;
    last.prev=null;
    last.next=null;
    return item;
    }
    //递归大蛇
    public T getRecursive(int index){
        if(index==0||index>=size){
            return null;
        }
        return helpRecursive(sentinel.next,index);
    }
    private T helpRecursive(Node cur,int index){
        if(index==0){
            return cur.item;
        }
        return helpRecursive(cur.next,index-1);
    }
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;

        }
        return current.item;
    }
    //aiaiaiaiaiaiaiaiai
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node current;

        LinkedListDequeIterator() {
            current = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return current != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            T item = current.item;
            current = current.next;
            return item;
        }
    }

    /**
     * Checks if this deque is equal to another object.
     * Two deques are equal if they contain the same items in the same order.
     * @param o the object to compare with
     * @return true if the object is a deque with the same contents in the same order
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<?> other = (LinkedListDeque<?>) o;
        if (this.size != other.size) {
            return false;
        }
        Node thisCurrent = this.sentinel.next;
        Node otherCurrent = (Node) other.sentinel.next;
        while (thisCurrent != this.sentinel) {
            if (!thisCurrent.item.equals(otherCurrent.item)) {
                return false;
            }
            thisCurrent = thisCurrent.next;
            otherCurrent = otherCurrent.next;
        }
        return true;
    }
}


