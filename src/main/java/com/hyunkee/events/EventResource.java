package com.hyunkee.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class EventResource extends EntityModel<Event>{
	
	public EventResource(Event event, Link...links) {
		super(event, links); //deprecated
		//of(event, links);      //public static <T> EntityModel<T> of(T content, Link... links) 
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel()); // self 링크 생성
	}
}
