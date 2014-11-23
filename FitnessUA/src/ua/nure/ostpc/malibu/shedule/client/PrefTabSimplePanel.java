package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;

import ua.nure.ostpc.malibu.shedule.entity.Preference;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PrefTabSimplePanel extends SimplePanel {
	private HTML durat;
	private HTML count;
	private StartSettingServiceAsync async;
	
	public PrefTabSimplePanel(StartSettingServiceAsync async) {
		this.async = async;
		VerticalPanel root = new VerticalPanel();
		durat = new HTML();
		count = new HTML();
		root.add(durat);
		root.add(count);
		final MyEventDialogBox createObject = new MyEventDialogBox();
		createObject.setAnimationEnabled(true);
		final Button add  = new Button("Изменить смену");
		root.add(add);
		add.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(add.getText()=="Добавить смену" )
					createPrefPanel(0, createObject, add);
				else
					createPrefPanel(1, createObject, add);
				createObject.center();
			}
		});
		loadPref(add);
		
		root.setCellHorizontalAlignment(count, HasHorizontalAlignment.ALIGN_LEFT);
		root.setCellHorizontalAlignment(add, HasHorizontalAlignment.ALIGN_LEFT);
		setWidget(root);
		
	}
	
	private void loadPref(final Button add) {
		
		async.getPreference(new AsyncCallback<Preference>() {
			
			@Override
			public void onSuccess(Preference result) {
				if(result == null){
					add.setText("Добавить смену");
					return;
				}
				String s = "Продолжительность рабочего дня: " + 
						String.valueOf(result.getWorkHoursInDay()) + " час";
				if(result.getWorkHoursInDay()%20 == 1)
					s += "";
				else if(result.getWorkHoursInDay()%20 == 2 ||
						result.getWorkHoursInDay()%20 == 3)
					s += "а";
				else
					s += "ов";
				durat.setHTML(s);
				count.setHTML("Количество смен: " + 
						String.valueOf(result.getShiftsNumber()));
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				durat = new HTML(caught.getMessage());
				
			}
		});
		
	}

	private void createPrefPanel(int type, final MyEventDialogBox createObject, final Button main) {
		createObject.clear();
		if(type == 0)
		createObject.setText("Добавление смен");
		else
			createObject.setText("Изменение смен");
		AbsolutePanel absPanel = new AbsolutePanel();
		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Длина рабочего дня:"));
		labelsNotNull.add(new Label("Количество смен:"));
		
		final ArrayList<TextBox> textBoxs = new ArrayList<TextBox>();
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());
		final Label errorLabel = new Label();
		errorLabel.setStyleName("serverResponseLabelError");

		Button delButton = new Button("Отмена", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createObject.hide();
			}
		});
		
		Button addButton = new Button("Добавить", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if ((textBoxs.get(0).getText()=="" || textBoxs.get(0).getText() == null) ||
					(textBoxs.get(1).getText()=="" || textBoxs.get(1).getText() == null)){
					errorLabel.setText("Вы заполнили не все поля");
				} else {
					int hours = 0, co = 0;
					try{
						hours = Integer.parseInt(textBoxs.get(0).getText());
						co = Integer.parseInt(textBoxs.get(1).getText());
					}
					catch(Exception e){
						errorLabel.setText("Данные должны быть числами.");
						return;
					}
					if(!(hours<=24 && hours>0)){
						errorLabel.setText("Количество рабочих часов не должно превышать 24 часа");
						return;
					}
					Preference pref = new Preference();
					pref.setShiftsNumber(co);
					pref.setWorkHoursInDay(hours);
					async.setPreference(pref, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							loadPref(main);
							createObject.hide();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(caught.getMessage());
						}
					});
					
				}
			}
		});

		if(type == 0)
			addButton.setText("Сохранить");
		else
			addButton.setText("Изменить");
		
		

		delButton.addStyleName("rightDown");

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
		absPanel.add(delButton);

		createObject.setOkButton(addButton);
		createObject.add(absPanel);
	}
}

