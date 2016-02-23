package simple.util.pdf4j;

import java.util.ArrayList;

/**
 * @author dkhileman
 *
 */
public class TableCell {

	private ArrayList<Content> contents;
	private TableRow parentRow;
	private TableColumn parentColumn;
	private int columnSpan;
	private Color backgroundColor;
	private Color borderColorTop;
	private Color borderColorBottom;
	private Color borderColorLeft;
	private Color borderColorRight;
	private float borderThickness;
	
	public ArrayList<Content> getContents() {
		return contents;
	}

	public void setContents(ArrayList<Content> contents) {
		this.contents = contents;
	}

	public TableRow getParentRow() {
		return parentRow;
	}

	public void setParentRow(TableRow parentRow) {
		this.parentRow = parentRow;
	}

	public TableColumn getParentColumn() {
		return parentColumn;
	}

	public void setParentColumn(TableColumn parentColumn) {
		this.parentColumn = parentColumn;
	}

	public int getColumnSpan() {
		return columnSpan;
	}

	public TableCell setColumnSpan(int columnSpan) {
		this.columnSpan = columnSpan;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public TableCell setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Color getBorderColorTop() {
		return borderColorTop;
	}

	public TableCell setBorderColorTop(Color borderColorTop) {
		this.borderColorTop = borderColorTop;
		return this;
	}

	public Color getBorderColorBottom() {
		return borderColorBottom;
	}

	public TableCell setBorderColorBottom(Color borderColorBottom) {
		this.borderColorBottom = borderColorBottom;
		return this;
	}

	public Color getBorderColorLeft() {
		return borderColorLeft;
	}

	public TableCell setBorderColorLeft(Color borderColorLeft) {
		this.borderColorLeft = borderColorLeft;
		return this;
	}

	public Color getBorderColorRight() {
		return borderColorRight;
	}

	public TableCell setBorderColorRight(Color borderColorRight) {
		this.borderColorRight = borderColorRight;
		return this;
	}
	
	public TableCell setBorderColor(Color borderColor) {
		this.borderColorTop = borderColor;
		this.borderColorBottom = borderColor;
		this.borderColorLeft = borderColor;
		this.borderColorRight = borderColor;
		return this;
	}

	public float getBorderThickness() {
		return borderThickness;
	}

	public TableCell setBorderThickness(float borderThickness) {
		this.borderThickness = borderThickness;
		return this;
	}

	public TableCell()
	{
		contents = new ArrayList<Content>();
		columnSpan = 1;
		borderThickness = 1.0f;
	}
	
	public TableCell(Text text)
	{
		this();
		contents.add(text);
	}
	
	public TableCell(Image image)
	{
		this();
		contents.add(image);
	}
	
	public TableCell(Table table)
	{
		this();
		contents.add(table);
	}
	
	public Text newText()
	{
		Text text = new Text();
		contents.add(text);
		return text;
	}
	
	public Text newText(String txt)
	{
		Text text = new Text(txt);
		contents.add(text);
		return text;
	}
	
	public Image newImage()
	{
		Image image = new Image();
		contents.add(image);
		return image;
	}
	
	public Image newImage(String url, float w, float h)
	{
		Image image = new Image(url, w, h);
		contents.add(image);
		return image;
	}
	
	public Table newTable()
	{
		Table table = new Table();
		contents.add(table);
		return table;
	}
	
	public float getMaxWidth()
	{
		float width = 0.0f;
		for(Content content: contents)
		{
			if(content instanceof Text)
			{
				Text text = (Text)content;
				width += text.getTextWidth();
			}
			else if(content instanceof Image)
			{
				Image image = (Image)content;
				width += image.getWidth();
			}
			else if(content instanceof Table)
			{
				Table table = (Table)content;
				width += table.getMaxWidth();
			}
		}
		return width;
	}
}
