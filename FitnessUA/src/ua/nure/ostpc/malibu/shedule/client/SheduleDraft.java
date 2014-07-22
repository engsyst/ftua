package ua.nure.ostpc.malibu.shedule.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SheduleDraft implements EntryPoint {
	
	private int CountPeopleOnSet;  
	private String ClubName;
	
	public enum Days{
		MONDAY, TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY
	}
	public void CreateTable (List<String> surnames)
	{
		
	}
//	public FlexTable InsertInTable(FlexTable flexTable, int column)
//	{
//		final FlexTable innerFlexTable = new FlexTable();
//		innerFlexTable.setStyleName("MainTable");
//		innerFlexTable.setStyleName("MainTable");
//		innerFlexTable.insertRow(0);
//		innerFlexTable.addCell(0);
//		innerFlexTable.setText(0, 0, "Kovaljov, Mezhevich");
//		innerFlexTable.addCell(0);
//		SimpleCheckBox checkbox = new SimpleCheckBox();
//		innerFlexTable.setWidget(0,1,checkbox);
//		flexTable.setWidget(1, column+1, innerFlexTable);
//		innerFlexTable.insertRow(1);
//		innerFlexTable.addCell(1);
//		innerFlexTable.setText(1, 0, "Kovaljov, Semerkoff");
//		innerFlexTable.addCell(1);
//		SimpleCheckBox checkbox1 = new SimpleCheckBox();
//		innerFlexTable.setWidget(1,1,checkbox1);
//		checkbox1.addClickHandler( new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				innerFlexTable.setText(1, 0, "Kovaljov, Semerkoff, Mezhevich");
//			}
//		});
//		checkbox.addClickHandler( new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				innerFlexTable.setText(1, 0, "Kovaljov, Semerkoff, Mezhevich");
//			}
//		});
//		if (innerFlexTable.getText(1, 0).split(",").length>2)
//		{
//		checkbox1.setEnabled(false);
//		}
//		return flexTable;
//	}
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setStyleName("MainPanel");
		
		AbsolutePanel ExtraBlock = new AbsolutePanel();
		ExtraBlock.setStyleName("extrablock");
		rootPanel.add(ExtraBlock, 26, 24);
		ExtraBlock.setSize("441px", "107px");
		
		InlineLabel nlnlblNewInlinelabel = new InlineLabel("\u0414\u043E\u0431\u0440\u043E \u043F\u043E\u0436\u0430\u043B\u043E\u0432\u0430\u0442\u044C \u0432 \u0447\u0435\u0440\u043D\u043E\u0432\u0438\u043A");
		ExtraBlock.add(nlnlblNewInlinelabel, 173, 10);
		nlnlblNewInlinelabel.setSize("208px", "18px");
		
		
		
		AbsolutePanel absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName("TableBlock");
		rootPanel.add(absolutePanel);
		
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("MainTable");
		absolutePanel.add(flexTable, 10, 10);
		flexTable.setSize("100px", "100px");
		
		flexTable.insertRow(0);
		flexTable.setText(0, 0, " ");
		flexTable.insertRow(1);
		flexTable.setText(1, 0, "Bayern");
		int count = 1;
		for (Days x: Days.values())
		{
			flexTable.insertCell(0, count);
			flexTable.insertCell(1, count);
			flexTable.setText(0, count, x.toString());
			count++;
		}
//		for (int i =0; i<7;i++)
//		{
//			flexTable = InsertInTable(flexTable,i);
//		}
		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
	}


	public int getCountPeopleOnSet() {
		return CountPeopleOnSet;
	}


	public void setCountPeopleOnSet(int countPeopleOnSet) {
		CountPeopleOnSet = countPeopleOnSet;
	}
	
	public void setClubName(String clubName)
	{
		this.ClubName = clubName;
	}
	public String getClubName()
	{
		return ClubName;
	}
}

