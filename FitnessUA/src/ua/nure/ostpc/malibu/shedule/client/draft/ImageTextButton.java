package ua.nure.ostpc.malibu.shedule.client.draft;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class ImageTextButton extends PushButton {

	public ImageTextButton(Image upImage, String text) {
		super(upImage);
		this.setHTML("<Table cellspacing=2> <tr> <td>" + upImage.toString()
				+ "</td> <td  align=middle>" + text + "</td></tr></Table>");
	}
	public ImageTextButton(Image upImage, String text, ClickHandler handler) {
		this(upImage, text);
		super.addClickHandler(handler);
	}
}