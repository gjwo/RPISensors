package dataTypes;

public abstract class Data3<E,F extends Enum<F>> {
    protected E f1;
    protected E f2;
    protected E f3;

    public Data3() {this.f1=null;}
    public Data3(E x) {this.f1 = x;}
    public E get(F name) {return f1;}
    public void setF1(E x) {this.f1 = x;}
    
    public abstract String toString();
    public abstract Data3<E,F> clone();
    public abstract Data3<E,F> multiply(Data3<E,F> b);
}