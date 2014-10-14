package ua.nure.ostpc.malibu.shedule.client;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;

public class LookingNearest extends SimplePanel {

	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);

	private Schedule schedule;

	public LookingNearest() {
		Date dateTime = new Date(System.currentTimeMillis());
		scheduleManagerService.getCurrentSchedule(dateTime,
				new AsyncCallback<Schedule>() {

					@Override
					public void onSuccess(Schedule result) {
						setSchedule(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Возникли проблемы с сервером, обратитесь к системному администратору");
					}
				});
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 10) {
					if (schedule != null) {
						cancel();
						drawPage();
					}
					count++;
				} else {
					Window.alert("Текущее расписание не составлено");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(100);
	}

	private void drawPage() {
		int count = 1;
		Map<Date, List<ClubDaySchedule>> dayScheduleMap = schedule
				.getDayScheduleMap();
		Set<Date> dates = dayScheduleMap.keySet();
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("myBestFlexTable");
		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.setText(0, 0, "");
		flexTable.insertCell(0, 1);
		for (int i = 1; i < 15; i++) {
			flexTable.insertCell(0, i);
		}
		Window.alert(Integer.toString(dates.size()));
		Iterator<Date> dateIterator = dates.iterator();
		while (dateIterator.hasNext() || count < 8) {
			String string = dateIterator.next().toString();
			try {
			if (string !=null){
				Window.alert("I'm here");
				flexTable.setText(0, count, string);
				count++;
			}
			}
			catch (Exception ex) {
				Window.alert(ex.getMessage());
			}
		}
		setWidget(flexTable);
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

}
