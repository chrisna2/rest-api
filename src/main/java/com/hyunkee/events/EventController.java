package com.hyunkee.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	
	public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
	}
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody EventDto eventDto) {
		
		
		/*원래 대로 라면
		Event event2 = Event.builder()
						.name(eventDto.getName())
						.description(eventDto.getDescription())
						..... 이런 셋팅 과정을 처리해야함)*/
		
		//modelMapper 라는 의존성 사용 : dto의 객체 명에 따라 자동으로 맵핑 처리함, 받는 객체의 크기가 더 커야 함
		Event event = modelMapper.map(eventDto, Event.class);
		
		Event newEvent = this.eventRepository.save(event);
		URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(createUri).body(event);
		
	}
	
} 
