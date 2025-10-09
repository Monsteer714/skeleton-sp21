package deque;

public class LinkedListDeque<T> {
    private class Node {
        public T data;
        public Node next;
        public Node prev;
        public Node(T _data) {
            data=_data;
            next=null;
            prev=null;
        }
    }
    private int size=0;
    private Node dummyHead=null;
    private Node dummyTail=null;

    public LinkedListDeque() {
        dummyHead=new Node(null);
        dummyTail=new Node(null);
        dummyHead.next=dummyTail;
        dummyTail.prev=dummyHead;
        size=0;
    }

    public boolean isEmpty() {
        return size==0;
    }

    public int size() {
        return size;
    }

    public void addFirst(T item){
        Node newNode=new Node(item);
        Node temp=dummyHead.next;
        dummyHead.next=newNode;
        newNode.prev=dummyHead;
        newNode.next=temp;
        temp.prev=newNode;
        this.size++;
    }

    public void addLast(T item){
        Node newNode=new Node(item);
        Node temp=dummyTail.prev;
        dummyTail.prev=newNode;
        newNode.next=dummyTail;
        newNode.prev=temp;
        temp.next=newNode;
        this.size++;
    }

    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        Node temp=dummyHead.next;
        T data=temp.data;
        dummyHead.next=temp.next;
        temp.next.prev=dummyHead;
        this.size--;
        return data;
    }

    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        Node temp=dummyTail.prev;
        T data=temp.data;
        dummyTail.prev=temp.prev;
        temp.prev.next=dummyTail;
        this.size--;
        return data;
    }

    public T get(int index){
        if(index<0 || index>=size){
            return null;
        }
        Node temp=dummyHead.next;
        for(int i=0;i<index;i++){
            temp=temp.next;
        }
        return temp.data;
    }

    private T getRecursiveHelper(Node cur,int index){
        if(index<0||index>=size){
            return null;
        }
        if(index==0){
            return cur.data;
        }
        return getRecursiveHelper(cur.next,index-1);
    }

    public T getRecursive(int index){
        return getRecursiveHelper(dummyHead.next,index);
    }

    public void printDeque() {
        Node temp = dummyHead.next;
        while (temp != null) {
            System.out.print(temp.data + " ");
            temp = temp.next;
        }
        System.out.println();
    }
}