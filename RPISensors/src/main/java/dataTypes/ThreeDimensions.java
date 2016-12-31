package dataTypes;

public interface ThreeDimensions<E> extends TwoDimensions<E>
{
	E getZ();
	void setZ(E z);
}