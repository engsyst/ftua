package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ErrorEntryPoint implements EntryPoint {

	public void onModuleLoad() {

		/*
		 * Server error.
		 */

		RootPanel errorPageRootPanel = RootPanel.get("serverError");
		if (errorPageRootPanel != null) {
			errorPageRootPanel.setStyleName("errorPanel");

			AbsolutePanel errorPageAbsolutePanel = new AbsolutePanel();
			errorPageAbsolutePanel.setSize("100%", "300px");

			final Label errorPageTitleLabel = new Label();
			errorPageTitleLabel.setSize("100%", "1px");
			errorPageTitleLabel.addStyleName("title");
			errorPageTitleLabel.addStyleName("errorLabel");
			errorPageTitleLabel.setText("SERVER ERROR!");
			errorPageAbsolutePanel.add(errorPageTitleLabel, 0, 10);

			errorPageRootPanel.add(errorPageAbsolutePanel, 0, 5);
		}

		/*
		 * Access error.
		 */

		RootPanel accessErrorPageRootPanel = RootPanel.get("accessError");
		if (accessErrorPageRootPanel != null) {
			accessErrorPageRootPanel.setStyleName("errorPanel");

			AbsolutePanel accessErrorPageAbsolutePanel = new AbsolutePanel();
			accessErrorPageAbsolutePanel.setSize("100%", "300px");

			final Label accessErrorPageTitleLabel = new Label();
			accessErrorPageTitleLabel.setSize("100%", "1px");
			accessErrorPageTitleLabel.addStyleName("title");
			accessErrorPageTitleLabel.addStyleName("errorLabel");
			accessErrorPageTitleLabel.setText("ACCESS ERROR!");
			accessErrorPageAbsolutePanel.add(accessErrorPageTitleLabel, 0, 10);

			accessErrorPageRootPanel.add(accessErrorPageAbsolutePanel, 0, 5);
		}

		/*
		 * Page not found.
		 */

		RootPanel pageNotFoundRootPanel = RootPanel.get("pageNotFound");
		if (pageNotFoundRootPanel != null) {
			pageNotFoundRootPanel.setStyleName("errorPanel");

			AbsolutePanel pageNotFoundAbsolutePanel = new AbsolutePanel();
			pageNotFoundAbsolutePanel.setSize("100%", "300px");

			final Label pageNotFoundTitleLabel = new Label();
			pageNotFoundTitleLabel.setSize("100%", "1px");
			pageNotFoundTitleLabel.addStyleName("title");
			pageNotFoundTitleLabel.addStyleName("errorLabel");
			pageNotFoundTitleLabel.setText("PAGE NOT FOUND!");
			pageNotFoundAbsolutePanel.add(pageNotFoundTitleLabel, 0, 10);

			pageNotFoundRootPanel.add(pageNotFoundAbsolutePanel, 0, 5);
		}

	}
}
