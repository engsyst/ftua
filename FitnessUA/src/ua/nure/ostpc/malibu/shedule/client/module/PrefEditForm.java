package ua.nure.ostpc.malibu.shedule.client.module;

import java.util.HashSet;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.client.StartSettingService;
import ua.nure.ostpc.malibu.shedule.client.StartSettingServiceAsync;
import ua.nure.ostpc.malibu.shedule.entity.GenFlags;
import ua.nure.ostpc.malibu.shedule.entity.Preference;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.SC;

public class PrefEditForm extends SimplePanel implements ClickHandler {
	/**
	 * <p>
	 * If you need notify what user change preferences, you need implements this
	 * interface and register implementation calling
	 * {@link PrefEditForm#registerUpdater(PreferenseUpdater)}
	 * </p>
	 * <p>
	 * To unregister call
	 * {@link PrefEditForm#unregisterUpdater(PreferenseUpdater)}
	 * </p>
	 * 
	 * @author engsyst
	 * 
	 */
	public interface PreferenseUpdater {
		public void updatePreference(Preference p);
	}

	public static final String errLabelPanelStyle = "epf-errLabelPanel";
	public static final String labelPanelStyle = "epf-labelPanel";
	public static final String tbPanelStyle = "epf-textBoxPanel";
	public static final String btnPanelStyle = "epf-buttonsPanel";

	private static final StartSettingServiceAsync service = GWT
			.create(StartSettingService.class);

	private static Set<PreferenseUpdater> updater = new HashSet<PrefEditForm.PreferenseUpdater>();

	private VerticalPanel panel = new VerticalPanel();
	private Preference preference;
	private Label errLabel;
	private Label shiftsNumberLabel;
	private TextBox shiftsNumberTB;
	private Label workHoursInDayLabel;
	private TextBox workHoursInDayTB;
	private Label workHoursInWeekLabel;
	private TextBox workHoursInWeekTB;
	private Label workContinusHoursLabel;
	private TextBox workContinusHoursTB;
	private Label flagCanEmptyLabel;
	private CheckBox flagCanEmptyCB;
	private Label flagMaxHoursInWeekLabel;
	private CheckBox flagMaxHoursInWeekCB;
	private Label flagMaxContinusHoursLabel;
	private CheckBox flagMaxContinusHoursCB;
	private Label flagCheckEmpMaxDaysLabel;
	private CheckBox flagCheckEmpMaxDaysCB;
	private Button btnSave;
	private boolean wordWrap = true;

