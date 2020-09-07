package com.hyunkee.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hyunkee.common.ErrorsResource;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final EventValidator eventValidator;

	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}

	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

		// 바인딩 할때 에러 발생 여부 확인
		if (errors.hasErrors()) {
			return badReq(errors);//이게 빠진거구나.. ㄷㄷ
		}
		
		//해당 리퀘스트에 Json을 보내고 싶으면 body에 json으로 변환이 가능한 대상의 값을 보내줘야 한다.
		//일반적인 DTO나, array, hashmap 등은 json등으로 변환이 가능한 데이터 형태이지만
		//Errors 같은 데이터의 형은 json으로 변환이 가능한 데이터가 아니다. 따라서 개발자는 해당 데이터에 대해서 json으로 변환이 가능하도록 해야 한다.
		//그걸 보통 serializer 클래스라고 약속한다. 		

		// 데이터 값의 정합성 여부 확인
		eventValidator.validate(eventDto, errors);

		if (errors.hasErrors()) {
			//return ResponseEntity.badRequest().body(errors);//이게 빠진거구나.. ㄷㄷ
			return badReq(errors);//리팩토링
			
			// Errors는 Json 형태의 데이터로 변환할 수 없기 때문에 해당 데이터에 대해 화면에 전송하기 위해서는 따로 JSON 변환을 해야
			// 한다.
		}

		/*
		 * 원래 대로 라면 Event event2 = Event.builder() .name(eventDto.getName())
		 * .description(eventDto.getDescription()) ..... 이런 셋팅 과정을 처리해야함)
		 */

		// modelMapper 라는 의존성 사용 : dto의 객체 명에 따라 자동으로 맵핑 처리함, 받는 객체의 크기가 더 커야 함
		Event event = modelMapper.map(eventDto, Event.class);
		
		// 이벤트 업데이트 처리 --> 실제로는 서비스 단에서 처리
		event.update();
		Event newEvent = this.eventRepository.save(event);
		// --> 실제로는 서비스 단에서 처리 (주의!!)
		
		//URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        //eventResource.add(selfLinkBuilder.withSelfRel()); //self link는  EventResource 생성자를 통해 생성
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.created(createdUri).body(eventResource); //또또 잊었네

	}
	
	//30개의 이벤트를 10개씩 두번째 페이지 조회하기
	@GetMapping
	public ResponseEntity inqueryEvent(Pageable pageable, PagedResourcesAssembler<Event> assambler) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		//각각에 들어있는 이벤트에 대한 링크를 작성하기 위해서 다음과 같이 작성
		var pagedResources = assambler.toModel(page, e -> new EventResource(e));
		pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
		return ResponseEntity.ok(pagedResources);
	}
	
	

	private ResponseEntity<ErrorsResource> badReq(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}

	@GetMapping("/{id}")
	public ResponseEntity getEventOne(@PathVariable Integer id) {
		
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        
        if (optionalEvent.isEmpty()) {
        	logger.info("★notFound");
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        logger.info("★ok");
        return ResponseEntity.ok(eventResource);
	}
	
	
	
	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id,
									  @RequestBody @Valid EventDto eventDto,
									  Errors errors) {
		
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		
		if (optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		if(errors.hasErrors()) {
			return badReq(errors);
		}
		
		this.eventValidator.validate(eventDto, errors);
		
		if(errors.hasErrors()) {
			return badReq(errors);
		}

		Event exiEvent = optionalEvent.get();
		this.modelMapper.map(eventDto, exiEvent);
		Event savedEvent = this.eventRepository.save(exiEvent);
				
		EventResource eventResource = new EventResource(savedEvent);
		eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
		
		return ResponseEntity.ok(eventResource);
	}
	
	
}
