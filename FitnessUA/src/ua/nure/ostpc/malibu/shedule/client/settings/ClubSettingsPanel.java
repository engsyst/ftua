package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.List;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.MyEventDialogBox;
import ua.nure.ostpc.malibu.shedule.client.settings.EditClubForm.ClubUpdater;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubSettingViewData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

public class ClubSettingsPanel extends Composite implements ClubUpdater {
	private List<ClubSettingViewData> clubs;
	private FlexTable t;
	protected MyEventDialogBox dlg;
	
	public ClubSettingsPanel() {
		drawHeader();
		initWidget(t);
		EditClubForm.registerUpdater(this);
		getAllClubs();
	}
	
	private void drawHeader() {
		if (t == null)
			t = new FlexTable();
		t.removeAllRows();
		t.setStyleName("mainTable");
		t.addStyleName("settingsTable");
		t.insertRow(0);
		
		InlineLabel l = new InlineLabel("Клубы");
		l.setWordWrap(true);
		l.setTitle("Клубы из подсистемы учета кадров");
		t.insertCell(0, 0);
		t.setWidget(0, 0, l);
		
		l = new InlineLabel("Импорт");
		l.setWordWrap(true);
		l.setTitle("Для импорта нажмите стрелку\n");
		t.insertCell(0, 1);
		t.setWidget(0, 1, l);
		
		l = new InlineLabel("Клубы");
		l.setWordWrap(true);
		l.setTitle("Клубы в подсистеме составления графика работ");
		t.insertCell(0, 2);
		t.setWidget(0, 2, l);
		
		l = new InlineLabel("Независимый");
		l.setWordWrap(true);
		l.setTitle("Если установлен, то данный клуб не используется при составлении графика работ");
		t.insertCell(0, 3);
		t.setWidget(0, 3, l);
		
		Image img = new Image("img/new_club.png");
		img.setStyleName("myImageAsButton");
		img.setTitle("Добавить новый клуб в подсистему составления графика работ");
		img.addClickHandler(newClubHandler);
		t.insertCell(0, 4);
		t.setWidget(0, 4, img);
		
		// Styling
		for (int i = 0; i < t.getCellCount(0); i++) {
			t.getFlexCellFormatter().addStyleName(0, i, "mainHeader");
		}
	}
	
	private void drawContent() {
		if (clubs == null) 
			return;
		int row = 1;
		for (ClubSettingViewData cvd : clubs) {
			if (cvd.getInner() != null && cvd.getInner().isDeleted()) {
				cvd.setRow(-1);
				continue;
			}
			
			t.insertRow(row);
			cvd.setRow(row);
			for (int i = 0; i < 5; i++) {
				t.insertCell(row, i);
			}
			
			if (cvd.getOuter() != null) {
				t.setWidget(row, 0, new InlineLabel(cvd.getOuter().getTitle()));
			}
			
			if (cvd.getOuter() != null && cvd.getInner() == null) {
				Image img = new Image("img/import.png");
				img.setTitle("Импортировать клуб");
//				img.setStyleName("buttonImport");
				t.setWidget(row, 1, img);
				img.addClickHandler(importClubHandler);
				img.getElement().setId("imp-" + cvd.getOuter().getClubId());
			}
			
			if (cvd.getInner() != null) {
				t.setWidget(row, 2, new InlineLabel(cvd.getInner().getTitle()));
				
				CheckBox cb = new CheckBox();
				cb.setTitle("Независимый");
				t.setWidget(row, 3, cb);
				cb.addClickHandler(checkHandler);
				cb.getElement().setId("cb-" + cvd.getInner().getClubId());
				cb.setValue(cvd.getInner().isIndependent());
				
				Image img = new Image("img/remove.png");
				img.setTitle("Удалить клуб");
				t.setWidget(row, 4, img);
				img.addClickHandler(deleteClubHandler);
				img.getElement().setId("del-" + cvd.getInner().getClubId());
			}
			row++;
		}
	}

