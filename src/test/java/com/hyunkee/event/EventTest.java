package com.hyunkee.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.hyunkee.events.Event;

public class EventTest {

	@Test
	public void builder() {
		Event event  =  Event.builder()
							.name("Inflearn Spring Test Api!")
							.description("REST API development with Spring")
							.build();
		assertThat(event).isNotNull();
	}
	
	
	@Test
	public void javBean() {
		Event event = new Event();
		String name = "NHyunKee";
		event.setName("NHyunKee");
		event.setDescription("Spring");
		
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo("Spring");
	}
}
