package com.hyunkee.event;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunkee.events.Event;
import com.hyunkee.events.EventRepository;
import com.hyunkee.events.EventStatus;

@ExtendWith(SpringExtension.class) //spring boot 5부터 RunWith을 다음으로 대체
//@WebMvcTest //슬라이싱 테스트 (mocktest)
@SpringBootTest //통합테스트, 웹 쪽으로 관련된 테스트를 진행하기 가장 좋다.
@AutoConfigureMockMvc
public class EventControllerTest {

	@Autowired
	MockMvc mockMvc; //웹서버를 띄우지 않고 디스패처 서블릿을 생성, 속도는 단위테스트보다는 좀 걸림
	
	@Autowired
	ObjectMapper objectMapper;
	
	//@MockBean //슬라이싱 테스트를 사용할 경우 사용
	//EventRepository eventRepository;
	
	//테스트케이스 1 : 입력값들을 전달하면 JSON 응답으로 201이 나오는지 확인
	@Test
	public void createEvent() throws Exception {
		
		Event event = Event.builder()
				.id(100)
				.name("Spring")
				.description("REST API Develop Test")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 28, 5, 18))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 1, 23, 59))
				.beginEventDateTime(LocalDateTime.of(2020, 8, 29, 5, 18))
				.endEventDateTime(LocalDateTime.of(2020, 9, 1, 20, 00))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("Seoul Sadangdong Mcdonald")
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.PUBLISHED)
				.build();
		
		//아이디 설정
		//event.setId(10);
		
		//원래는 테스트 코드를 만들고 나서 작성 코드를 만들어야 TDD다.
		//Mockito.when(eventRepository.save(event)).thenReturn(event);
		
		mockMvc.perform(post("/api/events/")
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaTypes.HAL_JSON)
			   .content(objectMapper.writeValueAsString(event)))
			   .andDo(print())
			   .andExpect(status().isCreated())
			   .andExpect(jsonPath("id").exists())
			   .andExpect(header().exists(HttpHeaders.LOCATION))
			   .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			   .andExpect(jsonPath("id").value(Matchers.not(100)))
			   .andExpect(jsonPath("free").value(Matchers.not(true)))
			   .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
		
	}
	
	//테스트케이스 2 : 입력값들을 전달하면 JSON 응답으로 201이 나오는지 확인
	@Test
	public void createEvent_bad_request() throws Exception {
		
		Event event = Event.builder()
							.id(100)
							.name("Spring")
							.description("REST API Develop Test")
							.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 28, 5, 18))
							.closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 1, 23, 59))
							.beginEventDateTime(LocalDateTime.of(2020, 8, 29, 5, 18))
							.endEventDateTime(LocalDateTime.of(2020, 9, 1, 20, 00))
							.basePrice(100)
							.maxPrice(200)
							.limitOfEnrollment(100)
							.location("Seoul Sadangdong Mcdonald")
							.free(true)
							.offline(false)
							.eventStatus(EventStatus.PUBLISHED)
							.build();
		
		mockMvc.perform(post("/api/events/")
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaTypes.HAL_JSON)
			   .content(objectMapper.writeValueAsString(event)))
			   .andDo(print())
			   .andExpect(status().isBadRequest());

		
	}
		
}
