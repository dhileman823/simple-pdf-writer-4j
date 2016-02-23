package simple.util.pdf4j;

import java.awt.image.BufferedImage;

import java.net.URL;

import javax.imageio.ImageIO;

/**
 * @author dkhileman
 *
 */
public class Image extends Content
{
	private float width;
	private float height;
	private BufferedImage image;
	private String alignment;
	
	public float getWidth() {
		return width;
	}

	public Image setWidth(float width) {
		this.width = width;
		return this;
	}

	public float getHeight() {
		return height;
	}

	public Image setHeight(float height) {
		this.height = height;
		return this;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Image setImage(BufferedImage image) {
		this.image = image;
		return this;
	}
	
	public Image setImage(String srcUrl) {
		try 
		{
			image = ImageIO.read(new URL(srcUrl));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return this;
	}
	
	public String getAlignment() {
		return alignment;
	}
	
	public Image setAlignment(String alignment) {
		this.alignment = alignment;
		return this;
	}
	
	public Image()
	{
		width = 0;
		height = 0;
		type = Content.IMAGE_TYPE_KEY;
		alignment = "left";
	}

	public Image(BufferedImage i, float w, float h)
	{
		width = w;
		height = h;
		image = i;
		type = Content.IMAGE_TYPE_KEY;
		alignment = "left";
	}
	
	public Image(String srcUrl, float w, float h)
	{
		width = w;
		height = h;
		type = Content.IMAGE_TYPE_KEY;
		alignment = "left";
		try 
		{
			image = ImageIO.read(new URL(srcUrl));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
