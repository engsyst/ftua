package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.HashSet;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.DialogBoxUtil;
import ua.nure.ostpc.malibu.shedule.client.LoadingImagePanel;
import ua.nure.ostpc.malibu.shedule.client.settings.ProfilePanel.EmployeeUpdater;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.smartgwt.client.util.SC;

public class EmployeeImportExportPanel extends HorizontalPanel {
	private static Set<EmployeeUpdater> updaterSet = new HashSet<EmployeeUpdater>();

	private Button importButton;
	private Button exportButton;

	public EmployeeImportExportPanel() {
		setStyleName("spaciousTable");
		importButton = new Button("Импортировать из Excel");
		importButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ImportPanel importPanel = new ImportPanel();
				DialogBoxUtil.callDialogBox("Импорт сотрудников", importPanel);
				importButton.setFocus(false);
			}
		});
		add(importButton);

		final FormPanel exportFormPanel = new FormPanel();
		exportFormPanel.setMethod(FormPanel.METHOD_GET);
		exportFormPanel.setAction(GWT.getHostPageBaseURL()
				+ Path.COMMAND__EXCEL_EMPLOYEE_EXPORT);
		exportFormPanel.getElement().<FormElement> cast().setTarget("_blank");
		exportButton = new Button("Экспортировать в Excel");
		exportFormPanel.add(exportButton);

		exportButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				exportFormPanel.submit();
				exportButton.setFocus(false);
			}
		});
		add(exportFormPanel);
	}

	private void notifyUpdaters() {
		for (EmployeeUpdater updater : updaterSet) {
			try {
				updater.updateEmployee();
			} catch (Exception caught) {
				SC.say(caught.getMessage());
			}
		}
	}

	public static boolean registerUpdater(EmployeeUpdater updater) {
		if (updater != null)
			return updaterSet.add(updater);
		return false;
	}

	public static boolean unregisterUpdater(EmployeeUpdater updater) {
		if (updater != null)
			return updaterSet.remove(updater);
		return false;
	}

	private class ImportPanel extends SimplePanel {
		private FormPanel formPanel;
		private FileUpload fileUpload;
		private Button uploadButton;
		private SuccessLabel successLabel;
		private ErrorHTML errorLabel;

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
					+ Path.COMMAND__EXCEL_EMPLOYEE_IMPORT);
			formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
			mainPanel.add(selectLabel);
			mainPanel.add(fileUpload);
			mainPanel.add(uploadButton);
			successLabel = new SuccessLabel();
			mainPanel.add(successLabel);
			errorLabel = new ErrorHTML();
			mainPanel.add(errorLabel);
			formPanel.add(mainPanel);
			add(formPanel);
			setHandlers();
		}

		private void setHandlers() {
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
					errorLabel.setHTML("");
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
							notifyUpdaters();
							successLabel
									.setText("Список сотрудников из файла успешно импортирован!");
						} else {
							String errorMsg = getErrorMessage(resultValue);
							if (errorMsg.isEmpty()) {
								errorMsg = "Загруженный файл имеет неверный формат данных! Первый лист должен содержать список сотрудников!";
							}
							errorLabel.setHTML(errorMsg);
						}
					} else {
						Window.alert("Ошибка отправки файла!");
					}
				}

				private String getErrorMessage(JSONValue resultValue) {
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					sb.appendEscaped("Список сотрудников из файла не удалось импортировать! ");
					int rowNumber = (int) resultValue.isObject()
							.get(AppConstants.EXCEL_JSON_ROW_NUMBER).isNumber()
							.doubleValue();
					JSONObject errorMap = resultValue.isObject()
							.get(AppConstants.EXCEL_JSON_ERROR_MAP).isObject();
					Set<String> keySet = errorMap.keySet();
					if (rowNumber != 0) {
						sb.appendHtmlConstant("Строка: ");
						sb.append(rowNumber);
						sb.appendHtmlConstant("<ul>");
						for (String key : keySet) {
							sb.appendHtmlConstant("<li>");
							sb.appendHtmlConstant(errorMap.get(key).isString()
									.stringValue());
							sb.appendHtmlConstant("</li>");
						}
						sb.appendHtmlConstant("</ul>");
						return sb.toSafeHtml().asString();
					} else {
						for (String key : keySet) {
							sb.appendHtmlConstant(errorMap.get(key).isString()
									.stringValue());
						}
						sb.appendHtmlConstant("<br/>");
						String str = sb.toSafeHtml().asString();
						str = str.replace("&lt;", "<");
						str = str.replace("&gt;", ">");
						return str;
					}
				}

			});
		}
	}

}
