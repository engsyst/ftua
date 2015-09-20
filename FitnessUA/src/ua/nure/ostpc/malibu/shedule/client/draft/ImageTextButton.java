package ua.nure.ostpc.malibu.shedule.client.draft;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class ImageTextButton extends PushButton {
	
	private String text;

	public String getText() {
		return text;
	}

	public ImageTextButton(Image upImage, String text) {
		super(upImage);
		this.text = text;
		this.setHTML("<Table class=\"itb-button\"> <tr> "
				+ "<td class=\"itb-textPanel\" align=middle>" + "<div class=\"itb-text\">"
				+ text + "</div>" + "</td>"
				+ "<td class=\"itb-imagePanel\">" + "<span class=\"itb-image\">" 
				+ upImage.toString() + "</span>" + "</td> "
				+ "</tr></Table>");
	}
	
	public ImageTextButton(Image upImage, String text, ClickHandler handler) {
		this(upImage, text);
		super.addClickHandler(handler);
	}
	
}