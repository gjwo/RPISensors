package dataTypes;

public enum NavFields
{
	F1("Yaw",1),
	F2("Pitch",2),
	F3("Roll",3);
		
	final String name;
	final int Position;
	
	NavFields(String name, int Position)  { this.name = name; this.Position = Position; }		

	public String getName(){return this.name;}
}