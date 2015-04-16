package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.List;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmployeeSettingsData;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

public class EmployeeSettingsPanel extends SimplePanel {
	private List<EmployeeSettingsData> data;
	private FlexTable t;

	public EmployeeSettingsPanel() {
		drawHeader();
		setWidget(t);
		getAllEmployees();
	}

	private void getAllEmployees() {
		AppState.startSettingsService
				.getEmployeeSettingsData(new AsyncCallback<List<EmployeeSettingsData>>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(List<EmployeeSettingsData> result) {
						if (result != null) {
							data = result;
							drawHeader();
							drawContent();
						}
					}

				});
	}

	private void drawHeader() {
		int curColumn = 0;
		if (t == null)
			t = new FlexTable();
		t.removeAllRows();
		t.setStyleName("mainTable");
		t.addStyleName("settingsTable");
		t.insertRow(0);

		InlineLabel l = new InlineLabel("Сотрудник");
		l.setWordWrap(true);
		l.setTitle("Сотрудники из подсистемы учета кадров");
		t.insertCell(0, curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Импорт");
		l.setWordWrap(true);
		l.setTitle("Для импорта нажмите стрелку\n");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Сотрудник");
		l.setWordWrap(true);
		l.setTitle("Сотрудники в подсистеме составления графика работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Администратор");
		l.setWordWrap(true);
		l.setTitle("Если установлен, то данный сотрудник используется при составлении графика работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Ответственное лицо");
		l.setWordWrap(true);
		l.setTitle("Если установлен, то данный сотрудник может создавать и редактировать графики работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		l = new InlineLabel("Подписка");
		l.setWordWrap(true);
		l.setTitle("Если установлен, то данный сотрудник будет получать рассылку графиков работ");
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, l);

		Image img = new Image("img/new_club.png");
		img.setStyleName("myImageAsButton");
		img.setTitle("Добавить нового сотрудника в подсистему составления графика работ");
		// img.addClickHandler(newEmployeeHandler);
		t.insertCell(0, ++curColumn);
		t.setWidget(0, curColumn, img);

		// Styling
		for (int i = 0; i < t.getCellCount(0); i++) {
			t.getFlexCellFormatter().addStyleName(0, i, "mainHeader");
		}
	}

	private void drawContent() {
		if (data == null)
			return;
		int row = 1;
		for (EmployeeSettingsData esd : data) {
			if (esd.getInEmployee() != null && esd.getInEmployee().isDeleted()) {
				esd.setRow(-1);
				continue;
			}

			t.insertRow(row);
			esd.setRow(row);
			for (int i = 0; i < 7; i++) {
				t.insertCell(row, i);
			}

			if (esd.getOutEmployee() != null) {
				t.setWidget(row, 0, new InlineLabel(esd.getOutEmployee()
						.getShortName()));
			}

			if (esd.getOutEmployee() != null && esd.getInEmployee() == null) {
				Image img = new Image("img/import.png");
				img.setTitle("Импортировать сотрудника");
				img.setStyleName("buttonImport");
				t.setWidget(row, 1, img);
				img.addClickHandler(importEmployeeHandler);
				img.getElement().setId(
						"imp-" + esd.getOutEmployee().getEmployeeId());
			}

			if (esd.getInEmployee() != null) {
				t.setWidget(row, 2, new ScheduleEmployeeNameLabel(esd
						.getInEmployee().getShortName(), esd.getInEmployee()
						.getEmployeeId()));

				CheckBox cb = new CheckBox();
				cb.setTitle("Администратор");
				t.setWidget(row, 3, cb);
				cb.addClickHandler(checkHandler);
				if (esd.getRoles() != null)
					for (Role r : esd.getRoles()) {
						if (Right.ADMIN.equals(r.getRight())) {
							cb.getElement().setId(
									"cb-"
											+ r.getRoleId()
											+ "-"
											+ esd.getInEmployee()
											.getEmployeeId());
							cb.setValue(true);
						}
					}
				
				cb = new CheckBox();
				cb.setTitle("Ответственное лицо");
				t.setWidget(row, 4, cb);
				cb.addClickHandler(checkHandler);
				if (esd.getRoles() != null)
					for (Role r : esd.getRoles()) {
						if (Right.RESPONSIBLE_PERSON.equals(r.getRight())) {
							cb.getElement().setId(
									"cb-"
											+ r.getRoleId()
											+ "-"
											+ esd.getInEmployee()
													.getEmployeeId());
							cb.setValue(true);
						}
					}

				cb = new CheckBox();
				cb.setTitle("Подписчик");
				t.setWidget(row, 5, cb);
				cb.addClickHandler(checkHandler);
				if (esd.getRoles() != null)
					for (Role r : esd.getRoles()) {
						if (Right.SUBSCRIBER.equals(r.getRight())) {
							cb.getElement().setId(
									"cb-"
											+ r.getRoleId()
											+ "-"
											+ esd.getInEmployee()
													.getEmployeeId());
							cb.setValue(true);
						}
					}

				Image img = new Image("img/remove.png");
				img.setTitle("Удалить сотрудника");
				t.setWidget(row, 6, img);
				if (AppState.employee.getEmployeeId() != esd.getInEmployee().getEmployeeId()) {
					img.addClickHandler(deleteEmployeeHandler);
					img.addStyleName("myImageAsButton");
				}
				img.getElement().setId(
						"del-" + esd.getInEmployee().getEmployeeId());
			}
			row++;
		}
	}

	private ClickHandler importEmployeeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Cell cell = t.getCellForEvent(event);
			final Image img = (Image) t.getWidget(cell.getRowIndex(),
					cell.getCellIndex());

			int id = Integer.parseInt(img.getElement().getId().split("-")[1]);
			int idx = getOuterIndexById(id);
			EmployeeSettingsData esd = data.get(idx);
			importEmployee(esd.getOutEmployee());
		}
	};

	protected void importEmployee(Employee outEmployee) {
		AppState.startSettingsService.importEmployee(outEmployee,
				new AsyncCallback<Employee>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Employee result) {
						drawHeader();
						getAllEmployees();
					}
				});
	}

	private ClickHandler deleteEmployeeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {

			Cell cell = t.getCellForEvent(event);
			final Image img = (Image) t.getWidget(cell.getRowIndex(),
					cell.getCellIndex());

			int id = Integer.parseInt(img.getElement().getId().split("-")[1]);
			int idx = getInnerIndexById(id);
			EmployeeSettingsData esd = data.get(idx);
			final Employee e = esd.getInEmployee();
			SC.ask("Вы уверены, что хотите удалить сотрудника\n"
					+ e.getShortName(), new BooleanCallback() {

				@Override
				public void execute(Boolean value) {
					if (value) {
						Image.setVisible(img.getElement(), false);
						removeEmployee(e);
					}
				}
			});
		}
	};

	private void removeEmployee(Employee emp) {
		AppState.startSettingsService.removeEmployee(emp.getEmployeeId(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						drawHeader();
						getAllEmployees();
					}
				});
	}

	private ClickHandler checkHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Cell cell = t.getCellForEvent(event);
			CheckBox cb = (CheckBox) t.getWidget(cell.getRowIndex(),
					cell.getCellIndex());
			cb.setEnabled(false);
			try {
				String[] ids = cb.getElement().getId().split("-");
				setEmployeeRole(Long.parseLong(ids[2]), Long.parseLong(ids[1]),
						cb.getValue());
			} catch (Exception e) {
			}
		}

	};

	private void setEmployeeRole(long empId, long roleId, boolean enable) {
		AppState.startSettingsService.updateEmployeeRole(empId, roleId, enable,
				new AsyncCallback<long[]>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}

					@Override
					public void onSuccess(long[] result) {
						if (result == null) {
							SC.say("Ошибка");
							return;
						}
						try {
							int idx = getInnerIndexById(result[0]);
							CheckBox cb = (CheckBox) t.getWidget(idx,
									(int) (2 + result[1]));
							cb.setEnabled(true);
						} catch (NumberFormatException e) {
							SC.say("Ошибка");
							return;
						}
					}
				});
	}

	private int getInnerIndexById(long id) {
		for (int i = 0; i < data.size(); i++) {
			EmployeeSettingsData esd = data.get(i);
			if (esd.getInEmployee() != null
					&& esd.getInEmployee().getEmployeeId() == id) {
				return i;
			}
		}
		return -1;
	}

	private int getOuterIndexById(long id) {
		for (int i = 0; i < data.size(); i++) {
			EmployeeSettingsData esd = data.get(i);
			if (esd.getOutEmployee() != null
					&& esd.getOutEmployee().getEmployeeId() == id) {
				return i;
			}
		}
		return -1;
	}

}