	private ClickHandler importClubHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			Cell cell = t.getCellForEvent(event);
			final Image img = (Image) t.getWidget(cell.getRowIndex(), cell.getCellIndex());
			
			int id = Integer.parseInt(img.getElement().getId().split("-")[1]);
			int idx = getOuterClubIndexById(id);
			ClubSettingViewData cvd = clubs.get(idx);
			importClub(cvd.getOuter());
		}
	};
	
	private ClickHandler checkHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			Cell cell = t.getCellForEvent(event);
			CheckBox cb = (CheckBox) t.getWidget(cell.getRowIndex(), cell.getCellIndex());
			cb.setEnabled(false);
			try {
				setClubIndepended(Long.parseLong(cb.getElement().getId().split("-")[1]), cb.getValue());
			} catch (Exception e) {
			}
		}
	};
	
	private ClickHandler deleteClubHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			
			Cell cell = t.getCellForEvent(event);
			final Image img = (Image) t.getWidget(cell.getRowIndex(), cell.getCellIndex());
			
			int id = Integer.parseInt(img.getElement().getId().split("-")[1]);
			int idx = getInnerClubIndexById(id);
			ClubSettingViewData cvd = clubs.get(idx);
			final Club club = cvd.getInner();
			SC.ask("Вы уверены, что хотите удалить клуб\n" + club.getTitle(), new BooleanCallback() {
				
				@Override
				public void execute(Boolean value) {
					if (value) {
						Image.setVisible(img.getElement(), false);
						removeClub(club);
					}
				}
			});
		}
	};
	private void removeClub(Club club) {
		AppState.startSettingsService.removeClub(club.getClubId(), new AsyncCallback<Club>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Club result) {
				drawHeader();
				getAllClubs();
			}
		});
	}
	
	protected void importClub(Club innerClub) {
		AppState.startSettingsService.importClub(innerClub, new AsyncCallback<Club>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Club result) {
				drawHeader();
				getAllClubs();
			}
		});
	}

	private void getAllClubs() {
		AppState.startSettingsService.getAllClubs(
				new AsyncCallback<List<ClubSettingViewData>>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(List<ClubSettingViewData> result) {
						if (result != null) {
							clubs = result;
							drawHeader();
							drawContent();
						}
					}
				});
	}

	private void setClubIndepended(long id, boolean isIndepended) {
		AppState.startSettingsService.setClubIndependent(id, isIndepended, new AsyncCallback<Club>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.say("Error", caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Club result) {
				int idx = updateIndependent(result);
				CheckBox cb = (CheckBox) t.getWidget(clubs.get(idx).getRow(), 3);
				cb.setValue(result.isIndependent());
				cb.setEnabled(true);
			}
		});
	}
	
	private int getInnerClubIndexById(long id) {
		for (int i = 0; i < clubs.size(); i++) {
			ClubSettingViewData c = clubs.get(i);
			if (c.getInner() != null && c.getInner().getClubId() == id) {
				return i;
			}
		}
		return -1;
	}
	
	private int getOuterClubIndexById(long id) {
		for (int i = 0; i < clubs.size(); i++) {
			ClubSettingViewData c = clubs.get(i);
			if (c.getOuter() != null && c.getOuter().getClubId() == id) {
				return i;
			}
		}
		return -1;
	}
	
	private ClickHandler newClubHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (dlg == null) {
				dlg = new MyEventDialogBox();
				dlg.setAnimationEnabled(true);
				dlg.setAutoHideEnabled(true);
				dlg.setText("Настройки графика работ");
				VerticalPanel panel = new VerticalPanel();
				panel.add(new EditClubForm());
				dlg.add(panel);
			}

			dlg.center();
		}
	};
	
	@Override
	public void updateClub(Club p) {
		drawHeader();
		getAllClubs();
	}

	private int updateIndependent(Club club) {
		int idx = getInnerClubIndexById(club.getClubId());
		if (idx != -1) {
			ClubSettingViewData c = clubs.get(idx);
			c.getInner().setIndependent(club.isIndependent());
		}
		return idx;
	}
}

