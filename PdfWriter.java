package simple.util.pdf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;

public class PdfWriter {

	protected PDDocument document;
	protected PDPage page;
	protected PDRectangle properties;
	protected PDPageContentStream stream;
	protected float cursorx;
	protected float cursory;
	protected int marginLeft;
	protected int marginRight;
	protected int marginTop;
	protected int marginBottom;
	
	private ArrayList<Content> contents;
	
	public PdfWriter()
	{
		marginLeft = 50;
		marginRight = 50;
		marginTop = 50;
		marginBottom = 50;
		contents = new ArrayList<Content>();
		document = new PDDocument();
		newPage();
	}
	
	public void close() 
	{
		try 
		{
			document.close();
			document = null;
			page = null;
			stream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(String filename) 
	{
		try 
		{
			render();
			closePage();
			document.save(filename);
		} catch (COSVisitorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(OutputStream out) 
	{
		try 
		{
			render();
			closePage();
			document.save(out);
		} catch (COSVisitorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closePage() 
	{
		if(stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void newPage() 
	{
		//close previous page
		closePage();
		//start new page
		page = new PDPage();
		properties = page.getMediaBox();
		cursorx = marginLeft;
		cursory = properties.getHeight()-marginTop;
		document.addPage(page);
		try {
			stream = new PDPageContentStream(document, page);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newLine() 
	{
		cursorx = marginLeft;
		cursory = cursory - 10;
		if(cursory < marginBottom)
		{
			//new line won't fit on the current page
			//start a new page
			newPage();
		}
	}
	
	public void breakPage()
	{
		contents.add(Content.PAGE_BREAK);
	}
	
	public void breakLine()
	{
		contents.add(Content.LINE_BREAK);
	}
	
	public Text newText()
	{
		Text text = new Text();
		contents.add(text);
		return text;
	}
	
	public Text newText(String s)
	{
		Text text = new Text(s);
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
	
	private void render()
	{
		for(Content content: contents)
		{
			float vPageSpaceRemaining = cursory-marginBottom;
			
			if(content.getType() == Content.TEXT_TYPE_KEY)
			{
				Text text = (Text)content;
				int lines = renderTextMock(text, cursorx, cursory, properties.getWidth()-marginRight);
				float h = lines*text.getFontHeight();
				
				if(h > vPageSpaceRemaining)
				{
					newPage();
				}
				
				renderText(text, cursorx, cursory, properties.getWidth()-marginRight);
				
				cursorx = marginLeft;
				cursory = cursory - h;
			}
			else if(content.getType() == Content.IMAGE_TYPE_KEY)
			{
				Image img = (Image)content;
				
				if(img.getHeight() > vPageSpaceRemaining)
				{
					newPage();
				}
				
				renderImage(img, cursorx, cursory-img.getHeight(), properties.getWidth()-marginRight);
				cursorx = marginLeft;
				cursory = cursory - img.getHeight();
				newLine();
			}
			else if(content.getType() == Content.TABLE_TYPE_KEY)
			{
				Table table = (Table)content;
				if(table.getRows().size() > 0) //ignore tables with no rows
				{
					if(table.getWidth() < 1)
					{
						table.setWidthAuto(properties.getWidth()-marginLeft-marginRight);
					}
					
					float h = renderTableMock((Table)content, cursorx, cursory);
					
					if(h > vPageSpaceRemaining)
					{
						newPage();
					}
					
					renderTable(table, cursorx, cursory, properties.getWidth()-marginRight);
					
					cursorx = marginLeft;
					cursory = cursory - h;
					newLine();
					newLine();
				}
			}
			else if(content.getType() == Content.PAGE_BREAK_TYPE_KEY)
			{
				newPage();
			}
			else if(content.getType() == Content.LINE_BREAK_TYPE_KEY)
			{
				newLine();
			}
		}
	}
	
	private void renderString(String str, PDFont font, int fontSize, float x, float y, Color color)
	{
		try {
			stream.setNonStrokingColor(color.getR(), color.getG(), color.getB());
			stream.beginText();
			stream.setFont(font, fontSize);
			stream.moveTextPositionByAmount(x, y);
			stream.drawString(str);
			stream.endText();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> splitForFit(String str, PDFont font, int fontSize, float maxWidth, ArrayList<String> resultList)
	{
		boolean splittedOnSpace = false;
		//look for space to split on
		int lastSpacePos = str.lastIndexOf(" ");
		if(lastSpacePos > 0)
		{
			String part1 = str;
			float strWidth = Text.getStringWidth(str, font, fontSize);
			while(strWidth > maxWidth && lastSpacePos > 0)
			{
				//get substring before the space
				part1 = part1.substring(0, lastSpacePos);
				//calculate new length
				strWidth = Text.getStringWidth(part1, font, fontSize);
				//get index of last space
				lastSpacePos = part1.lastIndexOf(" ");
			}
			if(strWidth <= maxWidth)
			{
				splittedOnSpace = true;
				//add the first part of the split
				resultList.add(part1);
				//check length of second part
				String part2 = str.substring(part1.length()+1);
				strWidth = Text.getStringWidth(part2, font, fontSize);
				if(strWidth > maxWidth)
				{
					//split the second part
					splitForFit(part2, font, fontSize, maxWidth, resultList);
				}
				else
				{
					resultList.add(part2);
				}
			}
		}
		
		if(!splittedOnSpace) //could not split on space, so split by fit
		{
			String part1 = str;
			float strWidth = Text.getStringWidth(str, font, fontSize);
			while(strWidth > maxWidth)
			{
				//subtract 1 character from string
				part1 = part1.substring(0, part1.length()-1);
				//calculate new length
				strWidth = Text.getStringWidth(part1, font, fontSize);
			}
			//add the first part of the split
			resultList.add(part1);
			//check length of second part
			String part2 = str.substring(part1.length());
			strWidth = Text.getStringWidth(part2, font, fontSize);
			if(strWidth > maxWidth)
			{
				//split the second part
				splitForFit(part2, font, fontSize, maxWidth, resultList);
			}
			else
			{
				resultList.add(part2);
			}
		}
		
		return resultList;
	}
	
	private int renderText(Text text, float x, float y, float rightBound)
	{
		int linesRendered = 0;
		String string = text.getText().trim();
		
		float maxWidth = rightBound - x;
		if(text.getTextWidth() <= maxWidth)
		{
			if(text.getAlignment().equalsIgnoreCase("center"))
			{
				float centerPoint = maxWidth/2+x;
				float centerAdj = text.getTextWidth()/2;
				x = centerPoint-centerAdj;
			}
			else if(text.getAlignment().equalsIgnoreCase("right"))
			{
				x = rightBound - text.getTextWidth();
			}
			renderString(text.getText(), text.generatePDFont(), text.getFontSize(), x, y, text.getColor());
			linesRendered++;
		}
		else //text wont fit on one line
		{
			//split in parts that will fit
			ArrayList<String> parts = splitForFit(string, text.generatePDFont(), text.getFontSize(), maxWidth, new ArrayList<String>());
			for(String s: parts)
			{
				float modx = x;
				if(text.getAlignment().equalsIgnoreCase("center"))
				{
					float centerPoint = maxWidth/2+x;
					float centerAdj = Text.getStringWidth(s, text.generatePDFont(), text.getFontSize())/2;
					modx = centerPoint-centerAdj;
				}
				else if(text.getAlignment().equalsIgnoreCase("right"))
				{
					modx = rightBound - Text.getStringWidth(s, text.generatePDFont(), text.getFontSize());
				}
				renderString(s, text.generatePDFont(), text.getFontSize(), modx, y, text.getColor());
				y-=text.getFontHeight();
				linesRendered++;
			}
		}
		return linesRendered;
	}
	
	private int renderTextMock(Text text, float x, float y, float rightBound)
	{
		int linesRendered = 0;
		String string = text.getText().trim();
		
		float maxWidth = rightBound - x;
		if(text.getTextWidth() <= maxWidth)
		{
			//renderString(text.getText(), text.generatePDFont(), text.getFontSize(), x, y, text.getColor());
			linesRendered++;
		}
		else //text wont fit on one line
		{
			//split in parts that will fit
			ArrayList<String> parts = splitForFit(string, text.generatePDFont(), text.getFontSize(), maxWidth, new ArrayList<String>());
			for(int i=0; i<parts.size(); i++)
			{
				//renderString(s, text.generatePDFont(), text.getFontSize(), x, y, text.getColor());
				y-=text.getFontHeight();
				linesRendered++;
			}
		}
		return linesRendered;
	}
	
	private float calculateRowHeight(TableRow row)
	{
		float rowHeight = 0;
		
		float x = cursorx;
		float y = cursory;
		
		for(int i=0;i<row.getCells().size();i++)
		{
			TableCell cell = row.getCells().get(i);
			//<column span processing>
			float nextColumnOffset = cell.getParentColumn().getWidth();
			if(cell.getColumnSpan()>1)
			{
				int j = i+1; //next column
				int spans = 1;
				while(spans < cell.getColumnSpan())
				{
					float nextColumnWidth = 50; //default
					if(j<row.getCells().size())
					{
						nextColumnWidth = row.getCells().get(j).getParentColumn().getWidth();
					}
					else
					{
						TableColumn nextColumn = cell.getParentColumn().getParent().getColumns().get(j);
						if(nextColumn != null)
						{
							nextColumnWidth = nextColumn.getWidth();
						}
					}
					nextColumnOffset+=nextColumnWidth;
					j++;
					spans++;
				}
			}
			//</column span processing>
			
			//float renderHeight = calculateCellHeight(cell, x, y);
			float renderHeight = renderCellMock(cell, x, y, x+nextColumnOffset, y-rowHeight);
			if(renderHeight > rowHeight)
			{
				rowHeight = renderHeight;
			}
			
			x += cell.getParentColumn().getWidth();
		}
		
		return rowHeight;
	}
	
	private float renderTable(Table table, float x, float y, float rightBound)
	{
		float maxWidth = rightBound-x;
		if(table.getAlignment().equalsIgnoreCase("center"))
		{
			float centerPoint = maxWidth/2+x;
			float centerAdj = table.getWidth()/2;
			x = centerPoint-centerAdj;
		}
		else if(table.getAlignment().equalsIgnoreCase("right"))
		{
			x = rightBound - table.getWidth();
		}
		
		float tableHeight = 0;
		float leftBound = x;
		for(TableRow row: table.getRows())
		{
			float rowHeight = calculateRowHeight(row);
			tableHeight += rowHeight;
			
			for(int i=0; i<row.getCells().size(); i++)
			{
				TableCell cell = row.getCells().get(i);
				
				//<column span processing>
				float nextColumnOffset = cell.getParentColumn().getWidth();
				if(cell.getColumnSpan()>1)
				{
					int j = i+1; //next column
					int spans = 1;
					while(spans < cell.getColumnSpan())
					{
						float nextColumnWidth = 50; //default
						if(j<row.getCells().size())
						{
							nextColumnWidth = row.getCells().get(j).getParentColumn().getWidth();
						}
						else
						{
							TableColumn nextColumn = cell.getParentColumn().getParent().getColumns().get(j);
							if(nextColumn != null)
							{
								nextColumnWidth = nextColumn.getWidth();
							}
						}
						nextColumnOffset+=nextColumnWidth;
						j++;
						spans++;
					}
					
					//fix parents of all cells to the right
					j = i+1;
					int k = i+2;
					while(j<row.getCells().size())
					{
						TableCell nextCell = row.getCells().get(j);
						if(k<row.getCells().size())
						{
							TableCell nextNextCell = row.getCells().get(k);
							nextCell.setParentColumn(nextNextCell.getParentColumn());
						}
						else if(k < nextCell.getParentColumn().getParent().getColumns().size())
						{
							TableColumn nextNextColumn = nextCell.getParentColumn().getParent().getColumns().get(k);
							nextCell.setParentColumn(nextNextColumn);
						}
						else
						{
							nextCell.setParentColumn(table.newColumn());
						}
						j++;
						k++;
					}
				}
				//</column span processing>
				
				renderCell(cell, x, y, x+nextColumnOffset, y-rowHeight);
				
				x += nextColumnOffset;
			}
			
			x = leftBound;
			y -= rowHeight;
		}
		
		return tableHeight;
	}
	
	private float renderTableMock(Table table, float x, float y)
	{
		float tableHeight = 0;
		float leftBound = x;
		for(TableRow row: table.getRows())
		{
			float rowHeight = calculateRowHeight(row);
			tableHeight += rowHeight;
			
			for(int i=0; i<row.getCells().size(); i++)
			{
				TableCell cell = row.getCells().get(i);
				
				//<column span processing>
				float nextColumnOffset = cell.getParentColumn().getWidth();
				if(cell.getColumnSpan()>1)
				{
					int j = i+1; //next column
					int spans = 1;
					while(spans < cell.getColumnSpan())
					{
						float nextColumnWidth = 50; //default
						if(j<row.getCells().size())
						{
							nextColumnWidth = row.getCells().get(j).getParentColumn().getWidth();
						}
						else
						{
							TableColumn nextColumn = cell.getParentColumn().getParent().getColumns().get(j);
							if(nextColumn != null)
							{
								nextColumnWidth = nextColumn.getWidth();
							}
						}
						nextColumnOffset+=nextColumnWidth;
						j++;
						spans++;
					}
				}
				//</column span processing>
				
				renderCellMock(cell, x, y, x+nextColumnOffset, y-rowHeight);
				
				x += nextColumnOffset;
			}
			
			x = leftBound;
			y -= rowHeight;
		}
		
		return tableHeight;
	}
	
	private float renderCell(TableCell cell, float x, float y, float rightBound, float bottom)
	{
		float renderHeight = 0;
		
		//render background
		if(cell.getBackgroundColor() != null)
		{
			drawRectangle(x,bottom,rightBound-x,y-bottom, cell.getBackgroundColor());
		}
		
		//render cell borders
		if(cell.getParentRow().getParent().getBorderColor() != null)
		{
			Color bdcolor = cell.getParentRow().getParent().getBorderColor();
			drawLine(x, y, rightBound, y, cell.getBorderThickness(), bdcolor);
			drawLine(x, bottom, rightBound, bottom, cell.getBorderThickness(), bdcolor);
			drawLine(x, y, x, bottom, cell.getBorderThickness(), bdcolor);
			drawLine(rightBound, y, rightBound, bottom, cell.getBorderThickness(), bdcolor);
		}
		if(cell.getBorderColorTop() != null)
		{
			drawLine(x, y, rightBound, y, cell.getBorderThickness(), cell.getBorderColorTop());
		}
		if(cell.getBorderColorBottom() != null)
		{
			drawLine(x, bottom, rightBound, bottom, cell.getBorderThickness(), cell.getBorderColorBottom());
		}
		if(cell.getBorderColorLeft() != null)
		{
			drawLine(x, y, x, bottom, cell.getBorderThickness(), cell.getBorderColorLeft());
		}
		if(cell.getBorderColorRight() != null)
		{
			drawLine(rightBound, y, rightBound, bottom, cell.getBorderThickness(), cell.getBorderColorRight());
		}
			
		//render content
		for(Content content: cell.getContents())
		{
			if(content.getType() == Content.TEXT_TYPE_KEY)
			{
				Text text = (Text)content;
				int linesRendered = renderText(text, x+1, y-text.getFontHeight(), rightBound);
				float textHeight = linesRendered*text.getFontHeight();
				if(textHeight > renderHeight)
				{
					renderHeight = textHeight+3;
				}
			}
			if(content.getType() == Content.IMAGE_TYPE_KEY)
			{
				Image img = (Image)content;
				renderImage(img, x+1, bottom, rightBound);
				if(img.getHeight()+3 > renderHeight)
				{
					renderHeight = img.getHeight()+3;
				}
			}
			if(content.getType() == Content.TABLE_TYPE_KEY)
			{
				Table innerTable = (Table)content;
				if(innerTable.getWidth() < 1)
				{
					innerTable.setWidth(rightBound-x-1);
				}
				float tableHeight = renderTable(innerTable, x, y, rightBound);
				if(tableHeight > renderHeight)
				{
					renderHeight = tableHeight+3;
				}
			}
		}
		
		return renderHeight;
	}
	
	private float renderCellMock(TableCell cell, float x, float y, float rightBound, float bottom)
	{
		float renderHeight = 0;
		
		//render background
		if(cell.getBackgroundColor() != null)
		{
			//drawRectangle(x,bottom-3,rightBound-x,y-bottom+3, cell.getBackgroundColor());
		}
		
		//render cell borders
		if(cell.getParentRow().getParent().getBorderColor() != null)
		{
			//Color bdcolor = cell.getParentRow().getParent().getBorderColor();
			//drawLine(x, y, rightBound, y, 0.5F, bdcolor);
			//drawLine(x, bottom-3, rightBound, bottom-3, 0.5F, bdcolor);
			//drawLine(x, y, x, bottom-3, 0.5F, bdcolor);
			//drawLine(rightBound, y, rightBound, bottom-3, 0.5F, bdcolor);
		}
		if(cell.getBorderColorTop() != null)
		{
			//drawLine(x, y, rightBound, y, 0.5F, cell.getBorderColorTop());
		}
		if(cell.getBorderColorBottom() != null)
		{
			//drawLine(x, bottom-3, rightBound, bottom-3, 0.5F, cell.getBorderColorBottom());
		}
		if(cell.getBorderColorLeft() != null)
		{
			//drawLine(x, y, x, bottom-3, 0.5F, cell.getBorderColorLeft());
		}
		if(cell.getBorderColorRight() != null)
		{
			//drawLine(rightBound, y, rightBound, bottom-3, 0.5F, cell.getBorderColorRight());
		}
			
		//render content
		for(Content content: cell.getContents())
		{
			if(content.getType() == Content.TEXT_TYPE_KEY)
			{
				Text text = (Text)content;
				int linesRendered = renderTextMock(text, x+1, y-text.getFontHeight(), rightBound);
				float textHeight = linesRendered*text.getFontHeight();
				if(textHeight > renderHeight)
				{
					renderHeight = textHeight+3;
				}
			}
			if(content.getType() == Content.IMAGE_TYPE_KEY)
			{
				Image img = (Image)content;
				//renderImage(img, x, bottom);
				if(img.getHeight()+3 > renderHeight)
				{
					renderHeight = img.getHeight()+3;
				}
			}
			if(content.getType() == Content.TABLE_TYPE_KEY)
			{
				Table innerTable = (Table)content;
				
				boolean innerTableTempChange = false;
				if(innerTable.getWidth() < 1)
				{
					innerTable.setWidthAuto(rightBound-x);
					innerTableTempChange = true;
				}
				
				float tableHeight = renderTableMock(innerTable, x, y);
				if(tableHeight > renderHeight)
				{
					renderHeight = tableHeight+3;
				}
				
				if(innerTableTempChange)
				{
					for(TableColumn c: innerTable.getColumns())
					{
						c.setWidth(0.0f);
					}
				}
			}
		}
		
		return renderHeight;
	}
	
	private void renderImage(Image image, float x, float y, float rightBound)
	{
		float maxWidth = rightBound-x;
		if(image.getAlignment().equalsIgnoreCase("center"))
		{
			float centerPoint = maxWidth/2+x;
			float centerAdj = image.getWidth()/2;
			x = centerPoint-centerAdj;
		}
		else if(image.getAlignment().equalsIgnoreCase("right"))
		{
			x = rightBound - image.getWidth();
		}
		
		try{
			PDPixelMap pic = new PDPixelMap(document, image.getImage());
			stream.drawXObject(pic, x, y, image.getWidth(), image.getHeight());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void drawLine(float xStart, float yStart, float xEnd, float yEnd, float thickness, Color color)
	{
		try {
			stream.setStrokingColor(color.getR(), color.getG(), color.getB());
			stream.setLineWidth(thickness);
			stream.addLine(xStart, yStart, xEnd, yEnd);
			stream.closeAndStroke();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void drawRectangle(float x, float y, float w, float h, Color color) 
	{
		try {
			stream.setNonStrokingColor(color.getR(), color.getG(), color.getB());
			stream.fillRect(x, y, w, h);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
