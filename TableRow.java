package simple.util.pdf4j;

import java.util.ArrayList;

public class TableRow {

	private ArrayList<TableCell> cells;
	private Table parent;

	public ArrayList<TableCell> getCells() {
		return cells;
	}

	public void setCells(ArrayList<TableCell> cells) {
		this.cells = cells;
	}
	
	public Table getParent() {
		return parent;
	}

	public void setParent(Table parent) {
		this.parent = parent;
	}

	public TableRow() {
		cells = new ArrayList<TableCell>();
	}
	
	public TableCell newCell() {
		TableCell cell = new TableCell();
		
		//add cell to appropriate column
		if(parent.getColumns().size() > cells.size())
		{
			//add to existing column
			TableColumn column = parent.getColumns().get(cells.size());
			cell.setParentColumn(column);
			column.getCells().add(cell);
		}
		else
		{
			//add to new column
			TableColumn newColumn = parent.newColumn("Column" + cells.size());
			cell.setParentColumn(newColumn);
			newColumn.getCells().add(cell);
		}
		
		//add cell to row
		cell.setParentRow(this);
		cells.add(cell);
		
		return cell;
	}
}
