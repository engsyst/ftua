package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.HashSet;

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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StartSettingEntryPoint implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	
	private Collection<Club> clubs;
	
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
		flexTable.setBorderWidth(2);
		absolutePanel.add(flexTable);
		
		final Button btnNewButton = new Button("Выбрать");
		absolutePanel.add(btnNewButton);
		
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

		tabPanel.selectTab(0);
		
		btnNewButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int i = 1;
				if(clubs == null){
					return;
				}
				for(Club elem : clubs){
					elem.setIsIndependent(((CheckBox)(flexTable.getWidget(i, 1))).getValue());
					i++;
				}
				btnNewButton.setEnabled(false);
				
				startSettingService.setClubs(clubs, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						html1.setHTML("All success");
						btnNewButton.setEnabled(true);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						html1.setHTML(caught.getMessage());
						btnNewButton.setEnabled(true);
						
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
		
		startSettingService.getClubs(new AsyncCallback<Collection<Club>>(){
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				flexTable.insertRow(0);
				flexTable.insertCell(0, 0);
				flexTable.setText(0, 0, caught.getMessage());
			}

			public void onSuccess(Collection<Club> result) {
				clubs = result;
				flexTable.insertRow(0);
				flexTable.insertCell(0, 0);
				flexTable.setText(0, 0, "Клубы");
				flexTable.insertCell(0, 1);
				flexTable.setText(0, 1, "Независимый");
				int i = 1;
				for (Club elem : result)
				{
					flexTable.insertRow(i);
					flexTable.insertCell(i, 0);
					flexTable.setText(i, 0, elem.getTitle());
					flexTable.insertCell(i, 1);
					CheckBox widget = new CheckBox("");
					flexTable.setWidget(i, 1, widget);
					i++;
				}
			}
		});
		
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
}
