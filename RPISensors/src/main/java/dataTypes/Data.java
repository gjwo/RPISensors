package dataTypes;

public abstract class Data<E> {
    protected E x;

    public Data() {this.x=null;}
    public Data(E x) {this.x = x;}
    public E get() {return x;}
    public void set(E x) {this.x = x;}
    
    public abstract String toString();
    public abstract Data<E> clone();
    public abstract Data<E> multiply(Data<E> a, Data<E> b);
}
