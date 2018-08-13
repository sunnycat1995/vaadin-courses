package my.vaadin.vaadin_courses;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
public class HotelUI extends UI {
	public static final String HOTEL = "Hotel";
	public static final String CATEGORY = "Category";
	final VerticalLayout layout = new VerticalLayout();
	final HotelService hotelService = HotelService.getInstance();
	final Grid<Hotel> hotelGrid = new Grid<>(Hotel.class);
	final TextField hotelNameFilter = new TextField("Hotel Name Filter");
	final TextField addressFilter = new TextField("Address Filter");
	final Button addHotel = new Button("Add hotel");
	final Button deleteHotel = new Button("Delete hotel");
	final Button editHotel = new Button("Edit hotel");
	
	final Button addCategory = new Button("Add category");
	final Button deleteCategory = new Button("Delete category");
	final Button editCategory = new Button("Edit category");
	
	private HotelEditForm form = new HotelEditForm(this);
	protected static final String MAINVIEW = "main";
	private NavigableMenuBar menubar;
	
	private Navigator navigator;
	final Grid<HotelCategory> categoryGrid = new Grid<>(HotelCategory.class);
	
	/** A menu bar that both controls and observes navigation */
    protected class NavigableMenuBar extends MenuBar implements ViewChangeListener {
        private static final long serialVersionUID = 7178106622402490205L;

        private MenuItem previous = null; // Previously selected item
        private MenuItem current  = null; // Currently selected item

        // Map view IDs to corresponding menu items
        HashMap<String,MenuItem> menuItems = new HashMap<String,MenuItem>();
        
        private Navigator navigator = null;
        
        public NavigableMenuBar(Navigator navigator) {
            this.navigator = navigator;
        }
        
        /** Navigate to a view by menu selection */ 
        MenuBar.Command mycommand = new MenuBar.Command() {
            private static final long serialVersionUID = 7920906555442357534L;

            public void menuSelected(MenuItem selectedItem) {
                String viewName = selectItem(selectedItem);
                navigator.navigateTo(viewName);
            }
        };
        
        public void addView(String viewName, String caption, Resource icon) {
            menuItems.put(viewName, addItem(caption, icon, mycommand));
        }

        /** Select a menu item by its view ID **/
        protected boolean selectView(String viewName) {
            // Check that the menu item exists
            if (!menuItems.containsKey(viewName))
                return false;

            if (previous != null)
                previous.setStyleName(null);
            if (current == null)
                current = menuItems.get(viewName);
            current.setStyleName("highlight");
            previous = current;
            
            return true;
        }

        /** Selects a new menu item */
        public String selectItem(MenuItem selectedItem) {
            current = selectedItem;
        
            // Do reverse lookup for the view ID
            for (String key: menuItems.keySet())
                if (menuItems.get(key) == selectedItem)
                    return key;
            
            return null;
        }

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return selectView(event.getViewName());
        }
            
        @Override
        public void afterViewChange(ViewChangeEvent event) {}
     };
	
	public class StartView extends VerticalLayout implements View {
    public StartView() {
        setSizeFull();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        Notification.show("Welcome to the Hotels form");
    }
}
	
	public class CategoryView extends VerticalLayout implements View {
	    public CategoryView() {
	        setSizeFull();        
	    }

	    @Override
	    public void enter(ViewChangeEvent event) {
	        Notification.show("Welcome to the Category form");
	        HorizontalLayout content = new HorizontalLayout();
	        HorizontalLayout controls = new HorizontalLayout();
	    	controls.addComponents(addCategory, deleteCategory, editCategory);
	    	categoryGrid.asSingleSelect().addValueChangeListener(e -> {
	    		if (e.getValue() != null) {
	    			deleteCategory.setEnabled(true);
	    			editCategory.setEnabled(true);
	    			//form.setCa(e.getValue());
	    		}
	    		else {
	    			deleteCategory.setEnabled(false);
	    			//form.setVisible(false);
	    		}
	    	});
	    	
	    	/*categoryGrid.asMultiSelect().addValueChangeListener(e -> {
	    		if (e.getValue() != null) {
	    			deleteCategory.setEnabled(true);
	    		}
	    		else {
	    			deleteCategory.setEnabled(false);
	    			//form.setVisible(false);
	    		}
	    	});*/
	    	
	        updateCategoryList();
	        //categoryGrid.setColumnOrder("category");
	    	content.addComponents(categoryGrid);
	    	layout.addComponent(menubar);
	    	layout.addComponent(content);
	    }
	}
	

    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	getPage().setTitle("Vaadin courses");
		setSizeFull();
		super.setId("mainView");
		layout.setId("mainLayout");
        ComponentContainerViewDisplay viewDisplay= new ComponentContainerViewDisplay(layout);
        navigator=new Navigator(UI.getCurrent(),viewDisplay);
    	
    	//navigator = new Navigator(this, viewDisplay);
        navigator.addView(HOTEL, new StartView());
        navigator.addView(CATEGORY, new CategoryView());
        navigator.navigateTo(HOTEL);
    	
    	setContent(layout);
    	
    	// Control and observe navigation by a main menu
    	menubar = new NavigableMenuBar(navigator);
        menubar.addStyleName("mybarmenu");
        layout.addComponent(menubar);
        navigator.addViewChangeListener(menubar);

