package dataTypes;

public abstract class Data<E> {
    protected E x;

    public Data(E x) {this.x = x;}
    public E get() {return x;}
    public void set(E x) {this.x = x;}

    public void scale(E xScale)
    {
    	System.out.println("function not implemented");
    }

    public void offset(E xOffset)
    {
    	System.out.println("function not implemented");
    }

    public String toString()
    {
        final String format = "%+07.3f";
        return 	"x: " + String.format(format,x);
    }
    public Data<E> clone()
    {
    	System.out.println("function not implemented");
    	return null;
    }
    public Data<E> multiply(Data<E> a, Data<E> b)
    {
    	System.out.println("function not implemented");
    	return null;
    }

}
