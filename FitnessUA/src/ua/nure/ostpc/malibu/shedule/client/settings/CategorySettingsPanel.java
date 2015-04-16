package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.shared.CategorySettingsData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.smartgwt.client.util.SC;

public class CategorySettingsPanel extends SimplePanel {
	private List<Category> categoryList;
	private Map<Long, String> employeeNameMap;
	private FlexTable emptyEmployeeTable;
	private FlexTable categoryEmployeeTable;
	private ListBox categoryListBox;
	private int selectedCategoryIndex;

	public CategorySettingsPanel() {
		HorizontalPanel mainPanel = new HorizontalPanel();
		mainPanel.setSize("100%", "100%");
		drawEmptyEmployeeTable(mainPanel);
		drawImportingImage(mainPanel);
		drawCategoryEmployeeTable(mainPanel);
		setWidget(mainPanel);
		getCategorySettingsData();
	}

	private void drawEmptyEmployeeTable(HorizontalPanel mainPanel) {
		if (emptyEmployeeTable == null) {
			emptyEmployeeTable = new FlexTable();
		}
		emptyEmployeeTable.removeAllRows();
		emptyEmployeeTable.setStyleName("mainTable");
		emptyEmployeeTable.addStyleName("settingsTable");
		emptyEmployeeTable.insertRow(0);

		InlineLabel titleLabel = new InlineLabel("Сотрудники");
		titleLabel.setWordWrap(true);
		titleLabel.setTitle("Сотрудники, не входящие в выбранную категорию");
		emptyEmployeeTable.insertCell(0, 0);
		emptyEmployeeTable.setWidget(0, 0, titleLabel);
		emptyEmployeeTable.getFlexCellFormatter().addStyleName(0, 0,
				"mainHeader");

		mainPanel.add(emptyEmployeeTable);
	}

	private void drawImportingImage(HorizontalPanel mainPanel) {
		Image importingImage = new Image(GWT.getHostPageBaseURL()
				+ "img/import.png");
		mainPanel.add(importingImage);
	}

