package dataTypes;

@SuppressWarnings("MalformedFormatString")
public abstract class Data1<E> {
    protected E x;

    public Data1(E x) {
        this.x = x;
    }

    public E get() {
        return x;
    }

    public void setX(E x) {
        this.x = x;
    }

    public String toString()
    {
        final String format = "%+07.3f";
        return 	"x: " + String.format(format,x);
    }
    public abstract Data1<E> clone();
    public abstract Data1<E> multiply(Data1<E> a, Data1<E> b);
}