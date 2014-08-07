package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.HeaderBuilder;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.dom.builder.shared.TableSectionBuilder;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.cellview.client.CellTable;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StartSettingEntryPoint implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	
	private ArrayList<Club> clubs;
	private ArrayList<Club> clubsOnlyOur = new ArrayList<Club>();
	private int countClubsOnlyOur = 0;
	private Map<Long, Club> clubsDictionary;
	private HashSet<Club> clubsForDelete = new HashSet<Club>();
	private HashSet<Club> clubsForUpdate = new HashSet<Club>();
	private HashSet<Club> clubsForInsert = new HashSet<Club>();
	
	private Collection<Employee> employees;
	
	/**
	 * Create a remote service proxy to talk to the server-side StartSetting service.
	 */
	private final StartSettingServiceAsync startSettingService = GWT
			.create(StartSettingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get("content");
		rootPanel.setStyleName((String) null);
		
		TabPanel tabPanel = new TabPanel();
		rootPanel.add(tabPanel);
		
		AbsolutePanel absolutePanel = new AbsolutePanel();
		tabPanel.add(absolutePanel, "Настройка клубов", true);
		
		final HTML html1 = new HTML();
		rootPanel.add(html1);
		
		Label label = new Label("Выберите независемые клубы:");
		absolutePanel.add(label);
		
		
		
		final FlexTable flexTable = new FlexTable();
		flexTable.setCellPadding(10);
		flexTable.setStyleName("mainTable");
		flexTable.setBorderWidth(1);
		absolutePanel.add(flexTable);
		
		final Button saveClubButton = new Button("Сохранить");
		saveClubButton.addStyleName("leftDown");
		absolutePanel.add(saveClubButton);
		
		final Button addClubButton = new Button("Добавить новый клуб");
		absolutePanel.add(addClubButton);
		
		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		tabPanel.add(absolutePanel_1, "Распределение сотрудников", false);
		
		Label lblNewLabel = new Label("Задайте роли сотрудникам:");
		absolutePanel_1.add(lblNewLabel);
		
		final FlexTable flexTable_1 = new FlexTable();
		flexTable_1.setBorderWidth(2);
		absolutePanel_1.add(flexTable_1);
		
		final Button btnNewButton_1 = new Button("New button");
		btnNewButton_1.setText("Выбрать");
		absolutePanel_1.add(btnNewButton_1);
		
		final DialogBox createObject = new DialogBox();
		createObject.setAnimationEnabled(true);
		
		tabPanel.selectTab(0);
		
		saveClubButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Collection<Club> clubsForOnlyOurInsert = new HashSet<Club>();
				for(int i=0;i<flexTable.getRowCount()-2;i++){
					if(i >= (clubs.size() + countClubsOnlyOur)){
						clubsOnlyOur.get(i - clubs.size()).setIsIndependent(
								((CheckBox)flexTable.getWidget(i + 2, 3)).getValue());
						clubsForOnlyOurInsert.add(clubsOnlyOur.get(i - clubs.size()));
					}
					else if(i >= clubs.size()){
						Club c = clubsOnlyOur.get(i - clubs.size());
						Boolean checked = ((CheckBox)flexTable.getWidget(i + 2, 3)).getValue();
						if(c.getIsIndependen()!=checked){
							c.setIsIndependent(checked);
							clubsForUpdate.add(c);
						}
					}
					else{
						if(clubsDictionary.containsKey(clubs.get(i).getClubId())){
							Club c = clubsDictionary.get(clubs.get(i).getClubId());
							Boolean checked = ((CheckBox)flexTable.getWidget(i + 2, 3)).getValue();
							if(c.getIsIndependen()!=checked){
								c.setIsIndependent(checked);
								clubsForUpdate.add(c);
							}
						}
						else if(clubsForInsert.contains(clubs.get(i))){
							clubs.get(i).setIsIndependent(
									((CheckBox)flexTable.getWidget(i + 2, 3)).getValue());
						}
					}
				}
				/*String s = "<h1>Для обновления:</h1>";
				for(Club elem : clubsForUpdate){
					s+=elem.getTitle()+" "+elem.getIsIndependen()+"<br/>";
				}
				s += "<h1>Для вставки:</h1>";
				for(Club elem : clubsForInsert){
					s+=elem.getTitle()+" " + elem.getIsIndependen()+"<br/>";
				}
				s += "<h1>Для удаления:</h1>";
				for(Club elem : clubsForDelete){
					s+=elem.getTitle()+" " + elem.getIsIndependen()+"<br/>";
				}
				s += "<h1>Только для нашей вставки:</h1>";
				for(Club elem : clubsForOnlyOurInsert){
					s+=elem.getTitle()+" " + elem.getIsIndependen()+"<br/>";
				}
				html1.setHTML(s);*/
				startSettingService.setClubs(clubsForInsert, clubsForOnlyOurInsert, clubsForUpdate, clubsForDelete,
						new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						html1.setHTML("Клубы успешно сохранены!");
						loadClubs(flexTable);
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						html1.setHTML("Произошла ошибка:(");
					}
				});
			}
		});
		
		btnNewButton_1.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Collection<Employee> admins = new HashSet<Employee>();
				Collection<Employee> responsiblePersons = new HashSet<Employee>();
				Collection<Employee> other = new HashSet<Employee>();
				int i=1;
				for(Employee elem : employees){
					Boolean admin = ((CheckBox)(flexTable_1.getWidget(i, 1))).getValue();
					Boolean responsible = ((CheckBox)(flexTable_1.getWidget(i, 2))).getValue();
					if(admin || responsible){
						if(admin){
							admins.add(elem);
						}
						if(responsible){
							responsiblePersons.add(elem);
						}
					}
					else{
						other.add(elem);
					}
					i++;
				}
				btnNewButton_1.setEnabled(false);
				
				startSettingService.setEmployees(admins, responsiblePersons, other,
						new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						html1.setHTML("All success");
						btnNewButton_1.setEnabled(true);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						html1.setHTML(caught.getMessage());
						btnNewButton_1.setEnabled(true);
						
					}
				});
				
			}
		});

		addClubButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createClubPanel(createObject, flexTable);
				createObject.center();
				
			}
		});
		
		loadClubs(flexTable);
		
		startSettingService.getEmployees(new AsyncCallback<Collection<Employee>>(){
			public void onFailure(Throwable caught) {
				flexTable_1.insertRow(0);
				flexTable_1.insertCell(0, 0);
				flexTable_1.setText(0, 0, caught.getMessage());
				
			}

			public void onSuccess(Collection<Employee> result) {
				employees=result;
				flexTable_1.insertRow(0);
				flexTable_1.insertCell(0, 0);
				flexTable_1.setText(0, 0, "Сотрудники");
				flexTable_1.insertCell(0, 1);
				flexTable_1.setText(0, 1, "Администртор");
				flexTable_1.insertCell(0, 2);
				flexTable_1.setText(0, 2, "Ответственное лицо");
				
				int i = 1;
				for (Employee elem : result)
				{
					flexTable_1.insertRow(i);
					flexTable_1.insertCell(i, 0);
					flexTable_1.setText(i, 0, elem.getLastName() + " " + elem.getFirstName()+ " " + elem.getSecondName());
					flexTable_1.insertCell(i, 1);
					CheckBox widget = new CheckBox("");
					flexTable_1.setWidget(i, 1, widget);
					flexTable_1.insertCell(i, 2);
					CheckBox widget1 = new CheckBox("");
					flexTable_1.setWidget(i, 2, widget1);
					i++;
				}
			}
		});
		
		
	}

	private void loadClubs(final FlexTable flexTable){
		flexTable.removeAllRows();
		clubsOnlyOur = new ArrayList<Club>();
		countClubsOnlyOur = 0;
		clubsDictionary = new HashMap<Long, Club>();
		clubsForDelete = new HashSet<Club>();
		clubsForUpdate = new HashSet<Club>();
		clubsForInsert = new HashSet<Club>();
		
		startSettingService.getClubs(new AsyncCallback<Collection<Club>>(){
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				flexTable.insertRow(0);
				flexTable.insertCell(0, 0);
				flexTable.setText(0, 0, caught.getMessage());
			}

			public void onSuccess(final Collection<Club> result) {
				startSettingService.getDictionaryClub(new AsyncCallback<Map<Long, Club>>() {
					
					@Override
					public void onSuccess(final Map<Long, Club> resultDictionary) {
						startSettingService.getOnlyOurClubs(new AsyncCallback<Collection<Club>>() {
							
							@Override
							public void onSuccess(Collection<Club> ourClub) {
								clubs = new ArrayList<Club>(result);
								clubsDictionary = resultDictionary;
								createClubTable(flexTable, result, resultDictionary);
								countClubsOnlyOur = 0;
								clubsOnlyOur = new ArrayList<Club>(ourClub);
								for(Club elem : ourClub){
									countClubsOnlyOur++;
									writeClub(flexTable, elem);
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								flexTable.insertRow(0);
								flexTable.insertCell(0, 0);
								flexTable.setText(0, 0, caught.getMessage());
								
							}
						});
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						flexTable.insertRow(0);
						flexTable.insertCell(0, 0);
						flexTable.setText(0, 0, caught.getMessage());
					}
				});
				
				
			}
		});
	}
	
	private void importClub(final FlexTable flexTable, int index) {
		if(index==1){
			for(int i=0;i<clubs.size();i++){
				importClub(flexTable, i + 2);
			}
		}
		else{
			if(clubsDictionary.containsKey(clubs.get(index - 2).getClubId())){
				Club c = clubsDictionary.get(clubs.get(index - 2).getClubId());
				if(c.getTitle() != clubs.get(index - 2).getTitle() || 
						c.getCash()!=clubs.get(index - 2).getCash()){
					c.setTitle(clubs.get(index - 2).getTitle());
					c.setCash(clubs.get(index - 2).getCash());
					clubsForUpdate.add(c);
					flexTable.setText(index, 2, c.getTitle());
				}
			}
			else{
				clubsForInsert.add(clubs.get(index - 2));
				writeClub(flexTable, clubs.get(index - 2), index);
			}
		}
	}
	
	private void writeClub(FlexTable flexTable, Club c) {
		int index = flexTable.getRowCount();
		flexTable.insertRow(index);
		for(int i = 0; i <= 4; i++)
			flexTable.insertCell(index, i);
		flexTable.getFlexCellFormatter().addStyleName(index, 0, "beforeImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 1, "beforeImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 1, "afterImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 2, "afterImport");
		
		writeClub(flexTable, c, index);
	}
	
	private void writeClub(final FlexTable flexTable, Club c, int index){
		if(flexTable.getWidget(index, 3)!=null){
			return;
		}
		flexTable.setText(index, 2, c.getTitle()); 
		CheckBox widget = new CheckBox();
		widget.setWidth("40px");
		widget.setHeight("40px");
		widget.setStyleName("checkbox");
		widget.setValue(c.getIsIndependen());
		flexTable.setWidget(index, 3, widget);
		
		
		Button btDel = new Button();
		btDel.setStyleName("buttonDelete");
		btDel.setWidth("40px");
		btDel.setHeight("40px");
		btDel.setTitle(String.valueOf(index));
		btDel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int index = Integer.parseInt(event.getRelativeElement().getTitle());
				deleteClub(flexTable, index);
				
			}
		});
		flexTable.setWidget(index, 4, btDel);
	}
	
	private void deleteClub(final FlexTable flexTable, int index) {
		if(index == 1){
			for(int i=flexTable.getRowCount()-1;i>=2;i--){
				deleteClub(flexTable, i);
			}
		}
		else{
			if(index >= (clubs.size() + countClubsOnlyOur + 2)){
				deleteOurClubFromTable(flexTable, index);
				return;
			}
			else if(index >= (clubs.size() + 2)){
				clubsForDelete.add(clubsOnlyOur.get(index - clubs.size() - 2));
				countClubsOnlyOur--;
				deleteOurClubFromTable(flexTable, index);
				return;
			}
			else if(clubsDictionary.containsKey(clubs.get(index - 2).getClubId())){
				clubsForDelete.add(clubsDictionary.get(clubs.get(index - 2).getClubId()));
				clubsForUpdate.remove(clubsDictionary.get(clubs.get(index - 2).getClubId()));
				clubsDictionary.remove(clubs.get(index - 2).getClubId());
			}
			else if(clubsForInsert.contains(clubs.get(index - 2))){
				clubsForInsert.remove(clubs.get(index - 2));
			}
			flexTable.setText(index, 2, "");
			flexTable.setText(index, 3, "");
			flexTable.setText(index, 4, "");
		}
	}
	
	private void deleteOurClubFromTable(FlexTable flexTable, int index){
		flexTable.removeRow(index);
		clubsOnlyOur.remove(index - clubs.size() - 2);
		for(int i = index;i<flexTable.getRowCount();i++){
			flexTable.getWidget(i, 4).setTitle(String.valueOf(i));
		}
	}
	
	private void createClubTable(final FlexTable flexTable, Collection<Club> result,
			Map<Long, Club> resultDictionary) {
		
		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.setText(0, 0, "Клубы");
		flexTable.getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Импорт");
		flexTable.getFlexCellFormatter().addStyleName(0, 1, "mainHeader");
		flexTable.insertCell(0, 2);
		flexTable.setText(0, 2, "Клубы в расписании");
		flexTable.getFlexCellFormatter().setColSpan(0, 2, 2);
		flexTable.getFlexCellFormatter().addStyleName(0, 2, "mainHeader");
		flexTable.insertCell(0, 3);
		flexTable.setText(0, 3, "Удалить");
		flexTable.getFlexCellFormatter().addStyleName(0, 3, "mainHeader");
		
		flexTable.insertRow(1);
		flexTable.insertCell(1, 0);
		flexTable.setText(1, 0, "Название");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "beforeImport");
		flexTable.insertCell(1, 1);
		Button bt = new Button();
		bt.setWidth("100px");
		bt.setHeight("40px");
		bt.setStyleName("buttonImport");
		bt.setTitle("Импорт всех клубов");
		bt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				importClub(flexTable, 1);
			}
		});
		
		flexTable.setWidget(1, 1, bt);
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "import");
		flexTable.insertCell(1, 2);
		flexTable.setText(1, 2, "Название");
		flexTable.getFlexCellFormatter().addStyleName(1, 2, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 2, "afterImport");
		flexTable.insertCell(1, 3);
		flexTable.setText(1, 3, "Независимый");
		flexTable.getFlexCellFormatter().addStyleName(1, 3, "secondHeader");
		flexTable.insertCell(1, 4);
		Button bt1 = new Button();
		bt1.setStyleName("buttonDelete");
		bt1.setWidth("40px");
		bt1.setHeight("40px");
		bt1.setTitle("Удалить все клубы");
		bt1.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteClub(flexTable, 1);
			}
		});
		
		flexTable.setWidget(1, 4, bt1);
		flexTable.getFlexCellFormatter().addStyleName(1, 4, "secondHeader");
		int i = 2;
		for (Club elem : result)
		{
			flexTable.insertRow(i);
			flexTable.insertCell(i, 0);
			flexTable.setText(i, 0, elem.getTitle());
			flexTable.getFlexCellFormatter().addStyleName(i, 0, "beforeImport");
			flexTable.insertCell(i, 1);
			
			Button btImp = new Button();
			btImp.setStyleName("buttonImport");
			btImp.setWidth("100px");
			btImp.setHeight("40px");
			btImp.setTitle(String.valueOf(i));
			btImp.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					int index = Integer.parseInt(event.getRelativeElement().getTitle());
					importClub(flexTable, index);
				}
			});
			
			flexTable.setWidget(i, 1, btImp);
			flexTable.getFlexCellFormatter().addStyleName(i, 1, "import");

			flexTable.getFlexCellFormatter().addStyleName(i, 2, "afterImport");
			flexTable.insertCell(i, 3);
			flexTable.insertCell(i, 4);
			if(clubsDictionary.containsKey(elem.getClubId())){
				writeClub(flexTable, clubsDictionary.get(elem.getClubId()), i);
			}
			i++;
		}
	}
	
	private void createEmployeeTable(final FlexTable flexTable, Collection<Employee> result) {
		
		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.setText(0, 0, "Сотрудники");
		flexTable.getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Импорт");
		flexTable.getFlexCellFormatter().addStyleName(0, 1, "mainHeader");
		flexTable.insertCell(0, 2);
		flexTable.setText(0, 2, "Сотрудники в расписании");
		flexTable.getFlexCellFormatter().setColSpan(0, 2, 2);
		flexTable.getFlexCellFormatter().addStyleName(0, 2, "mainHeader");
		flexTable.insertCell(0, 3);
		flexTable.setText(0, 3, "Удалить");
		flexTable.getFlexCellFormatter().addStyleName(0, 3, "mainHeader");
		
		flexTable.insertRow(1);
		flexTable.insertCell(1, 0);
		flexTable.setText(1, 0, "ФИО");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 0, "beforeImport");
		flexTable.insertCell(1, 1);
		Button bt = new Button();
		bt.setWidth("100px");
		bt.setHeight("40px");
		bt.setStyleName("buttonImport");
		bt.setTitle("Импорт всех сотрудников");
		bt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				importClub(flexTable, 1);
			}
		});
		
		flexTable.setWidget(1, 1, bt);
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 1, "import");
		flexTable.insertCell(1, 2);
		flexTable.setText(1, 2, "ФИО");
		flexTable.getFlexCellFormatter().addStyleName(1, 2, "secondHeader");
		flexTable.getFlexCellFormatter().addStyleName(1, 2, "afterImport");
		flexTable.insertCell(1, 3);
		flexTable.setText(1, 3, "Администратор");
		flexTable.getFlexCellFormatter().addStyleName(1, 3, "secondHeader");
		flexTable.insertCell(1, 4);
		flexTable.setText(1, 4, "Ответственное лицо");
		flexTable.getFlexCellFormatter().addStyleName(1, 4, "secondHeader");
		flexTable.insertCell(1, 5);
		flexTable.setText(1, 5, "Подписаться на рассылку");
		flexTable.getFlexCellFormatter().addStyleName(1, 5, "secondHeader");
		flexTable.insertCell(1, 6);
		Button bt1 = new Button();
		bt1.setStyleName("buttonDelete");
		bt1.setWidth("40px");
		bt1.setHeight("40px");
		bt1.setTitle("Удалить все клубы");
		bt1.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteEmployee(flexTable, 1);
			}
		});
		
		flexTable.setWidget(1, 4, bt1);
		flexTable.getFlexCellFormatter().addStyleName(1, 4, "secondHeader");
		int i = 2;
		for (Employee elem : result)
		{
			flexTable.insertRow(i);
			flexTable.insertCell(i, 0);
			flexTable.setText(i, 0, elem.getLastName() + " " + elem.getFirstName()+ " " + elem.getSecondName());
			flexTable.getFlexCellFormatter().addStyleName(i, 0, "beforeImport");
			flexTable.insertCell(i, 1);
			Button btImp = new Button();
			btImp.setStyleName("buttonImport");
			btImp.setWidth("100px");
			btImp.setHeight("40px");
			btImp.setTitle(String.valueOf(i));
			btImp.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					int index = Integer.parseInt(event.getRelativeElement().getTitle());
					importEmployee(flexTable, index);
				}
			});
			
			flexTable.setWidget(i, 1, btImp);
			flexTable.getFlexCellFormatter().addStyleName(i, 1, "import");

			flexTable.getFlexCellFormatter().addStyleName(i, 2, "afterImport");
			flexTable.insertCell(i, 3);
			flexTable.insertCell(i, 4);
			i++;
		}
	}

	private void importEmployee(FlexTable flexTable, int index) {
		// TODO Auto-generated method stub
	
	}

	private void deleteEmployee(FlexTable flexTable, int i) {
		// TODO Auto-generated method stub
	
	}
	
	private void createClubPanel(final DialogBox createObject, final FlexTable flexTable){
		createObject.clear();
		createObject.setText("Добавление нового клуба");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);
		
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");
		
		Label title = new Label("Название клуба:");
		final TextBox titleTextBox = new TextBox();
		Label cash = new Label("Cash клуба:");
		final TextBox cashTextBox = new TextBox();
		
		Button addButton = new Button("Добавить", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(titleTextBox.getText()==""||cashTextBox.getText()==""){
					errorLabel.setText("Вы заполнили не все поля");
				}
				else{
					try{
						double cashs = Double.parseDouble(cashTextBox.getText());
						Club c = new Club();
						c.setCash(cashs);
						c.setTitle(titleTextBox.getText());
						clubsOnlyOur.add(c);
						writeClub(flexTable, c);
						createObject.hide();
					}
					catch(NumberFormatException e){
						errorLabel.setText("Cash должен быть числом");
					}
					
				}
			}
		});
		
		Button delButton = new Button("Отмена", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});
		
		delButton.addStyleName("leftDown");
		
		table.insertRow(0);
		table.insertCell(0, 0);
		table.setWidget(0,0,title);
		table.insertCell(0, 1);
		table.setWidget(0, 1, titleTextBox);
		table.insertRow(1);
		table.insertCell(1, 0);
		table.setWidget(1, 0, cash);
		table.insertCell(1, 1);
		table.setWidget(1, 1, cashTextBox);
		
		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);
		
		createObject.add(absPanel);
	}
}
