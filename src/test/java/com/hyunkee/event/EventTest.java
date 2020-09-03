package com.hyunkee.event;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
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

	//테스트케이스 5 : 비즈니스 로직 테스트 구현(단위테스트, "무료" 여부 파악)
		@Test
		@DisplayName("비즈니스 로직 테스트 구현(단위테스트, 무료 여부 파악)")
		public void domain_free_Test() throws Exception {
			
			// Given
			Event event = Event.builder()
							.basePrice(0)
							.maxPrice(0)
							.build();
			
			// When
			event.update();
			assertThat(event.isFree()).isTrue();
			
			// Given
			event = Event.builder()
							.basePrice(100)
							.maxPrice(0)
							.build();
			
			// When
			event.update();
			assertThat(event.isFree()).isFalse();
			
			// Given
			event = Event.builder()
							.basePrice(0)
							.maxPrice(100)
							.build();
			// When
			event.update();
			assertThat(event.isFree()).isFalse();
			
		}
		
		//테스트케이스 5 : 비즈니스 로직 테스트 구현(단위테스트, "오프라인" 여부 파악)
		//TDD> 이렇게 테스트 코드를 작성하면서 원하는 로직의 형태를 도출하고 그 도출 양식에 따라 코딩을 진행하는 것이 TDD다.
		//TDD> 따라서 초기에 시간이 무지하게 들고 테스트코드가 실제 로직보다 긴 경우도 존재 하지만 코드에 대한 안정성이 답보된다.
		@Test
		@DisplayName("비즈니스 로직 테스트 구현(단위테스트, 오프라인 여부 파악)")
		public void domain_offline_Test() throws Exception {
			
			// Given
			Event event = Event.builder()
							.location("busan hawoondae")
							.build();
			// When
			event.update();
			assertThat(event.isOffline()).isTrue();
			
			// Given
			event = Event.builder()
					.location("  ")
					.build();
			// When
			event.update();
			assertThat(event.isOffline()).isFalse();
			
			// Given
			event = Event.builder()
					.location("")
					.build();
			// When
			event.update();
			assertThat(event.isOffline()).isFalse();	

			// Given
			event = Event.builder()
					.build();
			// When
			event.update();
			assertThat(event.isOffline()).isFalse();	
			
		}
		

}
