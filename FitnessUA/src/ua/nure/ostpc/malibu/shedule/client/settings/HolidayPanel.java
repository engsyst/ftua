package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.DialogBoxUtil;
import ua.nure.ostpc.malibu.shedule.client.settings.AddHolidayForm.HolidayUpdater;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

public class HolidayPanel extends SimplePanel implements HolidayUpdater {
	private static DateTimeFormat dateTimeFormat = DateTimeFormat
			.getFormat("dd.MM.yyyy");

	private List<Holiday> holidayList;

	private FlexTable holidayTable;

	public HolidayPanel() {
//		HorizontalPanel mainPanel = new HorizontalPanel();
//		mainPanel.setSize("100%", "100%");
		drawHolidayTable(/*mainPanel*/);
		setWidget(holidayTable/*mainPanel*/);
		AddHolidayForm.registerUpdater(this);
		getHolidaySettingsData();
	}

	private void drawHolidayTable(/*HorizontalPanel mainPanel*/) {
		if (holidayTable == null) {
			holidayTable = new FlexTable();
		}
		holidayTable.removeAllRows();
		holidayTable.setStyleName("mainTable");
		holidayTable.addStyleName("settingsTable");
		holidayTable.insertRow(0);

		InlineLabel titleLabel = new InlineLabel("Выходной");
		titleLabel.setWordWrap(true);
		holidayTable.insertCell(0, 0);
		holidayTable.setWidget(0, 0, titleLabel);

		Image creatingImage = new Image("img/new_holiday.png");
		creatingImage.setStyleName("myImageAsButton");
		creatingImage
				.setTitle("Добавить новый выходной в подсистему составления графика работ");

		creatingImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AddHolidayForm addHolidayForm = new AddHolidayForm();
				DialogBoxUtil.callDialogBox(
						"Добавление нового выходного", addHolidayForm);
			}
		});

		holidayTable.insertCell(0, 1);
		holidayTable.setWidget(0, 1, creatingImage);

		holidayTable.getRowFormatter().addStyleName(0, "mainHeader");

//		mainPanel.add(holidayTable);
	}

	private void getHolidaySettingsData() {
		AppState.startSettingsService
				.getHolidays(new AsyncCallback<Collection<Holiday>>() {

					@Override
					public void onSuccess(Collection<Holiday> result) {
						holidayList = (List<Holiday>) result;
						if (!holidayList.isEmpty()) {
							setHolidaysInTable();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say("Невозможно получить данные с сервера!");
					}
				});
	}

	private void setHolidaysInTable() {
		cleanTable(holidayTable);
		Iterator<Holiday> it = holidayList.iterator();
		int rowNumber = 1;
		while (it.hasNext()) {
			Holiday holiday = it.next();
			holidayTable.insertRow(rowNumber);
			holidayTable.insertCell(rowNumber, 0);
			InlineLabel holidayTitleLabel = new InlineLabel(
					dateTimeFormat.format(holiday.getDate()));
			holidayTable.setWidget(rowNumber, 0, holidayTitleLabel);
			holidayTable.insertCell(rowNumber, 1);
			if (holiday.getDate().after(new Date())) {
				HolidayRemovingImage holidayRemovingImage = new HolidayRemovingImage(
						holiday.getHolidayId());
				holidayTable.setWidget(rowNumber, 1, holidayRemovingImage);
			}
			rowNumber++;
		}
	}

	/**
	 * Removes all rows except header of the table.
	 * 
	 * @param table
	 *            - Table for row removing.
	 */
	private void cleanTable(FlexTable table) {
		int rowCount = table.getRowCount();
		for (int i = rowCount - 1; i > 0; i--) {
			table.removeRow(i);
		}
	}

	@Override
	public void updateHoliday(Holiday holiday) {
		getHolidaySettingsData();
	}

	private class HolidayRemovingImage extends Image {
		private long holidayId;

		private HolidayRemovingImage(long holidayId) {
			super("img/remove.png");
			this.holidayId = holidayId;
			setTitle("Удалить выходной день");
			setStyleName("cursor");

			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					HolidayRemovingImage holidayRemovingImage = (HolidayRemovingImage) event
							.getSource();
					final long holidayId = holidayRemovingImage.getHolidayId();
					SC.ask("Вы уверены, что хотите удалить выходной день?",
							new BooleanCallback() {

								@Override
								public void execute(Boolean value) {
									if (value) {
										AppState.startSettingsService
												.removeHoliday(
														holidayId,
														new AsyncCallback<Boolean>() {

															@Override
															public void onSuccess(
																	Boolean result) {
																if (result) {
																	getHolidaySettingsData();
																} else {
																	SC.say("Указанный выходной день удалить не удалось!");
																}
															}

															@Override
															public void onFailure(
																	Throwable caught) {
																SC.say(caught
																		.getMessage());
															}
														});
									}
								}
							});
				}
			});

		}

		public long getHolidayId() {
			return holidayId;
		}
	}

}