	private void drawCategoryEmployeeTable(HorizontalPanel mainPanel) {
		if (categoryEmployeeTable == null) {
			categoryEmployeeTable = new FlexTable();
		}
		categoryEmployeeTable.removeAllRows();
		categoryEmployeeTable.setStyleName("mainTable");
		categoryEmployeeTable.addStyleName("settingsTable");
		categoryEmployeeTable.insertRow(0);

		categoryListBox = new ListBox();
		categoryListBox.setTitle("Выбор категории сотрудников");

		categoryListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ListBox listBox = (ListBox) event.getSource();
				selectedCategoryIndex = listBox.getSelectedIndex();
				Category category = categoryList.get(selectedCategoryIndex);
				setEmptyEmployeesForCategory(category);
				setEmployeesForCategory(category);
			}
		});

		categoryEmployeeTable.insertCell(0, 0);
		categoryEmployeeTable.setWidget(0, 0, categoryListBox);
		categoryEmployeeTable.getFlexCellFormatter().addStyleName(0, 0,
				"mainHeader");

		mainPanel.add(categoryEmployeeTable);
	}

	private void updateCategoryListBox() {
		categoryListBox.clear();
		for (Category category : categoryList) {
			categoryListBox.addItem(category.getTitle());
		}
	}

	private void setEmptyEmployeesForCategory(Category category) {
		cleanEmployeeTable(emptyEmployeeTable);
		Iterator<Entry<Long, String>> it = employeeNameMap.entrySet()
				.iterator();
		int rowNumber = 1;
		while (it.hasNext()) {
			Entry<Long, String> entry = it.next();
			long employeeId = entry.getKey();
			String employeeName = entry.getValue();
			if (!category.getEmployeeIdList().contains(employeeId)) {
				emptyEmployeeTable.insertRow(rowNumber);
				emptyEmployeeTable.insertCell(rowNumber, 0);
				EmployeeNameLabel employeeNameLabel = new EmptyEmployeeNameLabel(
						employeeName, employeeId, rowNumber);
				emptyEmployeeTable.setWidget(rowNumber, 0, employeeNameLabel);
				rowNumber++;
			}
		}
	}

	private void setEmployeesForCategory(Category category) {
		cleanEmployeeTable(categoryEmployeeTable);
		Iterator<Entry<Long, String>> it = employeeNameMap.entrySet()
				.iterator();
		int rowNumber = 1;
		while (it.hasNext()) {
			Entry<Long, String> entry = it.next();
			long employeeId = entry.getKey();
			String employeeName = entry.getValue();
			if (category.getEmployeeIdList().contains(employeeId)) {
				categoryEmployeeTable.insertRow(rowNumber);
				categoryEmployeeTable.insertCell(rowNumber, 0);
				EmployeeNameLabel employeeNameLabel = new CategoryEmployeeNameLabel(
						employeeName, employeeId, rowNumber);
				categoryEmployeeTable
						.setWidget(rowNumber, 0, employeeNameLabel);
				rowNumber++;
			}
		}
	}

	private void cleanEmployeeTable(FlexTable employeeTable) {
		int rowCount = employeeTable.getRowCount();
		for (int i = rowCount - 1; i > 0; i--) {
			employeeTable.removeRow(i);
		}
	}

	private void getCategorySettingsData() {
		AppState.startSettingsService
				.getCategorySettingsData(new AsyncCallback<CategorySettingsData>() {

					@Override
					public void onSuccess(
							CategorySettingsData categorySettingsData) {
						categoryList = categorySettingsData.getCategoryList();
						employeeNameMap = categorySettingsData
								.getEmployeeNameMap();
						if (!categoryList.isEmpty()) {
							selectedCategoryIndex = 0;
							setEmptyEmployeesForCategory(categoryList
									.get(selectedCategoryIndex));
							updateCategoryListBox();
							setEmployeesForCategory(categoryList
									.get(selectedCategoryIndex));
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say("Невозможно получить данные с сервера!");
					}
				});
	}

	private abstract class EmployeeNameLabel extends InlineLabel {
		private long employeeId;
		private int rowNumber;

		private EmployeeNameLabel(String nameForSchedule, long employeeId,
				int rowNumber) {
			this.employeeId = employeeId;
			this.rowNumber = rowNumber;
			setText(nameForSchedule);
			setStyleName("cursor");
		}

		public long getEmployeeId() {
			return employeeId;
		}

		public void setRowNumber(int rowNumber) {
			this.rowNumber = rowNumber;
		}

		public int getRowNumber() {
			return rowNumber;
		}

		protected void refreshRowNumbers(int fromRowNumber,
				FlexTable employeeTable) {
			int rowCount = employeeTable.getRowCount();
			for (int i = fromRowNumber; i < rowCount; i++) {
				EmployeeNameLabel employeeNameLabel = (EmployeeNameLabel) employeeTable
						.getWidget(i, 0);
				employeeNameLabel.setRowNumber(i);
			}
		}
	}

	private class EmptyEmployeeNameLabel extends EmployeeNameLabel {

		private EmptyEmployeeNameLabel(String nameForSchedule, long employeeId,
				int rowNumber) {
			super(nameForSchedule, employeeId, rowNumber);

			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					EmployeeNameLabel employeeNameLabel = (EmployeeNameLabel) event
							.getSource();
					long employeeId = employeeNameLabel.getEmployeeId();
					int rowNumber = employeeNameLabel.getRowNumber();
					emptyEmployeeTable.removeRow(rowNumber);
					EmptyEmployeeNameLabel.this.refreshRowNumbers(rowNumber,
							emptyEmployeeTable);
					Category category = categoryList.get(selectedCategoryIndex);
					category.getEmployeeIdList().add(employeeId);
					AppState.startSettingsService.updateCategory(category,
							new AsyncCallback<Category>() {

								@Override
								public void onSuccess(Category result) {
									categoryList.set(selectedCategoryIndex,
											result);
									setEmployeesForCategory(categoryList
											.get(selectedCategoryIndex));
								}

								@Override
								public void onFailure(Throwable caught) {
									SC.say("Невозможно получить данные с сервера!");
								}
							});
				}
			});
		}
	}

	private class CategoryEmployeeNameLabel extends EmployeeNameLabel {

		private CategoryEmployeeNameLabel(String nameForSchedule,
				long employeeId, int rowNumber) {
			super(nameForSchedule, employeeId, rowNumber);

			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					EmployeeNameLabel employeeNameLabel = (EmployeeNameLabel) event
							.getSource();
					long employeeId = employeeNameLabel.getEmployeeId();
					int rowNumber = employeeNameLabel.getRowNumber();
					categoryEmployeeTable.removeRow(rowNumber);
					CategoryEmployeeNameLabel.this.refreshRowNumbers(rowNumber,
							categoryEmployeeTable);
					Category category = categoryList.get(selectedCategoryIndex);
					category.getEmployeeIdList().remove(employeeId);
					AppState.startSettingsService.updateCategory(category,
							new AsyncCallback<Category>() {

								@Override
								public void onSuccess(Category result) {
									categoryList.set(selectedCategoryIndex,
											result);
									setEmptyEmployeesForCategory(categoryList
											.get(selectedCategoryIndex));
								}

								@Override
								public void onFailure(Throwable caught) {
									SC.say("Невозможно получить данные с сервера!");
								}
							});
				}
			});
		}
	}

}
