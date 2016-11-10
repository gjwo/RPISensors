package dataTypes;

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

    public void scale(E xScale)
    {
    	System.out.println("Function not implemented");
    }

    public void offset(E xOffset)
    {
    	System.out.println("Function not implemented");
    }

    public String toString()
    {
        final String format = "%+07.3f";
        return 	"x: " + String.format(format,x);
    }
    public Data1<E> clone()
    {
        //return new Data1 <E>(x);
    	System.out.println("Function not implemented");
    	return null;
    }
    public Data1<E> multiply(Data1<E> a, Data1<E> b){
    	System.out.println("Function not implemented");
    	return null;
    }

}
