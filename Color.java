package simple.util.pdf4j;

/**
 * @author dkhileman
 * 
 * Custom color class. The purpose of using this class
 * is to avoid using java.awt.Color. By avoiding the awt
 * java package, this code should work on Android.
 * 
 */
public class Color 
{
	int r;
	int g;
	int b;
	
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = g;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	
	public Color()
	{
		r = 0;
		g = 0;
		b = 0;
	}
	
	public Color(int r, int g, int b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
