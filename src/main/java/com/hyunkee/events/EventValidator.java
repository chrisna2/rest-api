package com.hyunkee.events;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	public void validate(EventDto eventDto, Errors errors) {
		
		if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
			//errors.rejectValue("basePrice", "wrongValue", "basePrice is Wrong");
			//errors.rejectValue("maxPrice", "wrongValue", "maxPrice is Wrong"); //필드에러
			
			errors.reject("Wrong Prices", "Values for prices is Wrong");	   //글로벌 에러
		}

		LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
		LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
		LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
		LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();

		// 날짜 체크

		// TO-DO endEventDateTime
		if (endEventDateTime.isBefore(beginEventDateTime) 
				|| endEventDateTime.isBefore(closeEnrollmentDateTime)
				|| endEventDateTime.isBefore(beginEnrollmentDateTime)) {
			errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
		}

		// TO-DO BeginEventDateTime
		if (beginEventDateTime.isAfter(closeEnrollmentDateTime) 
				|| beginEventDateTime.isAfter(endEventDateTime)
				|| beginEventDateTime.isAfter(beginEnrollmentDateTime)) {
			errors.rejectValue("beginEventDateTime", "wrongValue", "beginEventDateTime is wrong");
		}

		// TO-DO beginEnrollmentDateTime
		if (beginEnrollmentDateTime.isAfter(closeEnrollmentDateTime)
				|| beginEnrollmentDateTime.isAfter(endEventDateTime)
				|| beginEnrollmentDateTime.isBefore(beginEventDateTime)) {
			errors.rejectValue("beginEnrollmentDateTime", "wrongValue", "beginEnrollmentDateTime is wrong");
		}

		// TO-DO closeEnrollmentDateTime
		if (closeEnrollmentDateTime.isBefore(beginEventDateTime) 
				|| closeEnrollmentDateTime.isAfter(endEventDateTime)
				|| closeEnrollmentDateTime.isBefore(beginEnrollmentDateTime)) {
			errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong");
		}

	}
}
