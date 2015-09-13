package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.DialogBoxUtil;
import ua.nure.ostpc.malibu.shedule.client.LoadingImagePanel;
import ua.nure.ostpc.malibu.shedule.client.settings.ProfilePanel.EmployeeUpdater;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmployeeSettingsData;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

public class EmployeeSettingsPanel extends VerticalPanel implements
		EmployeeUpdater {
	private List<EmployeeSettingsData> data;
	private FlexTable t;
	private Button importButton;
	private Button exportButton;

	public EmployeeSettingsPanel() {
		drawImportAndExportButtons();
		drawHeader();
		add(t);
		EditEmployeeForm.registerUpdater(this);
		getAllEmployees();
	}

	private void getAllEmployees() {
		AppState.startSettingsService
				.getEmployeeSettingsData(new AsyncCallback<List<EmployeeSettingsData>>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(List<EmployeeSettingsData> result) {
						if (result != null) {
							data = result;
							drawHeader();
							drawContent();
						}
					}

				});
	}

	private void drawImportAndExportButtons() {
		HorizontalPanel importExportPanel = new HorizontalPanel();
		importButton = new Button("Импортировать из Excel");
		importButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ImportPanel importPanel = new ImportPanel();
				DialogBoxUtil.callDialogBox("Импорт сотрудников", importPanel);
				importButton.setFocus(false);
			}
		});
		importExportPanel.add(importButton);

		exportButton = new Button("Экспортировать в Excel");
		exportButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub

			}
		});
		importExportPanel.add(exportButton);

		add(importExportPanel);
	}

	private void drawHeader() {
		int curColumn = 0;
		if (t == null)
			t = new FlexTable();
		t.removeAllRows();
		t.setStyleName("mainTable");
		t.addStyleName("settingsTable");
		t.insertRow(0);

		InlineLabel l = new InlineLabel("Сотрудник");
		l.setWordWrap(true);
		l.setTitle("Сотрудники из подсистемы учета кадров");
		t.insertCell(0, curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Импорт");
		l.setWordWrap(true);
		l.setTitle("Для импорта нажмите стрелку\n");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Сотрудник");
		l.setWordWrap(true);
		l.setTitle("Сотрудники в подсистеме составления графика работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Ответственное лицо");
		l.setWordWrap(true);
		l.setTitle("Если установлен, то данный сотрудник может создавать и редактировать графики работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Администратор");
		l.setWordWrap(true);
		l.setTitle("Если установлен, то данный сотрудник используется при составлении графика работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Подписка");
		l.setWordWrap(true);
		l.setTitle("Если установлен, то данный сотрудник будет получать рассылку графиков работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		Image createImage = new Image("img/new_user.png");
		createImage.setStyleName("myImageAsButton");
		createImage
				.setTitle("Добавить нового сотрудника в подсистему составления графика работ");

		createImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EditEmployeeForm editEmployeeForm = new EditEmployeeForm();
				DialogBoxUtil.callDialogBox("Добавление нового сотрудника",
						editEmployeeForm);
			}
		});

		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, createImage);

		// Styling
		for (int i = 0; i < t.getCellCount(0); i++) {
			t.getFlexCellFormatter().addStyleName(0, i, "mainHeader");
		}
	}

	private void drawContent() {
		if (data == null)
			return;
		int row = 1;
		for (EmployeeSettingsData esd : data) {
			if (esd.getOutEmployee() == null && esd.getInEmployee() != null
					&& esd.getInEmployee().isDeleted()) {
				esd.setRow(-1);
				continue;
			}

			t.insertRow(row);
			esd.setRow(row);
			for (int i = 0; i < 7; i++) {
				t.insertCell(row, i);
			}

			if (esd.getOutEmployee() != null) {
				t.setWidget(row, 0, new InlineLabel(esd.getOutEmployee()
						.getShortName()));
			}

			if (esd.getOutEmployee() != null && esd.getInEmployee() == null) {
				Image img = new Image("img/import.png");
				img.setTitle("Импортировать сотрудника");
				img.setStyleName("buttonImport");
				t.setWidget(row, 1, img);
				img.addClickHandler(importEmployeeHandler);
				img.getElement().setId(
						"imp-" + esd.getOutEmployee().getEmployeeId());
			}

			if (esd.getInEmployee() != null) {
				if (!esd.getInEmployee().isDeleted()) {
					t.setWidget(row, 2, new ScheduleEmployeeNameLabel(esd
							.getInEmployee().getShortName(), esd
							.getInEmployee().getEmployeeId()));

					CheckBox cb = new CheckBox();
					cb.setTitle("Ответственное лицо");
					t.setWidget(row, 3, cb);
					cb.getElement().setId(
							"cb-" + Right.RESPONSIBLE_PERSON.ordinal() + "-"
									+ esd.getInEmployee().getEmployeeId());
					cb.addClickHandler(checkHandler);
					if (esd.getRoles() != null)
						for (Role r : esd.getRoles()) {
							if (Right.RESPONSIBLE_PERSON.equals(r.getRight())) {
								cb.setValue(true);
							}
						}
					if (AppState.employee.getEmployeeId() == esd
							.getInEmployee().getEmployeeId())
						cb.setEnabled(false);

					cb = new CheckBox();
					cb.setTitle("Администратор");
					t.setWidget(row, 4, cb);
					cb.addClickHandler(checkHandler);
					cb.getElement().setId(
							"cb-" + Right.ADMIN.ordinal() + "-"
									+ esd.getInEmployee().getEmployeeId());
					if (esd.getRoles() != null)
						for (Role r : esd.getRoles()) {
							if (Right.ADMIN.equals(r.getRight())) {
								cb.setValue(true);
							}
						}

					cb = new CheckBox();
					cb.setTitle("Подписчик");
					t.setWidget(row, 5, cb);
					cb.addClickHandler(checkHandler);
					cb.getElement().setId(
							"cb-" + Right.SUBSCRIBER.ordinal() + "-"
									+ esd.getInEmployee().getEmployeeId());
					if (esd.getRoles() != null)
						for (Role r : esd.getRoles()) {
							if (Right.SUBSCRIBER.equals(r.getRight())) {
								cb.setValue(true);
							}
						}
				} else {
					t.setWidget(row, 2, new InlineLabel(esd.getInEmployee()
							.getShortName()));
				}

				Image img = new Image("img/remove.png");
				img.setTitle("Удалить сотрудника");
				if (!esd.getInEmployee().isDeleted()) {
					t.setWidget(row, 6, img);
				}
				if (AppState.employee.getEmployeeId() != esd.getInEmployee()
						.getEmployeeId()) {
					img.addClickHandler(deleteEmployeeHandler);
					img.addStyleName("myImageAsButton");
				}
				img.getElement().setId(
						"del-" + esd.getInEmployee().getEmployeeId());
			}
			row++;
		}
	}

	private ClickHandler importEmployeeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Cell cell = t.getCellForEvent(event);
			final Image img = (Image) t.getWidget(cell.getRowIndex(),
					cell.getCellIndex());

			int id = Integer.parseInt(img.getElement().getId().split("-")[1]);
			int idx = getOuterIndexById(id);
			EmployeeSettingsData esd = data.get(idx);
			importEmployee(esd.getOutEmployee());
		}
	};

	protected void importEmployee(Employee outEmployee) {
		AppState.startSettingsService.importEmployee(outEmployee,
				new AsyncCallback<Employee>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Employee result) {
						drawHeader();
						getAllEmployees();
					}
				});
	}

	private ClickHandler deleteEmployeeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {

			Cell cell = t.getCellForEvent(event);
			final Image img = (Image) t.getWidget(cell.getRowIndex(),
					cell.getCellIndex());

			int id = Integer.parseInt(img.getElement().getId().split("-")[1]);
			int idx = getInnerIndexById(id);
			EmployeeSettingsData esd = data.get(idx);
			final Employee e = esd.getInEmployee();
			SC.ask("Вы уверены, что хотите удалить сотрудника\n"
					+ e.getShortName(), new BooleanCallback() {

				@Override
				public void execute(Boolean value) {
					if (value) {
						Image.setVisible(img.getElement(), false);
						removeEmployee(e);
					}
				}
			});
		}
	};

	private void removeEmployee(Employee emp) {
		AppState.startSettingsService.removeEmployee(emp.getEmployeeId(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						drawHeader();
						getAllEmployees();
					}
				});
	}

	private ClickHandler checkHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Cell cell = t.getCellForEvent(event);
			CheckBox cb = (CheckBox) t.getWidget(cell.getRowIndex(),
					cell.getCellIndex());
			cb.setEnabled(false);
			try {
				String[] ids = cb.getElement().getId().split("-");
				setEmployeeRole(Long.parseLong(ids[2]),
						Integer.parseInt(ids[1]), cb.getValue());
			} catch (Exception e) {
			}
		}

	};

	private void setEmployeeRole(long empId, int right, boolean enable) {
		AppState.startSettingsService.updateEmployeeRole(empId, right, enable,
				new AsyncCallback<long[]>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}

					@Override
					public void onSuccess(long[] result) {
						if (result == null) {
							SC.say("Ошибка");
							return;
						}
						try {
							int idx = getInnerIndexById(result[0]);
							CheckBox cb = (CheckBox) t.getWidget(idx + 1,
									(int) (2 + result[1]));
							cb.setEnabled(true);
						} catch (NumberFormatException e) {
							SC.say("Ошибка");
							return;
						}
					}
				});
	}

	private int getInnerIndexById(long id) {
		for (int i = 0; i < data.size(); i++) {
			EmployeeSettingsData esd = data.get(i);
			if (esd.getInEmployee() != null
					&& esd.getInEmployee().getEmployeeId() == id) {
				return i;
			}
		}
		return -1;
	}

	private int getOuterIndexById(long id) {
		for (int i = 0; i < data.size(); i++) {
			EmployeeSettingsData esd = data.get(i);
			if (esd.getOutEmployee() != null
					&& esd.getOutEmployee().getEmployeeId() == id) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void updateEmployee() {
		drawHeader();
		getAllEmployees();
	}

	private class ImportPanel extends SimplePanel {
		private FormPanel formPanel;
		private FileUpload fileUpload;
		private Button uploadButton;
		private SuccessLabel successLabel;
		private ErrorLabel errorLabel;

		private ImportPanel() {
			VerticalPanel mainPanel = new VerticalPanel();
			mainPanel.setStyleName("spaciousTable");
			formPanel = new FormPanel();
			fileUpload = new FileUpload();
			fileUpload.setName(AppConstants.EXCEL_FILE);
			Label selectLabel = new Label(
					"Выберите Excel-файл для импорта списка сотрудников:");
			uploadButton = new Button("Загрузить файл");
			formPanel.setMethod(FormPanel.METHOD_POST);
			formPanel.setAction(GWT.getHostPageBaseURL()
					+ Path.COMMAND__EXCEL_IMPORT);
			formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
			mainPanel.add(selectLabel);
			mainPanel.add(fileUpload);
			mainPanel.add(uploadButton);

			uploadButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					boolean result = true;
					String filePath = fileUpload.getFilename();
					if (filePath.length() == 0) {
						Window.alert("Файл не выбран!");
						result = false;
					} else {
						String fileExtension = getFileExtension(filePath);
						if (!fileExtension
								.equalsIgnoreCase(AppConstants.EXCEL_FILE_EXTENSION)) {
							Window.alert("Файл не имеет расширения "
									+ AppConstants.EXCEL_FILE_EXTENSION + "!");
							result = false;
						} else {
							int fileSize = getFileSize(fileUpload.getElement());
							if (fileSize == 0) {
								Window.alert("Размер файла равен 0!");
								result = false;
							}
							if (fileSize > AppConstants.EXCEL_FILE_MAX_SIZE_MB * 1024 * 1024) {
								Window.alert("Размер файла не должен превышать "
										+ AppConstants.EXCEL_FILE_MAX_SIZE_MB
										+ " МБ!");
								result = false;
							}
						}
					}
					if (result) {
						formPanel.submit();
						LoadingImagePanel.start();
					}
					uploadButton.setFocus(false);
					successLabel.setText("");
					errorLabel.setText("");
				}

				/**
				 * This method returns file extension.
				 * 
				 * @param filePath
				 *            - File path.
				 * @return File extension or empty string if file doesn't have
				 *         an extension.
				 */
				private String getFileExtension(String filePath) {
					String fileExtension = "";
					int pointPos = filePath.lastIndexOf('.');
					int slashPos = Math.max(filePath.lastIndexOf('/'),
							filePath.lastIndexOf('\\'));
					if (pointPos > slashPos) {
						fileExtension = filePath.substring(pointPos + 1);
					}
					return fileExtension;
				}

				/**
				 * This method returns file size.
				 * 
				 * @param data
				 *            - DOM Element from {@code FileUpload} object.
				 * @return File size in bytes.
				 */
				@SuppressWarnings("deprecation")
				private native int getFileSize(Element data) /*-{
			return data.files[0].size;
		}-*/;

			});

			formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {

				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					LoadingImagePanel.stop();
					String results = event.getResults();
					if (results != null) {
						int firstPos = results.indexOf('{');
						int secondPos = results.lastIndexOf('}');
						results = results.substring(firstPos, secondPos + 1);
						JSONValue resultValue = JSONParser
								.parseLenient(results);
						boolean importResult = resultValue.isObject()
								.get(AppConstants.EXCEL_JSON_RESULT)
								.isBoolean().booleanValue();
						if (importResult) {
							EmployeeSettingsPanel.this.updateEmployee();
							successLabel
									.setText("Список сотрудников из файла успешно импортирован!");
						} else {
							String errorMsg = getErrorMessage(resultValue);
							if (errorMsg.isEmpty()) {
								errorMsg = "Загруженный файл имеет неверный формат данных! Первый лист должен содержать список сотрудников!";
							}
							errorLabel.setText(errorMsg);
						}
					} else {
						Window.alert("Ошибка отправки файла!");
					}
				}

				private String getErrorMessage(JSONValue resultValue) {
					StringBuilder sb = new StringBuilder();
					int rowNumber = (int) resultValue.isObject()
							.get(AppConstants.EXCEL_JSON_ROW_NUMBER).isNumber()
							.doubleValue();
					if (rowNumber != 0) {
						sb.append("Строка: ");
						sb.append(rowNumber);
						sb.append(" ");
					}
					JSONObject errorMap = resultValue.isObject()
							.get(AppConstants.EXCEL_JSON_ERROR_MAP).isObject();
					Set<String> keySet = errorMap.keySet();
					for (String key : keySet) {
						sb.append(errorMap.get(key).isString().stringValue());
						sb.append(" ");
					}
					return sb.toString();
				}

			});

			successLabel = new SuccessLabel();
			mainPanel.add(successLabel);
			errorLabel = new ErrorLabel();
			mainPanel.add(errorLabel);
			formPanel.add(mainPanel);
			add(formPanel);
		}
	}

}