	public PrefEditForm() {
		add(panel);
		service.getPreference(new AsyncCallback<Preference>() {

			@Override
			public void onSuccess(Preference result) {
				if (result == null) {
					preference = new Preference();
				} else {
					preference = result;
				}
				createView();
				setFormData(preference);
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.getMessage());
			}
		});
	}

	public PrefEditForm(Preference prefs) {
		add(panel);
		preference = prefs;
		createView();
		setFormData(prefs);
	}

	public Preference getPrefs() {
		return preference;
	}

	public void setPrefs(Preference prefs) {
		preference = prefs;
		setFormData(preference);
	}

	private void createView() {
		errLabel = new Label(" ");
		panel.add(errLabel);

		final int ROWS = 8;
		final int COLS = 2;
		Grid grid = new Grid(ROWS, COLS);
		panel.add(grid);

		// private TextBox shiftsNumberTB;
		shiftsNumberLabel = new Label("Количество смен: ");
		grid.setWidget(0, 0, shiftsNumberLabel);
		shiftsNumberLabel
				.setTitle("Количество смен в одном дне. Таким образом,\n"
						+ "'Количество часов в одной смене' = 'Количество часов в дне' / 'количество смен'");

		shiftsNumberTB = new TextBox();
		grid.setWidget(0, 1, shiftsNumberTB);
		shiftsNumberTB.setName("shiftsNumber");

		// private Label workHoursInDayLabel;
		workHoursInDayLabel = new Label("Количество часов в дне: ");
		grid.setWidget(1, 0, workHoursInDayLabel);
		// grid.getCellFormatter().setStyleName(1, 0, labelPanelStyle);
		workHoursInDayLabel.setWordWrap(wordWrap);
		workHoursInDayLabel
				.setTitle("Количество рабочих часов в одном дне. Таким образом,\n"
						+ "'Количество часов в одном дне' = 'Количество часов в смене' * 'количество смен'");

		workHoursInDayTB = new TextBox();
		grid.setWidget(1, 1, workHoursInDayTB);
		workHoursInDayTB.setName("workHoursInDay");

		// private TextBox workHoursInWeek;
		workHoursInWeekLabel = new Label("Количество рабочих часов в неделе: ");
		grid.setWidget(2, 0, workHoursInWeekLabel);
		// grid.getCellFormatter().setStyleName(2, 0, labelPanelStyle);
		workHoursInWeekLabel.setWordWrap(wordWrap);
		workHoursInWeekLabel
				.setTitle("Этот показатель обычно регулируется законодательством.\n"
						+ "В Украине это 40 часов. Но компании, также сами регулируют его.\n"
						+ "При составлении графика работ сотрудник, у которого следующее\n"
						+ "назначение на рабочую смену приведет к превышению этого числа\n"
						+ "не будет назначаться до конца недели.\n"
						+ "Отсчет недели начинается с первого дня графика работ.");

		workHoursInWeekTB = new TextBox();
		grid.setWidget(2, 1, workHoursInWeekTB);
		workHoursInWeekTB.setName("workHoursInWeek");

		// private TextBox workContinusHours;
		workContinusHoursLabel = new Label(
				"Количество рабочих часов в неделе подряд: ");
		grid.setWidget(3, 0, workContinusHoursLabel);
		// grid.getCellFormatter().setStyleName(3, 0, labelPanelStyle);
		workContinusHoursLabel.setWordWrap(wordWrap);
		workContinusHoursLabel
				.setTitle("По законодательству Украины на каждые 8 рабочих часов\n"
						+ "положено 16 часов отдыха. Компания может сама регулировать\n"
						+ "после скольки рабочих дней сотрудник получит выходной.\n"
						+ "Данный показатель задается в часах, и не должен превышать\n"
						+ "'Количество рабочих часов в неделе'. При составлении графика работ,\n"
						+ "подсчитывается количество рабочих дней подряд. Если следующее\n"
						+ "назначение приведет к превышению этого числа,\n"
						+ "то сотруднику будет предоставлен выходной.");

		workContinusHoursTB = new TextBox();
		grid.setWidget(3, 1, workContinusHoursTB);
		workContinusHoursTB.setName("workContinusHours");

		// Flags
		flagCanEmptyLabel = new Label("Разрешить пустые смены: ");
		grid.setWidget(4, 0, flagCanEmptyLabel);
		// grid.getCellFormatter().setStyleName(3, 0, labelPanelStyle);
		flagCanEmptyLabel.setWordWrap(wordWrap);
		flagCanEmptyLabel
				.setTitle("Если установлен, то разрешаются пустые смены.\n"
						+ "<b>Не рекомендуется снимать этот флаг</b>\n");

		flagCanEmptyCB = new CheckBox();
		grid.setWidget(4, 1, flagCanEmptyCB);
		flagCanEmptyCB.setName("flagCanEmpty");

		//
		flagMaxHoursInWeekLabel = new Label(
				"Проверять отработанные часы в неделю: ");
		grid.setWidget(5, 0, flagMaxHoursInWeekLabel);
		// grid.getCellFormatter().setStyleName(3, 0, labelPanelStyle);
		flagMaxHoursInWeekLabel.setWordWrap(wordWrap);
		flagMaxHoursInWeekLabel
				.setTitle("Если установлен, то проверяется ограничение\n"
						+ "на суммарное число часов отработанных сотрудником в неделю.\n"
						+ "Если флаг снят, ограничение не проверяется\n"
						+ "<b>Не рекомендуется снимать этот флаг</b>\n");

		flagMaxHoursInWeekCB = new CheckBox();
		grid.setWidget(5, 1, flagMaxHoursInWeekCB);
		flagMaxHoursInWeekCB.setName("flagMaxHoursInWeek");

		//
		flagMaxContinusHoursLabel = new Label("Выходные среди недели: ");
		grid.setWidget(6, 0, flagMaxContinusHoursLabel);
		// grid.getCellFormatter().setStyleName(3, 0, labelPanelStyle);
		flagMaxContinusHoursLabel.setWordWrap(wordWrap);
		flagMaxContinusHoursLabel
				.setTitle("Если установлен, то при достижении сотрудником\n"
						+ "'Количество рабочих часов в неделе подряд' ему будет назначаться выходной\n"
						+ "<b>Не рекомендуется снимать этот флаг</b>\n");

		flagMaxContinusHoursCB = new CheckBox();
		grid.setWidget(6, 1, flagMaxContinusHoursCB);
		flagMaxContinusHoursCB.setName("flagMaxContinusHours");

		//
		flagCheckEmpMaxDaysLabel = new Label(
				"Проверять предпочтения сотрудника: ");
		grid.setWidget(7, 0, flagCheckEmpMaxDaysLabel);
		// grid.getCellFormatter().setStyleName(3, 0, labelPanelStyle);
		flagCheckEmpMaxDaysLabel.setWordWrap(wordWrap);
		flagCheckEmpMaxDaysLabel
				.setTitle("Если установлен, то при достижении сотрудником\n"
						+ "его максимального количества рабочих дней в неделе, он не будет\n"
						+ "назначаться дальше до конца недели.\n"
						+ "Неделя отсчитывается от начала графика работ.\n"
						+ "<b>Не рекомендуется снимать этот флаг</b>\n");

		flagCheckEmpMaxDaysCB = new CheckBox();
		grid.setWidget(7, 1, flagCheckEmpMaxDaysCB);
		flagCheckEmpMaxDaysCB.setName("flagCheckEmpMaxDays");

		// Control buttons
		Grid btnGrid = new Grid(1, 2);
		// btnGrid.setStyleName(btnPanelStyle);
		btnSave = new Button("Сохранить");
		btnGrid.setWidget(0, 0, btnSave);
		btnSave.addClickHandler(buttonSaveClickHandler);

		Button btnReset = new Button("Сбросить");
		btnGrid.setWidget(0, 1, btnReset);
		btnReset.addClickHandler(this);

		panel.add(btnGrid);

		// Styling
		panel.addStyleName("epf-panel");
		errLabel.getElement().getParentElement()
				.setClassName(errLabelPanelStyle);
		for (int i = 0; i < ROWS; i++) {
			grid.getCellFormatter().setStyleName(i, 0, labelPanelStyle);
			grid.getCellFormatter().setStyleName(i, 1, tbPanelStyle);
		}

		btnGrid.addStyleName(btnPanelStyle);

		shiftsNumberTB.setFocus(true);

		// setFormData(prefs);
	}

	public void setFormData(Preference p) {
		assert p != null : "Preference can not be a null ";
		preference = p;
		shiftsNumberTB.setText(String.valueOf(p.getShiftsNumber()));
		workHoursInDayTB.setText(String.valueOf(p.getWorkHoursInDay()));
		workHoursInWeekTB.setText(String.valueOf(p.getWorkHoursInWeek()));
		workContinusHoursTB.setText(String.valueOf(p.getWorkContinusHours()));
		flagCanEmptyCB.setValue(p.isFlagsSet(GenFlags.SCHEDULE_CAN_EMPTY));
		flagMaxHoursInWeekCB.setValue(p
				.isFlagsSet(GenFlags.CHECK_MAX_HOURS_IN_WEEK));
		flagMaxContinusHoursCB.setValue(p
				.isFlagsSet(GenFlags.WEEKEND_AFTER_MAX_HOURS));
		flagCheckEmpMaxDaysCB.setValue(p
				.isFlagsSet(GenFlags.CHECK_EMP_MAX_DAYS));
	}

	private Set<String> validate(Preference p) {
		Set<String> err = new HashSet<String>();
		try {
			p.setShiftsNumber(Integer.parseInt(shiftsNumberTB.getText()));
			if (p.getShiftsNumber() <= 0) {
				err.add(shiftsNumberTB.getName());
			}
		} catch (Exception e) {
			err.add(shiftsNumberTB.getName());
		}
		try {
			p.setWorkHoursInDay(Integer.parseInt(workHoursInDayTB.getText()));
			if (p.getWorkHoursInDay() <= 0 || (p.getWorkHoursInDay() > 24)) {
				err.add(workHoursInDayTB.getName());
			}
		} catch (Exception e) {
			err.add(workHoursInDayTB.getName());
		}
		try {
			p.setWorkHoursInWeek(Integer.parseInt(workHoursInWeekTB.getText()));
			if (p.getWorkHoursInWeek() <= 0
					|| (p.getWorkHoursInWeek() < p.getWorkHoursInDay())) {
				err.add(workHoursInWeekTB.getName());
			}
		} catch (Exception e) {
			err.add(workHoursInWeekTB.getName());
		}
		try {
			p.setWorkContinusHours(Integer.parseInt(workContinusHoursTB
					.getText()));
			if (p.getWorkContinusHours() <= 0
					|| (p.getWorkContinusHours() > p.getWorkHoursInWeek())) {
				err.add(workHoursInWeekTB.getName());
				err.add(workContinusHoursTB.getName());
			}
		} catch (Exception e) {
			err.add(workContinusHoursTB.getName());
		}

		p.setMode(0);
		if (flagCanEmptyCB.getValue())
			p.setModeFlags(false, GenFlags.SCHEDULE_CAN_EMPTY);
		if (flagCheckEmpMaxDaysCB.getValue())
			p.setModeFlags(false, GenFlags.CHECK_EMP_MAX_DAYS);
		if (flagMaxContinusHoursCB.getValue())
			p.setModeFlags(false, GenFlags.WEEKEND_AFTER_MAX_HOURS);
		if (flagMaxHoursInWeekCB.getValue())
			p.setModeFlags(false, GenFlags.CHECK_MAX_HOURS_IN_WEEK);
		return err;
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
	 * See: {@link PrefEditForm#setBtnSaveClickHandler(ClickHandler)}
	 * 
	 */
	private ClickHandler buttonSaveClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final Preference p = new Preference();
			clearErrors();
			Set<String> e = validate(p);
			if (e.size() > 0) {
				setErrors(e);
			} else {
				preference = p;
				service.setPreference(preference, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						for (PreferenseUpdater u : updater) {
							try {
								u.updatePreference(p);
							} catch (Exception e2) {
							}
						}
						errLabel.setText("Настройки сохранены");
						Timer t = new Timer() {

							@Override
							public void run() {
								errLabel.setText(" ");
							}
						};
						t.schedule(20000);
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}
				});
			}
		}
	};

	@Override
	public void onClick(ClickEvent event) {
		clearErrors();
		setFormData(preference);
	}

	private void setErrors(Set<String> e) {
		if (e.contains(shiftsNumberTB.getName())) {
			shiftsNumberLabel.addStyleDependentName("error");
			shiftsNumberTB.addStyleDependentName("error");
		}
		if (e.contains(workHoursInDayTB.getName())) {
			workHoursInDayLabel.addStyleDependentName("error");
			workHoursInDayTB.addStyleDependentName("error");
		}
		if (e.contains(workHoursInWeekTB.getName())) {
			workHoursInWeekLabel.addStyleDependentName("error");
			workHoursInWeekTB.addStyleDependentName("error");
		}
		if (e.contains(workContinusHoursTB.getName())) {
			workContinusHoursLabel.addStyleDependentName("error");
			workContinusHoursTB.addStyleDependentName("error");
		}
	}

	private void clearErrors() {
		shiftsNumberLabel.setStyleDependentName("error", false);
		shiftsNumberTB.setStyleDependentName("error", false);
		workHoursInDayLabel.setStyleDependentName("error", false);
		workHoursInDayTB.setStyleDependentName("error", false);
		workHoursInWeekLabel.setStyleDependentName("error", false);
		workHoursInWeekTB.setStyleDependentName("error", false);
		workContinusHoursLabel.setStyleDependentName("error", false);
		workContinusHoursTB.setStyleDependentName("error", false);
	}

	public static boolean registerUpdater(PreferenseUpdater u) {
		if (u != null)
			return updater.add(u);
		return false;
	}

	public static boolean unregisterUpdater(PreferenseUpdater u) {
		if (u != null)
			return updater.remove(u);
		return false;
	}
}
