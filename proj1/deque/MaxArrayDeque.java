package deque;

import java.util.Comparator;


public class MaxArrayDeque<T> extends ArrayDeque<T> {

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




