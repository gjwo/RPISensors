package dataTypes;

public abstract class Data<E> {
    protected E f1;

    public Data() {this.f1=null;}
    public Data(E x) {this.f1 = x;}
    public E getF1() {return f1;}
    public void setF1(E x) {this.f1 = x;}
    
    public abstract String toString();
    public abstract Data<E> clone();
    public abstract Data<E> multiply(Data<E> b);
}