package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Category;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

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

	private ArrayList<Employee> employees;
	private ArrayList<Employee> employeesOnlyOur = new ArrayList<Employee>();
	private int countEmployeesOnlyOur = 0;
	private Map<Long, Employee> employeesDictionary;
	private Map<Long, Collection<Boolean>> employeeRole;
	private HashSet<Employee> employeesForDelete = new HashSet<Employee>();
	private HashSet<Employee> employeesForUpdate = new HashSet<Employee>();
	private HashSet<Employee> employeesForInsert = new HashSet<Employee>();
	
	private ArrayList<Employee> allEmployee;
	private ArrayList<Category> categories;
	private Map<Long, Collection<Long>> employeeInCategoriesForDelete;
	private Map<Long, Collection<Long>> employeeInCategoriesForInsert;
	private ArrayList<Category> categoriesForDelete;
	private ArrayList<Category> categoriesForInsert;
	private int selectedCategory = -1;
	

	/**
	 * Create a remote service proxy to talk to the server-side StartSetting
	 * service.
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
		saveClubButton.addStyleName("rightDown");
		absolutePanel.add(saveClubButton);

		final Button addClubButton = new Button("Добавить новый клуб");
		absolutePanel.add(addClubButton);

		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		tabPanel.add(absolutePanel_1, "Распределение сотрудников", false);

		Label lblNewLabel = new Label("Задайте роли сотрудникам:");
		absolutePanel_1.add(lblNewLabel);

		final FlexTable flexTable_1 = new FlexTable();
		flexTable_1.setBorderWidth(1);
		flexTable_1.setCellPadding(10);
		flexTable_1.setStyleName("mainTable");
		absolutePanel_1.add(flexTable_1);

		final Button saveEmployeeButton = new Button("Сохранить");
		saveEmployeeButton.addStyleName("rightDown");
		absolutePanel_1.add(saveEmployeeButton);

		final Button addEmployeeButton = new Button("Добавить нового сотрудника");
		absolutePanel_1.add(addEmployeeButton);
		
		final DialogBox createObject = new DialogBox();
		createObject.setAnimationEnabled(true);

		tabPanel.selectTab(0);

		saveClubButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveClubButton.setEnabled(false);
				Collection<Club> clubsForOnlyOurInsert = new HashSet<Club>();
				for (int i = 0; i < flexTable.getRowCount() - 2; i++) {
					if (i >= (clubs.size() + countClubsOnlyOur)) {
						clubsOnlyOur.get(i - clubs.size()).setIsIndependent(
								((CheckBox) flexTable.getWidget(i + 2, 3))
										.getValue());
						clubsForOnlyOurInsert.add(clubsOnlyOur.get(i
								- clubs.size()));
					} else if (i >= clubs.size()) {
						Club c = clubsOnlyOur.get(i - clubs.size());
						Boolean checked = ((CheckBox) flexTable.getWidget(
								i + 2, 3)).getValue();
						if (c.getIsIndependent() != checked) {
							c.setIsIndependent(checked);
							clubsForUpdate.add(c);
						}
					} else {
						if (clubsDictionary.containsKey(clubs.get(i)
								.getClubId())) {
							Club c = clubsDictionary.get(clubs.get(i)
									.getClubId());
							Boolean checked = ((CheckBox) flexTable.getWidget(
									i + 2, 3)).getValue();
							if (c.getIsIndependent() != checked) {
								c.setIsIndependent(checked);
								clubsForUpdate.add(c);
							}
						} else if (clubsForInsert.contains(clubs.get(i))) {
							clubs.get(i).setIsIndependent(
									((CheckBox) flexTable.getWidget(i + 2, 3))
											.getValue());
						}
					}
				}
				/*
				 * String s = "<h1>Для обновления:</h1>"; for(Club elem :
				 * clubsForUpdate){
				 * s+=elem.getTitle()+" "+elem.getIsIndependen()+"<br/>"; } s +=
				 * "<h1>Для вставки:</h1>"; for(Club elem : clubsForInsert){
				 * s+=elem.getTitle()+" " + elem.getIsIndependen()+"<br/>"; } s
				 * += "<h1>Для удаления:</h1>"; for(Club elem : clubsForDelete){
				 * s+=elem.getTitle()+" " + elem.getIsIndependen()+"<br/>"; } s
				 * += "<h1>Только для нашей вставки:</h1>"; for(Club elem :
				 * clubsForOnlyOurInsert){ s+=elem.getTitle()+" " +
				 * elem.getIsIndependen()+"<br/>"; } html1.setHTML(s);
				 */
				startSettingService.setClubs(clubsForInsert,
						clubsForOnlyOurInsert, clubsForUpdate, clubsForDelete,
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								html1.setHTML("Клубы успешно сохранены!");
								loadClubs(flexTable);
								saveClubButton.setEnabled(true);

							}

							@Override
							public void onFailure(Throwable caught) {
								html1.setHTML(caught.getMessage());
								saveClubButton.setEnabled(true);
							}
						});
			}
		});

		saveEmployeeButton.addClickHandler(new ClickHandler() {

			private void setRoles(int i, Employee e, Map<Integer,Collection<Long>> roleForDelete,
					Map<Integer,Collection<Long>> roleForInsert){
				ArrayList<Boolean> roles = new ArrayList<Boolean>(employeeRole.get(e.getEmployeeId()));
				for(int j=1;j<=3;j++){
					if(((CheckBox) flexTable_1.getWidget(i + 2, j + 2)).getValue()!=roles.get(j - 1)){
						if(roles.get(j - 1)){
							roleForDelete.get(j).add(e.getEmployeeId());
						}
						else{
							roleForInsert.get(j).add(e.getEmployeeId());
						}
					}
				}
			}
			
			@Override
			public void onClick(ClickEvent event) {
				saveEmployeeButton.setEnabled(false);
				Collection<Employee> employeesForOnlyOurInsert = new HashSet<Employee>();
				Map<Integer,Collection<Long>> roleForInsert = new HashMap<Integer,Collection<Long>>();
				roleForInsert.put(1, new ArrayList<Long>());
				roleForInsert.put(2, new ArrayList<Long>());
				roleForInsert.put(3, new ArrayList<Long>());
				
				Map<Integer, Collection<Long>> roleForDelete = new HashMap<Integer,Collection<Long>>();
				roleForDelete.put(1, new ArrayList<Long>());
				roleForDelete.put(2, new ArrayList<Long>());
				roleForDelete.put(3, new ArrayList<Long>());
				
				Map<Integer,Collection<Employee>> roleForInsertNew = new HashMap<Integer,Collection<Employee>>();
				roleForInsertNew.put(1, new ArrayList<Employee>());
				roleForInsertNew.put(2, new ArrayList<Employee>());
				roleForInsertNew.put(3, new ArrayList<Employee>());
				
				Map<Integer,Collection<Employee>> roleForInsertNewWithoutConformity = new HashMap<Integer,Collection<Employee>>();
				roleForInsertNewWithoutConformity.put(1, new ArrayList<Employee>());
				roleForInsertNewWithoutConformity.put(2, new ArrayList<Employee>());
				roleForInsertNewWithoutConformity.put(3, new ArrayList<Employee>());
				
				for (int i = 0; i < flexTable_1.getRowCount() - 2; i++) {
					if (i >= (employees.size() + countEmployeesOnlyOur)) {
						boolean withoutRoles = true;
						for(int j=1;j<=3;j++){
							if(((CheckBox) flexTable_1.getWidget(i + 2, j + 2)).getValue()){
								roleForInsertNewWithoutConformity.get(j).add(
										employeesOnlyOur.get(i - employees.size()));
								withoutRoles = false;
							}
						}
						if(withoutRoles)
							employeesForOnlyOurInsert.add(employeesOnlyOur.get(i - employees.size()));

					} else if (i >= employees.size()) {
						setRoles(i,employeesOnlyOur.get(i - employees.size()), roleForDelete, roleForInsert);
					} else {
						if (employeesDictionary.containsKey(employees.get(i)
								.getEmployeeId())) {
							setRoles(i,employeesDictionary.get(employees.get(i)
									.getEmployeeId()), roleForDelete, roleForInsert);
						} else if (employeesForInsert.contains(employees.get(i))) {
							boolean withRoles = false;
							for(int j=1;j<=3;j++){
								if(((CheckBox) flexTable_1.getWidget(i + 2, j + 2)).getValue()){
									roleForInsertNew.get(j).add(
											employees.get(i));
									withRoles = true;
								}
							}
							if(withRoles)
								employeesForInsert.remove(employees.get(i));
						}
					}
				}
				
				 String s = "<h1>Для обновления:</h1>";
				 for(Employee elem : employeesForUpdate){
					 s+=elem.getEmployeeId()+" " + elem.getNameForSchedule()+"<br/>";
				 }
				 s += "<h1>Для вставки:</h1>";
				 for(Employee elem : employeesForInsert){
					 s+=elem.getEmployeeId()+" " + elem.getNameForSchedule()+"<br/>";
				 } 
				 s += "<h1>Для удаления:</h1>";
				 for(Employee elem : employeesForDelete){
					 s+=elem.getEmployeeId()+" " + elem.getNameForSchedule()+"<br/>"; 
				 } 
				 s += "<h1>Только для нашей вставки:</h1>";
				 for(Employee elem : employeesForOnlyOurInsert){ 
					 s+=elem.getEmployeeId()+" " + elem.getNameForSchedule()+"<br/>";
				 }
				 s += "<h1>Роли для вставки:</h1>";
					 s += "<h2>администраторы:</h2>";
					 for(long i : roleForInsert.get(1)){ 
						 s+=i+"<br/>";
					 }
					 s += "<h2>responsible:</h2>";
					 for(long i : roleForInsert.get(2)){ 
						 s+=i+"<br/>";
					 }
					 s += "<h2>подписаны:</h2>";
					 for(long i : roleForInsert.get(3)){ 
						 s+=i+"<br/>";
					 }
				 s += "<h1>Роли для удаления:</h1>";
					 s += "<h2>администраторы:</h2>";
					 for(long i : roleForDelete.get(1)){ 
						 s+=i+"<br/>";
					 }
					 s += "<h2>responsible:</h2>";
					 for(long i : roleForDelete.get(2)){ 
						 s+=i+"<br/>";
					 }
					 s += "<h2>подписаны:</h2>";
					 for(long i : roleForDelete.get(3)){ 
						 s+=i+"<br/>";
					 }
				s += "<h1>Новые роли для вставки:</h1>";
					 s += "<h2>администраторы:</h2>";
					 for(Employee i : roleForInsertNew.get(1)){ 
						 s+=i.getNameForSchedule()+"<br/>";
					 }
					 s += "<h2>responsible:</h2>";
					 for(Employee i : roleForInsertNew.get(2)){ 
						 s+=i.getNameForSchedule()+"<br/>";
					 }
					 s += "<h2>подписаны:</h2>";
					 for(Employee i : roleForInsertNew.get(3)){ 
						 s+=i.getNameForSchedule()+"<br/>";
					 }
				 html1.setHTML(s);
				 
				 startSettingService.setEmployees(employeesForInsert, employeesForOnlyOurInsert,
						 employeesForUpdate, employeesForDelete, roleForInsert, roleForDelete, roleForInsertNew,
						 roleForInsertNewWithoutConformity,
						 new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								saveEmployeeButton.setEnabled(true);
								html1.setHTML(caught.getMessage());
							}

							@Override
							public void onSuccess(Void result) {
								html1.setHTML("Сотрудники успешно сохранены");
								loadEmployees(flexTable_1);			
								saveEmployeeButton.setEnabled(true);
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
		
		addEmployeeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createEmployeePanel(createObject, flexTable_1);
				createObject.center();
				
			}
		});

		loadClubs(flexTable);

		loadEmployees(flexTable_1);
		
		AbsolutePanel absolutePanel_2 = new AbsolutePanel();
		tabPanel.add(absolutePanel_2, "Категории", false);
		
		VerticalPanel verticalPanel = new VerticalPanel();
		absolutePanel_2.add(verticalPanel);
		verticalPanel.setSize("100%", "100%");
		
		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_1);
		horizontalPanel_1.setSize("100%", "100%");
		
		final ListBox comboBox = new ListBox();
		horizontalPanel_1.add(comboBox);
		horizontalPanel_1.setCellHorizontalAlignment(comboBox, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellHorizontalAlignment(comboBox, HasHorizontalAlignment.ALIGN_CENTER);
		
		Button btnNewButton = new Button("Добавить новую категорию");
		horizontalPanel_1.add(btnNewButton);
		horizontalPanel_1.setCellHorizontalAlignment(btnNewButton, HasHorizontalAlignment.ALIGN_RIGHT);
		
		btnNewButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				createCategoryPanel(createObject, comboBox);
				createObject.center();
			}
			
		});
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		
		final FlexTable flexTable_3 = new FlexTable();
		flexTable_3.addStyleName("empCategoryTable");
		horizontalPanel.add(flexTable_3);
		
		Image image = new Image("img/import.png");
		horizontalPanel.add(image);
		horizontalPanel.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
		
		final FlexTable insertedEmployeeInCategoryflexTable = new FlexTable();
		insertedEmployeeInCategoryflexTable.addStyleName("empCategoryTable");
		horizontalPanel.add(insertedEmployeeInCategoryflexTable);
		
		Button btnNewButton_1 = new Button("Сохранить");
		
		btnNewButton_1.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				startSettingService.setCategory(categories, employeeInCategoriesForDelete,
						employeeInCategoriesForInsert, categoriesForDelete, categoriesForInsert,
						new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						html1.setText("Категории успешно сохранены");
						loadCategories(comboBox, insertedEmployeeInCategoryflexTable, flexTable_3);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						html1.setText(caught.getMessage());						
					}
				});
			}
		});
		
		absolutePanel_2.add(btnNewButton_1);
		
		comboBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				int index = comboBox.getSelectedIndex();
				if(index>=categories.size())
					writeEmployeeInCategory(categoriesForInsert.get(index-categories.size()), insertedEmployeeInCategoryflexTable, flexTable_3);
				else
					writeEmployeeInCategory(categories.get(index), insertedEmployeeInCategoryflexTable, flexTable_3);
				selectedCategory = index;
				
			}
		});
		
		loadCategories(comboBox, insertedEmployeeInCategoryflexTable, flexTable_3);
		
		Button btnNewButton_2 = new Button("Удалить выбранную категорию");
		btnNewButton_2.addStyleName("rightDown");
		absolutePanel_2.add(btnNewButton_2);
		
		btnNewButton_2.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(selectedCategory<categories.size()){
					categoriesForDelete.add(categories.get(selectedCategory));
					categories.remove(selectedCategory);
				}
				else{
					categoriesForInsert.remove(selectedCategory - categories.size());
				}
				comboBox.removeItem(selectedCategory);
				if(comboBox.getItemCount()==0){
					insertedEmployeeInCategoryflexTable.removeAllRows();
					flexTable_3.removeAllRows();
					selectedCategory = -1;
					return;
				}
				comboBox.setSelectedIndex(0);
				if(0==categories.size())
					writeEmployeeInCategory(categoriesForInsert.get(0), insertedEmployeeInCategoryflexTable, flexTable_3);
				else
					writeEmployeeInCategory(categories.get(0), insertedEmployeeInCategoryflexTable, flexTable_3);
				selectedCategory = 0;
			}
			
		});

	}

	private void createCategoryPanel(final DialogBox createObject, final ListBox comboBox) {
		createObject.clear();
		createObject.setText("Добавление новой категории");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Label title = new Label("Название категории:");
		final TextBox titleTextBox = new TextBox();

		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (titleTextBox.getText() == null
						|| titleTextBox.getText().isEmpty()) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					Category c = new Category();
					c.setTitle(titleTextBox.getText());
					c.setEmployeeIdList(new ArrayList<Long>());
					categoriesForInsert.add(c);
					comboBox.addItem(c.getTitle());
					createObject.hide();
				}
			}
		});

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		delButton.addStyleName("rightDown");

		table.insertRow(0);
		table.insertCell(0, 0);
		table.setWidget(0, 0, title);
		table.insertCell(0, 1);
		table.setWidget(0, 1, titleTextBox);

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

		createObject.add(absPanel);
	}

	private void writeEmployeeInCategory(Category category, FlexTable flexTable, FlexTable flexTable_1) {
		flexTable.removeAllRows();
		flexTable_1.removeAllRows();
		for(Employee e : allEmployee){
			boolean added = false;
			for(Long id : category.getEmployeeIdList()){
				if(e.getEmployeeId()==id){
					added = true;
					writeCellEmployeeForCategory(e, flexTable, flexTable_1, true);
					break;
				}
			}
			if(!added)
				writeCellEmployeeForCategory(e, flexTable_1, flexTable, false);
		}
	}
	private void writeCellEmployeeForCategory(final Employee e, final FlexTable flexTable,
			final FlexTable flexTableNew, final boolean added){
		int indexRow = flexTable.getRowCount();
		flexTable.insertRow(indexRow);
		flexTable.insertCell(0, indexRow);
		Button bt = new Button();
		bt.setText(e.getNameForSchedule());
		bt.setStyleName("empCategoryButton");
		bt.setTitle(String.valueOf(indexRow));
		
		if(added){
			bt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					int index = Integer.parseInt(event.getRelativeElement()
							.getTitle());
					flexTable.removeRow(index);
					for(;index<flexTable.getRowCount();index++)
						flexTable.getWidget(index, 0).setTitle(String.valueOf(index));
					writeCellEmployeeForCategory(e, flexTableNew, flexTable, !added);
					Category c = null;
					boolean newCate = selectedCategory>=categories.size();
					if(newCate)
						c = categoriesForInsert.get(selectedCategory - categories.size());
					else{
						c = categories.get(selectedCategory);
						
						if(!employeeInCategoriesForDelete.containsKey(c.getCategoryId()))
							employeeInCategoriesForDelete.put(c.getCategoryId(), new ArrayList<Long>());
						
						if(employeeInCategoriesForInsert.containsKey(c.getCategoryId()) &&
								employeeInCategoriesForInsert.get(c.getCategoryId()).contains(e.getEmployeeId()))
							employeeInCategoriesForInsert.get(c.getCategoryId()).remove(e.getEmployeeId());
						else
							employeeInCategoriesForDelete.get(c.getCategoryId()).add(e.getEmployeeId());
					}
					c.getEmployeeIdList().remove(e.getEmployeeId());
				}
			});
		}
		else{
			bt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					int index = Integer.parseInt(event.getRelativeElement()
							.getTitle());
					flexTable.removeRow(index);
					for(;index<flexTable.getRowCount();index++)
						flexTable.getWidget(index, 0).setTitle(String.valueOf(index));
					writeCellEmployeeForCategory(e, flexTableNew, flexTable, !added);
					Category c = null;
					boolean newCate = selectedCategory>=categories.size();
					if(newCate)
						c = categoriesForInsert.get(selectedCategory - categories.size());
					else{
						c = categories.get(selectedCategory);
						
						if(!employeeInCategoriesForInsert.containsKey(c.getCategoryId()))
							employeeInCategoriesForInsert.put(c.getCategoryId(), new ArrayList<Long>());
						
						employeeInCategoriesForInsert.get(c.getCategoryId()).add(e.getEmployeeId());
					}
					c.getEmployeeIdList().add(e.getEmployeeId());
				}
			});
		}
		
		flexTable.setWidget(indexRow, 0, bt);
	}

	private void loadCategories(final ListBox comboBox, final FlexTable flexTable,
			final FlexTable flexTable_1) {
		allEmployee = new ArrayList<Employee>();
		categories = new ArrayList<Category>();
		//categoriesDictionary = new HashMap<Long, Collection<Employee>>();
		selectedCategory = -1;
		employeeInCategoriesForDelete = new HashMap<Long, Collection<Long>>();
		employeeInCategoriesForInsert = new HashMap<Long, Collection<Long>>();
		categoriesForDelete = new ArrayList<Category>();
		categoriesForInsert = new ArrayList<Category>();
		
		flexTable.removeAllRows();
		flexTable_1.removeAllRows();
		comboBox.clear();
		
		startSettingService.getAllEmploee(new AsyncCallback<Collection<Employee>>() {
			
			@Override
			public void onSuccess(final Collection<Employee> all) {
				startSettingService.getCategories(new AsyncCallback<Collection<Category>>() {
					
					@Override
					public void onSuccess(final Collection<Category> categoriesResult) {
						startSettingService.getCategoriesDictionary(new AsyncCallback<Map<Long,Collection<Employee>>>() {
							
							@Override
							public void onSuccess(Map<Long, Collection<Employee>> result) {
								allEmployee = new ArrayList<Employee>(all);
								categories = new ArrayList<Category>(categoriesResult);
								//categoriesDictionary = result;
								for(Category c : categories)
									comboBox.addItem(c.getTitle());
								if(categories.size()!=0){
									comboBox.setSelectedIndex(0);
									writeEmployeeInCategory(categories.get(0), flexTable, flexTable_1);
									selectedCategory = 0;
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								comboBox.getElement().getParentElement().setInnerHTML(caught.getMessage());
								
							}
						});
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						comboBox.getElement().getParentElement().setInnerHTML(caught.getMessage());
						
					}
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				comboBox.getElement().getParentElement().setInnerHTML(caught.getMessage());
				
			}
		});
		
	}

	private void loadEmployees(final FlexTable flexTable) {
		flexTable.removeAllRows();
		employeesOnlyOur = new ArrayList<Employee>();
		countEmployeesOnlyOur = 0;
		employeesDictionary = new HashMap<Long, Employee>();
		employeesForDelete = new HashSet<Employee>();
		employeesForUpdate = new HashSet<Employee>();
		employeesForInsert = new HashSet<Employee>();
		startSettingService
				.getEmployees(new AsyncCallback<Collection<Employee>>() {
					public void onFailure(Throwable caught) {
						flexTable.insertRow(0);
						flexTable.insertCell(0, 0);
						flexTable.setText(0, 0, caught.getMessage());

					}

					public void onSuccess(final Collection<Employee> result) {
						startSettingService.getDictionaryEmployee(new AsyncCallback<Map<Long,Employee>>() {
							
							@Override
							public void onSuccess(final Map<Long, Employee> dictionaryEmployee) {
								startSettingService.getRoleEmployee(new AsyncCallback<Map<Long,Collection<Boolean>>>() {
									
									@Override
									public void onSuccess(final Map<Long, Collection<Boolean>> roles) {
										startSettingService.getOnlyOurEmployees(new AsyncCallback<Collection<Employee>>() {
											
											@Override
											public void onSuccess(Collection<Employee> ourEmployee) {
												employees = new ArrayList<Employee>(result);
												employeesDictionary = dictionaryEmployee;
												employeeRole = roles;
												createEmployeeTable(flexTable, result);
												
												employeesOnlyOur = new ArrayList<Employee>(ourEmployee);
												countEmployeesOnlyOur = 0;
												for (Employee elem : ourEmployee) {
													countEmployeesOnlyOur++;
													writeEmployee(flexTable, elem);
													setRoleForEmployee(flexTable, elem, flexTable.getRowCount() - 1);
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
	
	private void loadClubs(final FlexTable flexTable) {
		flexTable.removeAllRows();
		clubsOnlyOur = new ArrayList<Club>();
		countClubsOnlyOur = 0;
		clubsDictionary = new HashMap<Long, Club>();
		clubsForDelete = new HashSet<Club>();
		clubsForUpdate = new HashSet<Club>();
		clubsForInsert = new HashSet<Club>();

		startSettingService.getClubs(new AsyncCallback<Collection<Club>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				flexTable.insertRow(0);
				flexTable.insertCell(0, 0);
				flexTable.setText(0, 0, caught.getMessage());
			}

			public void onSuccess(final Collection<Club> result) {
				startSettingService
						.getDictionaryClub(new AsyncCallback<Map<Long, Club>>() {

							@Override
							public void onSuccess(
									final Map<Long, Club> resultDictionary) {
								startSettingService
										.getOnlyOurClubs(new AsyncCallback<Collection<Club>>() {

											@Override
											public void onSuccess(
													Collection<Club> ourClub) {
												clubs = new ArrayList<Club>(
														result);
												clubsDictionary = resultDictionary;
												createClubTable(flexTable,
														result);
												countClubsOnlyOur = 0;
												clubsOnlyOur = new ArrayList<Club>(
														ourClub);
												for (Club elem : ourClub) {
													countClubsOnlyOur++;
													writeClub(flexTable, elem);
												}
											}

											@Override
											public void onFailure(
													Throwable caught) {
												flexTable.insertRow(0);
												flexTable.insertCell(0, 0);
												flexTable.setText(0, 0,
														caught.getMessage());

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
		if (index == 1) {
			for (int i = 0; i < clubs.size(); i++) {
				importClub(flexTable, i + 2);
			}
		} else {
			if (clubsDictionary.containsKey(clubs.get(index - 2).getClubId())) {
				Club c = clubsDictionary.get(clubs.get(index - 2).getClubId());
				if (c.getTitle() != clubs.get(index - 2).getTitle()) {
					c.setTitle(clubs.get(index - 2).getTitle());
					clubsForUpdate.add(c);
					flexTable.setText(index, 2, c.getTitle());
				}
			} else {
				clubsForInsert.add(clubs.get(index - 2));
				writeClub(flexTable, clubs.get(index - 2), index);
			}
		}
	}

	private void writeClub(FlexTable flexTable, Club c) {
		int index = flexTable.getRowCount();
		flexTable.insertRow(index);
		for (int i = 0; i <= 4; i++)
			flexTable.insertCell(index, i);
		flexTable.getFlexCellFormatter().addStyleName(index, 0, "beforeImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 1, "beforeImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 1, "afterImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 2, "afterImport");

		writeClub(flexTable, c, index);
	}

	private void writeClub(final FlexTable flexTable, Club c, int index) {
		if (flexTable.getWidget(index, 3) != null) {
			return;
		}
		flexTable.setText(index, 2, c.getTitle());
		CheckBox widget = new CheckBox();
		widget.setWidth("40px");
		widget.setHeight("40px");
		widget.setStyleName("checkbox");
		widget.setValue(c.getIsIndependent());
		flexTable.setWidget(index, 3, widget);

		Button btDel = new Button();
		btDel.setStyleName("buttonDelete");
		btDel.setWidth("40px");
		btDel.setHeight("40px");
		btDel.setTitle(String.valueOf(index));
		btDel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int index = Integer.parseInt(event.getRelativeElement()
						.getTitle());
				deleteClub(flexTable, index);

			}
		});
		flexTable.setWidget(index, 4, btDel);
	}

	private void deleteClub(final FlexTable flexTable, int index) {
		if (index == 1) {
			for (int i = flexTable.getRowCount() - 1; i >= 2; i--) {
				deleteClub(flexTable, i);
			}
		} else {
			if (index >= (clubs.size() + countClubsOnlyOur + 2)) {
				deleteOurItemFromTable(flexTable, index, clubsOnlyOur, clubs.size());
				return;
			} else if (index >= (clubs.size() + 2)) {
				clubsForDelete.add(clubsOnlyOur.get(index - clubs.size() - 2));
				countClubsOnlyOur--;
				deleteOurItemFromTable(flexTable, index, clubsOnlyOur, clubs.size());
				return;
			} else if (clubsDictionary.containsKey(clubs.get(index - 2)
					.getClubId())) {
				clubsForDelete.add(clubsDictionary.get(clubs.get(index - 2)
						.getClubId()));
				clubsForUpdate.remove(clubsDictionary.get(clubs.get(index - 2)
						.getClubId()));
				clubsDictionary.remove(clubs.get(index - 2).getClubId());
			} else if (clubsForInsert.contains(clubs.get(index - 2))) {
				clubsForInsert.remove(clubs.get(index - 2));
			}
			flexTable.setText(index, 2, "");
			flexTable.setText(index, 3, "");
			flexTable.setText(index, 4, "");
		}
	}

	private void deleteOurItemFromTable(FlexTable flexTable, int index, ArrayList<?> item, int lengthBefore) {
		flexTable.removeRow(index);
		item.remove(index - lengthBefore - 2);
		for (int i = index; i < flexTable.getRowCount(); i++) {
			flexTable.getWidget(i, 4).setTitle(String.valueOf(i));
		}
	}

	private void createClubTable(final FlexTable flexTable,
			Collection<Club> result) {

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
		for (Club elem : result) {
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
					int index = Integer.parseInt(event.getRelativeElement()
							.getTitle());
					importClub(flexTable, index);
				}
			});

			flexTable.setWidget(i, 1, btImp);
			flexTable.getFlexCellFormatter().addStyleName(i, 1, "import");

			flexTable.getFlexCellFormatter().addStyleName(i, 2, "afterImport");
			flexTable.insertCell(i, 3);
			flexTable.insertCell(i, 4);
			if (clubsDictionary.containsKey(elem.getClubId())) {
				writeClub(flexTable, clubsDictionary.get(elem.getClubId()), i);
			}
			i++;
		}
	}

	private void createEmployeeTable(final FlexTable flexTable,
			Collection<Employee> result) {

		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.setText(0, 0, "Сотрудники");
		flexTable.getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Импорт");
		flexTable.getFlexCellFormatter().addStyleName(0, 1, "mainHeader");
		flexTable.insertCell(0, 2);
		flexTable.setText(0, 2, "Сотрудники в расписании");
		flexTable.getFlexCellFormatter().setColSpan(0, 2, 4);
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
				importEmployee(flexTable, 1);
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
		flexTable.setWidget(1, 4, new HTML("Ответственное<br/> лицо"));
		flexTable.getFlexCellFormatter().addStyleName(1, 4, "secondHeader");
		flexTable.insertCell(1, 5);
		flexTable.setWidget(1, 5, new HTML("Подписаться<br/> на рассылку"));
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

		flexTable.setWidget(1, 6, bt1);
		flexTable.getFlexCellFormatter().addStyleName(1, 6, "secondHeader");
		int i = 2;
		for (Employee elem : result) {
			flexTable.insertRow(i);
			flexTable.insertCell(i, 0);
			flexTable.setText(
					i,
					0,
					elem.getNameForSchedule());
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
					int index = Integer.parseInt(event.getRelativeElement()
							.getTitle());
					importEmployee(flexTable, index);
				}
			});

			flexTable.setWidget(i, 1, btImp);
			flexTable.getFlexCellFormatter().addStyleName(i, 1, "import");

			flexTable.getFlexCellFormatter().addStyleName(i, 2, "afterImport");
			flexTable.insertCell(i, 3);
			flexTable.insertCell(i, 4);
			flexTable.insertCell(i, 5);
			flexTable.insertCell(i, 6);
			if (employeesDictionary.containsKey(elem.getEmployeeId())) {
				writeEmployee(flexTable, employeesDictionary.get(elem.getEmployeeId()), i);
				setRoleForEmployee(flexTable, employeesDictionary.get(elem.getEmployeeId()), i);
			}
			i++;
		}
	}

	private void setRoleForEmployee(FlexTable flexTable, Employee emp, int index) {
		if(employeeRole.containsKey(emp.getEmployeeId())){
			int i = 3;
			for(boolean check : employeeRole.get(emp.getEmployeeId())){
				((CheckBox)flexTable.getWidget(index, i)).setValue(check);
				i++;
			}
		}
	}
	
	private void writeEmployee(FlexTable flexTable, Employee employee) {
		int index = flexTable.getRowCount();
		flexTable.insertRow(index);
		for (int i = 0; i <= 6; i++)
			flexTable.insertCell(index, i);
		flexTable.getFlexCellFormatter().addStyleName(index, 0, "beforeImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 1, "beforeImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 1, "afterImport");
		flexTable.getFlexCellFormatter().addStyleName(index, 2, "afterImport");

		writeEmployee(flexTable, employee, index);
	}
	
	private void writeEmployee(final FlexTable flexTable, Employee employee, int index) {
		if (flexTable.getWidget(index, 3) != null) {
			return;
		}
		flexTable.setText(index, 2, employee.getNameForSchedule());
		CheckBox widget = new CheckBox();
		widget.setWidth("40px");
		widget.setHeight("40px");
		widget.setStyleName("checkbox");
		widget.setValue(false);
		CheckBox widget1 = new CheckBox();
		widget1.setWidth("40px");
		widget1.setHeight("40px");
		widget1.setStyleName("checkbox");
		widget1.setValue(false);
		CheckBox widget2 = new CheckBox();
		widget2.setWidth("40px");
		widget2.setHeight("40px");
		widget2.setStyleName("checkbox");
		widget2.setValue(true);
		flexTable.setWidget(index, 3, widget);
		flexTable.setWidget(index, 4, widget1);
		flexTable.setWidget(index, 5, widget2);

		Button btDel = new Button();
		btDel.setStyleName("buttonDelete");
		btDel.setWidth("40px");
		btDel.setHeight("40px");
		btDel.setTitle(String.valueOf(index));
		btDel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int index = Integer.parseInt(event.getRelativeElement()
						.getTitle());
				deleteEmployee(flexTable, index);

			}
		});
		flexTable.setWidget(index, 6, btDel);
		
	}

	private void importEmployee(FlexTable flexTable, int index) {
		if (index == 1) {
			for (int i = 0; i < employees.size(); i++) {
				importEmployee(flexTable, i + 2);
			}
		} else {
			if (employeesDictionary.containsKey(employees.get(index - 2).getEmployeeId())) {
				Employee e = employeesDictionary.get(employees.get(index - 2).getEmployeeId());
				if (e.compareTo(employees.get(index - 2))!=0) {
					e.setLastName((employees.get(index - 2).getLastName()));
					employeesForUpdate.add(e);
					flexTable.setText(index, 2, e.getNameForSchedule());
				}
			} else {
				employeesForInsert.add(employees.get(index - 2));
				writeEmployee(flexTable, employees.get(index - 2), index);
			}
		}
	}

	private void deleteEmployee(FlexTable flexTable, int index) {
		if (index == 1) {
			for (int i = flexTable.getRowCount() - 1; i >= 2; i--) {
				deleteEmployee(flexTable, i);
			}
		} else {
			if (index >= (employees.size() + countEmployeesOnlyOur + 2)) {
				deleteOurItemFromTable(flexTable, index, employeesOnlyOur, employees.size());
				return;
			} else if (index >= (employees.size() + 2)) {
				employeesForDelete.add(employeesOnlyOur.get(index - employees.size() - 2));
				countEmployeesOnlyOur--;
				deleteOurItemFromTable(flexTable, index, employeesOnlyOur, employees.size());
				return;
			} else if (employeesDictionary.containsKey(employees.get(index - 2)
					.getEmployeeId())) {
				employeesForDelete.add(employeesDictionary.get(employees.get(index - 2)
						.getEmployeeId()));
				employeesForUpdate.remove(employeesDictionary.get(employees.get(index - 2)
						.getEmployeeId()));
				employeesDictionary.remove(employees.get(index - 2).getEmployeeId());
			} else if (employeesForInsert.contains(employees.get(index - 2))) {
				employeesForInsert.remove(employees.get(index - 2));
			}
			flexTable.setText(index, 2, "");
			flexTable.setText(index, 3, "");
			flexTable.setText(index, 4, "");
			flexTable.setText(index, 5, "");
			flexTable.setText(index, 6, "");
		}
	}

	private void createClubPanel(final DialogBox createObject,
			final FlexTable flexTable) {
		createObject.clear();
		createObject.setText("Добавление нового клуба");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Label title = new Label("Название клуба:");
		final TextBox titleTextBox = new TextBox();

		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (titleTextBox.getText() == null
						|| titleTextBox.getText().isEmpty()) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					Club c = new Club();
					c.setTitle(titleTextBox.getText());
					clubsOnlyOur.add(c);
					writeClub(flexTable, c);
					createObject.hide();
				}
			}
		});

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		delButton.addStyleName("rightDown");

		table.insertRow(0);
		table.insertCell(0, 0);
		table.setWidget(0, 0, title);
		table.insertCell(0, 1);
		table.setWidget(0, 1, titleTextBox);

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

		createObject.add(absPanel);
	}
	
	private void createEmployeePanel(final DialogBox createObject,
			final FlexTable flexTable) {
		createObject.clear();
		createObject.setText("Добавление нового сотрудника");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labels = new ArrayList<Label>();
		labels.add(new Label("Фамилия:"));
		labels.add(new Label("Имя:"));
		labels.add(new Label("Отчество:"));
		labels.add(new Label("Email:"));
		
		final ArrayList<TextBox> textBoxs = new ArrayList<TextBox>();
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					Employee e = new Employee();
					e.setLastName(textBoxs.get(0).getText());
					e.setFirstName(textBoxs.get(1).getText());
					e.setSecondName(textBoxs.get(2).getText());
					e.setEmail(textBoxs.get(3).getText());
					employeesOnlyOur.add(e);
					writeEmployee(flexTable, e);
					createObject.hide();
				}
			}
		});

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});

		delButton.addStyleName("rightDown");

		for(int i=0;i<labels.size();i++){
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labels.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		absPanel.add(delButton);

		createObject.add(absPanel);
		
	}
	
	private boolean fieldsIsEmpty(ArrayList<TextBox> textBoxs){
		for(int i=0;i<textBoxs.size();i++){
			if(textBoxs.get(i).getText() == null
						|| textBoxs.get(i).getText().isEmpty())
				return true;
		}
		return false;
	}
}