final Label selection = new Label("-");
MenuBar.Command command = new MenuBar.Command() {
    public void menuSelected(MenuItem selectedItem) {
        selection.setValue(selectedItem.getText() + " selected.");
    }
};       
		menubar.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
		// Add items in the menu and associate them with a view ID
		menubar.addView(HOTEL, HOTEL, VaadinIcons.BUILDING);
		menubar.addView(CATEGORY, CATEGORY, VaadinIcons.ACADEMY_CAP); 	
		
    	HorizontalLayout controls = new HorizontalLayout();
    	controls.addComponents(hotelNameFilter, addressFilter, addHotel, deleteHotel, editHotel);
    	deleteHotel.setEnabled(false);
    	editHotel.setEnabled(false);
    	
    	HorizontalLayout content = new HorizontalLayout();
    	content.addComponents(hotelGrid, form);
    	
    	layout.addComponents(menubar, controls, content);
    	
    	//data
    	//hotelGrid.setColumnOrder("id");
    	Column<Hotel, String> htmlColumn = hotelGrid.addColumn(
    			hotel -> "<a href='"+ hotel.getUrl() + "' target='_blank'>"+hotel.getUrl()+"</a>", new HtmlRenderer());
    	htmlColumn.setCaption("Url");
    	
    	Long aaa = 1524579202787L;
    	Date d = new Date (aaa);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		LocalDate ld = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		System.out.println(ld);
    	
    	Column<Hotel, Long> idColumn = (Column<Hotel, Long>) hotelGrid.getColumn("id"); 
    	hotelGrid.sort(idColumn.getId());
    	hotelGrid.setWidth(900, Unit.PIXELS);
    	hotelGrid.setColumnOrder("id", "name", "address", "rating", "category", "operatesFrom", "description");
    	hotelGrid.asSingleSelect().addValueChangeListener(e -> {
    		if (e.getValue() != null) {
    			deleteHotel.setEnabled(true);
    			editHotel.setEnabled(true);
    			form.setHotel(e.getValue());
    		}
    		else {
    			deleteHotel.setEnabled(false);
    			form.setVisible(false);
    		}
    	});
    	
    	/*hotelGrid.asMultiSelect().addSelectionListener(e -> {
    		deleteHotel.setEnabled(true);
			editHotel.setEnabled(false);
			addHotel.setEnabled(true);
    	});*/
    	
    	deleteHotel.addClickListener(e -> {
    		Hotel delCandidate = hotelGrid.getSelectedItems().iterator().next();
    		hotelService.delete(delCandidate);
    		deleteHotel.setEnabled(false);
    		updateList();
    		form.setVisible(false);
    	});
    	
    	form.setVisible(false);
    	
    	addHotel.addClickListener(e -> form.setHotel(new Hotel()));
    	hotelNameFilter.addValueChangeListener(e -> updateList());
    	hotelNameFilter.setValueChangeMode(ValueChangeMode.LAZY); 
    	
    	addressFilter.addValueChangeListener(e -> updateList());
    	addressFilter.setValueChangeMode(ValueChangeMode.LAZY); 
    	updateList();
    }

	public void updateList() {
		List<Hotel> hotelList = hotelService.findAll(hotelNameFilter.getValue(), addressFilter.getValue());
		form.setVisible(false);
		hotelGrid.setItems(hotelList);
	}
	
	public void updateCategoryList() {
		HotelCategory.values();
		ArrayList<HotelCategory> categoryList = new ArrayList<>(Arrays.asList(HotelCategory.values()));
		System.out.println(categoryList);
		//form.setVisible(false);
		categoryGrid.setItems(categoryList);
	}

    @WebServlet(urlPatterns = "/*", name = "HotelUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = HotelUI.class, productionMode = false)
    public static class HotelUIServlet extends VaadinServlet {
    }
}
