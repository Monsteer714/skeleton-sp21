package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    Comparator<T> compare;
    public MaxArrayDeque(Comparator<T> c){
        compare = c;
    }

    public T max(){
        return (T)null;
    }

    public T max(Comparator<T> c){
        return (T)null;
    }
}
