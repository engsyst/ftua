package ua.nure.ostpc.malibu.shedule.client.draft;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetList extends Composite {
	private VerticalPanel vp;
	private Widget addBtn;
	private ArrayList<Widget> widgets = new ArrayList<Widget>();

	public WidgetList() {
		super();
		vp = new VerticalPanel();
		vp.addStyleName("dsi-panel");
		initWidget(vp);
	}
	public WidgetList(Widget addButton) {
		this();
		addBtn = addButton;
		vp.add(addBtn);
		addBtn.getElement().getParentElement().addClassName("dsi-newItemPanel");
		addBtn.addStyleName("dsi-newItemImage");
	}

	public int addItem(Widget w) {
		widgets.add(w);
		int i = widgets.size() - 1;
		vp.add(widgets.get(i));
		widgets.get(i).getElement().getParentElement().addClassName("dsi-itemPanel");
		return i;
	}
	
	public int removeItem(Widget w) {
		int i = widgets.indexOf(w);
		widgets.remove(i);
		vp.remove(w);
		return i;
	}
	
	public Widget removeItem(int idx) {
		vp.remove(idx);
		return widgets.remove(idx);
	}
	
	public void removeAll() {
		for (Widget w : widgets) {
			vp.remove(w);
		}
		widgets = new ArrayList<Widget>();
	}
	
	public Widget getWidget(int index) {
		return widgets.get(index);
	}

	public Widget getAddBtn() {
		return addBtn;
	}
	
	public void setAddBtn(Widget addBtn) {
		this.addBtn = addBtn;
	}
	
	public int indexOf(Widget w) {
		return widgets.indexOf(w);
	}
	
	public int size() {
		return widgets.size();
	}
}


