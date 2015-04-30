package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.SC;

public class EditCategoryForm extends SimplePanel implements ClickHandler {

	public interface CategoryUpdater {
		public void updateCategory(Category category);
	}

	private static Set<CategoryUpdater> updaterSet = new HashSet<EditCategoryForm.CategoryUpdater>();

	private static VerticalPanel panel = new VerticalPanel();
	private static Label errLabel;
	private static Label categoryTitleLabel;
	private static TextBox categoryTitleTextBox;
	private static Button btnSave;
	private Category category;

	public EditCategoryForm() {
		setWidget(panel);
		panel.clear();
		createView();
		setFormData(category = new Category());
	}

	public EditCategoryForm(Category category) {
		setWidget(panel);
		panel.clear();
		createView();
		this.category = category;
		setFormData(category);
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
		setFormData(category);
	}

	private void createView() {
		errLabel = new Label(" ");
		panel.add(errLabel);

		final int ROWS = 2;
		final int COLS = 2;
		Grid grid = new Grid(ROWS, COLS);
		panel.add(grid);

		categoryTitleLabel = new Label("Название категории: ");
		grid.setWidget(0, 0, categoryTitleLabel);
		categoryTitleLabel.setTitle("Введите название категории");

		categoryTitleTextBox = new TextBox();
		grid.setWidget(0, 1, categoryTitleTextBox);
		categoryTitleTextBox.setName("categoryName");

		// Control buttons
		Grid btnGrid = new Grid(1, 2);
		btnSave = new Button("Сохранить");
		btnGrid.setWidget(0, 0, btnSave);
		btnSave.addClickHandler(buttonSaveClickHandler);

		Button btnReset = new Button("Сбросить");
		btnGrid.setWidget(0, 1, btnReset);
		btnReset.addClickHandler(this);

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

		categoryTitleTextBox.setFocus(true);
		categoryTitleTextBox.selectAll();
	}

	public void setFormData(Category category) {
		assert category != null : "Category can not be a null ";
		this.category = category;
		if (category.getTitle() == null)
			category.setTitle("");
		categoryTitleTextBox.setText(String.valueOf(category.getTitle()));
	}

	public Category getFormData() {
		Category category = new Category();
		if (this.category != null && this.category.getCategoryId() != 0) {
			category.setCategoryId(this.category.getCategoryId());
			category.setEmployeeIdList((List<Long>) this.category
					.getEmployeeIdList());
		} else {
			category.setEmployeeIdList(new ArrayList<Long>());
		}
		category.setTitle(categoryTitleTextBox.getText());
		return category;
	}

	private Set<String> validate(Category category) {
		Set<String> errorSet = new HashSet<String>();
		try {
			category.setTitle(categoryTitleTextBox.getText());
			if (category.getTitle().trim().length() == 0) {
				errorSet.add(categoryTitleTextBox.getName());
			}
		} catch (Exception e) {
			errorSet.add(categoryTitleTextBox.getName());
		}
		return errorSet;
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
	 * See: {@link EditCategoryForm#setBtnSaveClickHandler(ClickHandler)}
	 * 
	 */
	private ClickHandler buttonSaveClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Category category = getFormData();
			clearErrors();
			Set<String> errorSet = validate(category);
			if (errorSet.size() != 0) {
				setErrors(errorSet);
			} else {
				if (category.getCategoryId() == 0) {
					AppState.startSettingsService.insertCategory(category,
							new AsyncCallback<Category>() {

								@Override
								public void onSuccess(Category result) {
									errLabel.setText("Категория добавлена!");
									onResult(result);
								}

								@Override
								public void onFailure(Throwable caught) {
									errLabel.setText(caught.getMessage());
								}
							});
				} else {
					AppState.startSettingsService.updateCategory(category,
							new AsyncCallback<Category>() {

								@Override
								public void onSuccess(Category result) {
									errLabel.setText("Категория изменена!");
									onResult(result);
								}

								@Override
								public void onFailure(Throwable caught) {
									errLabel.setText(caught.getMessage());
								}
							});
				}
			}
		}

		private void onResult(Category result) {
			EditCategoryForm.this.category = result;
			for (CategoryUpdater updater : updaterSet) {
				try {
					updater.updateCategory(result);
				} catch (Exception caught) {
					SC.say(caught.getMessage());
				}
			}
			btnSave.setFocus(false);
			Timer timer = new Timer() {

				@Override
				public void run() {
					errLabel.setText(" ");
				}
			};
			timer.schedule(20000);
		}

	};

	@Override
	public void onClick(ClickEvent event) {
		clearErrors();
		setFormData(category);
	}

	private void setErrors(Set<String> errorSet) {
		if (errorSet.contains(categoryTitleTextBox.getName())) {
			categoryTitleLabel.addStyleDependentName("error");
			categoryTitleTextBox.addStyleDependentName("error");
		}
		btnSave.setFocus(false);
	}

	private void clearErrors() {
		categoryTitleLabel.setStyleDependentName("error", false);
		categoryTitleTextBox.setStyleDependentName("error", false);
	}

	public static boolean registerUpdater(CategoryUpdater updater) {
		if (updater != null)
			return updaterSet.add(updater);
		return false;
	}

	public static boolean unregisterUpdater(CategoryUpdater updater) {
		if (updater != null)
			return updaterSet.remove(updater);
		return false;
	}
}
