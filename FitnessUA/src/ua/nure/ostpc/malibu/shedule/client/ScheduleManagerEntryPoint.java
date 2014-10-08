package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.panel.creation.CreateScheduleEntryPoint;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class ScheduleManagerEntryPoint implements EntryPoint {

	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);

	private List<Period> periodList;
	private Map<Long, Status> scheduleStatusMap;
	List<Role> roles = null;
	private int counter = 1;
	private Boolean isResponsible = false;
	ListGrid listGrid = new ListGrid();

	public void onModuleLoad() {
		getAllPeriodsFromServer();
		getScheduleStatusMapFromServer();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 10) {
					if (periodList != null && scheduleStatusMap != null) {
						cancel();
						drawPrimaryPage();
						// drawPage();
					}
					count++;
				} else {
					Window.alert("Cannot get data from server!");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(100);
	}

	private void getAllPeriodsFromServer() {
		scheduleManagerService.getAllPeriods(new AsyncCallback<List<Period>>() {

			@Override
			public void onSuccess(List<Period> result) {
				if (result != null) {
					periodList = result;
				} else {
					periodList = new ArrayList<Period>();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot get clubs from server!");
			}
		});
	}

	private void getScheduleStatusMapFromServer() {
		scheduleManagerService
				.getScheduleStatusMap(new AsyncCallback<Map<Long, Status>>() {

					@Override
					public void onSuccess(Map<Long, Status> result) {
						if (result != null) {
							scheduleStatusMap = result;
						} else {
							scheduleStatusMap = new HashMap<Long, Status>();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot get clubs from server!");
					}
				});
	}

	private void drawPage(AbsolutePanel absolutePanel) {
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
							scheduleManagerService
									.userRoles(new AsyncCallback<List<Role>>() {

										@Override
										public void onFailure(Throwable caught) {

										}

										@Override
										public void onSuccess(List<Role> result) {
											roles = result;
											for (Role role : roles) {
												if (role.getRight() == Right.RESPONSIBLE_PERSON) {
													isResponsible = true;
												}

											}
											if (isResponsible == true) {
												Window.alert("Before");
												long periodId = Long.parseLong(record
														.getAttribute("Редактирование"));
												Window.alert(String
														.valueOf(periodId));
												scheduleManagerService
														.lockSchedule(
																periodId,
																new AsyncCallback<Boolean>() {

																	@Override
																	public void onSuccess(
																			Boolean result) {
																		if (result) {
																			SC.say("Режим редактирования запущен");

																		} else {
																			SC.say("Режим редактирования не запущен");
																		}
																	}

																	@Override
																	public void onFailure(
																			Throwable caught) {
																		SC.say("ошибка!!");
																	}
																});
											} else {
												SC.say("Режим черновика запущен");
											}
										}
									});

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
		int number = 1;
		for (Period period : periodList) {
			ListGridRecord rec = new ListGridRecord();
			rec.setAttribute("rowNum", period.getPeriodId());
			rec.setAttribute("Статус",
					scheduleStatusMap.get(period.getPeriodId()));
			rec.setAttribute("Дата начала", period.getStartDate());
			rec.setAttribute("Дата окончания", period.getEndDate());
			rec.setAttribute("Просмотр", period.getPeriodId());
			rec.setAttribute("Редактирование", period.getPeriodId());
			rec.setAttribute("Отправить", period.getPeriodId());
			listGrid.addData(rec);
			number = getNextNumber();
		}
		listGrid.setWidth(600);
		listGrid.setHeight(224);
		absolutePanel.add(listGrid);
		listGrid.draw();
	}

	private int getNextNumber() {
		return counter++;
	}

//	private void drawPrimaryPage() {
//		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
//		rootPanel.setSize("100%", "100%");
//
//		AbsolutePanel MainPanel = new AbsolutePanel();
//		MainPanel.setStyleName("ExternalPanel");
//		rootPanel.add(MainPanel, 10, 10);
//		MainPanel.setSize("98%", "98%");
//
//		DockPanel dockPanel = new DockPanel();
//		dockPanel.setStyleName("MainPanel");
//		MainPanel.add(dockPanel, 10, 10);
//		dockPanel.setSize("98%", "98%");
//
//		AbsolutePanel absolutePanel = new AbsolutePanel();
//		absolutePanel.setStyleName("MainPanel");
//
//		dockPanel.add(absolutePanel, DockPanel.WEST);
//		dockPanel.setCellHeight(absolutePanel, "100%");
//		dockPanel.setCellWidth(absolutePanel, "14%");
//		absolutePanel.setSize("98%", "98%");
//
//		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
//		absolutePanel_1.setStyleName("MainPanel");
//		dockPanel.add(absolutePanel_1, DockPanel.NORTH);
//		absolutePanel_1.setSize("98%", "98%");
//		dockPanel.setCellHeight(absolutePanel_1, "10%");
//		dockPanel.setCellWidth(absolutePanel_1, "78%");
//
//		final AbsolutePanel absolutePanel_2 = new AbsolutePanel();
//		absolutePanel_2.setStyleName("CentralPanel");
//		dockPanel.add(absolutePanel_2, DockPanel.CENTER);
//		absolutePanel_2.setSize("98%", "98%");
//		dockPanel.setCellWidth(absolutePanel_2, "78%");
//		dockPanel.setCellHeight(absolutePanel_2, "88%");
//		IButton draft = new IButton("Драфт");
//		absolutePanel.add(draft, 10, 10);
//
//		IButton manager = new IButton("Менеджер");
//		absolutePanel.add(manager, 10, 46);
//
//		IButton startSettings = new IButton("Стартовые настройки");
//		IButton userSettings = new IButton("Настройки");
//		IButton createScheduleBtn = new IButton("Создать расписание");
//		absolutePanel.add(createScheduleBtn, 10, 136);
//
//		Button btnNewButton_1 = new Button("New button");
//		absolutePanel.add(btnNewButton_1, 8, 190);
//		draft.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				try {
//					absolutePanel_2.remove(0);
//					CopyOfScheduleDraft copy = new CopyOfScheduleDraft();
//					absolutePanel_2.add(copy);
//				} catch (Exception ex) {
//					CopyOfScheduleDraft copy = new CopyOfScheduleDraft();
//					absolutePanel_2.add(copy);
//				}
//			}
//		});
//		manager.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				absolutePanel_2.remove(0);
//				drawPage(absolutePanel_2);
//				try {
//					absolutePanel_2.remove(0);
//					drawPage(absolutePanel_2);
//				} catch (Exception ex) {
//					drawPage(absolutePanel_2);
//				}
//			}
//		});
//		absolutePanel.add(startSettings, 8, 82);
//		startSettings.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				try {
//					absolutePanel_2.remove(0);
//					StartSettingEntryPoint startSetting = new StartSettingEntryPoint();
//					absolutePanel_2.add(startSetting);
//				} catch (Exception ex) {
//					StartSettingEntryPoint startSetting = new StartSettingEntryPoint();
//					absolutePanel_2.add(startSetting);
//				}
//			}
//		});
//		createScheduleBtn.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				try {
//					absolutePanel_2.remove(0);
//					CreateScheduleEntryPoint startSetting = new CreateScheduleEntryPoint();
//					absolutePanel_2.add(startSetting);
//				} catch (Exception ex) {
//					try {
//						CreateScheduleEntryPoint startSetting = new CreateScheduleEntryPoint();
//						absolutePanel_2.add(startSetting);
//					} catch (Exception exception) {
//						Window.alert(exception.getMessage());
//					}
//				}
//			}
//		});
//		
//		absolutePanel.add(userSettings,8,220);
//		userSettings.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				try {
//					absolutePanel_2.remove(0);
//					UserSettingSimplePanel userSetting = new UserSettingSimplePanel();
//					absolutePanel_2.add(userSetting);
//				} catch (Exception ex) {
//					UserSettingSimplePanel userSetting = new UserSettingSimplePanel();
//					absolutePanel_2.add(userSetting);
//				}
//			}
//		});
//	}
	private void drawPrimaryPage() {
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setSize("100%", "100%");
		
		DockPanel dockPanel = new DockPanel();
		rootPanel.add(dockPanel, 0, 0);
		dockPanel.setSize("100%", "100%");
		
		AbsolutePanel absolutePanel = new AbsolutePanel();
		dockPanel.add(absolutePanel, DockPanel.NORTH);
		absolutePanel.setSize("100%", "100%");
		dockPanel.setCellHeight(absolutePanel, "15%");
		dockPanel.setCellWidth(absolutePanel, "auto");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		absolutePanel.add(horizontalPanel, 0, 0);
		horizontalPanel.setSize("100%", "100%");
		
		AbsolutePanel absolutePanel_3 = new AbsolutePanel();
		absolutePanel_3.setStyleName("NapLogo");
		horizontalPanel.add(absolutePanel_3);
		absolutePanel_3.setSize("100%", "100%");
		horizontalPanel.setCellHeight(absolutePanel_3, "100%");
		horizontalPanel.setCellWidth(absolutePanel_3, "20%");
		
		Image image = new Image("/img/1_01.png");
		absolutePanel_3.add(image, 0, 0);
		image.setSize("273px", "96px");
		
		AbsolutePanel absolutePanel_4 = new AbsolutePanel();
		horizontalPanel.add(absolutePanel_4);
		absolutePanel_4.setSize("100%", "100%");
		
		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		absolutePanel_1.setStyleName("westPanelNap");
		dockPanel.add(absolutePanel_1, DockPanel.WEST);
		absolutePanel_1.setSize("100%", "100%");
		dockPanel.setCellHeight(absolutePanel_1, "100%");
		dockPanel.setCellWidth(absolutePanel_1, "20%");
		
		StackPanel stackPanel = new StackPanel();
		stackPanel.setStyleName("gwt-StackPanel .gwt-StackPanelItem");
		absolutePanel_1.add(stackPanel, 0, 0);
		stackPanel.setSize("100%", "100%");
		
		AbsolutePanel absolutePanel_5 = new AbsolutePanel();
		absolutePanel_5.setStyleName("StackBlock");
		stackPanel.add(absolutePanel_5, "Основное", false);
		absolutePanel_5.setSize("100%", "100%");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setBorderWidth(0);
		verticalPanel.setStyleName("StackBlock");
		absolutePanel_5.add(verticalPanel);
		verticalPanel.setSize("100%", "40%");
		
		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_1);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_1, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setSize("100%", "40px");
		verticalPanel.setCellHeight(horizontalPanel_1, "30px");
		verticalPanel.setCellWidth(horizontalPanel_1, "100%");
		
		Image image_1 = new Image("/img/1_25.png");
		horizontalPanel_1.add(image_1);
		horizontalPanel_1.setCellHorizontalAlignment(image_1, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(image_1, HasVerticalAlignment.ALIGN_MIDDLE);
		image_1.setSize("40%", "50%");
		horizontalPanel_1.setCellWidth(image_1, "25%");
		
		AbsolutePanel absolutePanel_9 = new AbsolutePanel();
		horizontalPanel_1.add(absolutePanel_9);
		horizontalPanel_1.setCellHorizontalAlignment(absolutePanel_9, HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_9.setSize("100%", "100%");
		horizontalPanel_1.setCellWidth(absolutePanel_9, "50%");
		
		InlineLabel nlnlblNewInlinelabel = new InlineLabel("Отправить по почте");
		nlnlblNewInlinelabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_9.add(nlnlblNewInlinelabel, 0, 10);
		
		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_2);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_2, HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_2, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setSize("100%", "40px");
		verticalPanel.setCellHeight(horizontalPanel_2, "30px");
		verticalPanel.setCellWidth(horizontalPanel_2, "100%");
		
		Image image_2 = new Image("/img/1_19.png");
		horizontalPanel_2.add(image_2);
		horizontalPanel_2.setCellHorizontalAlignment(image_2, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(image_2, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setCellHeight(image_2, "100%");
		image_2.setSize("40%", "50%");
		horizontalPanel_2.setCellWidth(image_2, "25%");
		
		AbsolutePanel absolutePanel_11 = new AbsolutePanel();
		horizontalPanel_2.add(absolutePanel_11);
		absolutePanel_11.setSize("100%", "100%");
		horizontalPanel_2.setCellWidth(absolutePanel_11, "50%");
		
		InlineLabel nlnlblNewInlinelabel_1 = new InlineLabel("Сохранить график");
		nlnlblNewInlinelabel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_11.add(nlnlblNewInlinelabel_1, 0, 10);
		
		HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_3);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_3, HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.setCellHorizontalAlignment(horizontalPanel_3, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setSize("100%", "40px");
		verticalPanel.setCellHeight(horizontalPanel_3, "30px");
		verticalPanel.setCellWidth(horizontalPanel_3, "100%");
		
		Image image_3 = new Image("/img/1_27.png");
		horizontalPanel_3.add(image_3);
		horizontalPanel_3.setCellVerticalAlignment(image_3, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(image_3, HasHorizontalAlignment.ALIGN_CENTER);
		image_3.setSize("40%", "50%");
		horizontalPanel_3.setCellHeight(image_3, "100%");
		horizontalPanel_3.setCellWidth(image_3, "25%");
		
		AbsolutePanel absolutePanel_13 = new AbsolutePanel();
		horizontalPanel_3.add(absolutePanel_13);
		horizontalPanel_3.setCellVerticalAlignment(absolutePanel_13, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHorizontalAlignment(absolutePanel_13, HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_13.setSize("100%", "100%");
		horizontalPanel_3.setCellHeight(absolutePanel_13, "100%");
		horizontalPanel_3.setCellWidth(absolutePanel_13, "50%");
		
		InlineLabel nlnlblNewInlinelabel_2 = new InlineLabel("Распечатать");
		nlnlblNewInlinelabel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_13.add(nlnlblNewInlinelabel_2, 0, 10);
		
		HorizontalPanel horizontalPanel_4 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_4);
		verticalPanel.setCellHeight(horizontalPanel_4, "30px");
		verticalPanel.setCellWidth(horizontalPanel_4, "100%");
		horizontalPanel_4.setSize("100%", "40px");
		
		Image image_4 = new Image("/img/1_29.png");
		horizontalPanel_4.add(image_4);
		horizontalPanel_4.setCellVerticalAlignment(image_4, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_4.setCellHorizontalAlignment(image_4, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_4.setCellHeight(image_4, "100%");
		horizontalPanel_4.setCellWidth(image_4, "25%");
		image_4.setSize("40%", "50%");
		
		AbsolutePanel absolutePanel_8 = new AbsolutePanel();
		horizontalPanel_4.add(absolutePanel_8);
		horizontalPanel_4.setCellVerticalAlignment(absolutePanel_8, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_4.setCellHorizontalAlignment(absolutePanel_8, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_4.setCellHeight(absolutePanel_8, "100%");
		horizontalPanel_4.setCellWidth(absolutePanel_8, "50%");
		absolutePanel_8.setSize("100%", "100%");
		
		InlineLabel inlineLabel = new InlineLabel("Создать новое");
		inlineLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_8.add(inlineLabel, 0, 10);
		
		AbsolutePanel absolutePanel_6 = new AbsolutePanel();
		absolutePanel_6.setStyleName("StackBlock");
		stackPanel.add(absolutePanel_6, "Больше", false);
		absolutePanel_6.setSize("100%", "100%");
		
		VerticalPanel verticalPanel_3 = new VerticalPanel();
		verticalPanel_3.setStyleName("StackBlock");
		verticalPanel_3.setBorderWidth(0);
		absolutePanel_6.add(verticalPanel_3);
		verticalPanel_3.setSize("100%", "10%");
		
		HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		verticalPanel_3.add(horizontalPanel_5);
		verticalPanel_3.setCellHeight(horizontalPanel_5, "30px");
		verticalPanel_3.setCellWidth(horizontalPanel_5, "100%");
		horizontalPanel_5.setSize("100%", "40px");
		
		Image image_5 = new Image("/img/1_35.png");
		horizontalPanel_5.add(image_5);
		horizontalPanel_5.setCellVerticalAlignment(image_5, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellHorizontalAlignment(image_5, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_5.setCellHeight(image_5, "100%");
		horizontalPanel_5.setCellWidth(image_5, "25%");
		image_5.setSize("36px", "31px");
		
		AbsolutePanel absolutePanel_10 = new AbsolutePanel();
		horizontalPanel_5.add(absolutePanel_10);
		horizontalPanel_5.setCellWidth(absolutePanel_10, "50%");
		horizontalPanel_5.setCellVerticalAlignment(absolutePanel_10, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_5.setCellHorizontalAlignment(absolutePanel_10, HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_10.setSize("100%", "100%");
		
		InlineLabel inlineLabel_1 = new InlineLabel("Показать календарь");
		inlineLabel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_10.add(inlineLabel_1, 0, 10);
		
		HorizontalPanel horizontalPanel_6 = new HorizontalPanel();
		verticalPanel_3.add(horizontalPanel_6);
		verticalPanel_3.setCellHeight(horizontalPanel_6, "30px");
		verticalPanel_3.setCellWidth(horizontalPanel_6, "100%");
		horizontalPanel_6.setSize("100%", "40px");
		
		Image image_6 = new Image("/img/1_37.png");
		horizontalPanel_6.add(image_6);
		horizontalPanel_6.setCellVerticalAlignment(image_6, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(image_6, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setCellHeight(image_6, "100%");
		horizontalPanel_6.setCellWidth(image_6, "25%");
		image_6.setSize("36px", "31px");
		
		AbsolutePanel absolutePanel_12 = new AbsolutePanel();
		horizontalPanel_6.add(absolutePanel_12);
		horizontalPanel_6.setCellWidth(absolutePanel_12, "50%");
		horizontalPanel_6.setCellVerticalAlignment(absolutePanel_12, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(absolutePanel_12, HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_12.setSize("100%", "100%");
		
		InlineLabel inlineLabel_2 = new InlineLabel("Справка");
		inlineLabel_2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_12.add(inlineLabel_2, 0, 10);
		
		AbsolutePanel absolutePanel_7 = new AbsolutePanel();
		absolutePanel_7.setStyleName("StackBlock");
		stackPanel.add(absolutePanel_7, "Варианты", false);
		absolutePanel_7.setSize("100%", "100%");
		
		VerticalPanel verticalPanel_2 = new VerticalPanel();
		absolutePanel_7.add(verticalPanel_2);
		
		AbsolutePanel absolutePanel_2 = new AbsolutePanel();
		dockPanel.add(absolutePanel_2, DockPanel.CENTER);
		absolutePanel_2.setSize("100%", "100%");
		
		VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel_1.setStyleName("CentralPanelGraphique");
		absolutePanel_2.add(verticalPanel_1);
		verticalPanel_1.setSize("100%", "80px");
		
		AbsolutePanel absolutePanel_14 = new AbsolutePanel();
		verticalPanel_1.add(absolutePanel_14);
		absolutePanel_14.setSize("100%", "100%");
		verticalPanel_1.setCellHeight(absolutePanel_14, "80px");
		verticalPanel_1.setCellWidth(absolutePanel_14, "100%");
	}
}