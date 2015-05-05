package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingPanel;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.Format;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.smartgwt.client.util.SC;

public class HolidaySettingsPanel extends SimplePanel {
	private Set<Holiday> holidays = new TreeSet<Holiday>();
	private Holiday oldHoliday = new Holiday();
	private boolean[] weekends;
	private FlexTable main;
	private FlexTable ht;
	private FlexTable wt;
	
	private ClickHandler removeClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public HolidaySettingsPanel() {
		if (main == null) {
			main = new FlexTable();
			main.insertRow(0);
			main.insertCell(0, 0);
			ht = new FlexTable();
			main.setWidget(0, 0, ht);
			drawHolidaysHeader();
			wt = new FlexTable();
			drawWeekendsHeader();
			main.setWidget(0, 1, wt);
		}
		setWidget(main);
		readHolidays();
		readWeekens();
	}


	private void readWeekens() {
		// TODO Auto-generated method stub
		
	}

	private void readHolidays() {
		LoadingPanel.start();
		AppState.startSettingsService.getHolidays(
				new AsyncCallback<Collection<Holiday>>() {
			
			@Override
			public void onSuccess(Collection<Holiday> result) {
				LoadingPanel.stop();
				holidays = new TreeSet<Holiday>(result);
				drawHolidaysContent(holidays);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				LoadingPanel.stop();
				SC.say("Ошибка", caught.getMessage());
			}
		});
		
	}

	protected void drawHolidaysHeader() {
//		if (ht == null)
//			ht = new FlexTable();
		int r = 0;
		int c = 0;
		ht.removeAllRows();
		ht.insertRow(r);
		ht.insertCell(r, c);
		final Label noLabel = new Label("№");
		ht.setWidget(r, c, noLabel);
		
		ht.insertCell(r, ++c);
		final Label dateLabel = new Label("Дата");
		ht.setWidget(r, c, dateLabel);
		
		ht.insertCell(r, ++c);
		final Image removeIcon = new Image("img/new_club.png"); 
		ht.setWidget(r, c, removeIcon);
	}

	protected void drawHolidaysContent() {
		if (holidays == null)
			return;
		drawHolidaysContent(holidays);
	}

	protected void drawHolidaysContent(Set<Holiday> holidays) {
		if (holidays == null) {
			return;
		}
		int r = 0;
		Format format = new DateBox.DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy"));
		for (Holiday h: holidays) {
			int c = 0;
			ht.setWidget(++r, c++, new HTML(String.valueOf(r)));
			
			final DateBox d = new DateBox(new DatePicker(), h.getDate(), format);
			d.getDatePicker().setYearAndMonthDropdownVisible(true);
			d.getElement().setId(String.valueOf(h.getHolidayId()));
			d.addValueChangeHandler(new ValueChangeHandler<Date>() {

				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					DateBox db = (DateBox) event.getSource();
					long id = Long.parseLong(db.getElement().getId());
					for (Holiday holiday : HolidaySettingsPanel.this.holidays) {
						if (holiday.getHolidayId() == id) {
							// backup holiday
//							oldHoliday.setHolidayid(holiday.getHolidayId());
//							oldHoliday.setDate(holiday.getDate());
//							oldHoliday.setRepeate(holiday.getRepeate());
							holiday.setDate(event.getValue());
							updateHoliday(holiday);
							break;
						}
					}
				}
			});
			ht.setWidget(r, c++, d);
			
			final Image removeIcon = new Image("img/remove.png");
			removeIcon.addClickHandler(removeClickHandler);
			ht.setWidget(r, c++, removeIcon);
			// TODO: Styling
			
		}
	}
	
	protected void updateHoliday(Holiday holiday) {
		LoadingPanel.start();
		AppState.startSettingsService.updateHoliday(holiday, new AsyncCallback<Long>() {
			
			@Override
			public void onSuccess(Long result) {
				LoadingPanel.stop();
			}

			@Override
			public void onFailure(Throwable caught) {
				LoadingPanel.stop();
				
				// Restore previous value
//				for (Holiday holiday : holidays) {
//					if (holiday.getHolidayId() == oldHoliday.getHolidayId()) {
//						holiday.setDate(oldHoliday.getDate());
//						DateBox db = (DateBox) getWidget(DOM.getElementById(String.valueOf(oldHoliday.getHolidayId())));
//						db.setValue(holiday.getDate());
//						break;
//					}
//				}
				Window.alert("Такая дата уже существует");
				drawHolidaysHeader();
				readHolidays();
			}
		});
		
	}


	private void drawWeekendsHeader() {
		int r = 0;
		int c = 0;
		wt.removeAllRows();
		wt.insertRow(r);
		wt.insertCell(r, c);
		final Label dayLabel = new Label("День недели");
		dayLabel.setWordWrap(true);
		wt.setWidget(r, c, dayLabel);
	}
	protected void drawWeekendContent(boolean[] wends) {
		// TODO Auto-generated method stub
//		if (wends == null) {
//			return;
//		}
		int r = 0;
		for (boolean w: wends) {
			final CheckBox cb = new CheckBox(String.valueOf(r));
			cb.getElement().setId(String.valueOf(r));
			cb.setValue(w);
			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					CheckBox c = (CheckBox) event.getSource();
					int id = Integer.parseInt(c.getElement().getId());
					weekends[id] = event.getValue();
					saveWeekens();
				}
			});
			wt.setWidget(++r, 0, cb);
		}
	}


	protected void saveWeekens() {
		// TODO Auto-generated method stub
		
	}
	
	public static IsWidget getWidget(Element element) {
	    EventListener listener = DOM
	            .getEventListener((Element) element);
	    // No listener attached to the element, so no widget exist for this
	    // element
	    if (listener == null) {
	        return null;
	    }
	    if (listener instanceof Widget) {
	        // GWT uses the widget as event listener
	        return (Widget) listener;
	    }
	    return null;
	}
}

