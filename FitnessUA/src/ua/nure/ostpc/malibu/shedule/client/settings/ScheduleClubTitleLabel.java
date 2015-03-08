package ua.nure.ostpc.malibu.shedule.client.settings;

import ua.nure.ostpc.malibu.shedule.client.DialogBoxUtil;
import ua.nure.ostpc.malibu.shedule.entity.Club;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class ScheduleClubTitleLabel extends Label {
	private Club club;

	public ScheduleClubTitleLabel(Club club) {
		this.club = club;
		setText(club.getTitle());
		setStyleName("cursor");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ScheduleClubTitleLabel clubTitleLabel = (ScheduleClubTitleLabel) event
						.getSource();
				Club club = clubTitleLabel.getClub();
				EditClubForm editClubForm = new EditClubForm(club);
				DialogBoxUtil.callDialogBox(editClubForm);
			}
		});
	}

	public Club getClub() {
		return club;
	}
}
