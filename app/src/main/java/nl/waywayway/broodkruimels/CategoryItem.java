package nl.waywayway.broodkruimels;

public class CategoryItem
{
	private int number;
	private String name;
	private int parent;

	public void setNumber(int number)
	{
		this.number = number;
	}

	public int getNumber()
	{
		return number;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setParent(int parent)
	{
		this.parent = parent;
	}

	public int getParent()
	{
		return parent;
	}
}
