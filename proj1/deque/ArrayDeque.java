package deque;
import java.util.Comparator;

public class ArrayDeque<T> implements Deque<T> {
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
    public boolean isEmpty(){
        return size==0;
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
}
class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }
    public T max(){
        if (isEmpty()) {
            return null;
        }
        T max=get(0);
        for (int i=0;i<size();i++){
            T element = get(i);
            if (comparator.compare(element,max)>0){
                max = element;

            }
        }
        return max;
    }
    public T max(Comparator<T> c){
        if (isEmpty()) {
            return null;
        }
        T max=get(0);
        for (int i=0;i<size();i++){
            T element = get(i);
            if (c.compare(element,max)>0){
                max = element;
            }
        }
        return max;
    }





}


