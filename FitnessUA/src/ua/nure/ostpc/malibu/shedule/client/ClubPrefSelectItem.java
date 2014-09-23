package ua.nure.ostpc.malibu.shedule.client;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Club preference select item.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ClubPrefSelectItem extends SelectItem {

	public ClubPrefSelectItem(LinkedHashMap<String, String> valueMap) {
		super("name");
		setTextBoxStyle("item");
		setMultiple(true);
		setShowTitle(false);
		setMultipleAppearance(MultipleAppearance.PICKLIST);
		setValueMap(valueMap);

		addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				Window.alert(event.getValue().toString());
				Window.alert(event.getItem().getSelectedRecord()
						.getAttribute("name"));
				Window.alert(event
						.getItem()
						.getSelectedRecord()
						.getAttribute(
								event.getItem().getSelectedRecord()
										.getAttributes()[1]));
			}
		});
	}
}
