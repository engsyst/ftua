package ua.nure.ostpc.malibu.shedule.client.draft;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetList extends VerticalPanel implements HasEnabled {
	private Widget addBtn;
	private ArrayList<Widget> widgets = new ArrayList<Widget>();
	private boolean enabled = true;

	public WidgetList() {
		super();
		addStyleName("dsi-panel");
//		initWidget(vp);
	}
	
	public WidgetList(Widget addButton) {
		this();
		addBtn = addButton;
		add(addBtn);
		addBtn.getElement().getParentElement().addClassName("dsi-newItemPanel");
		addBtn.addStyleName("dsi-newItemImage");
	}

	public int addItem(Widget w) {
		widgets.add(w);
		int i = widgets.size() - 1;
		add(widgets.get(i));
		widgets.get(i).getElement().getParentElement().addClassName("dsi-itemPanel");
		return i;
	}
	
	public int removeItem(Widget w) {
		int i = widgets.indexOf(w);
		widgets.remove(i);
		remove(w);
		return i;
	}
	
	public Widget removeItem(int idx) {
		remove(idx);
		return widgets.remove(idx);
	}
	
	public void removeAll() {
		for (Widget w : widgets) {
			remove(w);
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
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (addBtn != null && addBtn instanceof HasEnabled) {
			((HasEnabled) addBtn).setEnabled(enabled);
//			addBtn.setStyleDependentName("disabled", !enabled);
		}
		if (widgets != null) {
			for (Widget w : widgets) {
				if (w !=null && w instanceof HasEnabled) {
					((HasEnabled) w).setEnabled(enabled);
//					w.setStyleDependentName("disabled", !enabled);
				}
			}
		}
	}
	
	public void setAddEnabled(boolean enabled) {
		if (addBtn instanceof HasEnabled) {
			((HasEnabled) addBtn).setEnabled(enabled);
		}
	}
	public boolean isAddEnabled() {
		return addBtn != null && addBtn instanceof HasEnabled 
				? ((HasEnabled) addBtn).isEnabled() : false;
	}
}


