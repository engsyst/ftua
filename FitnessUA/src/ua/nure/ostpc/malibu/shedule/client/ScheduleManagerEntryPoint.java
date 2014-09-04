package ua.nure.ostpc.malibu.shedule.client;

import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class ScheduleManagerEntryPoint implements EntryPoint {
	private int counter = 1;

	public void onModuleLoad() {

		final ListGrid listGrid = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {

				String fieldName = this.getFieldName(colNum);

				if (fieldName.equals("Статус")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(60);
					button.setTitle("");
					button.setIcon("/img/" + record.getAttribute("Статус")
							+ ".png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							SC.say("Статус расписания: "
									+ record.getAttribute("Статус"));
						}
					});
					return button;
				} else if (fieldName.equals("view")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("");
					button.setIcon("/img/view_icon.png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							SC.say("Режим просмотра выбран");
						}
					});
					return button;
				} else if (fieldName.equals("edit")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("");
					button.setIcon("/img/file_edit.png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							SC.say("Режим редактирования запущен");
						}
					});
					return button;
				} else if (fieldName.equals("emails")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("");
					button.setIcon("/img/mail_send.png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							SC.say("Расписание отправлено");
						}
					});
					return button;
				} else {
					return null;
				}

			}
		};
		listGrid.setShowRecordComponents(true);
		listGrid.setShowRecordComponentsByCell(true);
		listGrid.setCanRemoveRecords(true);
		ListGridField rowNum = new ListGridField("itemNum", "No.");
		rowNum.setWidth(35);
		rowNum.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				return rowNum + "";
			}
		});

		ListGridField status = new ListGridField("Статус");
		ListGridField start = new ListGridField("Дата начала");
		ListGridField end = new ListGridField("Дата окончания");
		ListGridField view = new ListGridField("view", "Просмотр");
		ListGridField edit = new ListGridField("edit", "Редактирование");
		ListGridField emails = new ListGridField("emails", "Отправить");

		listGrid.setFields(rowNum, status, start, end, view, edit, emails);
		ListGridRecord rec = new ListGridRecord();
		int number = getNextNumber();
		rec.setAttribute("rowNum", number);
		rec.setAttribute("Статус", "Готово");
		rec.setAttribute("Дата начала", "16/08/2014");
		rec.setAttribute("Дата окончания", "22/08/2014");
		rec.setAttribute("Просмотр", "Просмотреть");
		rec.setAttribute("Редактирование", "Редактировать");
		rec.setAttribute("Отправить", "отправить");
		ListGridRecord rec1 = new ListGridRecord();
		listGrid.addData(rec);
		number = getNextNumber();
		rec1.setAttribute("rowNum", number);
		rec1.setAttribute("Статус", "Черновик");
		rec1.setAttribute("Дата начала", "16/08/2014");
		rec1.setAttribute("Дата окончания", "22/08/2014");
		rec1.setAttribute("Просмотр", "Просмотреть");
		rec1.setAttribute("Редактирование", "Редактировать");
		rec1.setAttribute("Отправить", "отправить");
		listGrid.addData(rec1);
		ListGridRecord rec2 = new ListGridRecord();
		number = getNextNumber();
		rec2.setAttribute("rowNum", number);
		rec2.setAttribute("Статус", "Окончено");
		rec2.setAttribute("Дата начала", "16/08/2014");
		rec2.setAttribute("Дата окончания", "22/08/2014");
		rec2.setAttribute("Просмотр", "Просмотреть");
		rec2.setAttribute("Редактирование", "Редактировать");
		rec2.setAttribute("Отправить", "отправить");
		listGrid.addData(rec2);
		listGrid.setWidth(600);
		listGrid.setHeight(224);
		listGrid.draw();

	}

	private int getNextNumber() {
		return counter++;
	}
}