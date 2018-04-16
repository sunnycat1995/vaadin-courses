package my.vaadin.vaadin_courses;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
public class HotelUI extends UI {
	final VerticalLayout layout = new VerticalLayout();
	final HotelService hotelService = HotelService.getInstance();
	final Grid<Hotel> hotelGrid = new Grid<>(Hotel.class);
	final TextField hotelNameFilter = new TextField("Hotel Name Filter");
	final TextField addressFilter = new TextField("Address Filter");
	final Button addHotel = new Button("Add hotel");
	final Button deleteHotel = new Button("Delete hotel");
	private HotelEditForm form = new HotelEditForm(this);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	setContent(layout);
    	VerticalLayout utilities = new VerticalLayout();
    	Link bookingcomLink = new Link("Search on Booking.com", new ExternalResource("https://www.booking.com"));
		bookingcomLink.setTargetName("_blank");
		
    	HorizontalLayout controls = new HorizontalLayout();
    	controls.addComponents(hotelNameFilter, addressFilter, addHotel, deleteHotel);
    	utilities.addComponents(controls, bookingcomLink);
    	deleteHotel.setEnabled(false);
    	
    	HorizontalLayout content = new HorizontalLayout();
    	content.addComponents(hotelGrid, form);
    	
    	layout.addComponents(utilities, content);
    	
    	hotelGrid.setColumnOrder("id", "name", "address", "rating", "category", "description");
    	Column<Hotel, Long> idColumn = (Column<Hotel, Long>) hotelGrid.getColumn("id"); 
    	hotelGrid.sort(idColumn.getId());
    	hotelGrid.setWidth(900, Unit.PIXELS);
    	hotelGrid.asSingleSelect().addValueChangeListener(e -> {
    		if (e.getValue() != null) {
    			deleteHotel.setEnabled(true);
    			form.setHotel(e.getValue());
    		}
    		else {
    			deleteHotel.setEnabled(false);
    			form.setVisible(false);
    		}
    	});
    	
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

    @WebServlet(urlPatterns = "/*", name = "HotelUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = HotelUI.class, productionMode = false)
    public static class HotelUIServlet extends VaadinServlet {
    }
}
