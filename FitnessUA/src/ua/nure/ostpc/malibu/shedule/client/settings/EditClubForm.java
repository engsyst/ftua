package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.HashSet;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingPanel;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

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

public class EditClubForm extends SimplePanel implements ClickHandler {
	/**
	 * <p>
	 * If you need notify what user change Clubs, you need implements this
	 * interface and register implementation calling
	 * {@link EditClubForm#registerUpdater(ClubUpdater)}
	 * </p>
	 * <p>
	 * To unregister call {@link EditClubForm#unregisterUpdater(ClubUpdater)}
	 * </p>
	 * 
	 * @author engsyst
	 * 
	 */
	public interface ClubUpdater {
		public void updateClub(Club p);
	}

	private static Set<ClubUpdater> updater = new HashSet<EditClubForm.ClubUpdater>();

	private static VerticalPanel panel = new VerticalPanel();
	private static Label errLabel;
	private static Label clubNameLabel;
	private static TextBox clubNameTB;
	private static Label clubIndependentLabel;
	private static CheckBox clubIndependentCB;
	private static Button btnSave;
	private boolean wordWrap = true;
	private Club club;

	public EditClubForm() {
		setWidget(panel);
		panel.clear();
		createView();
		setFormData(club = new Club());
	}

	public EditClubForm(Club club) {
		setWidget(panel);
		panel.clear();
		createView();
		this.club = club;
		setFormData(club);
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club c) {
		club = c;
		setFormData(club);
	}

	private void createView() {
		errLabel = new Label(" ");
		panel.add(errLabel);

		final int ROWS = 3;
		final int COLS = 2;
		Grid grid = new Grid(ROWS, COLS);
		panel.add(grid);

		// private TextBox shiftsNumberTB;
		clubNameLabel = new Label("Название клуба: ");
		grid.setWidget(0, 0, clubNameLabel);
		clubNameLabel.setTitle("Введите название клуба");

		clubNameTB = new TextBox();
		grid.setWidget(0, 1, clubNameTB);
		clubNameTB.setName("clubName");

		// Flags
		clubIndependentLabel = new Label("Независимый: ");
		grid.setWidget(1, 0, clubIndependentLabel);
		// grid.getCellFormatter().setStyleName(3, 0, labelPanelStyle);
		clubIndependentLabel.setWordWrap(wordWrap);
		clubIndependentLabel
				.setTitle("Если установлен, то клуб не участвует в составлении графика работ");

		clubIndependentCB = new CheckBox();
		grid.setWidget(1, 1, clubIndependentCB);
		clubIndependentCB.setName("clubIndependent");

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

		clubNameTB.setFocus(true);
		clubNameTB.selectAll();

		// setFormData(prefs);
	}

	public void setFormData(Club c) {
		assert c != null : "Club can not be a null ";
		club = c;
		if (c.getTitle() == null)
			c.setTitle("");
		clubNameTB.setText(String.valueOf(c.getTitle()));
		clubIndependentCB.setValue(c.isIndependent());
	}

	public Club getFormData() {
		Club c = new Club();
		if (this.club != null && this.club.getClubId() != 0) {
			c.setClubId(this.club.getClubId());
			c.setDeleted(this.club.isDeleted());
		}
		c.setTitle(clubNameTB.getText());
		c.setIndependent(clubIndependentCB.getValue());
		return c;
	}

	private Set<String> validate(Club c) {
		Set<String> err = new HashSet<String>();
		try {
			c.setTitle(clubNameTB.getText());
			if (c.getTitle().trim().length() <= 0) {
				err.add(clubNameTB.getName());
			}
		} catch (Exception e) {
			err.add(clubNameTB.getName());
		}
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
	 * See: {@link EditClubForm#setBtnSaveClickHandler(ClickHandler)}
	 * 
	 */
	private ClickHandler buttonSaveClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final Club c = getFormData();

			clearErrors();
			Set<String> e = validate(c);
			if (e.size() > 0) {
				setErrors(e);
			} else {
				club = c;
				LoadingPanel.start();
				AppState.startSettingsService.setClub(club,
						new AsyncCallback<Club>() {

							@Override
							public void onSuccess(Club result) {
								for (ClubUpdater u : updater) {
									try {
										u.updateClub(club);
									} catch (Exception caught) {
										SC.say(caught.getMessage());
									}
								}
								LoadingPanel.stop();
								errLabel.setText("Клуб добавлен");
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
								LoadingPanel.stop();
								SC.say(caught.getMessage());
							}
						});
			}
		}
	};

	@Override
	public void onClick(ClickEvent event) {
		clearErrors();
		setFormData(club);
	}

	private void setErrors(Set<String> e) {
		if (e.contains(clubNameTB.getName())) {
			clubNameLabel.addStyleDependentName("error");
			clubNameTB.addStyleDependentName("error");
		}
	}

	private void clearErrors() {
		clubNameLabel.setStyleDependentName("error", false);
		clubNameTB.setStyleDependentName("error", false);
	}

	public static boolean registerUpdater(ClubUpdater u) {
		if (u != null)
			return updater.add(u);
		return false;
	}

	public static boolean unregisterUpdater(ClubUpdater u) {
		if (u != null)
			return updater.remove(u);
		return false;
	}
}
