package deque;
import java.util.Comparator;
import java.util.Iterator;
public class ArrayDeque<T> implements Deque<T> ,Iterable<T>{
   private T array [];
   private int size;
    private int front; // 前端第一个元素的索引
    private int rear;//后面的索引+1
    public ArrayDeque() {
       array = (T[]) new Object[8];//强制转换
       size = 0;
       front = 0;
       rear = 0;
   }//循环队列可以类比一个圆圈
    public void addFirst(T item){
        if(size == array.length){
            resize(array.length * 2);
        }
        front = (front - 1+array.length) % array.length;array[front] = item;
        size++;

    }
    public void addLast(T item){
        if(size == array.length){
            resize(array.length * 2);
        }
        array[rear] = item;
        rear = (rear + 1) % array.length;

        size++;
    }
    public T removeFirst(){
        if(size == 0){
            return null;
        }
        T item = array[front];
        array[front] = null;
        front = (front + 1+array.length) % array.length;
        size--;
        if(size>0&&size<array.length/4){
            resize(array.length/2);
        }
        return item;
    }
    public T removeLast(){
        if(size == 0){
            return null;
        }
        rear = (rear - 1+array.length) % array.length;
        T item = array[rear];
        array[rear] = null;

        size--;
        if(size>0&&size<array.length/4){
            resize(array.length/2);
        }
        return item;

    }
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i));
            if (i < size - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public int size() {
        return size;
    }
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int actualIndex = (front + index) % array.length;
        return array[actualIndex];
    }

    private void resize(int newCapacity) {
        T[] newArray = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            int index = (front + i) % array.length; // Account for circular array
            newArray[i] = array[index];
        }
        array = newArray;
        front = 0;
        rear = size>0?size:0;
    }
    public Iterator<T> iterator(){
        return new arraydequeiterator();
    }
    private class arraydequeiterator implements Iterator<T>{
        private int i;
        private int cur;
        public arraydequeiterator() {
            cur = front;
            i=0;
        }
        public boolean hasNext(){
            return i<size;
        }
        public T next(){
            T item = array[cur];
                    cur = (cur + 1) % array.length;
                    i++;
                    return item;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj==null||obj.getClass()!=this.getClass()){
            return false;
        }
        ArrayDeque <T> other = (ArrayDeque<T>) obj;
        if(other.size()!=size){
            return false;
        }
        Iterator<T> thisIterator = iterator();
        Iterator<T> otherIterator = other.iterator();
        while(thisIterator.hasNext()){
            Object thisItem = thisIterator.next();
            Object otherItem = otherIterator.next();
            if(!thisItem.equals(otherItem)){
                return false;
            }
        }
        return true;
    }
}
