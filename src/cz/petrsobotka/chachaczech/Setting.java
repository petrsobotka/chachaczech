package cz.petrsobotka.chachaczech;

public class Setting {
	
	public int id;
	public String name;
	public boolean active;
	
	public Setting(int id, String name, boolean active)
	{
		this.id = id;
		this.name = name;
		this.active = active;	
	}
}