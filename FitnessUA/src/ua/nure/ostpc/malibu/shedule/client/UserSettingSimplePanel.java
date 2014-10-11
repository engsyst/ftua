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
import com.google.gwt.i18n.client.DefaultDateTimeFormatInfo;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.impl.cldr.DateTimeFormatInfoImpl_ru;
import com.google.gwt.user.client.Window;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class UserSettingSimplePanel extends SimplePanel {
	
	private final UserSettingServiceAsync userSettingService = GWT
			.create(UserSettingService.class);
	private ArrayList<AbsolutePanel> abs;
	private Label errorLabel;
	
	
	public UserSettingSimplePanel() {
		loadData();
	}

	private void loadData() {
		VerticalPanel vp = new VerticalPanel();
		TabPanel tabPanel = new TabPanel();
		vp.add(tabPanel);
		errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");
		vp.add(errorLabel);
		setWidget(vp);
		abs = new ArrayList<AbsolutePanel>();
		abs.add(new AbsolutePanel());
		abs.add(new AbsolutePanel());
		abs.add(new AbsolutePanel());
		tabPanel.add(abs.get(0), "Личные данные", true);
		tabPanel.add(abs.get(1), "Предпочтения", true);
		tabPanel.add(abs.get(2), "Изменение пароля", true);
		tabPanel.selectTab(0);
		
		userSettingService.getDataEmployee(new AsyncCallback<Employee>() {
			
			@Override
			public void onSuccess(Employee result) {
				if(result == null)
					return;
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
		DateTimeFormat format = DateTimeFormat
				.getFormat("dd.MM.yyyy");
		dateBox.setFormat(new DateBox.DefaultFormat(format));
		textBoxs.add(dateBox);
		
		((TextBox)textBoxs.get(0)).setValue(emp.getLastName());
		((TextBox)textBoxs.get(1)).setValue(emp.getFirstName());
		((TextBox)textBoxs.get(2)).setValue(emp.getSecondName());
		((TextBox)textBoxs.get(3)).setValue(emp.getEmail());
		((TextBox)textBoxs.get(4)).setValue(emp.getAddress());
		((TextBox)textBoxs.get(5)).setValue(emp.getCellPhone());
		((TextBox)textBoxs.get(6)).setValue(emp.getPassportNumber());
		((TextBox)textBoxs.get(7)).setValue(emp.getIdNumber());
		dateBox.getTextBox().setText(format.format(emp.getBirthday()));
		//dateBox.getDatePicker().setValue(emp.getBirthday());
		//((DateBox)textBoxs.get(0)).setValue(emp.getBirthday());

		final Button addButton = new Button("Изменить");
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					emp.setLastName(((TextBox)textBoxs.get(0)).getValue());
					emp.setFirstName(((TextBox)textBoxs.get(1)).getValue());
					emp.setSecondName(((TextBox)textBoxs.get(2)).getValue());
					emp.setEmail(((TextBox)textBoxs.get(3)).getValue());
					emp.setAddress(((TextBox)textBoxs.get(4)).getValue());
					emp.setCellPhone(((TextBox)textBoxs.get(5)).getValue());
					emp.setPassportNumber(((TextBox)textBoxs.get(6)).getValue());
					emp.setIdNumber(((TextBox)textBoxs.get(7)).getValue());
					emp.setBirthday(((DateBox)textBoxs.get(8)).getValue());
					addButton.setEnabled(false);
					userSettingService.setDataEmployee(emp, new AsyncCallback<Void>() {
						
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

		for(int i=0;i<labelsNotNull.size();i++){
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}
		absPanel.add(table);
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

		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Минимальное:"));
		labelsNotNull.add(new Label("Максимальное:"));

		
		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		
		((TextBox)textBoxs.get(0)).setText(String.valueOf(emp.getMin()));
		((TextBox)textBoxs.get(1)).setText(String.valueOf(emp.getMaxDays()));
		
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
				if(min>=max)
					errorLabel.setText("Минимальное колчисество должно быть меньше максимального.");
				else {
					final Employee e = new Employee();
					try{
					e.setMinAndMaxDays(min, max);
					}
					catch(Exception exc){
						errorLabel.setText(exc.getMessage());
					}
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

		table.insertRow(0);
		table.insertCell(0, 0);
		table.insertCell(0, 1);
		table.setText(0, 0, "Количество рабчих дней:");
		table.getFlexCellFormatter().setRowSpan(0, 0, 2);
		for(int i=0;i<labelsNotNull.size();i++){
			table.insertRow(i+1);
			table.insertCell(i+1, 0);
			table.setWidget(i+1, 0, labelsNotNull.get(i));
			table.insertCell(i+1, 1);
			table.setWidget(i+1, 1, textBoxs.get(i));
		}
		absPanel.add(table);
		absPanel.add(addButton);
		abs.get(1).clear();
		abs.get(1).add(absPanel);			
	}
	
	private void createUserPanel() {
		final AbsolutePanel absPanel = new AbsolutePanel();

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
		
		final Button addButton = new Button("Изменить");
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
				else if(!FieldVerifier.validateSigninPassword(((PasswordTextBox)textBoxs.get(1)).getValue())){
					((PasswordTextBox)(textBoxs.get(1))).setValue("");
					((PasswordTextBox)(textBoxs.get(2))).setValue("");
					errorLabel.setText("Password must contains at least 8 characters, lower-case and upper-case characters, digits, wildcard characters!");
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

		for(int i=0;i<labelsNotNull.size();i++){
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}
		absPanel.add(table);
		absPanel.add(addButton);
		abs.get(2).clear();
		abs.get(2).add(absPanel);
	}
	
	
}
