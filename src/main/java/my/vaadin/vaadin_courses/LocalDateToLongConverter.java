package my.vaadin.vaadin_courses;

import java.text.ParsePosition;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public class LocalDateToLongConverter implements Converter<LocalDate, Long> {
	private String error;
	public Long localDateToLongConverter(LocalDate localDate) {
		if (localDate != null) {
			Calendar cal = Calendar.getInstance();
			int year = localDate.getYear();
			int month = localDate.getMonth().getValue();
			int day = localDate.getDayOfMonth();
			cal.set(year, month, day);
			Long timeInMillis = cal.getTimeInMillis();
			return timeInMillis;
		}
		return null;
	}
	
	public LocalDateToLongConverter(){};
	

	public LocalDateToLongConverter(String error) {
		super();
		this.error = error;
	}

		  public Class<Long> getModelType() {
		    return Long.class;
		  }

		  public Class<LocalDate> getPresentationType() {
		    return LocalDate.class;
		  }

		@Override
		public Result<Long> convertToModel(LocalDate localDate, ValueContext context) {
			if (localDate != null) {
				Calendar cal = Calendar.getInstance();
				int year = localDate.getYear();
				int month = localDate.getMonth().getValue();
				int day = localDate.getDayOfMonth();
				cal.set(year, month, day);
				Long timeInMillis = cal.getTimeInMillis();
				Result<Long> n = Result.ok(timeInMillis.longValue());
				return n;
			}
			return null;
		}

		@Override
		public LocalDate convertToPresentation(Long value, ValueContext context) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(value.longValue());
			if (value > 0) {
				LocalDate localDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				return localDate;
			}
			return null;
		}

		
}
