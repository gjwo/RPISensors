package dataTypes;

public enum NavFeilds
{
	F1("Yaw",1),
	F2("Pitch",2),
	F3("Roll",3);
		
	final String name;
	final int Position;
	
	NavFeilds(String name, int Position)  { this.name = name; this.Position = Position; }		

	public String getName(){return this.name;}
}