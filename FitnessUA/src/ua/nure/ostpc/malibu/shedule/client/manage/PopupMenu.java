package ua.nure.ostpc.malibu.shedule.client.manage;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupMenu extends PopupPanel {
	private VerticalPanel panel;

/*	private class Item {
		private String text;
		private ScheduledCommand cmd;

		public Item(String text, ScheduledCommand cmd) {
			super();
			this.setText(text);
			this.setCmd(cmd);
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public ScheduledCommand getCmd() {
			return cmd;
		}

		public void setCmd(ScheduledCommand cmd) {
			this.cmd = cmd;
		}
	}*/

//	private ArrayList<Item> items = new ArrayList<PopupMenu.Item>();

	public PopupMenu() {
		super();
		panel = new VerticalPanel();
		add(panel);
	}

	public void addPopupItem(String text, final ScheduledCommand cmd) {
//		items.add(new Item(text, cmd));
		final Label label = new Label(text);
		label.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cmd.execute();
			}
		});
		panel.add(label);
	}
}