package dataTypes;

interface TwoDimensions<E> extends OneDimension <E>
{
	E getY();
	void setY(E y);
}