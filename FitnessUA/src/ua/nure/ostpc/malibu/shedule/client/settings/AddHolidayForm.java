package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingImagePanel;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.smartgwt.client.util.SC;

public class AddHolidayForm extends SimplePanel {

	public interface HolidayUpdater {
		public void updateHoliday(Holiday holiday);
	}

	private String datePattern = "dd/MM/yyyy";
	private static Set<HolidayUpdater> updaterSet = new HashSet<AddHolidayForm.HolidayUpdater>();

	private static VerticalPanel panel = new VerticalPanel();
	private static Label errLabel;
	private static Label holidayDateLabel;
	private static DateBox holidayDateBox;
	private static Button btnSave;

	public AddHolidayForm() {
		setWidget(panel);
		panel.clear();
		createView();
	}

	private void createView() {
		errLabel = new Label(" ");
		panel.add(errLabel);

		final int ROWS = 2;
		final int COLS = 2;
		Grid grid = new Grid(ROWS, COLS);
		panel.add(grid);

		holidayDateLabel = new Label("Дата выходного: ");
		grid.setWidget(0, 0, holidayDateLabel);
		holidayDateLabel.setTitle("Введите дату выходного.");

		holidayDateBox = new DateBox();
		holidayDateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getFormat(datePattern)));
		grid.setWidget(0, 1, holidayDateBox);

		// Control buttons
		Grid btnGrid = new Grid(1, 2);
		btnSave = new Button("Сохранить");
		btnGrid.setWidget(0, 0, btnSave);
		btnSave.addClickHandler(buttonSaveClickHandler);

		panel.add(btnGrid);

		// Styling
		panel.addStyleName(AppConstants.CSS_PANEL_STYLE);
		errLabel.getElement().getParentElement()
				.setClassName(AppConstants.CSS_ERROR_LABEL_PANEL_STYLE);
		for (int i = 0; i < ROWS; i++) {
			grid.getCellFormatter().setStyleName(i, 0,
					AppConstants.CSS_LABEL_PANEL_STYLE);
			grid.getCellFormatter().setStyleName(i, 1,
					AppConstants.CSS_TEXT_BOX_PANEL_STYLE);
		}

		btnGrid.addStyleName(AppConstants.CSS_BUTTON_PANEL_STYLE);

		holidayDateBox.setFocus(true);
	}

	public Holiday getFormData() {
		Holiday holiday = new Holiday();
		holiday.setDate(holidayDateBox.getValue());
		return holiday;
	}

	private boolean validateForm() {
		if (!AppState.clientSideValidator.validateDate(holidayDateBox
				.getTextBox().getText(), datePattern)) {
			errLabel.setText("Дата выходного дня указана некорректно!");
			return false;
		} else {
			if (holidayDateBox.getValue().before(new Date())) {
				errLabel.setText("Задайте выходной после сегодняшней даты!");
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Set your own ClickHandler.<br />
	 * If you need provide own validation you must provide own ClickHandler to
	 * validate and save data.<br />
	 * Default ClickHandler validate text boxes and save data to the server
	 * </p>
	 * 
	 * @param handler
	 *            Your own Save button ClickHandler
	 */
	public void setBtnSaveClickHandler(ClickHandler handler) {
		if (btnSave != null)
			btnSave.addClickHandler(handler);
		throw new IllegalStateException();
	}

	/**
	 * <p>
	 * Default Save button ClickHandler.<br />
	 * You can set own ClickHandler.
	 * </p>
	 * See: {@link AddHolidayForm#setBtnSaveClickHandler(ClickHandler)}
	 * 
	 */
	private ClickHandler buttonSaveClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			clearErrors();
			if (validateForm()) {
				Holiday holiday = new Holiday();
				holiday.setDate(holidayDateBox.getValue());
				btnSave.setVisible(false);
				LoadingImagePanel.start();
				AppState.startSettingsService.insertHoliday(holiday,
						new AsyncCallback<Holiday>() {

							@Override
							public void onSuccess(Holiday result) {
								errLabel.setText("Выходной день добавлен!");
								for (HolidayUpdater updater : updaterSet) {
									try {
										updater.updateHoliday(result);
									} catch (Exception caught) {
										SC.say(caught.getMessage());
									}
								}
								LoadingImagePanel.stop();
								btnSave.setVisible(true);
								Timer timer = new Timer() {

									@Override
									public void run() {
										errLabel.setText(" ");
									}
								};
								timer.schedule(20000);
							}

							@Override
							public void onFailure(Throwable caught) {
								LoadingImagePanel.stop();
								errLabel.setText(caught.getMessage());
							}
						});
			}
		}
	};

	private void clearErrors() {
		errLabel.setText("");
	}

	public static boolean registerUpdater(HolidayUpdater updater) {
		if (updater != null)
			return updaterSet.add(updater);
		return false;
	}

	public static boolean unregisterUpdater(HolidayUpdater updater) {
		if (updater != null)
			return updaterSet.remove(updater);
		return false;
	}
}
