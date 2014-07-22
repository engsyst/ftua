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
import com.google.gwt.user.client.ui.CheckBox;
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
	private boolean isClicked;
	private int CountPeopleOnShift;  
	private String ClubName;
	private String EmployeeSurname;
	private String[] Surnames;
	private int CountShifts;
	
	public enum Days{
		MONDAY, TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY
	}
	public void CreateTable (List<String> surnames)
	{
		
	}
	private FlexTable OnCheckClick (FlexTable innerFlexTable, int column, int row)
	{
		String surnames ="";
		if (isClicked == false)
		{
			surnames = innerFlexTable.getText(row, column);
			for (String surname: this.getSurnames())
			{
				surnames = surnames +"," + surname;
			}
			innerFlexTable.setText(row, column, surnames);
			return innerFlexTable;
		}
		else
		{
			surnames = innerFlexTable.getText(row, column);
			surnames.replaceFirst(","+this.getEmployeeSurname(), "");
			innerFlexTable.setText(row, column, surnames);
			return innerFlexTable;
		}
	}
	public FlexTable InsertInTable(FlexTable flexTable, int CountShifts )
	{
		final FlexTable innerFlexTable = new FlexTable();
		innerFlexTable.setStyleName("MainTable");
		for (int i =0; i<CountShifts;i++)
		{
			innerFlexTable.insertRow(i);
			innerFlexTable.setText(i, 0, "Kovaljov, Mezhevich");
			innerFlexTable.insertCell(i, 1);
			CheckBox checkbox = new CheckBox();
			innerFlexTable.setWidget(i, 1, checkbox);
		}
		return innerFlexTable;
	}
	

	public void onModuleLoad() {
		String[] surnames = {"Semerkoff","Mezhevich"};
		this.setSurnames(surnames);
		this.setClubName("Bayern");
		this.setEmployeeSurname("Kovaljov");
		this.setCountPeopleOnShift(3);
		this.setCountShifts(2);
		
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setStyleName("MainPanel");
		
		AbsolutePanel ExtraBlock = new AbsolutePanel();
		ExtraBlock.setStyleName("extrablock");
		rootPanel.add(ExtraBlock, 26, 24);
		ExtraBlock.setSize("441px", "107px");
		
		InlineLabel Greetings = new InlineLabel();
		ExtraBlock.add(Greetings, 173, 10);
		Greetings.setSize("208px", "18px");
		Greetings.setText("\u0414\u043E\u0431\u0440\u043E \u043F\u043E\u0436\u0430\u043B\u043E\u0432\u0430\u0442\u044C \u0432 \u0447\u0435\u0440\u043D\u043E\u0432\u0438\u043A" + " " + this.getEmployeeSurname() );
		
		
		
		AbsolutePanel absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName("TableBlock");
		rootPanel.add(absolutePanel);
		
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("MainTable");
		absolutePanel.add(flexTable, 10, 10);
		flexTable.setSize("100px", "100px");
		
		flexTable.insertRow(0);
		flexTable.setText(0, 0, " ");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Число рабочих на смене");
		flexTable.insertRow(1);
		flexTable.insertCell(1, 1);
		
		flexTable.setText(1, 1, Integer.toString(this.getCountPeopleOnShift()));
		flexTable.setText(1, 0, this.getClubName());
		int count = 2;
		for (Days x: Days.values())
		{
			flexTable.insertCell(0, count);
			flexTable.insertCell(1, count);
			flexTable.setText(0, count, x.toString());
			count++;
		}
		for (int i = 2; i<=8;i++)
		{
			flexTable.setWidget(1, i, InsertInTable(flexTable, this.getCountShifts()));
		}
//		for (int i =0; i<7;i++)
//		{
//			flexTable = InsertInTable(flexTable,i);
//			checkbox.addClickHandler( new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				innerFlexTable.setText(1, 0, "Kovaljov, Semerkoff, Mezhevich");
//			}
//		});
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


	public int getCountPeopleOnShift() {
		return CountPeopleOnShift;
	}


	public void setCountPeopleOnShift(int CountPeopleOnShift) {
		this.CountPeopleOnShift = CountPeopleOnShift;
	}
	
	public void setClubName(String clubName)
	{
		this.ClubName = clubName;
	}
	public String getClubName()
	{
		return ClubName;
	}
	public boolean isClicked() {
		return isClicked;
	}
	public void setClicked(boolean isClicked) {
		this.isClicked = isClicked;
	}
	public String getEmployeeSurname() {
		return EmployeeSurname;
	}
	public void setEmployeeSurname(String employeeSurname) {
		EmployeeSurname = employeeSurname;
	}
	public String[] getSurnames() {
		return Surnames;
	}
	public void setSurnames(String[] surnames) {
		Surnames = surnames;
	}
	public int getCountShifts() {
		return CountShifts;
	}
	public void setCountShifts(int countShifts) {
		CountShifts = countShifts;
	}
}

