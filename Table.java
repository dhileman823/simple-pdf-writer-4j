package simple.util.pdf4j;

import java.util.ArrayList;

public class Table extends Content {

	private ArrayList<TableRow> rows;
	private ArrayList<TableColumn> columns;
	private Color borderColor;
	private String alignment;
	private float width;
	
	public ArrayList<TableRow> getRows() {
		return rows;
	}
	public void setRows(ArrayList<TableRow> rows) {
		this.rows = rows;
	}
	public ArrayList<TableColumn> getColumns() {
		return columns;
	}
	public void setColumns(ArrayList<TableColumn> columns) {
		this.columns = columns;
	}
	public Color getBorderColor() {
		return borderColor;
	}
	public Table setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}
	public String getAlignment() {
		return alignment;
	}
	public Table setAlignment(String alignment) {
		this.alignment = alignment;
		return this;
	}
	
	public Table()
	{
		rows = new ArrayList<TableRow>();
		columns = new ArrayList<TableColumn>();
		type = Content.TABLE_TYPE_KEY;
		alignment = "left";
	}
	
	public TableColumn newColumn()
	{
		TableColumn column = new TableColumn();
		column.setParent(this);
		columns.add(column);
		return column;
	}
	
	public TableColumn newColumn(String name)
	{
		TableColumn column = new TableColumn(name);
		column.setParent(this);
		columns.add(column);
		return column;
	}
	
	public TableRow newRow()
	{
		TableRow row = new TableRow();
		row.setParent(this);
		rows.add(row);
		return row;
	}
	
	public void setWidthAuto(float maxWidthAllowed)
	{
		System.out.println("setWidthAuto");
		if(this.getMaxWidth() > maxWidthAllowed)
		{
			this.setWidth(maxWidthAllowed);
		}
		else
		{
			this.setWidth(this.getMaxWidth());
		}
	}
	
	public Table setWidth(float width)
	{
		this.width = width;
		
		//first, set widths to there max
		for(TableColumn column: columns)
		{
			column.setWidth(column.getMaxWidth()+2);
		}
		
		//adjust column widths to fit in table width
		float currentWidth = getWidth();
		while(currentWidth > this.width)
		{
			//get widest column
			TableColumn widestColumn = new TableColumn().setWidth(1);
			for(TableColumn column: columns)
			{
				if(column.getWidth() > widestColumn.getWidth())
				{
					widestColumn = column;
				}
			}
			//shrink widest column
			widestColumn.setWidth(widestColumn.getWidth()-1);
			
			currentWidth = getWidth();
		}
		while(currentWidth < this.width)
		{
			//get narrowest column
			TableColumn narrowestColumn = new TableColumn().setWidth(611);
			for(TableColumn column: columns)
			{
				if(column.getWidth() < narrowestColumn.getWidth())
				{
					narrowestColumn = column;
				}
			}
			
			//widen narrowest column
			narrowestColumn.setWidth(narrowestColumn.getWidth()+1);
			
			currentWidth = getWidth();
		}
		
		return this;
	}
	
	public float getWidth()
	{
		float width = 0;
		for(TableColumn column: columns)
		{
			width += column.getWidth();
		}
		return width;
	}
	
	public float getMaxWidth()
	{
		float width = 0.0f;
		for(TableColumn column: columns)
		{
			width  += column.getMaxWidth()+2;
		}
		return width;
	}
}
