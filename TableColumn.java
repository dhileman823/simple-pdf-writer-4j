package simple.util.pdf4j;

import java.util.ArrayList;

public class TableColumn {

	private ArrayList<TableCell> cells;
	private String name;
	private float width;
	private Table parent;

	public ArrayList<TableCell> getCells() {
		return cells;
	}

	public void setCells(ArrayList<TableCell> cells) {
		this.cells = cells;
	}

	public String getName() {
		return name;
	}

	public TableColumn setName(String name) {
		this.name = name;
		return this;
	}
	
	public float getWidth() {
		return width;
	}

	public TableColumn setWidth(float width) {
		this.width = width;
		return this;
	}
	
	public Table getParent() {
		return parent;
	}

	public void setParent(Table parent) {
		this.parent = parent;
	}

	public TableColumn() {
		this.cells = new ArrayList<TableCell>();
		this.width = 0;
	}
	
	public TableColumn(String name) {
		this.name = name;
		this.width = 0;
		this.cells = new ArrayList<TableCell>();
	}
	
	public TableCell newCell() {
		TableCell cell = new TableCell();
		
		//add cell to appropriate row
		if(parent.getRows().size() > cells.size())
		{
			//add to existing row
			TableRow row = parent.getRows().get(cells.size());
			cell.setParentRow(row);
			row.getCells().add(cell);
		}
		else
		{
			//add to new row
			TableRow newRow = parent.newRow();
			cell.setParentRow(newRow);
			newRow.getCells().add(cell);
		}
		
		//add cell to column
		cell.setParentColumn(this);
		cells.add(cell);
		
		return cell;
	}
	
	public float getMaxWidth()
	{
		float max = 0.0f;
		for(TableCell cell: cells)
		{
			if(cell.getMaxWidth() > max)
			{
				max = cell.getMaxWidth();
			}
		}
		return max;
	}
}
