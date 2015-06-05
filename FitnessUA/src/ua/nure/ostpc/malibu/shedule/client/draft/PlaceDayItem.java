package ua.nure.ostpc.malibu.shedule.client.draft;

import java.util.ArrayList;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PlaceDayItem extends VerticalPanel implements ValueChangeHandler<DraftShiftItem>{

	private int row, col;
	private ArrayList<DraftShiftItem> items;
	
	public PlaceDayItem(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}
	

	public PlaceDayItem(int row, int col, ArrayList<DraftShiftItem> items) {
		this(row, col);
		for (DraftShiftItem draftShiftItem : items) {
			add(draftShiftItem);
		}
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	@Override
	public void onValueChange(ValueChangeEvent<DraftShiftItem> event) {
		DraftShiftItem item = event.getValue();
	}

}
