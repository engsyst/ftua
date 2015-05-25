package ua.nure.ostpc.malibu.shedule.client.settings;

import ua.nure.ostpc.malibu.shedule.client.AppState;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.smartgwt.client.util.SC;

public class WeekendPanel extends SimplePanel {

	public static final String[] days = new String[] { "Понедельник",
			"Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" };
	private CheckBox[] daysBoxes;
	private FlexTable weekendTable;
	private ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			CheckBox cb = (CheckBox) event.getSource();
			cb.setEnabled(false);
			updateWeekendSettingsData();
		}
	};

	public WeekendPanel() {
		drawWeekendTable();
		setWidget(weekendTable);
		getWeekendsSettingsData();
	}

	private void drawWeekendTable() {
		if (weekendTable == null) {
			weekendTable = new FlexTable();
			if (daysBoxes == null) {
				daysBoxes = new CheckBox[days.length];
			}
		}
		weekendTable.removeAllRows();
		weekendTable.setStyleName("mainTable");
		weekendTable.addStyleName("settingsTable");
		weekendTable.insertRow(0);

		InlineLabel titleLabel = new InlineLabel("День недели");
		titleLabel.setWordWrap(true);
		titleLabel.setTitle("Установите флаг если данный день недели\n"
				+ "должен считаться выходным.\n"
				+ "Состояние этих флагов учитывается только при\n"
				+ "создании графика работ и автоматической генерации.\n"
				+ "ВАЖНО:\n - назначения сотрудников не снимаются в существующих графиках работ."
				+ " - дни остаются доступны для назначений.");
		weekendTable.insertCell(0, 0);
		weekendTable.setWidget(0, 0, titleLabel);
		weekendTable.getRowFormatter().addStyleName(0, "mainHeader");
		
		int i = 0;
		int r = 1;
		while (i < days.length) {
			daysBoxes[i] = new CheckBox(days[i]);
			daysBoxes[i].addValueChangeHandler(handler);
			weekendTable.insertRow(r);
			weekendTable.insertCell(r, 0);
			weekendTable.setWidget(r++, 0, daysBoxes[i++]);
		}
		weekendTable.addStyleName("WeekendPanel");
	}

	private void getWeekendsSettingsData() {
		AppState.startSettingsService
				.getWeekends(new AsyncCallback<boolean[]>() {

					@Override
					public void onSuccess(boolean[] result) {
						setData(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say("Невозможно получить данные с сервера!");
					}
				});
	}

	protected void setData(boolean[] weekends) {
		for (int i = 0; i < daysBoxes.length; i++) {
			daysBoxes[i].setValue(weekends[i]);
		}
	}

	protected boolean[] getData() {
		boolean[] weekends = new boolean[7];
		for (int i = 0; i < daysBoxes.length; i++) {
			weekends[i] = daysBoxes[i].getValue();
		}
		return weekends;
	}
	
	public void updateWeekendSettingsData() {
		AppState.startSettingsService.updateWeekends(getData(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				// Enable all checkboxes
				for (CheckBox cb : daysBoxes) {
					cb.setEnabled(true);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.getMessage());
				// Enable all checkboxes
				for (CheckBox cb : daysBoxes) {
					cb.setEnabled(true);
				}
			}
		});
	}

}
