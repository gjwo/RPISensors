package dataTypes;

public abstract class Data<E> {

    public Data() {}
    public Data(E x) {}
    public abstract E get();
    public abstract void set(E x);  
    public abstract String toString();
    public abstract Data<E> clone();
    public abstract Data<E> multiply(Data<E> b);
}