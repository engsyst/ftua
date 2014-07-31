package ua.nure.ostpc.malibu.shedule.client;

import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ScheduleDraft implements EntryPoint {
	private final ScheduleDraftServiceAsync scheduleDraftServiceAsync = GWT
			.create(ScheduleDraftService.class);
	private Employee employee = new Employee();
	private boolean isClicked;
	private int countPeopleOnShift;
	private String clubName;
	private String[] surnames;
	private String surnamesAvecComa;
	private int countShifts;
	private int counts;

	public enum Days {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	}

	public void CreateTable(List<String> surnames) {

	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getCountPeopleOnShift() {
		return countPeopleOnShift;
	}

	public void setCountPeopleOnShift(int CountPeopleOnShift) {
		this.countPeopleOnShift = CountPeopleOnShift;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getClubName() {
		return clubName;
	}

	public boolean isClicked() {
		return isClicked;
	}

	public void setClicked(boolean isClicked) {
		this.isClicked = isClicked;
	}

	public String[] getSurnames() {
		return surnames;
	}

	public void setSurnames(String[] surnames) {
		this.surnames = surnames;
	}

	public int getCountShifts() {
		return countShifts;
	}

	public void setCountShifts(int countShifts) {
		this.countShifts = countShifts;
	}

	public String getSurnamesAvecComa() {
		return surnamesAvecComa;
	}

	public void setSurnamesAvecComa(String surnamesAvecComa) {
		this.surnamesAvecComa = surnamesAvecComa;
	}

	public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
	}

	public void onModuleLoad() {
		//TO DO Хранить все расписание в статическом поле класса Шедул. 
		String[] surnames = { "Семерков", "Морозов" };
		this.setSurnames(surnames);
		this.setClubName("Новая Бавария");
		final InlineLabel Greetings = new InlineLabel();
		scheduleDraftServiceAsync.getEmployee(new AsyncCallback<Employee>() {

			@Override
			public void onSuccess(Employee result) {
				setEmployee(result);
				Greetings
						.setText("\u0414\u043E\u0431\u0440\u043E \u043F\u043E\u0436\u0430\u043B\u043E\u0432\u0430\u0442\u044C \u0432 \u0447\u0435\u0440\u043D\u043E\u0432\u0438\u043A"
								+ " " + employee.getLastName());
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});

		this.setCountPeopleOnShift(3);
		this.setCountShifts(2);

		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setStyleName("MainPanel");

		AbsolutePanel ExtraBlock = new AbsolutePanel();
		ExtraBlock.setStyleName("extrablock");
		rootPanel.add(ExtraBlock, 26, 24);
		ExtraBlock.setSize("441px", "107px");

		ExtraBlock.add(Greetings, 173, 10);
		Greetings.setSize("208px", "18px");

		AbsolutePanel absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName("TableBlock");
		rootPanel.add(absolutePanel);

		final FlexTable flexTable = new FlexTable();
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
		flexTable.insertRow(2);
		flexTable.setText(2, 0, "Марашала Жукова");
		flexTable.insertCell(2, 1);
		flexTable.setText(2, 1, Integer.toString(this.getCountPeopleOnShift()));
		int count = 2;
		for (Days x : Days.values()) {
			flexTable.insertCell(0, count);
			flexTable.insertCell(1, count);
			flexTable.insertCell(2, count);
			flexTable.setText(0, count, x.toString());
			count++;
		}
		for (int i = 2; i <= 8; i++) {
			flexTable.setWidget(1, i,
					InsertInTable(flexTable, this.getCountShifts(), i, 1));
			flexTable.setWidget(2, i,
					InsertInTable(flexTable, this.getCountShifts(), i, 2));
		}
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

	private FlexTable InsertInTable(FlexTable flexTable, int CountShifts,
			int column, int rowNumber) {
		final FlexTable innerFlexTable = new FlexTable();
		final FlexTable reserveFlexTable = flexTable;
		final int col = column;
		final int rownumber = rowNumber;
		innerFlexTable.setStyleName("MainTable");
		for (int i = 0; i < CountShifts; i++) {
			final int row = i;
			innerFlexTable.insertRow(i);
			String values = "";
			for (String value : getSurnames()) {
				values = values + value + " ";
			}
			innerFlexTable.setText(i, 0, values);
			innerFlexTable.insertCell(i, 1);
			final CheckBox checkbox = new CheckBox();
			if (innerFlexTable.getText(row, 0).split(" ").length == getCountPeopleOnShift()
					& innerFlexTable.getText(row, 0).contains(
							employee.getLastName()) == false) {
				checkbox.setEnabled(false);
			} else if (innerFlexTable.getText(row, 0).contains(
					employee.getLastName()) == true) {
				checkbox.setValue(true);
				checkbox.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						String surnames = "";
						setCounts(0);
						if (checkbox.getValue() == false) {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames.replace(employee.getLastName(),
									"");
							innerFlexTable.setText(row, 0, surnames);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, true);
						} else {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames + " " + employee.getLastName();
							innerFlexTable.setText(row, 0, surnames);
							setCounts(getCounts() + 1);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, false);
						}
					}
				});
			} else {
				checkbox.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						String surnames = "";
						if (checkbox.getValue() == false) {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames.replace(employee.getLastName(),
									"");
							innerFlexTable.setText(row, 0, surnames);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, true);

						} else {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames + " " + employee.getLastName();
							innerFlexTable.setText(row, 0, surnames);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, false);
						}
					}
				});
			}
			innerFlexTable.setWidget(i, 1, checkbox);
		}
		flexTable = reserveFlexTable;
		return innerFlexTable;
	}

	private void MakeOthersDisabled(FlexTable flexTable, int column,
			int rowNumber, boolean isEnabled) {
		setCounts(0);
		for (int row = 1; row < flexTable.getRowCount(); row++) {
			if (row == rowNumber) {
				continue;
			} else {
				FlexTable innerFlexTable = (FlexTable) flexTable.getWidget(row,
						column);
				for (int i = 0; i < getCountShifts(); i++) {
					if (isEnabled == false) {
						CheckBox checkbox = (CheckBox) innerFlexTable
								.getWidget(i, 1);
						checkbox.setEnabled(false);
						innerFlexTable.setWidget(i, 1, checkbox);
					} else {
						FlexTable innerCopyFlexTable = (FlexTable) flexTable
								.getWidget(rowNumber, column);
						int count = 0;
						for (int j = 0; j < getCountShifts(); j++) {
							CheckBox checkbox = (CheckBox) innerCopyFlexTable
									.getWidget(j, 1);
							if (checkbox.getValue() == true) {
								count++;
							}
						}
						if (count >= 1) {
							return;
						} else {
							CheckBox checkbox = (CheckBox) innerFlexTable
									.getWidget(i, 1);
							checkbox.setEnabled(true);
							innerFlexTable.setWidget(i, 1, checkbox);
						}
					}
				}
				flexTable.setWidget(row, column, innerFlexTable);
			}
		}
	}
}
