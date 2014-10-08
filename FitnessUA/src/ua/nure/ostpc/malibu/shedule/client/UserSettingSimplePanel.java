package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.shared.FieldVerifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class UserSettingSimplePanel extends SimplePanel {
	
	private final UserSettingServiceAsync userSettingService = GWT
			.create(UserSettingService.class);
	private ArrayList<AbsolutePanel> abs;
	
	public UserSettingSimplePanel() {
		loadData();
	}

	private void loadData() {
		
		TabPanel tabPanel = new TabPanel();
		setWidget(tabPanel);

		abs = new ArrayList<AbsolutePanel>();
		abs.add(new AbsolutePanel());
		tabPanel.add(abs.get(0), "Личные данные", true);
		abs.add(new AbsolutePanel());
		tabPanel.add(abs.get(2), "Изменение пароля", true);
		abs.add(new AbsolutePanel());
		tabPanel.add(abs.get(1), "Предпочтения", true);
		
		userSettingService.getDataEmployee(new AsyncCallback<Employee>() {
			
			@Override
			public void onSuccess(Employee result) {
				
				createEmployeePanel(result);
				createUserPanel();
				createPrefPanel(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				setWidget(new Label(caught.getMessage()));
				
			}
		});
	}
	
	private void createEmployeePanel(final Employee emp) {
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Фамилия:"));
		labelsNotNull.add(new Label("Имя:"));
		labelsNotNull.add(new Label("Отчество:"));
		labelsNotNull.add(new Label("Email:"));
		labelsNotNull.add(new Label("Адресс:"));
		labelsNotNull.add(new Label("Мобильный телефон:"));
		labelsNotNull.add(new Label("Номер паспорта:"));
		labelsNotNull.add(new Label("IdNumber:"));
		labelsNotNull.add(new Label("Дата рождения: "));
		
		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		DateBox dateBox = new DateBox();
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getFormat("dd.MM.yyyy")));
		textBoxs.add(dateBox);
		
		((TextBox)textBoxs.get(0)).setValue(emp.getLastName());
		((TextBox)textBoxs.get(0)).setValue(emp.getFirstName());
		((TextBox)textBoxs.get(0)).setValue(emp.getSecondName());
		((TextBox)textBoxs.get(0)).setValue(emp.getEmail());
		((TextBox)textBoxs.get(0)).setValue(emp.getAddress());
		((TextBox)textBoxs.get(0)).setValue(emp.getCellPhone());
		((TextBox)textBoxs.get(0)).setValue(emp.getPassportNumber());
		((TextBox)textBoxs.get(0)).setValue(emp.getIdNumber());
		((DateBox)textBoxs.get(0)).setValue(emp.getBirthday());
		
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		final Button addButton = new Button("Изменить");
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					Employee e = new Employee();
					e.setLastName(((TextBox)textBoxs.get(0)).getValue());
					e.setFirstName(((TextBox)textBoxs.get(1)).getValue());
					e.setSecondName(((TextBox)textBoxs.get(2)).getValue());
					e.setEmail(((TextBox)textBoxs.get(3)).getValue());
					e.setAddress(((TextBox)textBoxs.get(4)).getValue());
					e.setCellPhone(((TextBox)textBoxs.get(5)).getValue());
					e.setPassportNumber(((TextBox)textBoxs.get(6)).getValue());
					e.setIdNumber(((TextBox)textBoxs.get(7)).getValue());
					e.setBirthday(((DateBox)textBoxs.get(8)).getValue());
					addButton.setEnabled(false);
					userSettingService.setDataEmployee(e, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							errorLabel.setText("Данный успешно сохранены!");
							addButton.setEnabled(true);
							createEmployeePanel(emp);
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(caught.getMessage());
							addButton.setEnabled(false);
						}
					});
				}
			}
		});

		addButton.addStyleName("rightDown");

		for(int i=0;i<labelsNotNull.size();i++){
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}

		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		abs.get(0).clear();
		abs.get(0).add(absPanel);
		
	}
	
	private boolean fieldsIsEmpty(ArrayList<Widget> textBoxs){
		for(int i=0;i<textBoxs.size();i++){
			switch(textBoxs.get(i).getClass().getSimpleName()){
			case "TextBox":
			case "PasswordTextBox":
				if(((TextBox)textBoxs.get(i)).getValue() == null 
					|| ((TextBox)textBoxs.get(i)).getValue().isEmpty())
						return true;
				break;
			case "DateBox":
				if(((DateBox)textBoxs.get(i)).getValue() == null)
					return true;
				break;
			}
		}
		return false;
	}

	private void createPrefPanel(Employee emp) {
		final AbsolutePanel absPanel = new AbsolutePanel();
		final ListBox comboBox = new ListBox();

		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Минимальное:"));
		labelsNotNull.add(new Label("Максимальное:"));

		
		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		
		((TextBox)textBoxs.get(0)).setText(String.valueOf(emp.getMin()));
		((TextBox)textBoxs.get(0)).setText(String.valueOf(emp.getMaxDays()));
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");
		
		final Button addButton = new Button("Изменить");
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
					return;
				}
				int min, max;
				try{
					min = Integer.parseInt(((TextBox)(textBoxs.get(0))).getValue());
					max = Integer.parseInt(((TextBox)(textBoxs.get(1))).getValue());
					if(min<0 || max<0 || min>7 || max>7)
						throw new Exception();
				}
				catch(Exception e){
					errorLabel.setText("Данные должны быть положительными числами меньше или равными 7");
					return;
				}
				if(min>max)
					errorLabel.setText("Минимальное колчисество должно быть мень максимального.");
				else {
					addButton.setEnabled(false);
					final Employee e = new Employee();
					e.setMinAndMaxDays(min, max);
					addButton.setEnabled(false);
					userSettingService.setPreference(e, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							addButton.setEnabled(true);
							errorLabel.setText("Данные успешно обновлены.");
							createPrefPanel(e);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(caught.getMessage());
							addButton.setEnabled(true);
						}
					});
				}
			}
		});

		addButton.addStyleName("rightDown");

		table.insertRow(0);
		table.insertCell(0, 0);
		table.getFlexCellFormatter().setRowSpan(0, 0, 2);
		table.setText(0, 0, "Количество рабчих дней:");
		for(int i=0;i<labelsNotNull.size();i++){
			table.insertRow(i+1);
			table.insertCell(i+1, 0);
			table.setWidget(i+1, 0, labelsNotNull.get(i));
			table.insertCell(i+1, 1);
			table.setWidget(i+1, 1, textBoxs.get(i));
		}
		
		absPanel.add(comboBox);
		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		abs.get(0).clear();
		abs.get(0).add(absPanel);
			
	}
	
	private void createUserPanel() {
		final AbsolutePanel absPanel = new AbsolutePanel();
		final ListBox comboBox = new ListBox();

		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Старый пароль:"));
		labelsNotNull.add(new Label("Новый пароль:"));
		labelsNotNull.add(new Label("Повторите новый пароль:"));

		
		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new PasswordTextBox());
		textBoxs.add(new PasswordTextBox());
		textBoxs.add(new PasswordTextBox());
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");
		
		final Button addButton = new Button("Изменитьить");
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
					((PasswordTextBox)(textBoxs.get(0))).setValue("");
					((PasswordTextBox)(textBoxs.get(1))).setValue("");
					((PasswordTextBox)(textBoxs.get(2))).setValue("");
				}
				else if(!((PasswordTextBox)(textBoxs.get(1))).getValue().equals(
						((PasswordTextBox)(textBoxs.get(2))).getValue())){
					errorLabel.setText("Пароли не совпадают");
					((PasswordTextBox)(textBoxs.get(1))).setValue("");
					((PasswordTextBox)(textBoxs.get(2))).setValue("");
				}
				else if(!FieldVerifier.validateSigninData(((TextBox)textBoxs.get(0)).getValue(),
						((PasswordTextBox)textBoxs.get(1)).getValue()).isEmpty()){
					String s="";
					Map<String, String> maps = FieldVerifier.validateSigninData(((TextBox)textBoxs.get(0)).getValue(),
							((PasswordTextBox)textBoxs.get(1)).getValue());
					for(String key : maps.keySet()){
						s+=maps.get(key)+"\n";
					}
					errorLabel.setText(s);
				}
				else {
					addButton.setEnabled(false);
					userSettingService.setPass(((PasswordTextBox)textBoxs.get(0)).getValue(),
							((PasswordTextBox)textBoxs.get(1)).getValue(), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							addButton.setEnabled(true);
							errorLabel.setText("Пароль успешно изменен!");
							createUserPanel();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(caught.getMessage());
							addButton.setEnabled(true);
						}
					});
				}
			}
		});

		addButton.addStyleName("rightDown");

		for(int i=0;i<labelsNotNull.size();i++){
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}
		
		absPanel.add(comboBox);
		absPanel.add(table);
		absPanel.add(errorLabel);
		absPanel.add(addButton);
		abs.get(0).clear();
		abs.get(0).add(absPanel);
	}
	
	
}
