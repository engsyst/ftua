package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.settings.ScheduleEmployeeNameLabel;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.validator.ClientSideValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.smartgwt.client.util.SC;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StartSettingEntryPoint extends SimplePanel {
	private ArrayList<Club> malibuClubs;
	private ArrayList<Club> clubsOnlyOur;
	private Map<Long, Club> clubsDictionary;
	private HashSet<Club> clubsForDelete;
	private HashSet<Club> clubsForUpdate;
	private HashSet<Club> clubsForInsert;

	private ArrayList<Employee> malibuEmployees;
	private ArrayList<Employee> employeesOnlyOur;
	private Map<Long, Employee> employeesDictionary;
	private Map<Long, Collection<Boolean>> employeeRole;
	private HashSet<Employee> employeesForDelete;
	private HashSet<Employee> employeesForUpdate;
	private HashSet<Employee> employeesForInsert;

	private ArrayList<Employee> allEmployee;
	private ArrayList<Category> categories;
	private Map<Long, Collection<Long>> employeeInCategoriesForDelete;
	private Map<Long, Collection<Long>> employeeInCategoriesForInsert;
	private ArrayList<Category> categoriesForDelete;
	private ArrayList<Category> categoriesForInsert;
	private int selectedCategory = -1;

	private ArrayList<Holiday> holidays;
	private ArrayList<Holiday> holidaysForInsert;
	private ArrayList<Holiday> holidaysForDelete;

	/**
	 * Create a remote service proxy to talk to the server-side StartSetting
	 * service.
	 */
	private final StartSettingServiceAsync startSettingService = GWT
			.create(StartSettingService.class);

	/**
	 * This is the entry point method.
	 */
	public StartSettingEntryPoint() {
		VerticalPanel rootPanel = new VerticalPanel();

		final MyEventDialogBox createObject = new MyEventDialogBox();
		createObject.setAnimationEnabled(true);

		TabPanel tabPanel = new TabPanel();
		rootPanel.add(tabPanel);

		AbsolutePanel absolutePanel = new AbsolutePanel();
		tabPanel.add(absolutePanel, "Клубы", true);

		final HTML html1 = new HTML();
		rootPanel.add(html1);

		final FlexTable flexTable = new FlexTable();
		flexTable.setCellPadding(10);
		flexTable.setStyleName("mainTable");
		flexTable.setBorderWidth(1);

		ClickHandler[] clubButtons = new ClickHandler[2];
		clubButtons[0] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createClubPanel(createObject, flexTable);
				createObject.center();

			}
		};
		clubButtons[1] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final Button saveButton = ((Button) event.getSource());
				saveButton.setEnabled(false);
				Collection<Club> clubsForOnlyOurInsert = new HashSet<Club>();

				int allClubsCount = flexTable.getRowCount() - 2;
				for (int i = 0; i < allClubsCount; i++) {
					if (i < clubsOnlyOur.size()) {
						Club scheduleClub = clubsOnlyOur.get(i);
						if (scheduleClub.getClubId() == 0) {
							scheduleClub.setIndependent(((CheckBox) flexTable
									.getWidget(i + 2, 3)).getValue());
							clubsForOnlyOurInsert.add(scheduleClub);
						} else {
							boolean isChecked = ((CheckBox) flexTable
									.getWidget(i + 2, 3)).getValue();
							if (scheduleClub.isIndependent() != isChecked) {
								scheduleClub.setIndependent(isChecked);
								clubsForUpdate.add(scheduleClub);
							}
						}
					} else {
						Club malibuClub = malibuClubs.get(i
								- clubsOnlyOur.size());
						if (clubsDictionary.containsKey(malibuClub.getClubId())) {
							Club scheduleClub = clubsDictionary.get(malibuClub
									.getClubId());
							boolean isChecked = ((CheckBox) flexTable
									.getWidget(i + 2, 3)).getValue();
							if (scheduleClub.isIndependent() != isChecked) {
								scheduleClub.setIndependent(isChecked);
								clubsForUpdate.add(scheduleClub);
							}
						} else {
							if (clubsForInsert.contains(malibuClub)) {
								for (Club club : clubsForInsert) {
									if (club.equals(malibuClub)) {
										clubsForInsert.remove(club);
										club.setIndependent(((CheckBox) flexTable
												.getWidget(i + 2, 3))
												.getValue());
										clubsForInsert.add(club);
										break;
									}
								}
							}
						}
					}
				}

				startSettingService.setClubs(clubsForInsert,
						clubsForOnlyOurInsert, clubsForUpdate, clubsForDelete,
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								html1.setHTML("Клубы успешно сохранены!");
								loadClubs(flexTable);
								saveButton.setEnabled(true);
								SC.say("Клубы успешно сохранены!");
							}

							@Override
							public void onFailure(Throwable caught) {
								html1.setHTML(caught.getMessage());
								saveButton.setEnabled(true);
							}
						});
			}
		};

		String[] clubButtonsName = new String[2];
		clubButtonsName[0] = "Добавить новый клуб";
		clubButtonsName[1] = "Сохранить";

		absolutePanel.add(new ButtonPanel(clubButtons, clubButtonsName));
		absolutePanel.add(flexTable);
		absolutePanel.add(new ButtonPanel(clubButtons, clubButtonsName));

		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		tabPanel.add(absolutePanel_1, "Сотрудники", false);

		final FlexTable flexTable_1 = new FlexTable();
		flexTable_1.setBorderWidth(1);
		flexTable_1.setCellPadding(10);
		flexTable_1.setStyleName("mainTable");

		tabPanel.selectTab(0);

		ClickHandler[] empButtons = new ClickHandler[3];
		empButtons[0] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createEmployeePanel(createObject, flexTable_1);
				createObject.center();

			}
		};
		empButtons[1] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createUserPanel(createObject);
				createObject.center();

			}
		};
		empButtons[2] = new ClickHandler() {

			private void setRoles(int rowNumber, Employee employee,
					Map<Integer, Collection<Long>> roleForDelete,
					Map<Integer, Collection<Long>> roleForInsert) {
				ArrayList<Boolean> roles = new ArrayList<Boolean>(
						employeeRole.get(employee.getEmployeeId()));
				for (int j = 1; j <= 3; j++) {
					if (((CheckBox) flexTable_1.getWidget(rowNumber + 2, j + 2))
							.getValue() != roles.get(j - 1)) {
						if (roles.get(j - 1)) {
							roleForDelete.get(j).add(employee.getEmployeeId());
						} else {
							roleForInsert.get(j).add(employee.getEmployeeId());
						}
					}
				}
			}

			@Override
			public void onClick(ClickEvent event) {
				final Button saveButton = ((Button) event.getSource());
				saveButton.setEnabled(false);
				Collection<Employee> employeesForOnlyOurInsert = new HashSet<Employee>();

				Map<Integer, Collection<Long>> roleForInsert = new HashMap<Integer, Collection<Long>>();
				roleForInsert.put(1, new ArrayList<Long>());
				roleForInsert.put(2, new ArrayList<Long>());
				roleForInsert.put(3, new ArrayList<Long>());

				Map<Integer, Collection<Long>> roleForDelete = new HashMap<Integer, Collection<Long>>();
				roleForDelete.put(1, new ArrayList<Long>());
				roleForDelete.put(2, new ArrayList<Long>());
				roleForDelete.put(3, new ArrayList<Long>());

				Map<Integer, Collection<Employee>> roleForInsertNew = new HashMap<Integer, Collection<Employee>>();
				roleForInsertNew.put(1, new ArrayList<Employee>());
				roleForInsertNew.put(2, new ArrayList<Employee>());
				roleForInsertNew.put(3, new ArrayList<Employee>());

				Map<Integer, Collection<Employee>> roleForInsertNewWithoutConformity = new HashMap<Integer, Collection<Employee>>();
				roleForInsertNewWithoutConformity.put(1,
						new ArrayList<Employee>());
				roleForInsertNewWithoutConformity.put(2,
						new ArrayList<Employee>());
				roleForInsertNewWithoutConformity.put(3,
						new ArrayList<Employee>());

				int allEmpCount = flexTable_1.getRowCount() - 2;
				for (int i = 0; i < allEmpCount; i++) {
					if (i < employeesOnlyOur.size()) {
						Employee scheduleEmployee = employeesOnlyOur.get(i);
						if (scheduleEmployee.getEmployeeId() == 0) {
							boolean withoutRoles = true;
							for (int j = 1; j <= 3; j++) {
								if (((CheckBox) flexTable_1.getWidget(i + 2,
										j + 2)).getValue()) {
									roleForInsertNewWithoutConformity.get(j)
											.add(employeesOnlyOur.get(i));
									withoutRoles = false;
								}
							}
							if (withoutRoles) {
								employeesForOnlyOurInsert.add(employeesOnlyOur
										.get(i));
							}
						} else {
							setRoles(i, employeesOnlyOur.get(i), roleForDelete,
									roleForInsert);
						}
					} else {
						Employee malibuEmployee = malibuEmployees.get(i
								- employeesOnlyOur.size());
						if (employeesDictionary.containsKey(malibuEmployee
								.getEmployeeId())) {
							Employee scheduleEmployee = employeesDictionary
									.get(malibuEmployee.getEmployeeId());
							setRoles(i, scheduleEmployee, roleForDelete,
									roleForInsert);
						} else {
							if (employeesForInsert.contains(malibuEmployee)) {
								boolean withRoles = false;
								for (int j = 1; j <= 3; j++) {
									if (((CheckBox) flexTable_1.getWidget(
											i + 2, j + 2)).getValue()) {
										roleForInsertNew.get(j).add(
												malibuEmployee);
										withRoles = true;
									}
								}
								if (withRoles) {
									employeesForInsert.remove(malibuEmployee);
								}
							}
						}
					}
				}

				startSettingService.setEmployees(employeesForInsert,
						employeesForOnlyOurInsert, employeesForUpdate,
						employeesForDelete, roleForInsert, roleForDelete,
						roleForInsertNew, roleForInsertNewWithoutConformity,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								saveButton.setEnabled(true);
								html1.setHTML(caught.getMessage());
								SC.warn(caught.getMessage());
							}

							@Override
							public void onSuccess(Void result) {
								html1.setHTML("Сотрудники успешно сохранены!");
								loadEmployees(flexTable_1);
								saveButton.setEnabled(true);
								SC.say("Сотрудники успешно сохранены!");
							}
						});

			}
		};

		String[] empNames = new String[3];
		empNames[0] = "Добавить нового сотрудника";
		empNames[1] = "Добавить пользователя для сотрудника";
		empNames[2] = "Сохранить";

		absolutePanel_1.add(new ButtonPanel(empButtons, empNames));
		absolutePanel_1.add(flexTable_1);
		absolutePanel_1.add(new ButtonPanel(empButtons, empNames));

		loadClubs(flexTable);

		loadEmployees(flexTable_1);

		AbsolutePanel absolutePanel_2 = new AbsolutePanel();
		tabPanel.add(absolutePanel_2, "Категории", false);

		VerticalPanel verticalPanel = new VerticalPanel();
		absolutePanel_2.add(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setSize("100%", "100%");

		final ListBox comboBox = new ListBox();
		horizontalPanel_1.add(comboBox);
		horizontalPanel_1.setCellHorizontalAlignment(comboBox,
				HasHorizontalAlignment.ALIGN_LEFT);
		verticalPanel.setCellHorizontalAlignment(comboBox,
				HasHorizontalAlignment.ALIGN_LEFT);

		HorizontalPanel horizontalPanel = new HorizontalPanel();

		final FlexTable flexTable_3 = new FlexTable();
		flexTable_3.addStyleName("empCategoryTable");
		flexTable_3.addStyleName("mainTable");
		horizontalPanel.add(flexTable_3);
		horizontalPanel.setCellVerticalAlignment(flexTable_3,
				HasVerticalAlignment.ALIGN_TOP);
		Image image = new Image(GWT.getHostPageBaseURL() + "img/import.png");
		horizontalPanel.add(image);
		horizontalPanel.setCellVerticalAlignment(image,
				HasVerticalAlignment.ALIGN_MIDDLE);

		final FlexTable insertedEmployeeInCategoryflexTable = new FlexTable();
		insertedEmployeeInCategoryflexTable.addStyleName("empCategoryTable");
		insertedEmployeeInCategoryflexTable.addStyleName("mainTable");
		horizontalPanel.add(insertedEmployeeInCategoryflexTable);
		horizontalPanel.setCellVerticalAlignment(
				insertedEmployeeInCategoryflexTable,
				HasVerticalAlignment.ALIGN_TOP);

		comboBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = comboBox.getSelectedIndex();
				if (index >= categories.size())
					writeEmployeeInCategory(
							categoriesForInsert.get(index - categories.size()),
							insertedEmployeeInCategoryflexTable, flexTable_3,
							comboBox);
				else
					writeEmployeeInCategory(categories.get(index),
							insertedEmployeeInCategoryflexTable, flexTable_3,
							comboBox);
				selectedCategory = index;

			}
		});

		ClickHandler[] categButtons = new ClickHandler[3];
		categButtons[0] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createCategoryPanel(createObject, comboBox);
				createObject.center();
			}

		};
		categButtons[1] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final Button thisButton = ((Button) event.getSource());
				thisButton.setEnabled(false);
				startSettingService.setCategory(categories,
						employeeInCategoriesForDelete,
						employeeInCategoriesForInsert, categoriesForDelete,
						categoriesForInsert, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								html1.setText("Категории успешно сохранены!");
								loadCategories(comboBox,
										insertedEmployeeInCategoryflexTable,
										flexTable_3);
								thisButton.setEnabled(true);
							}

							@Override
							public void onFailure(Throwable caught) {
								html1.setText(caught.getMessage());

							}
						});
			}
		};
		categButtons[2] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (selectedCategory < categories.size()) {
					categoriesForDelete.add(categories.get(selectedCategory));
					categories.remove(selectedCategory);
				} else {
					categoriesForInsert.remove(selectedCategory
							- categories.size());
				}
				comboBox.removeItem(selectedCategory);
				if (comboBox.getItemCount() == 0) {
					insertedEmployeeInCategoryflexTable.removeAllRows();
					flexTable_3.removeAllRows();
					selectedCategory = -1;
					return;
				}
				comboBox.setSelectedIndex(0);
				if (0 == categories.size())
					writeEmployeeInCategory(categoriesForInsert.get(0),
							insertedEmployeeInCategoryflexTable, flexTable_3,
							comboBox);
				else
					writeEmployeeInCategory(categories.get(0),
							insertedEmployeeInCategoryflexTable, flexTable_3,
							comboBox);
				selectedCategory = 0;
			}

		};

		String[] categNames = new String[3];
		categNames[0] = "Добавить новую категорию";
		categNames[1] = "Сохранить";
		categNames[2] = "Удалить выбранную категорию";

		verticalPanel.add(new ButtonPanel(categButtons, categNames));
		verticalPanel.add(horizontalPanel_1);
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(new ButtonPanel(categButtons, categNames));

		loadCategories(comboBox, insertedEmployeeInCategoryflexTable,
				flexTable_3);

		AbsolutePanel absolutePanel_3 = new AbsolutePanel();
		tabPanel.add(absolutePanel_3, "Выходные", false);
		absolutePanel_3.setSize("", "");

		final FlexTable holidaysFlexTable = new FlexTable();
		holidaysFlexTable.setStyleName("mainTable");
		holidaysFlexTable.setBorderWidth(1);

		ClickHandler[] holidButtons = new ClickHandler[2];
		holidButtons[0] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createHolidayPanel(createObject, holidaysFlexTable);
				createObject.center();
			}
		};
		holidButtons[1] = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final Button thisButton = ((Button) event.getSource());
				thisButton.setEnabled(false);
				startSettingService.setHolidays(holidaysForDelete,
						holidaysForInsert, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								html1.setHTML("Выходные успешно добавлены");
								loadHolidays(holidaysFlexTable);
								thisButton.setEnabled(true);
							}

							@Override
							public void onFailure(Throwable caught) {
								html1.setHTML(caught.getMessage());
								thisButton.setEnabled(true);
							}
						});

			}
		};

		String[] holidNames = new String[2];
		holidNames[0] = "Добавить выходной";
		holidNames[1] = "Сохранить";

		absolutePanel_3.add(new ButtonPanel(holidButtons, holidNames));
		absolutePanel_3.add(holidaysFlexTable);
		absolutePanel_3.add(new ButtonPanel(holidButtons, holidNames));

		loadHolidays(holidaysFlexTable);

		AbsolutePanel absolutePanel_4 = new AbsolutePanel();
		tabPanel.add(absolutePanel_4, "Смены", false);
		absolutePanel_4.add(new PrefTabSimplePanel(startSettingService));

		setWidget(rootPanel);
	}

	private void createHolidayPanel(final MyEventDialogBox createObject,
			final FlexTable flexTable) {
		createObject.clear();
		createObject.setText("Добавление нового выходного");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		Label label = new Label("Дата выходного:");
		final DateBox dateBox = new DateBox();
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getFormat("dd/MM/yyyy")));

		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (dateBox.getValue() == null) {
					errorLabel.setText("Вы заполнили не все поля");
				} else if (dateBox.getValue().before(new Date())) {
					errorLabel
							.setText("Задайте выходной после сегодняшней даты");
				} else {
					Holiday h = new Holiday();
					h.setDate(dateBox.getValue());
					holidaysForInsert.add(h);
					writeHoliday(h, flexTable);
					createObject.hide();
				}
			}
		});

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		delButton.addStyleName("rightDown");

		table.insertRow(0);
		table.insertCell(0, 0);
		table.setWidget(0, 0, label);
		table.insertCell(0, 1);
		table.setWidget(0, 1, dateBox);

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

		createObject.setOkButton(addButton);
		createObject.add(absPanel);

	}

	private void writeHoliday(Holiday h, final FlexTable flexTable) {
		int rowCount = flexTable.getRowCount();
		flexTable.insertRow(rowCount);
		flexTable.insertCell(rowCount, 0);
		flexTable.insertCell(rowCount, 1);
		flexTable.setText(rowCount, 0, DateTimeFormat.getFormat("dd.MM.yyyy")
				.format(h.getDate()));
		Button btDel = new Button();
		btDel.setStyleName("buttonDelete");
		btDel.setWidth("30px");
		btDel.setHeight("30px");
		btDel.setTitle(String.valueOf(rowCount));
		btDel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int index = Integer.parseInt(event.getRelativeElement()
						.getTitle());
				deleteHoliday(flexTable, index);

			}
		});
		flexTable.setWidget(rowCount, 1, btDel);
	}

	private void deleteHoliday(FlexTable flexTable, int index) {
		if (index >= holidays.size() + 1)
			holidaysForInsert.remove(index - holidays.size() - 1);
		else {
			holidaysForDelete.add(holidays.get(index - 1));
			holidays.remove(index - 1);
		}
		flexTable.removeRow(index);
		for (int i = index; i < flexTable.getRowCount(); i++) {
			flexTable.getWidget(i, 1).setTitle(String.valueOf(i));
		}
	}

	private void createUserPanel(final MyEventDialogBox createObject) {
		createObject.clear();
		createObject.setText("Добавление нового пользователя");
		final AbsolutePanel absPanel = new AbsolutePanel();
		final ListBox comboBox = new ListBox();

		startSettingService
				.getEmployeeWithoutUser(new AsyncCallback<Collection<Long>>() {

					@Override
					public void onSuccess(Collection<Long> result) {
						createObject.add(absPanel);
						for (Employee e : allEmployee) {
							if (result.contains(e.getEmployeeId()))
								comboBox.insertItem(e.getNameForSchedule(),
										String.valueOf(e.getEmployeeId()),
										comboBox.getItemCount());
						}
						comboBox.setSelectedIndex(-1);
					}

					@Override
					public void onFailure(Throwable caught) {
						createObject.add(new Label(caught.getMessage()));
					}
				});

		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Логин:"));
		labelsNotNull.add(new Label("Пароль:"));
		labelsNotNull.add(new Label("Повторите пароль:"));

		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new TextBox());
		textBoxs.add(new PasswordTextBox());
		textBoxs.add(new PasswordTextBox());
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		comboBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				for (Widget w : textBoxs)
					((TextBox) w).setValue("");
			}
		});

		final Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		final Button addButton = new Button("Добавить");
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ClientSideValidator validator = new ClientSideValidator();
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
					((PasswordTextBox) (textBoxs.get(1))).setValue("");
					((PasswordTextBox) (textBoxs.get(2))).setValue("");
				} else if (!((PasswordTextBox) (textBoxs.get(1))).getValue()
						.equals(((PasswordTextBox) (textBoxs.get(2)))
								.getValue())) {
					errorLabel.setText("Пароли не совпадают");
					((PasswordTextBox) (textBoxs.get(1))).setValue("");
					((PasswordTextBox) (textBoxs.get(2))).setValue("");
				} else if (!validator.validateSigninData(
						((TextBox) textBoxs.get(0)).getValue(),
						((PasswordTextBox) textBoxs.get(1)).getValue())
						.isEmpty()) {
					String s = "";
					Map<String, String> maps = validator.validateSigninData(
							((TextBox) textBoxs.get(0)).getValue(),
							((PasswordTextBox) textBoxs.get(1)).getValue());
					for (String key : maps.keySet()) {
						s += maps.get(key) + "\n";
					}
					errorLabel.setText(s);
				} else {
					addButton.setEnabled(false);
					delButton.setEnabled(false);
					User user = new User();
					user.setLogin(((TextBox) textBoxs.get(0)).getValue());
					user.setPassword(((PasswordTextBox) textBoxs.get(1))
							.getValue());
					user.setEmployeeId(Integer.parseInt(comboBox
							.getValue(comboBox.getSelectedIndex())));
					startSettingService.setUser(user,
							new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									addButton.setEnabled(true);
									delButton.setEnabled(true);
									createObject.hide();
								}

								@Override
								public void onFailure(Throwable caught) {
									errorLabel.setText(caught.getMessage());
									addButton.setEnabled(true);
									delButton.setEnabled(true);
								}
							});
				}
			}
		});

		delButton.addStyleName("rightDown");

		for (int i = 0; i < labelsNotNull.size(); i++) {
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}

		absPanel.add(comboBox);
		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

	}

	private void createCategoryPanel(final MyEventDialogBox createObject,
			final ListBox comboBox) {
		createObject.clear();
		createObject.setText("Добавление новой категории");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Label title = new Label("Название категории:");
		final TextBox titleTextBox = new TextBox();

		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (titleTextBox.getText() == null
						|| titleTextBox.getText().isEmpty()) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					Category c = new Category();
					c.setTitle(titleTextBox.getText());
					c.setEmployeeIdList(new ArrayList<Long>());
					categoriesForInsert.add(c);
					comboBox.addItem(c.getTitle());
					createObject.hide();
				}
			}
		});

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		delButton.addStyleName("rightDown");

		table.insertRow(0);
		table.insertCell(0, 0);
		table.setWidget(0, 0, title);
		table.insertCell(0, 1);
		table.setWidget(0, 1, titleTextBox);

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

		createObject.setOkButton(addButton);
		createObject.add(absPanel);
	}

	private void writeEmployeeInCategory(Category category,
			FlexTable flexTable, FlexTable flexTable_1, ListBox cmbox) {
		cmbox.removeFromParent();
		flexTable.removeAllRows();
		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.getCellFormatter().addStyleName(0, 0, "secondHeader");
		flexTable.getCellFormatter().addStyleName(0, 0, "mainHeader");

		flexTable.setWidget(0, 0, cmbox);
		flexTable_1.removeAllRows();
		flexTable_1.insertRow(0);
		flexTable_1.insertCell(0, 0);
		flexTable_1.setText(0, 0, "Сотрудники");
		flexTable_1.getCellFormatter().addStyleName(0, 0, "secondHeader");
		flexTable_1.getCellFormatter().addStyleName(0, 0, "mainHeader");
		for (Employee e : allEmployee) {
			boolean added = false;
			for (Long id : category.getEmployeeIdList()) {
				if (e.getEmployeeId() == id) {
					added = true;
					writeCellEmployeeForCategory(e, flexTable, flexTable_1,
							true);
					break;
				}
			}
			if (!added)
				writeCellEmployeeForCategory(e, flexTable_1, flexTable, false);
		}
	}

	private void writeCellEmployeeForCategory(final Employee e,
			final FlexTable flexTable, final FlexTable flexTableNew,
			final boolean added) {
		int indexRow = flexTable.getRowCount();
		flexTable.insertRow(indexRow);
		flexTable.insertCell(indexRow, 0);
		Button bt = new Button();
		bt.setText(e.getNameForSchedule());
		bt.setStyleName("empCategoryButton");
		bt.setTitle(String.valueOf(indexRow));

		if (added) {
			bt.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int index = Integer.parseInt(event.getRelativeElement()
							.getTitle());
					flexTable.removeRow(index);
					for (; index < flexTable.getRowCount(); index++)
						flexTable.getWidget(index, 0).setTitle(
								String.valueOf(index));
					writeCellEmployeeForCategory(e, flexTableNew, flexTable,
							!added);
					Category c = null;
					boolean newCate = selectedCategory >= categories.size();
					if (newCate)
						c = categoriesForInsert.get(selectedCategory
								- categories.size());
					else {
						c = categories.get(selectedCategory);

						if (!employeeInCategoriesForDelete.containsKey(c
								.getCategoryId()))
							employeeInCategoriesForDelete.put(
									c.getCategoryId(), new ArrayList<Long>());

						if (employeeInCategoriesForInsert.containsKey(c
								.getCategoryId())
								&& employeeInCategoriesForInsert.get(
										c.getCategoryId()).contains(
										e.getEmployeeId()))
							employeeInCategoriesForInsert
									.get(c.getCategoryId()).remove(
											e.getEmployeeId());
						else
							employeeInCategoriesForDelete
									.get(c.getCategoryId()).add(
											e.getEmployeeId());
					}
					c.getEmployeeIdList().remove(e.getEmployeeId());
				}
			});
		} else {
			bt.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int index = Integer.parseInt(event.getRelativeElement()
							.getTitle());
					flexTable.removeRow(index);
					for (; index < flexTable.getRowCount(); index++)
						flexTable.getWidget(index, 0).setTitle(
								String.valueOf(index));
					writeCellEmployeeForCategory(e, flexTableNew, flexTable,
							!added);
					Category c = null;
					boolean newCate = selectedCategory >= categories.size();
					if (newCate)
						c = categoriesForInsert.get(selectedCategory
								- categories.size());
					else {
						c = categories.get(selectedCategory);

						if (!employeeInCategoriesForInsert.containsKey(c
								.getCategoryId()))
							employeeInCategoriesForInsert.put(
									c.getCategoryId(), new ArrayList<Long>());

						employeeInCategoriesForInsert.get(c.getCategoryId())
								.add(e.getEmployeeId());
					}
					c.getEmployeeIdList().add(e.getEmployeeId());
				}
			});
		}

		flexTable.setWidget(indexRow, 0, bt);
	}

	private void loadHolidays(final FlexTable flexTable) {
		flexTable.removeAllRows();
		holidays = new ArrayList<Holiday>();
		holidaysForDelete = new ArrayList<Holiday>();
		holidaysForInsert = new ArrayList<Holiday>();

		startSettingService
				.getHolidays(new AsyncCallback<Collection<Holiday>>() {

					@Override
					public void onSuccess(Collection<Holiday> result) {
						holidays = new ArrayList<Holiday>(result);
						flexTable.insertRow(0);
						flexTable.insertCell(0, 0);
						flexTable.setText(0, 0, "Выходной");
						flexTable.getFlexCellFormatter().addStyleName(0, 0,
								"secondHeader");
						flexTable.insertCell(0, 1);
						flexTable.setText(0, 1, "Удалить");
						flexTable.getFlexCellFormatter().addStyleName(0, 1,
								"secondHeader");
						for (Holiday h : holidays) {
							if (h.getDate().before(new Date())) {
								int rowCount = flexTable.getRowCount();
								flexTable.insertRow(rowCount);
								flexTable.insertCell(rowCount, 0);
								flexTable.insertCell(rowCount, 1);
								flexTable.setText(rowCount, 0, h.getDate()
										.toString());
							} else {
								writeHoliday(h, flexTable);
							}
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						flexTable.insertRow(0);
						flexTable.insertCell(0, 0);
						flexTable.setText(0, 0, caught.getMessage());
					}
				});
	}

	private void loadCategories(final ListBox comboBox,
			final FlexTable flexTable, final FlexTable flexTable_1) {
		categories = new ArrayList<Category>();
		selectedCategory = -1;
		employeeInCategoriesForDelete = new HashMap<Long, Collection<Long>>();
		employeeInCategoriesForInsert = new HashMap<Long, Collection<Long>>();
		categoriesForDelete = new ArrayList<Category>();
		categoriesForInsert = new ArrayList<Category>();

		flexTable.removeAllRows();
		// flexTable.insertRow(0);
		flexTable_1.removeAllRows();
		flexTable_1.insertRow(0);
		flexTable_1.setWidget(0, 0, comboBox);
		comboBox.clear();
		startSettingService
				.getCategoriesWithEmployees(new AsyncCallback<Collection<Category>>() {

					@Override
					public void onSuccess(
							final Collection<Category> categoriesResult) {
						startSettingService
								.getCategoriesDictionary(new AsyncCallback<Map<Long, Collection<Employee>>>() {

									@Override
									public void onSuccess(
											Map<Long, Collection<Employee>> result) {
										categories = new ArrayList<Category>(
												categoriesResult);
										for (Category c : categories)
											comboBox.addItem(c.getTitle());
										// if(categories.size()!=0){
										comboBox.setSelectedIndex(-1);
										// writeEmployeeInCategory(categories.get(0),
										// flexTable, flexTable_1);
										selectedCategory = -1;
										// }
									}

									@Override
									public void onFailure(Throwable caught) {
										comboBox.getElement()
												.getParentElement()
												.setInnerHTML(
														caught.getMessage());

									}
								});

					}

					@Override
					public void onFailure(Throwable caught) {
						comboBox.getElement().getParentElement()
								.setInnerHTML(caught.getMessage());

					}
				});

	}

	private void loadEmployees(final FlexTable flexTable) {
		flexTable.removeAllRows();
		employeesOnlyOur = new ArrayList<Employee>();
		employeesDictionary = new HashMap<Long, Employee>();
		employeesForDelete = new HashSet<Employee>();
		employeesForUpdate = new HashSet<Employee>();
		employeesForInsert = new HashSet<Employee>();
		allEmployee = new ArrayList<Employee>();
		startSettingService
				.getMalibuEmployees(new AsyncCallback<Collection<Employee>>() {
					public void onFailure(Throwable caught) {
						flexTable.insertRow(0);
						flexTable.insertCell(0, 0);
						flexTable.setText(0, 0, caught.getMessage());

					}

					public void onSuccess(
							final Collection<Employee> malibuEmployees) {
						startSettingService
								.getDictionaryEmployee(new AsyncCallback<Map<Long, Employee>>() {

									@Override
									public void onSuccess(
											final Map<Long, Employee> dictionaryEmployee) {
										startSettingService
												.getRoleEmployee(new AsyncCallback<Map<Long, Collection<Boolean>>>() {

													@Override
													public void onSuccess(
															final Map<Long, Collection<Boolean>> roles) {
														startSettingService
																.getOnlyOurEmployees(new AsyncCallback<Collection<Employee>>() {

																	@Override
																	public void onSuccess(
																			final Collection<Employee> ourEmployee) {

																		startSettingService
																				.getAllEmployees(new AsyncCallback<Collection<Employee>>() {

																					@Override
																					public void onSuccess(
																							Collection<Employee> allEmp) {
																						allEmployee = new ArrayList<Employee>(
																								allEmp);
																						StartSettingEntryPoint.this.malibuEmployees = new ArrayList<Employee>(
																								malibuEmployees);
																						employeesDictionary = dictionaryEmployee;
																						employeeRole = roles;
																						createEmployeeTableHeader(flexTable);
																						employeesOnlyOur = new ArrayList<Employee>(
																								ourEmployee);
																						writeOnlyScheduleEmployees(
																								flexTable,
																								employeesOnlyOur);
																						writeMalibuEmployees(
																								flexTable,
																								StartSettingEntryPoint.this.malibuEmployees);
																					}

																					@Override
																					public void onFailure(
																							Throwable caught) {
																						flexTable
																								.insertRow(0);
																						flexTable
																								.insertCell(
																										0,
																										0);
																						flexTable
																								.setText(
																										0,
																										0,
																										caught.getMessage());
																					}
																				});
																	}

																	@Override
																	public void onFailure(
																			Throwable caught) {
																		flexTable
																				.insertRow(0);
																		flexTable
																				.insertCell(
																						0,
																						0);
																		flexTable
																				.setText(
																						0,
																						0,
																						caught.getMessage());
																	}
																});

													}

													@Override
													public void onFailure(
															Throwable caught) {
														flexTable.insertRow(0);
														flexTable.insertCell(0,
																0);
														flexTable
																.setText(
																		0,
																		0,
																		caught.getMessage());
													}
												});

									}

									@Override
									public void onFailure(Throwable caught) {
										flexTable.insertRow(0);
										flexTable.insertCell(0, 0);
										flexTable.setText(0, 0,
												caught.getMessage());
									}
								});
					}
				});
	}

	private void loadClubs(final FlexTable flexTable) {
		flexTable.removeAllRows();
		clubsOnlyOur = new ArrayList<Club>();
		clubsDictionary = new HashMap<Long, Club>();
		clubsForDelete = new HashSet<Club>();
		clubsForUpdate = new HashSet<Club>();
		clubsForInsert = new HashSet<Club>();

		startSettingService
				.getMalibuClubs(new AsyncCallback<Collection<Club>>() {

					public void onFailure(Throwable caught) {
						flexTable.insertRow(0);
						flexTable.insertCell(0, 0);
						flexTable.setText(0, 0, caught.getMessage());
					}

					public void onSuccess(final Collection<Club> malibuClubs) {
						startSettingService
								.getDictionaryClub(new AsyncCallback<Map<Long, Club>>() {

									@Override
									public void onSuccess(
											final Map<Long, Club> resultDictionary) {
										startSettingService
												.getOnlyOurClubs(new AsyncCallback<Collection<Club>>() {

													@Override
													public void onSuccess(
															Collection<Club> ourClub) {
														StartSettingEntryPoint.this.malibuClubs = new ArrayList<Club>(
																malibuClubs);
														clubsDictionary = resultDictionary;
														createClubTableHeader(flexTable);
														clubsOnlyOur = new ArrayList<Club>(
																ourClub);
														writeOnlyScheduleClubs(
																flexTable,
																clubsOnlyOur);
														writeMalibuClubs(
																flexTable,
																StartSettingEntryPoint.this.malibuClubs);
													}

													@Override
													public void onFailure(
															Throwable caught) {
														flexTable.insertRow(0);
														flexTable.insertCell(0,
																0);
														flexTable
																.setText(
																		0,
																		0,
																		caught.getMessage());

													}
												});

									}

									@Override
									public void onFailure(Throwable caught) {
										flexTable.insertRow(0);
										flexTable.insertCell(0, 0);
										flexTable.setText(0, 0,
												caught.getMessage());
									}
								});

					}
				});
	}

	private void importAllMalibuClubs(FlexTable flexTable) {
		int numberOfRows = clubsOnlyOur.size();
		numberOfRows += 2;
		if (malibuClubs != null) {
			for (int i = 0; i < malibuClubs.size(); i++) {
				importClub(flexTable, numberOfRows + i);
			}
		}
	}

	private void importClub(final FlexTable flexTable, int rowNumber) {
		Club malibuClub = malibuClubs.get(rowNumber - clubsOnlyOur.size() - 2);
		if (clubsDictionary.containsKey(malibuClub.getClubId())) {
			Club dictionaryClub = clubsDictionary.get(malibuClub.getClubId());
			if (dictionaryClub.getTitle() != malibuClub.getTitle()) {
				dictionaryClub.setTitle(malibuClub.getTitle());
				clubsForUpdate.add(dictionaryClub);
				flexTable.setText(rowNumber, 2, dictionaryClub.getTitle());
			}
		} else {
			clubsForInsert.add(malibuClub);
			writeScheduleClub(flexTable, malibuClub, rowNumber);
		}
	}

	private void writeNewScheduleClub(FlexTable flexTable, Club club) {
		int rowNumber = clubsOnlyOur.size() + 1;
		flexTable.insertRow(rowNumber);
		for (int i = 0; i < 5; i++) {
			flexTable.insertCell(rowNumber, i);
		}
		writeScheduleClub(flexTable, club, rowNumber);
		changeRowNumbersForClubs(flexTable, rowNumber);
	}

	private void writeClub(FlexTable flexTable, Club club) {
		int index = flexTable.getRowCount();
		flexTable.insertRow(index);
		for (int i = 0; i < 5; i++) {
			flexTable.insertCell(index, i);
		}
		writeScheduleClub(flexTable, club, index);
	}

	private void writeScheduleClub(final FlexTable flexTable, Club club,
			int rowNumber) {
		if (flexTable.getWidget(rowNumber, 3) != null) {
			return;
		}
		flexTable.setText(rowNumber, 2, club.getTitle());
		CheckBox widget = new CheckBox();
		widget.setWidth("40px");
		widget.setHeight("40px");
		widget.setStyleName("checkbox");
		widget.setValue(club.isIndependent());
		flexTable.setWidget(rowNumber, 3, widget);

		if (!club.isDeleted()) {
			UtilButton scheduleClubRemovingButton = new UtilButton(rowNumber);
			scheduleClubRemovingButton.setStyleName("buttonDelete");
			scheduleClubRemovingButton.setWidth("30px");
			scheduleClubRemovingButton.setHeight("30px");

			scheduleClubRemovingButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					UtilButton button = (UtilButton) event.getSource();
					removeClub(flexTable, button.getRowNumber());
				}
			});
			flexTable.setWidget(rowNumber, 4, scheduleClubRemovingButton);
		}
	}

	private void removeAllScheduleClubs(FlexTable flexTable) {
		int rowCount = flexTable.getRowCount();
		int malibuClubCount = malibuClubs.size();
		while (malibuClubCount != 0) {
			removeClub(flexTable, rowCount - 1);
			malibuClubCount--;
			rowCount--;
		}
		int scheduleClubCount = clubsOnlyOur.size();
		while (scheduleClubCount != 0) {
			removeClub(flexTable, rowCount - 1);
			scheduleClubCount--;
			rowCount--;
		}
	}

	private void removeClub(final FlexTable flexTable, int rowNumber) {
		if (rowNumber - 2 < clubsOnlyOur.size()) {
			Club removedClub = clubsOnlyOur.remove(rowNumber - 2);
			if (removedClub != null) {
				if (removedClub.getClubId() == 0) {
					Iterator<Club> it = clubsForInsert.iterator();
					while (it.hasNext()) {
						Club club = it.next();
						if (club.equals(removedClub)) {
							it.remove();
							break;
						}
					}
				} else {
					clubsForDelete.add(removedClub);
				}
			}
			flexTable.removeRow(rowNumber);
			changeRowNumbersForClubs(flexTable, rowNumber);
			return;
		} else {
			Club malibuClub = malibuClubs.get(rowNumber - clubsOnlyOur.size()
					- 2);
			if (clubsDictionary.containsKey(malibuClub.getClubId())) {
				Club club = clubsDictionary.remove(malibuClub.getClubId());
				clubsForDelete.add(club);
				clubsForUpdate.remove(club);
			} else {
				clubsForInsert.remove(malibuClub);
			}
		}
		flexTable.setText(rowNumber, 2, "");
		flexTable.setText(rowNumber, 3, "");
		flexTable.setText(rowNumber, 4, "");
	}

	private void createClubTableHeader(final FlexTable flexTable) {
		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.setText(0, 0, "Клубы");
		flexTable.getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Импорт");
		flexTable.getFlexCellFormatter().addStyleName(0, 1, "mainHeader");
		flexTable.insertCell(0, 2);
		flexTable.setText(0, 2, "Клубы в графике");
		flexTable.getFlexCellFormatter().setColSpan(0, 2, 2);
		flexTable.getFlexCellFormatter().addStyleName(0, 2, "mainHeader");
		flexTable.insertCell(0, 3);
		flexTable.setText(0, 3, "Удалить");
		flexTable.getFlexCellFormatter().addStyleName(0, 3, "mainHeader");

		flexTable.insertRow(1);
		flexTable.insertCell(1, 0);
		flexTable.setText(1, 0, "Название");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "import");
		flexTable.insertCell(1, 1);
		Button allMalibuClubsImportingButton = new Button();
		allMalibuClubsImportingButton.setWidth("75px");
		allMalibuClubsImportingButton.setHeight("30px");
		allMalibuClubsImportingButton.setStyleName("buttonImport");
		allMalibuClubsImportingButton.setTitle("Импорт всех клубов");
		allMalibuClubsImportingButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				importAllMalibuClubs(flexTable);
			}
		});

		flexTable.setWidget(1, 1, allMalibuClubsImportingButton);
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "import");
		flexTable.insertCell(1, 2);
		flexTable.setText(1, 2, "Название");
		flexTable.getFlexCellFormatter().addStyleName(1, 2, "secondHeader");
		flexTable.insertCell(1, 3);
		flexTable.setText(1, 3, "Независимый");
		flexTable.getFlexCellFormatter().addStyleName(1, 3, "secondHeader");
		flexTable.insertCell(1, 4);
		Button allScheduleClubsRemovingButton = new Button();
		allScheduleClubsRemovingButton.setStyleName("buttonDelete");
		allScheduleClubsRemovingButton.setWidth("30px");
		allScheduleClubsRemovingButton.setHeight("30px");
		allScheduleClubsRemovingButton.setTitle("Удалить все клубы");
		allScheduleClubsRemovingButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				removeAllScheduleClubs(flexTable);
			}
		});

		flexTable.setWidget(1, 4, allScheduleClubsRemovingButton);
		flexTable.getFlexCellFormatter().addStyleName(1, 4, "secondHeader");
	}

	private void writeOnlyScheduleClubs(FlexTable flexTable,
			Collection<Club> onlyScheduleClubs) {
		for (Club elem : onlyScheduleClubs) {
			writeClub(flexTable, elem);
		}
	}

	private void writeMalibuClubs(final FlexTable flexTable,
			Collection<Club> malibuClubs) {
		int rowNumber = flexTable.getRowCount();
		for (Club malibuClub : malibuClubs) {
			flexTable.insertRow(rowNumber);
			flexTable.insertCell(rowNumber, 0);
			flexTable.setText(rowNumber, 0, malibuClub.getTitle());
			flexTable.getFlexCellFormatter().addStyleName(rowNumber, 0,
					"import");
			flexTable.insertCell(rowNumber, 1);

			UtilButton malibuClubImportingButton = new UtilButton(rowNumber);
			malibuClubImportingButton.setStyleName("buttonImport");
			malibuClubImportingButton.setWidth("75px");
			malibuClubImportingButton.setHeight("30px");
			malibuClubImportingButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					UtilButton button = (UtilButton) event.getSource();
					importClub(flexTable, button.getRowNumber());
				}
			});

			flexTable.setWidget(rowNumber, 1, malibuClubImportingButton);
			flexTable.getFlexCellFormatter().addStyleName(rowNumber, 1,
					"mainHeader");
			flexTable.insertCell(rowNumber, 2);
			flexTable.insertCell(rowNumber, 3);
			flexTable.insertCell(rowNumber, 4);
			if (clubsDictionary.containsKey(malibuClub.getClubId())) {
				writeScheduleClub(flexTable,
						clubsDictionary.get(malibuClub.getClubId()), rowNumber);
			}
			rowNumber++;
		}
	}

	private void createEmployeeTableHeader(final FlexTable flexTable) {
		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.setText(0, 0, "Сотрудники");
		flexTable.getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Импорт");
		flexTable.getFlexCellFormatter().addStyleName(0, 1, "mainHeader");
		flexTable.insertCell(0, 2);
		flexTable.setText(0, 2, "Сотрудники в графике");
		flexTable.getFlexCellFormatter().setColSpan(0, 2, 4);
		flexTable.getFlexCellFormatter().addStyleName(0, 2, "mainHeader");
		flexTable.insertCell(0, 3);
		flexTable.setText(0, 3, "Удалить");
		flexTable.getFlexCellFormatter().addStyleName(0, 3, "mainHeader");

		flexTable.insertRow(1);
		flexTable.insertCell(1, 0);
		flexTable.setText(1, 0, "ФИО");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "mainHeader");
		flexTable.insertCell(1, 1);
		Button allMalibuEmployeesImportingButton = new Button();
		allMalibuEmployeesImportingButton.setWidth("75px");
		allMalibuEmployeesImportingButton.setHeight("30px");
		allMalibuEmployeesImportingButton.setStyleName("buttonImport");
		allMalibuEmployeesImportingButton.setTitle("Импорт всех сотрудников");
		allMalibuEmployeesImportingButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				importAllMalibuEmployees(flexTable);
			}
		});

		flexTable.setWidget(1, 1, allMalibuEmployeesImportingButton);
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "mainHeader");
		flexTable.insertCell(1, 2);
		flexTable.setText(1, 2, "ФИО");
		flexTable.getFlexCellFormatter().addStyleName(1, 2, "secondHeader");
		flexTable.insertCell(1, 3);
		flexTable.setText(1, 3, "Администратор");
		flexTable.getFlexCellFormatter().addStyleName(1, 3, "secondHeader");
		flexTable.insertCell(1, 4);
		flexTable.setWidget(1, 4, new HTML("Ответственное<br/> лицо"));
		flexTable.getFlexCellFormatter().addStyleName(1, 4, "secondHeader");
		flexTable.insertCell(1, 5);
		flexTable.setWidget(1, 5, new HTML("Подписаться<br/> на рассылку"));
		flexTable.getFlexCellFormatter().addStyleName(1, 5, "secondHeader");
		flexTable.insertCell(1, 6);
		Button allSchEmployeesRemovingButton = new Button();
		allSchEmployeesRemovingButton.setStyleName("buttonDelete");
		allSchEmployeesRemovingButton.setWidth("30px");
		allSchEmployeesRemovingButton.setHeight("30px");
		allSchEmployeesRemovingButton.setTitle("Удалить всех сотрудников");
		allSchEmployeesRemovingButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				removeAllScheduleEmployees(flexTable);
			}
		});

		flexTable.setWidget(1, 6, allSchEmployeesRemovingButton);
		flexTable.getFlexCellFormatter().addStyleName(1, 6, "secondHeader");
	}

	private void writeOnlyScheduleEmployees(FlexTable flexTable,
			Collection<Employee> onlyScheduleEmployees) {
		for (Employee elem : onlyScheduleEmployees) {
			writeEmployee(flexTable, elem);
			setRoleForEmployee(flexTable, elem, flexTable.getRowCount() - 1);
		}
	}

	private void writeMalibuEmployees(final FlexTable flexTable,
			Collection<Employee> malibuEmployees) {
		int rowNumber = flexTable.getRowCount();
		for (Employee malibuEmployee : malibuEmployees) {
			flexTable.insertRow(rowNumber);
			flexTable.insertCell(rowNumber, 0);
			flexTable
					.setText(rowNumber, 0, malibuEmployee.getNameForSchedule());
			flexTable.getFlexCellFormatter().addStyleName(rowNumber, 0,
					"import");
			flexTable.insertCell(rowNumber, 1);
			UtilButton malibuEmployeeImportingButton = new UtilButton(rowNumber);
			malibuEmployeeImportingButton.setStyleName("buttonImport");
			malibuEmployeeImportingButton.setWidth("75px");
			malibuEmployeeImportingButton.setHeight("30px");
			malibuEmployeeImportingButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					UtilButton button = (UtilButton) event.getSource();
					importEmployee(flexTable, button.getRowNumber());
				}
			});

			flexTable.setWidget(rowNumber, 1, malibuEmployeeImportingButton);
			flexTable.getFlexCellFormatter().addStyleName(rowNumber, 1,
					"mainHeader");
			flexTable.insertCell(rowNumber, 2);
			flexTable.insertCell(rowNumber, 3);
			flexTable.insertCell(rowNumber, 4);
			flexTable.insertCell(rowNumber, 5);
			flexTable.insertCell(rowNumber, 6);
			if (employeesDictionary.containsKey(malibuEmployee.getEmployeeId())) {
				writeScheduleEmployee(
						flexTable,
						employeesDictionary.get(malibuEmployee.getEmployeeId()),
						rowNumber, true);
				setRoleForEmployee(
						flexTable,
						employeesDictionary.get(malibuEmployee.getEmployeeId()),
						rowNumber);
			}
			rowNumber++;
		}
	}

	private void setRoleForEmployee(FlexTable flexTable, Employee employee,
			int rowNumber) {
		if (employeeRole.containsKey(employee.getEmployeeId())) {
			int i = 3;
			for (boolean check : employeeRole.get(employee.getEmployeeId())) {
				((CheckBox) flexTable.getWidget(rowNumber, i)).setValue(check);
				i++;
			}
		}
	}

	private void writeNewScheduleEmployee(FlexTable flexTable, Employee employee) {
		int rowNumber = employeesOnlyOur.size() + 1;
		flexTable.insertRow(rowNumber);
		for (int i = 0; i <= 6; i++) {
			flexTable.insertCell(rowNumber, i);
		}
		writeScheduleEmployee(flexTable, employee, rowNumber, true);
		changeRowNumbersForEmployees(flexTable, rowNumber);
	}

	private void writeEmployee(FlexTable flexTable, Employee employee) {
		int index = flexTable.getRowCount();
		flexTable.insertRow(index);
		for (int i = 0; i <= 6; i++) {
			flexTable.insertCell(index, i);
		}
		writeScheduleEmployee(flexTable, employee, index, true);
	}

	private void writeScheduleEmployee(final FlexTable flexTable,
			Employee employee, int rowNumber, boolean isScheduleEmployee) {
		if (flexTable.getWidget(rowNumber, 3) != null) {
			return;
		}
		Label employeeNameLabel;
		if (!isScheduleEmployee) {
			employeeNameLabel = new Label(employee.getNameForSchedule());
		} else {
			employeeNameLabel = new ScheduleEmployeeNameLabel(
					employee.getNameForSchedule(), employee.getEmployeeId());
		}
		flexTable.setWidget(rowNumber, 2, employeeNameLabel);

		CheckBox widget = new CheckBox();
		widget.setWidth("40px");
		widget.setHeight("40px");
		widget.setStyleName("checkbox");
		widget.setValue(false);
		CheckBox widget1 = new CheckBox();
		widget1.setWidth("40px");
		widget1.setHeight("40px");
		widget1.setStyleName("checkbox");
		widget1.setValue(false);
		CheckBox widget2 = new CheckBox();
		widget2.setWidth("40px");
		widget2.setHeight("40px");
		widget2.setStyleName("checkbox");
		widget2.setValue(true);
		flexTable.setWidget(rowNumber, 3, widget);
		flexTable.setWidget(rowNumber, 4, widget1);
		flexTable.setWidget(rowNumber, 5, widget2);

		if (!employee.isDeleted()) {
			UtilButton scheduleEmployeeRemovingButton = new UtilButton(
					rowNumber);
			scheduleEmployeeRemovingButton.setStyleName("buttonDelete");
			scheduleEmployeeRemovingButton.setWidth("30px");
			scheduleEmployeeRemovingButton.setHeight("30px");
			scheduleEmployeeRemovingButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					UtilButton button = (UtilButton) event.getSource();
					removeEmployee(flexTable, button.getRowNumber());
				}
			});
			flexTable.setWidget(rowNumber, 6, scheduleEmployeeRemovingButton);
		}
	}

	private void importAllMalibuEmployees(FlexTable flexTable) {
		int numberOfRows = employeesOnlyOur.size();
		numberOfRows += 2;
		if (malibuEmployees != null) {
			for (int i = 0; i < malibuEmployees.size(); i++) {
				importEmployee(flexTable, numberOfRows + i);
			}
		}
	}

	private void importEmployee(FlexTable flexTable, int rowNumber) {
		Employee malibuEmployee = malibuEmployees.get(rowNumber
				- employeesOnlyOur.size() - 2);
		if (employeesDictionary.containsKey(malibuEmployee.getEmployeeId())) {
			Employee dictionaryEmployee = employeesDictionary
					.get(malibuEmployee.getEmployeeId());
			if (!dictionaryEmployee.equals(malibuEmployee)) {
				dictionaryEmployee.setLastName(malibuEmployee.getLastName());
				dictionaryEmployee.setFirstName(malibuEmployee.getFirstName());
				dictionaryEmployee
						.setSecondName(malibuEmployee.getSecondName());
				dictionaryEmployee.setBirthday(malibuEmployee.getBirthday());
				dictionaryEmployee.setAddress(malibuEmployee.getAddress());
				dictionaryEmployee.setPassportNumber(malibuEmployee
						.getPassportNumber());
				dictionaryEmployee.setIdNumber(malibuEmployee.getIdNumber());
				dictionaryEmployee.setCellPhone(malibuEmployee.getCellPhone());
				dictionaryEmployee.setWorkPhone(malibuEmployee.getWorkPhone());
				dictionaryEmployee.setHomePhone(malibuEmployee.getHomePhone());
				dictionaryEmployee.setEmail(malibuEmployee.getEmail());
				dictionaryEmployee.setEducation(malibuEmployee.getEducation());
				dictionaryEmployee.setNotes(malibuEmployee.getNotes());
				dictionaryEmployee.setPassportIssuedBy(malibuEmployee
						.getPassportIssuedBy());
				employeesForUpdate.add(dictionaryEmployee);
				flexTable.setText(rowNumber, 2,
						dictionaryEmployee.getNameForSchedule());
			}
		} else {
			employeesForInsert.add(malibuEmployee);
			writeScheduleEmployee(flexTable, malibuEmployee, rowNumber, false);
		}
	}

	private void removeAllScheduleEmployees(FlexTable flexTable) {
		int rowCount = flexTable.getRowCount();
		int malibuEmployeeCount = malibuEmployees.size();
		while (malibuEmployeeCount != 0) {
			removeEmployee(flexTable, rowCount - 1);
			malibuEmployeeCount--;
			rowCount--;
		}
		int scheduleEmployeeCount = employeesOnlyOur.size();
		while (scheduleEmployeeCount != 0) {
			removeEmployee(flexTable, rowCount - 1);
			scheduleEmployeeCount--;
			rowCount--;
		}
	}

	private void removeEmployee(FlexTable flexTable, int rowNumber) {
		if (rowNumber - 2 < employeesOnlyOur.size()) {
			Employee removedEmployee = employeesOnlyOur.remove(rowNumber - 2);
			if (removedEmployee != null) {
				if (removedEmployee.getEmployeeId() == 0) {
					Iterator<Employee> it = employeesForInsert.iterator();
					while (it.hasNext()) {
						Employee employee = it.next();
						if (employee.equals(removedEmployee)) {
							it.remove();
							break;
						}
					}
				} else {
					employeesForDelete.add(removedEmployee);
				}
			}
			flexTable.removeRow(rowNumber);
			changeRowNumbersForEmployees(flexTable, rowNumber);
			return;
		} else {
			Employee malibuEmployee = malibuEmployees.get(rowNumber
					- employeesOnlyOur.size() - 2);
			if (employeesDictionary.containsKey(malibuEmployee.getEmployeeId())) {
				Employee employee = employeesDictionary.remove(malibuEmployee
						.getEmployeeId());
				employeesForDelete.add(employee);
				employeesForUpdate.remove(employee);
			} else {
				employeesForInsert.remove(malibuEmployee);
			}
		}
		flexTable.setText(rowNumber, 2, "");
		flexTable.setText(rowNumber, 3, "");
		flexTable.setText(rowNumber, 4, "");
		flexTable.setText(rowNumber, 5, "");
		flexTable.setText(rowNumber, 6, "");
	}

	private void changeRowNumbersForEmployees(FlexTable flexTable,
			int startRowNumber) {
		int rowCount = flexTable.getRowCount();
		for (int rowNumber = startRowNumber; rowNumber < rowCount; rowNumber++) {
			changeRowNumberForUtilButton(flexTable, rowNumber, 1);
			changeRowNumberForUtilButton(flexTable, rowNumber, 6);
		}
	}

	private void changeRowNumbersForClubs(FlexTable flexTable,
			int startRowNumber) {
		int rowCount = flexTable.getRowCount();
		for (int rowNumber = startRowNumber; rowNumber < rowCount; rowNumber++) {
			changeRowNumberForUtilButton(flexTable, rowNumber, 1);
			changeRowNumberForUtilButton(flexTable, rowNumber, 4);
		}
	}

	private void changeRowNumberForUtilButton(FlexTable flexTable,
			int rowNumber, int columnNumber) {
		Widget widget = flexTable.getWidget(rowNumber, columnNumber);
		if (widget != null) {
			UtilButton importingButton = (UtilButton) widget;
			importingButton.setRowNumber(rowNumber);
			flexTable.setWidget(rowNumber, columnNumber, importingButton);
		}
	}

	private void createClubPanel(final MyEventDialogBox createObject,
			final FlexTable flexTable) {
		createObject.clear();
		createObject.setText("Добавление нового клуба");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Label title = new Label("Название клуба:");
		final TextBox titleTextBox = new TextBox();

		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (titleTextBox.getText() == null
						|| titleTextBox.getText().isEmpty()) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					Club c = new Club();
					c.setTitle(titleTextBox.getText());
					clubsOnlyOur.add(c);
					writeNewScheduleClub(flexTable, c);
					createObject.hide();
				}
			}
		});

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		delButton.addStyleName("rightDown");

		table.insertRow(0);
		table.insertCell(0, 0);
		table.setWidget(0, 0, title);
		table.insertCell(0, 1);
		table.setWidget(0, 1, titleTextBox);

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

		createObject.setOkButton(addButton);
		createObject.add(absPanel);
	}

	private void createEmployeePanel(final MyEventDialogBox createObject,
			final FlexTable flexTable) {
		createObject.clear();
		createObject.setText("Добавление нового сотрудника");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Фамилия:"));
		labelsNotNull.add(new Label("Имя:"));
		labelsNotNull.add(new Label("Отчество:"));
		labelsNotNull.add(new Label("Email:"));
		labelsNotNull.add(new Label("Адресс:"));
		labelsNotNull.add(new Label("Мобильный телефон:"));
		labelsNotNull.add(new Label("Номер паспорта:"));
		labelsNotNull.add(new Label("Идентификационный код:"));
		labelsNotNull.add(new Label("Дата рождения: "));

		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		DateBox dateBox = new DateBox();
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getFormat("dd/MM/yyyy")));
		textBoxs.add(dateBox);
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					Employee e = new Employee();
					e.setLastName(((TextBox) textBoxs.get(0)).getValue());
					e.setFirstName(((TextBox) textBoxs.get(1)).getValue());
					e.setSecondName(((TextBox) textBoxs.get(2)).getValue());
					e.setEmail(((TextBox) textBoxs.get(3)).getValue());
					e.setAddress(((TextBox) textBoxs.get(4)).getValue());
					e.setCellPhone(((TextBox) textBoxs.get(5)).getValue());
					e.setPassportNumber(((TextBox) textBoxs.get(6)).getValue());
					e.setIdNumber(((TextBox) textBoxs.get(7)).getValue());
					e.setBirthday(((DateBox) textBoxs.get(8)).getValue());
					employeesOnlyOur.add(e);
					writeNewScheduleEmployee(flexTable, e);
					createObject.hide();
				}
			}
		});

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		delButton.addStyleName("rightDown");

		for (int i = 0; i < labelsNotNull.size(); i++) {
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

		createObject.setOkButton(addButton);
		createObject.add(absPanel);

	}

	private boolean fieldsIsEmpty(ArrayList<Widget> textBoxs) {
		for (int i = 0; i < textBoxs.size(); i++) {
			if ("TextBox".equals(textBoxs.get(i).getClass().getSimpleName())
					|| "PasswordTextBox".equals(textBoxs.get(i).getClass()
							.getSimpleName()))
				if (((TextBox) textBoxs.get(i)).getValue() == null
						|| ((TextBox) textBoxs.get(i)).getValue().isEmpty())
					return true;
			if ("DateBox".equals(textBoxs.get(i).getClass().getSimpleName()))
				if (((DateBox) textBoxs.get(i)).getValue() == null)
					return true;
		}
		return false;
	}

	private class UtilButton extends Button {
		private int rowNumber;

		public UtilButton(int rowNumber) {
			this.rowNumber = rowNumber;
		}

		public int getRowNumber() {
			return rowNumber;
		}

		public void setRowNumber(int rowNumber) {
			this.rowNumber = rowNumber;
		}
	}

}
