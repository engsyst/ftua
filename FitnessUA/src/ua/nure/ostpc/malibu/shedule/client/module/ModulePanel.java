package ua.nure.ostpc.malibu.shedule.client.module;

//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.logical.shared.ValueChangeEvent;
//import com.google.gwt.event.logical.shared.ValueChangeHandler;
//import com.google.gwt.user.client.History;
//import com.google.gwt.user.client.ui.RootPanel;
//import com.google.gwt.user.client.ui.VerticalPanel;

public class ModulePanel /* implements ValueChangeHandler<String>  */{
/*	public static final String VIEW_ITEM = "view";
	public static final String CREATE_ITEM = "create";
	
	private VerticalPanel panel = new VerticalPanel();
	private ModulePanelItem viewItem;
	private ModulePanelItem createItem;

	public VerticalPanel getPanel() {
		return panel;
	}

	public ModulePanelItem getViewItem() {
		return viewItem;
	}

	public ModulePanelItem getCreateItem() {
		return createItem;
	}

//	public void addClickHandler(ClickHandler handler, String item) {
//		if  (VIEW_ITEM.equals(item)) {
//			viewItem.getImg().addClickHandler(handler);
//			viewItem.getLabel().addClickHandler(handler);
//		} else if (CREATE_ITEM.equals(item)) {
//			createItem.getImg().addClickHandler(handler);
//			createItem.getLabel().addClickHandler(handler);
//		}
//	}
//	
	public ModulePanel() {
		panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.STYLE_MODULE_PANEL);
		viewItem = new ModulePanelItem("Просмотр текущего", "img/viewIcon.png");
		panel.add(viewItem.getPanel());
		createItem = new ModulePanelItem("Создать новый", "img/createIcon.png");
		panel.add(createItem.getPanel());
		
		// Add handlers
		viewItem.getImg().addClickHandler(new ViewModuleHandler());
		viewItem.getLabel().addClickHandler(new ViewModuleHandler());
		createItem.getImg().addClickHandler(new CreateModuleHandler());
		createItem.getLabel().addClickHandler(new CreateModuleHandler());
	}
	
	private void doCreate() {
		History.newItem("create");
		RootPanel root = RootPanel.get("moduleContentContainer");
		root.clear();
		root.add(createModule.getPanel());
	}
	
	private void doView() {
		History.newItem("view");
		RootPanel root = RootPanel.get("moduleContentContainer");
		root.clear();
		root.add(viewModule.getPanel());
	}
	
	class ViewModuleHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			doView();
		}
	}
	
	class CreateModuleHandler implements ClickHandler {
		
		@Override
		public void onClick(ClickEvent event) {
			doCreate();
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		
		if (token != null) {
			if (VIEW_ITEM.equals(token)) {
				doView();
			} else if (CREATE_ITEM.equals(token)) {
				doCreate();
			} else {
				doView();
			}
		}
	} */
}
