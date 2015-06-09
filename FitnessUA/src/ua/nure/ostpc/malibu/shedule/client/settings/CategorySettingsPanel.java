package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.DialogBoxUtil;
import ua.nure.ostpc.malibu.shedule.client.settings.EditCategoryForm.CategoryUpdater;
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
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

public class CategorySettingsPanel extends SimplePanel implements
		CategoryUpdater {
	private List<Category> categoryList;
	private Map<Long, String> allEmployeeNameMap;
	private Map<Long, String> notRemovedEmployeeNameMap;

	private FlexTable emptyEmployeeTable;
	private FlexTable categoryEmployeeTable;
	private ListBox categoryListBox;
	private int selectedCategoryIndex;

	private FlexTable categoryEditingTable;

	public CategorySettingsPanel() {
		HorizontalPanel mainPanel = new HorizontalPanel();
		mainPanel.setSize("100%", "100%");
		drawEmptyEmployeeTable(mainPanel);
		drawImportingImage(mainPanel);
		drawCategoryEmployeeTable(mainPanel);
		drawCategoryEditingTable(mainPanel);
		setWidget(mainPanel);
		EditCategoryForm.registerUpdater(this);
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
		emptyEmployeeTable.getRowFormatter().addStyleName(0, "mainHeader");

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
		categoryEmployeeTable.getRowFormatter().addStyleName(0, "mainHeader");

		mainPanel.add(categoryEmployeeTable);
	}

	private void drawCategoryEditingTable(HorizontalPanel mainPanel) {
		if (categoryEditingTable == null) {
			categoryEditingTable = new FlexTable();
		}
		categoryEditingTable.removeAllRows();
		categoryEditingTable.setStyleName("mainTable");
		categoryEditingTable.addStyleName("settingsTable");
		categoryEditingTable.addStyleName("categoryEditTable");
		categoryEditingTable.insertRow(0);

		InlineLabel titleLabel = new InlineLabel("Категория");
		titleLabel.setWordWrap(true);
		categoryEditingTable.insertCell(0, 0);
		categoryEditingTable.setWidget(0, 0, titleLabel);

		Image creatingImage = new Image("img/new_category.png");
		creatingImage.setStyleName("myImageAsButton");
		creatingImage
				.setTitle("Добавить новую категорию в подсистему составления графика работ");

		creatingImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EditCategoryForm editCategoryForm = new EditCategoryForm();
				DialogBoxUtil.callDialogBox("Добавление новой категории",
						editCategoryForm);
			}
		});

		categoryEditingTable.insertCell(0, 1);
		categoryEditingTable.setWidget(0, 1, creatingImage);

		categoryEditingTable.getRowFormatter().addStyleName(0, "mainHeader");

		mainPanel.add(categoryEditingTable);
	}

	private void updateCategoryListBox() {
		categoryListBox.clear();
		for (Category category : categoryList) {
			categoryListBox.addItem(category.getTitle());
		}
	}

	private void setEmptyEmployeesForCategory(Category category) {
		cleanTable(emptyEmployeeTable);
		Iterator<Entry<Long, String>> it = notRemovedEmployeeNameMap.entrySet()
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
		cleanTable(categoryEmployeeTable);
		Iterator<Entry<Long, String>> it = allEmployeeNameMap.entrySet()
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

	private void setCategoriesInEditingTable() {
		cleanTable(categoryEditingTable);
		Iterator<Category> it = categoryList.iterator();
		int rowNumber = 1;
		while (it.hasNext()) {
			Category category = it.next();
			categoryEditingTable.insertRow(rowNumber);
			categoryEditingTable.insertCell(rowNumber, 0);
			CategoryTitleLabel categoryTitleLabel = new CategoryTitleLabel(
					category);
			categoryEditingTable.setWidget(rowNumber, 0, categoryTitleLabel);
			categoryEditingTable.insertCell(rowNumber, 1);
			CategoryRemovingImage categoryRemovingImage = new CategoryRemovingImage(
					category.getCategoryId());
			categoryEditingTable.setWidget(rowNumber, 1, categoryRemovingImage);
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

	private void getCategorySettingsData() {
		AppState.startSettingsService
				.getCategorySettingsData(new AsyncCallback<CategorySettingsData>() {

					@Override
					public void onSuccess(
							CategorySettingsData categorySettingsData) {
						categoryList = categorySettingsData.getCategoryList();
						allEmployeeNameMap = categorySettingsData
								.getAllEmployeeNameMap();
						notRemovedEmployeeNameMap = categorySettingsData
								.getNotRemovedEmployeeNameMap();
						if (!categoryList.isEmpty()) {
							selectedCategoryIndex = 0;
							setEmptyEmployeesForCategory(categoryList
									.get(selectedCategoryIndex));
							updateCategoryListBox();
							setEmployeesForCategory(categoryList
									.get(selectedCategoryIndex));
							setCategoriesInEditingTable();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say("Невозможно получить данные с сервера!");
					}
				});
	}

	@Override
	public void updateCategory(Category category) {
		getCategorySettingsData();
	}

	private Category getCategoryById(long categoryId) {
		for (Category category : categoryList) {
			if (category.getCategoryId() == categoryId) {
				return category;
			}
		}
		return null;
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
									SC.say(caught.getMessage());
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
									SC.say(caught.getMessage());
								}
							});
				}
			});
		}
	}

	private class CategoryTitleLabel extends InlineLabel {
		private Category category;

		private CategoryTitleLabel(Category category) {
			this.category = category;
			setText(category.getTitle());
			setStyleName("cursor");

			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					CategoryTitleLabel categoryTitleLabel = (CategoryTitleLabel) event
							.getSource();
					Category category = categoryTitleLabel.getCategory();
					EditCategoryForm editCategoryForm = new EditCategoryForm(
							category);
					DialogBoxUtil.callDialogBox("Редактирование категории",
							editCategoryForm);
				}
			});
		}

		public Category getCategory() {
			return category;
		}
	}

	private class CategoryRemovingImage extends Image {
		private long categoryId;

		private CategoryRemovingImage(long categoryId) {
			super("img/remove.png");
			this.categoryId = categoryId;
			setTitle("Удалить категорию");
			setStyleName("cursor");

			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					CategoryRemovingImage categoryRemovingImage = (CategoryRemovingImage) event
							.getSource();
					final long categoryId = categoryRemovingImage
							.getCategoryId();
					Category category = getCategoryById(categoryId);
					StringBuilder sb = new StringBuilder();
					if (category.getEmployeeIdList().isEmpty()) {
						sb.append("В категории нет сотрудников.\n");
					} else {
						sb.append("В категории есть сотрудники.\n");
					}
					sb.append("Вы уверены, что хотите удалить категорию ");
					sb.append("\"");
					sb.append(category.getTitle());
					sb.append("\"?");
					SC.ask(sb.toString(), new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							if (value) {
								AppState.startSettingsService.removeCategory(
										categoryId,
										new AsyncCallback<Boolean>() {

											@Override
											public void onSuccess(Boolean result) {
												if (result) {
													getCategorySettingsData();
												} else {
													SC.say("Указанную категорию удалить не удалось!");
												}
											}

											@Override
											public void onFailure(
													Throwable caught) {
												SC.say(caught.getMessage());
											}

										});

							}
						}
					});
				}
			});

		}

		public long getCategoryId() {
			return categoryId;
		}
	}

}
