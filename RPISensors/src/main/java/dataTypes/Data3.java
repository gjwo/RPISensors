package dataTypes;

public abstract class Data3<E,F extends Enum<F>> {
    private E f1;
    private E f2;
    private E f3;

    public Data3() {this.f1=null;}
    public Data3(E x) {this.f1 = x;}
    public E get(F name) {return f1;}
    public void setF1(E x) {this.f1 = x;}
    
    public abstract String toString();
    public abstract Data3<E,F> clone();
    public abstract Data3<E,F> multiply(Data3<E,F> b);
}