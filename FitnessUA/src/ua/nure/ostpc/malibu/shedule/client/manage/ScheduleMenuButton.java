package ua.nure.ostpc.malibu.shedule.client.manage;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.shared.OperationCallException;

public class ScheduleMenuButton extends PopupButton implements HasValueChangeHandlers<Boolean>{
	public final static String DRAFT_IMG_URL = "img/draft.png";
	public final static String FUTURE_IMG_URL = "img/future.png";
	public final static String STASUS_DRAFT = "Черновик";
	public final static String STASUS_FUTURE = "Принят";
	
	private Period period;

	public ScheduleMenuButton(Period period) {
		super(String.valueOf(period), getImageUrl(period.getStatus()), null);
		this.period = period;
		Status status = this.period.getStatus();
		if (status != Status.DRAFT || status != Status.FUTURE)
			return;
		menu.addPopupItem("Удалить" + getNewStatus(period), removeSchedule);
		menu.addPopupItem("Сделать как " + getNewStatus(period), changeScheduleStatus);
	}

	private String getNewStatus(Period p) {
		if (p.getStatus() == Status.DRAFT)
			return STASUS_FUTURE;
		return STASUS_DRAFT;
	}

	private ScheduledCommand changeScheduleStatus = new ScheduledCommand() {
		
		@Override
		public void execute() {
			changeScheduleStatus();
		}
	};
	
	protected void changeScheduleStatus() {
		
	}
	
	private ScheduledCommand removeSchedule = new ScheduledCommand() {

		@Override
		public void execute() {
			DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy");
			SC.confirm("Внимание", "Вы дествительно хотите удалить этот график работ?\n"
					+ "на период с: " + dtf.format(period.getStartDate()) 
					+ " по: " + dtf.format(period.getStartDate())
					+ "\nВместе с ним будут удалены все назначения сотрудников!!!", 
					new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if (value == null || value.equals(false)) return;
								removeSchedule();
						}
					});
		}
	};

	protected void removeSchedule() {
		AppState.scheduleManagerService.removeSchedule(period.getPeriodId(), 
				new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						SC.say("График работ удален");
					}
					
					@Override
					public void onFailure(Throwable caught) {
						SC.say("Произошла ошибка удаления графика работ.\n"
								+ "Проверьте имеет ли данный график статус 'Черновик'?");
					}
				});
		
	}
	
	private static String getImageUrl(Status status) {
		switch (status) {
		case DRAFT:
			return DRAFT_IMG_URL;
		case FUTURE:
			return FUTURE_IMG_URL;
		default:
			throw new IllegalArgumentException("Illegal shedule status");
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Boolean> handler) {
		return  addHandler(handler, ValueChangeEvent.getType());
	}

}
