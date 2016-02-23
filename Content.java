package simple.util.pdf4j;

/**
 * @author dkhileman
 *
 */
public class Content 
{
	public static int UNDEFINED_TYPE_KEY = 0;
	public static int PAGE_BREAK_TYPE_KEY = 101;
	public static int LINE_BREAK_TYPE_KEY = 102;
	public static int TABLE_TYPE_KEY = 103;
	public static int TEXT_TYPE_KEY = 104;
	public static int IMAGE_TYPE_KEY = 105;
	public static int PARAGRAPH_TYPE_KEY = 106;
	
	public static Content PAGE_BREAK = new Content(PAGE_BREAK_TYPE_KEY);
	public static Content LINE_BREAK = new Content(LINE_BREAK_TYPE_KEY);
	
	protected int type;
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public Content()
	{
		this.type = UNDEFINED_TYPE_KEY;
	}
	
	public Content(int type)
	{
		this.type = type;
	}
}
