package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CreateScheduleEntryPoint implements EntryPoint {
	private final CreateScheduleServiceAsync createScheduleService = GWT
			.create(CreateScheduleService.class);

	public void onModuleLoad() {
		RootPanel.get().add(getViewPanel());
	}

	public Canvas getViewPanel() {
		DynamicForm form = new DynamicForm();

		TextItem textItem = new TextItem();
		textItem.setTitle("Text");
		textItem.setHint("<nobr>A plain text field</nobr>");

		form.setFields(textItem);

		return form;
	}
}
