package ua.nure.ostpc.malibu.shedule.client.draft;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingImagePanel;
import ua.nure.ostpc.malibu.shedule.client.editor.ScheduleWeekTable;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.DraftViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentInfo;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentResultInfo;
import ua.nure.ostpc.malibu.shedule.shared.DateUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.util.SC;

public class DraftPanel extends VerticalPanel implements
		ValueChangeHandler<Boolean> {

	private FlexTable[] dt;
	private Schedule schedule;
	private Map<Long, HashSet<String>> prefSetMap;
	private HashSet<Club> pc;
	private DraftShiftItem[][][][] widgets;
	// private TreeSet<DraftShiftItem>[] widgets;
	private final static int HEADER_ROWS = 2;

	public DraftPanel(Long periodId) {
		setStyleName("ScheduleBlock");
		// LoadingPanel.stop();
		getDraftViewData(periodId);
		// initDraftTable(dtf.parse("18.05.2015"), dtf.parse("24.05.2015"));

	}

	private void getDraftViewData(Long periodId) {
		LoadingImagePanel.start();
		AppState.scheduleDraftService.getDraftView(periodId,
				new AsyncCallback<DraftViewData>() {

					@Override
					public void onFailure(Throwable caught) {
						LoadingImagePanel.stop();
						SC.say(caught.getMessage());
					}

					@Override
					public void onSuccess(DraftViewData result) {
						LoadingImagePanel.stop();
						if (result.getSchedule() != null) {
							schedule = result.getSchedule();
							prefSetMap = result.getPrefSetMap();
							// e = result.getEmployee();
							getPrefferedClubs(result.getClubPrefs());
							redraw();
						} else {
							SC.warn("Данный график работы не существует или был ранее удалён!");
							History.newItem(AppConstants.HISTORY_MANAGE);
						}
					}
				});

	}

	private void redraw() {
		clear();
		initTableHeader();
		initTableBody();
		updateAddEnabled();
	}

	Comparator<DraftShiftItem> comparator = new Comparator<DraftShiftItem>() {

		@Override
		public int compare(DraftShiftItem o1, DraftShiftItem o2) {
			int r = compareInt(o1.getTab(), o2.getTab());
			if (r != 0) {
				return r;
			} else {
				r = compareInt(o1.getCol(), o2.getCol());
				if (r != 0) {
					return r;
				}
				return compareInt(o1.getRow(), o2.getRow());
			}
		}

		private int compareInt(int x, int y) {
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}
	};

	void initTableHeader() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
		DateTimeFormat dtfc = DateTimeFormat.getFormat("E");

		Date sd = CalendarUtil.copyDate(schedule.getPeriod().getStartDate());
		CalendarUtil.addDaysToDate(sd, 0 - DateUtil.dayOfWeak(sd));
		Date ed = CalendarUtil.copyDate(schedule.getPeriod().getEndDate());
		CalendarUtil.addDaysToDate(ed, 6 - DateUtil.dayOfWeak(ed));
		int dur = CalendarUtil.getDaysBetween(sd, ed) + 1; // DateUtil.duration(start,
															// end);
		int tabCount = dur / 7;
		if (dt == null) {
			dt = new FlexTable[tabCount];
		}
		widgets = new DraftShiftItem[tabCount][][][];
		Date d = sd;
		for (int t = 0; t < tabCount; t++) {
			if (dt[t] == null)
				dt[t] = new FlexTable();
			dt[t].removeAllRows();
			dt[t].addStyleName("mainTable");

			// Header rows
			dt[t].insertRow(0);
			dt[t].insertRow(1);

			dt[t].insertCell(0, 0);
			dt[t].getFlexCellFormatter().setRowSpan(0, 0, 1);
			dt[t].setText(0, 0, "Клубы");
			int weekOfYear = ScheduleWeekTable.getWeekOfYear(sd);
			dt[t].setText(1, 0, "Неделя: " + weekOfYear);
			dt[t].getColumnFormatter().setStyleName(0, "clubColumn");

			dt[t].getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
			dt[t].getFlexCellFormatter().addStyleName(1, 0, "secondHeader");

			// get start table date

			for (int col = 1; col < 8; col++) {
				dt[t].insertCell(0, col);
				dt[t].getColumnFormatter().setStyleName(col, "scheduleColumn");
				dt[t].setWidget(0, col, new InlineLabel(dtf.format(d)));
				dt[t].getCellFormatter().addStyleName(0, col, "mainHeader");
				dt[t].setWidget(1, col, new InlineLabel(dtfc.format(d)));
				dt[t].getCellFormatter().addStyleName(1, col, "secondHeader");
				CalendarUtil.addDaysToDate(d, 1);
			}
			add(dt[t]);
			if (t + 1 < tabCount) {
				Image divideImage = createDivideImage();
				add(divideImage);
				divideImage.getElement().getParentElement()
						.addClassName("dsi-dividerPanel");
			}
		}
	}

	public static Image createDivideImage() {
		Image divideImage = new Image(GWT.getHostPageBaseURL()
				+ "img/divider.png");
		divideImage.setStyleName("dsi-divider");
		return divideImage;
	}

	private void sortByClub(List<ClubDaySchedule> lst) {
		java.util.Collections.sort(lst, new Comparator<ClubDaySchedule>() {

			@Override
			public int compare(ClubDaySchedule o1, ClubDaySchedule o2) {
				return o1.getClub().getTitle()
						.compareToIgnoreCase(o2.getClub().getTitle());
			}

		});
	}

	private void addBodyRows(FlexTable tab, List<ClubDaySchedule> cds) {
		int row = 2; // First body row
		for (Iterator<ClubDaySchedule> iterator = cds.iterator(); iterator
				.hasNext();) {
			ClubDaySchedule clubDaySchedule = iterator.next();
			tab.insertRow(row);
			tab.insertCell(row, 0);

			VerticalPanel verticalPanel = new VerticalPanel();
			verticalPanel.setStyleName("draftVPanel");
			verticalPanel.setHorizontalAlignment(ALIGN_CENTER);
			verticalPanel.setVerticalAlignment(ALIGN_MIDDLE);
			Label clubTitle = new Label(clubDaySchedule.getClub().getTitle());
			verticalPanel.add(clubTitle);
			ListBox listBox = new ListBox();
			listBox.setStyleName("selectItem");
			HashSet<String> valueSet = prefSetMap.get(clubDaySchedule.getClub()
					.getClubId());
			if (valueSet != null) {
				for (String value : valueSet) {
					listBox.addItem(value);
				}
			}
			verticalPanel.add(listBox);
			tab.setWidget(row, 0, verticalPanel);

			if (pc.contains(clubDaySchedule.getClub())) {
				tab.getRowFormatter().addStyleName(row, "prefferedClub");
			}
			for (int i = 1; i < 8; i++) {
				tab.insertCell(row, i);
				if (pc.contains(clubDaySchedule.getClub())) {
					tab.getCellFormatter().setStyleName(row, i, "prefDayCell");
				} else {
					tab.getCellFormatter().setStyleName(row, i, "dayCell");
				}
			}
			row++;
		}

	}

	private void initTableBody() {
		List<ClubDaySchedule> daySchedules = schedule.getDayScheduleMap().get(
				schedule.getPeriod().getStartDate());
		Date sd = CalendarUtil.copyDate(schedule.getPeriod().getStartDate());
		Date ed = CalendarUtil.copyDate(schedule.getPeriod().getEndDate());
		CalendarUtil.addDaysToDate(sd, 0 - DateUtil.dayOfWeak(sd));
		CalendarUtil.addDaysToDate(ed, 6 - DateUtil.dayOfWeak(ed));
		Date d = CalendarUtil.copyDate(sd);
		for (int t = 0; t < dt.length; t++) {
			// sortByClub(daySchedules);
			widgets[t] = new DraftShiftItem[8][daySchedules.size()
					+ HEADER_ROWS][];
			addBodyRows(dt[t], daySchedules);

			int col = 1;
			do {

				int row = 2;
				daySchedules = schedule.getDayScheduleMap().get(d);

				// By club
				if (daySchedules != null) {
					// sortByClub(daySchedules);
					ListIterator<ClubDaySchedule> cdsIter = daySchedules
							.listIterator();
					while (cdsIter.hasNext()) {
						setDraftShiftItem(dt[t], t, row++, col, cdsIter.next()
								.getShifts());
					}
				}
				col++;
				CalendarUtil.addDaysToDate(d, 1);
			} while ((!d.after(ed)) && (DateUtil.dayOfWeak(d) != 0));
		}
	}

	public void setDraftShiftItem(FlexTable tab, int t, int row, int col,
			List<Shift> shifts) {
		VerticalPanel p = new VerticalPanel();
		p.addStyleName("dsi-shiftsPanel");
		widgets[t][col][row] = new DraftShiftItem[shifts.size()];
		for (int j = 0; j < shifts.size(); j++) {
			DraftShiftItem dsf = new DraftShiftItem(shifts.get(j), t, row, col);
			widgets[t][col][row][j] = dsf;
			dsf.addValueChangeHandler(this);
			p.add(dsf);
			if (j + 1 < shifts.size()) {
				Image div = new Image("img/divider.png");
				div.setStyleName("dsi-divider");
				p.add(div);
				div.getElement().getParentElement()
						.addClassName("dsi-dividerPanel");
			}
		}
		tab.setWidget(row, col, p);
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		DraftShiftItem draftShiftItem = (DraftShiftItem) event.getSource();
		long periodId = schedule.getPeriod().getPeriodId();
		long shiftId = draftShiftItem.getShift().getShiftId();
		boolean isAdded = event.getValue();
		AssignmentInfo assignmentInfo = new AssignmentInfo(periodId, shiftId,
				isAdded);
		updateShift(assignmentInfo);
	}

	private void updateColumn(int t, int c) {
		for (int r = HEADER_ROWS; r < widgets[t][c].length; r++) {
			for (int s = 0; s < widgets[t][c][r].length; s++) {
				if (widgets[t][c][r][s] != null)
					widgets[t][c][r][s].setAddEnabled(false);
			}
		}

	}

	private void updateAddEnabled() {
		for (int t = 0; t < widgets.length; t++) {
			for (int c = 0; c < widgets[t].length; c++) {
				boolean nextColumn = false;
				if (widgets[t][c] != null) {
					for (int r = HEADER_ROWS; r < widgets[t][c].length; r++) {
						if (widgets[t][c][r] != null && !nextColumn) {
							for (int i = 0; i < widgets[t][c][r].length; i++) {
								if (widgets[t][c][r][i] != null) {
									if (((DraftShiftItem) widgets[t][c][r][i])
											.getShift().isFull())
										widgets[t][c][r][i]
												.setAddEnabled(false);
									else
										widgets[t][c][r][i].setAddEnabled(true);
									if (widgets[t][c][r][i].getShift()
											.containsEmployee(
													AppState.employee
															.getEmployeeId())) {
										// for all in this column set disable
										// AddButton
										updateColumn(t, c);
										nextColumn = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void updateShift(AssignmentInfo assignmentInfo) {
		LoadingImagePanel.start();
		AppState.scheduleDraftService.updateShift(assignmentInfo,
				new AsyncCallback<AssignmentResultInfo>() {

					@Override
					public void onSuccess(AssignmentResultInfo result) {
						LoadingImagePanel.stop();
						schedule = result.getSchedule();
						redraw();
						if (!result.getUpdateResult()) {
							SC.say("Данное место уже занято!");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						LoadingImagePanel.stop();
						SC.say(caught.getLocalizedMessage());
					}
				});
	}

	private HashSet<Club> getPrefferedClubs(Map<Club, List<Employee>> cp) {
		pc = new HashSet<Club>();
		Set<Club> clubs = cp.keySet();
		for (Iterator<Club> iterator = clubs.iterator(); iterator.hasNext();) {
			Club club = iterator.next();
			if (cp.get(club).contains(AppState.employee))
				pc.add(club);
		}
		return pc;
	}
}
