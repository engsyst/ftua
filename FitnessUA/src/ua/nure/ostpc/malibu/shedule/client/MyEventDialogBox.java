package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;

public class MyEventDialogBox extends DialogBox {
	
	private Button okButton;
	
	public MyEventDialogBox() {
	}
	
	public MyEventDialogBox(Button ok) {
		setOkButton(ok);
	}
	
	public void setOkButton(Button ok) {
		okButton = ok;
	}

	@Override
    protected void onPreviewNativeEvent(NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        switch (event.getTypeInt()) {
            case Event.ONKEYDOWN:
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    hide();
                }
                else if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER 
                		&& okButton != null){
                	okButton.click();
                }
                break;
        }
    }
}
