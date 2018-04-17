package my.vaadin.vaadin_courses;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.annotations.PropertyId;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class HotelEditForm extends FormLayout{
	HotelUI ui;
	private HotelService hotelService = HotelService.getInstance();
	private Hotel hotel;
	private Binder<Hotel> binder = new Binder<>(Hotel.class);
	
	private TextField name = new TextField("Name");
	private TextField address = new TextField("Address");
	
	@PropertyId(value = "rating")
	private TextField rating = new TextField("Rating");
	
	private DateField  operatesFrom = new DateField("Operates from");
	private NativeSelect<HotelCategory> category = new NativeSelect<>("Category");
	private TextField url = new TextField("URL");
	private TextArea description = new TextArea("Description");
	
	private Button save = new Button("Save");
	private Button close = new Button("Close");
	
	Binding<Hotel, Integer> ratingBinding;
	Binding<Hotel, String> nameBinding;
	
	public HotelEditForm(HotelUI hotelUI) {
		this.ui = hotelUI;
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(save, close);
		addComponents(name, address, rating, operatesFrom, category, url, description, buttons);
		binder.forMemberField(name).withValidator(value -> !name.getValue().trim().isEmpty(), "Name cannot be empty");
		binder.forMemberField(address).withValidator(new StringLengthValidator("Please add the address", 1, null));
		binder.forMemberField(rating)
			    .withValidator(string -> string != null && !string.isEmpty(), "Rating should not be empty")
			    .withConverter(new StringToIntegerConverter("Rating should be an integer"))
			    .withValidator(integer -> integer > 0, "Rating should be a positive integer");
		
		binder.bindInstanceFields(this);
		
		name.setRequiredIndicatorVisible(true);
		address.setRequiredIndicatorVisible(true);
		rating.setRequiredIndicatorVisible(true);
		
		category.setItems(HotelCategory.values());
		
		Hotel hotelBeingEdited = new Hotel();
		// Click listeners for the buttons
		save.addClickListener(event -> {
		    if (binder.writeBeanIfValid(hotelBeingEdited)) {
		        System.out.println("Saved bean values: " + hotelBeingEdited);
		        save();
		    } else {
		        BinderValidationStatus<Hotel> validate = binder.validate();
		        String errorText = validate.getFieldValidationStatuses()
		                .stream().filter(BindingValidationStatus::isError)
		                .map(BindingValidationStatus::getMessage)
		                .map(Optional::get).distinct()
		                .collect(Collectors.joining(", "));
		        System.out.println("There are errors: " + errorText);
		    }
		});
		
		close.addClickListener(e -> setVisible(false));
	}

	private void save() {
		hotelService.save(getHotel());
		ui.updateList();
		setVisible(false);
	}
	
	public Hotel getHotel() {
		return hotel;
	}
	
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		binder.setBean(this.hotel);
		setVisible(true);
	}
}
