package ua.nure.ostpc.malibu.shedule.client.draft;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeSet;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingPanel;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.DraftViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.shared.DateUtil;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.util.SC;

public class DraftPanel extends VerticalPanel implements ValueChangeHandler<Shift> {

	private FlexTable[] dt;
	private Date tStart;
	private Schedule s;
	private Map<Club, List<Employee>> cp;
	private HashSet<DraftShiftItem> widgets = new HashSet<DraftShiftItem>();
	
	public DraftPanel(Long periodId) {
		// TODO Auto-generated constructor stub
		super();
		LoadingPanel.stop();
		getDraftViewData(periodId);
//		initDraftTable(dtf.parse("18.05.2015"), dtf.parse("24.05.2015"));
		
	}
	
	private void getDraftViewData(Long periodId) {
		AppState.scheduleDraftService.getDraftView(periodId, new AsyncCallback<DraftViewData>() {

			@Override
			public void onFailure(Throwable caught) {
				LoadingPanel.stop();
				SC.say(caught.getMessage());
			}

			@Override
			public void onSuccess(DraftViewData result) {
				LoadingPanel.stop();
				s = result.getSchedule();
//				e = result.getEmployee();
				cp = result.getClubPrefs();
				initTableHeader();
				initTableBody();
			}
		});
		
	}

	void initTableHeader() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
		DateTimeFormat dtfc = DateTimeFormat.getFormat("E");
		
		int dur = CalendarUtil.getDaysBetween(s.getPeriod().getStartDate(), s.getPeriod().getEndDate()); // DateUtil.duration(start, end);
		int tabCount = dur / 7 + 1;
		dt = new FlexTable[tabCount];
		for (int t = 0; t < tabCount; t++) {
			dt[t] = new FlexTable();
			dt[t].addStyleName("MainTable");
			dt[t].addStyleName("mainTable");

			
			// Header rows
			dt[t].insertRow(0);
			dt[t].insertRow(1);

			dt[t].insertCell(0, 0);
			dt[t].getFlexCellFormatter().setRowSpan(0, 0, 1);
			InlineLabel l = new InlineLabel("Клубы");
			dt[t].setWidget(0, 0, l);
			
			// get start table date
			
			tStart = s.getPeriod().getStartDate();
			CalendarUtil.addDaysToDate(tStart, 0 - DateUtil.dayOfWeak(tStart));
			Date date = tStart;
			for (int col = 1; col <= 7; col++) {
				dt[t].insertCell(0, col);
				dt[t].setWidget(0, col, new InlineLabel(dtf.format(date)));
				dt[t].getCellFormatter().addStyleName(1, col, "secondHeader");
				dt[t].setWidget(1, col, new InlineLabel(dtfc.format(date)));
				dt[t].getCellFormatter().addStyleName(1, col, "secondHeader");
				CalendarUtil.addDaysToDate(date, 1);
			}
			add(dt[t]);
		}
	}
	
	private void sortByClub(List<ClubDaySchedule> lst) {
		java.util.Collections.sort(lst, new Comparator<ClubDaySchedule>() {

			@Override
			public int compare(ClubDaySchedule o1, ClubDaySchedule o2) {
				return o1.getClub().getTitle().compareToIgnoreCase(o2.getClub().getTitle());
			}
			
		});
	}
	
	private void addBodyRows(FlexTable tab, List<ClubDaySchedule> cds) {
		int row = 2; // First body row
		for (Iterator<ClubDaySchedule> iterator = cds.iterator(); iterator.hasNext();) {
			ClubDaySchedule clubDaySchedule = iterator.next();
			tab.insertRow(row);
			tab.insertCell(row, 0);
			tab.setWidget(row, 0, new Label(clubDaySchedule.getClub().getTitle()));
			tab.getRowFormatter().addStyleName(row, "prefferedClub");
			for (int i = 1; i < 8; i++) {
				tab.insertCell(row, i);
			}
			row++;
		}
		
	}
	
	private void initTableBody() {
		List<ClubDaySchedule> daySchedules = s.getDayScheduleMap().get(s.getPeriod().getStartDate());
		Date sd = s.getPeriod().getStartDate();
		Date ed = s.getPeriod().getEndDate();
		CalendarUtil.addDaysToDate(sd, 0 - DateUtil.dayOfWeak(sd));
		CalendarUtil.addDaysToDate(ed, 6 - DateUtil.dayOfWeak(ed));
		Date d = sd;
		for (int t = 0; t < dt.length; t++) {
			sortByClub(daySchedules);
			addBodyRows(dt[t], daySchedules);

			int col = 1;
			do {
				
				int row = 2;
				daySchedules = s.getDayScheduleMap().get(d);
				sortByClub(daySchedules);
				
				// By club
				if (daySchedules != null) {
					ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
					while (cdsIter.hasNext()) {
						setDraftShiftItem(dt[t], row++, col, cdsIter.next().getShifts());
					}
				}
				col++;
				CalendarUtil.addDaysToDate(d, 1);
			} while ((!d.after(ed)) && (DateUtil.dayOfWeak(d) != 0));
		}
	}
	
	public void setDraftShiftItem(FlexTable tab, int row, int col, 
			List<Shift> shifts) {
		VerticalPanel p = new VerticalPanel();
		for (int j = 0; j < shifts.size(); j++) {
			DraftShiftItem dsf = new DraftShiftItem(shifts.get(j));
			widgets.add(dsf);
			dsf.addValueChangeHandler(this);
			p.add(dsf);
		}
		tab.setWidget(row, col, p);
	}

	@Override
	public void onValueChange(ValueChangeEvent<Shift> event) {
		updateShift(event.getValue());
	}

	private void updateShift(Shift value) {
		AppState.scheduleDraftService.updateShift(value, s.getPeriod().getPeriodId(), new AsyncCallback<Schedule>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Schedule result) {
				s = result;
				for (DraftShiftItem dfs : widgets) {
					dfs.update();
				}
			}
		});
	}
}
