package simple.util.pdf4j;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * @author dkhileman
 *
 */
public class Text extends Content
{
	private String text;
	private String fontFamily;
	private int fontSize;
	private boolean bold;
	private boolean italic;
	private Color color;
	private String alignment;
	
	public String getText() {
		return text==null?"":text;
	}
	public Text setText(String text) {
		this.text = text;
		return this;
	}
	public String getFontFamily() {
		return fontFamily;
	}
	public Text setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
		return this;
	}
	public int getFontSize() {
		return fontSize;
	}
	public Text setFontSize(int fontSize) {
		this.fontSize = fontSize;
		return this;
	}
	public boolean isBold() {
		return bold;
	}
	public Text setBold(boolean bold) {
		this.bold = bold;
		return this;
	}
	public boolean isItalic() {
		return italic;
	}
	public Text setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}
	public Color getColor() {
		return color;
	}
	public Text setColor(Color color) {
		this.color = color;
		return this;
	}
	public String getAlignment() {
		return alignment;
	}
	public Text setAlignment(String alignment) {
		this.alignment = alignment;
		return this;
	}
	
	public Text()
	{
		text = "";
		fontFamily = "HELVETICA";
		fontSize = 10;
		bold = false;
		italic = false;
		type = Content.TEXT_TYPE_KEY;
		color = new Color(0, 0, 0);
		alignment = "left";
	}
	
	public Text(String txt)
	{
		text = txt;
		fontFamily = "HELVETICA";
		fontSize = 10;
		bold = false;
		italic = false;
		type = Content.TEXT_TYPE_KEY;
		color = new Color(0, 0, 0);
		alignment = "left";
	}
	
	public Text(String txt, int size, boolean b, boolean i)
	{
		text = txt;
		fontFamily = "HELVETICA";
		fontSize = size;
		bold = b;
		italic = i;
		type = Content.TEXT_TYPE_KEY;
		color = new Color(0, 0, 0);
		alignment = "left";
	}
	
	public float getTextWidth()
	{
		PDFont font = generatePDFont();
		float width = 0.0f;
		try {
			if(text != null)
			{
				width = font.getStringWidth(text) / 1000 * fontSize;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return width;
	}
	
	public float getFontHeight()
	{
		float fontHeight = 0;
		try {
			fontHeight = generatePDFont().getFontBoundingBox().getHeight()/1000*fontSize*0.865f;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fontHeight;
	}
	
	public static float getStringWidth(String str, PDFont strFont, int strFontSize)
	{
		float width = 0.0f;
		try {
			width = strFont.getStringWidth(str) / 1000 * strFontSize;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return width;
	}
	
	public PDFont generatePDFont()
	{
		PDFont font = PDType1Font.HELVETICA;
		if(fontFamily.equalsIgnoreCase("HELVETICA"))
		{
			if(bold && italic)
			{
				font = PDType1Font.HELVETICA_BOLD_OBLIQUE;
			}
			else if(bold)
			{
				font = PDType1Font.HELVETICA_BOLD;
			}
			else if(italic)
			{
				font = PDType1Font.HELVETICA_OBLIQUE;
			}
			else
			{
				font = PDType1Font.HELVETICA;
			}
		}
		else if(fontFamily.equalsIgnoreCase("COURIER"))
		{
			if(bold && italic)
			{
				font = PDType1Font.COURIER_BOLD_OBLIQUE;
			}
			else if(bold)
			{
				font = PDType1Font.COURIER_BOLD;
			}
			else if(italic)
			{
				font = PDType1Font.COURIER_OBLIQUE;
			}
			else
			{
				font = PDType1Font.COURIER;
			}
		}
		else if(fontFamily.equalsIgnoreCase("TIMES"))
		{
			if(bold && italic)
			{
				font = PDType1Font.TIMES_BOLD_ITALIC;
			}
			else if(bold)
			{
				font = PDType1Font.TIMES_BOLD;
			}
			else if(italic)
			{
				font = PDType1Font.TIMES_ITALIC;
			}
			else
			{
				font = PDType1Font.TIMES_ROMAN;
			}
		}
		
		return font;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}
