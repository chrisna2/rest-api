package com.hyunkee.event;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunkee.common.RestDocsConfiguration;
import com.hyunkee.events.Event;
import com.hyunkee.events.EventDto;
import com.hyunkee.events.EventStatus;


//@WebMvcTest //슬라이싱 테스트 (mocktest)
@SpringBootTest //통합테스트, 웹 쪽으로 관련된 테스트를 진행하기 가장 좋다.
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@AutoConfigureRestDocs // API 문서 작성
@Import(RestDocsConfiguration.class) // API 문서 포맷팅 처리
public class EventControllerTest {

	@Autowired
	MockMvc mockMvc; //웹서버를 띄우지 않고 디스패처 서블릿을 생성, 속도는 단위테스트보다는 좀 걸림
	
	@Autowired
	ObjectMapper objectMapper;
	
	//@MockBean //슬라이싱 테스트를 사용할 경우 사용
	//EventRepository eventRepository;
	
	//테스트케이스 1 : 입력값들을 전달하면 JSON 응답으로 201이 나오는지 확인
	@Test
	@DisplayName("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		
		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Develop Test")
				.beginEventDateTime(LocalDateTime.of(2020, 8, 21, 5, 18))
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 28, 5, 18))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 1, 23, 59))
				.endEventDateTime(LocalDateTime.of(2020, 9, 2, 23, 00))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("Seoul Sadangdong Mcdonald")
				.build();
		
		//아이디 설정
		//event.setId(10);
		
		//원래는 테스트 코드를 먼저 만들고 나서 실행 코드를 만들어야 TDD다.
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
	
	//테스트케이스 2 : 잘못된 파라미터 입력값 입력시 Bad request 응답확인
	@Test
	@DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
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
	
	//테스트케이스 3 : 입력 값이 비어있는 경우에 에러가 발생하는 테스트
    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
    	
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());
    }
	
    
    //테스트케이스 4 : 입력한 값이 잘못 정의된 값인 경우에 에러가 발생하는 테스트
	@Test
	@DisplayName("입력한 값이 잘못 정의된 값인 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Wrong_Input() throws Exception{
 		
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Develop Test")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 28, 5, 18)) //┐잘못된 값 입력
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 7, 1, 23, 59)) //┘
				.beginEventDateTime(LocalDateTime.of(2020, 8, 29, 5, 18))
				.endEventDateTime(LocalDateTime.of(2020, 9, 1, 20, 00))
				.basePrice(10000) //┐잘못된 값 입력
				.maxPrice(2000)   //┘
				.limitOfEnrollment(100)
				.location("Seoul Sadangdong Mcdonald")
				.build();
		
		this.mockMvc.perform(post("/api/events")
							.contentType(MediaType.APPLICATION_JSON)
							.content(this.objectMapper.writeValueAsString(eventDto)))
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("fieldError[0].objectName").exists())
					.andExpect(jsonPath("fieldError[0].defaultMessage").exists())
					.andExpect(jsonPath("fieldError[0].code").exists())
					.andExpect(jsonPath("globalError[0].objectName").exists())
					.andExpect(jsonPath("globalError[0].defaultMessage").exists())
					.andExpect(jsonPath("globalError[0].code").exists())
					;
		
	}

	//테스트케이스 5 : 비즈니스 로직 테스트 구현
	@Test
	@DisplayName("비즈니스 로직 테스트 구현(통합테스트)")
	public void createEvent_Business_Test() throws Exception {
		
		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Develop Test")
				.beginEventDateTime(LocalDateTime.of(2020, 8, 21, 5, 18))
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 28, 5, 18))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 1, 23, 59))
				.endEventDateTime(LocalDateTime.of(2020, 9, 2, 23, 00))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("Seoul Sadangdong Mcdonald")
				.build();
		
		//아이디 설정
		//event.setId(10);
		
		//원래는 테스트 코드를 먼저 만들고 나서 실행 코드를 만들어야 TDD다.
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
			   .andExpect(jsonPath("free").value(Matchers.is(false)))
			   .andExpect(jsonPath("offline").value(Matchers.is(true)))
			   .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
		
	}
	
	
	//테스트케이스 6 : 스프링 HATEOAS 적용 테스트 + Rest Docs
	@Test
	@DisplayName("스프링 HATEOAS 적용 테스트 + Rest Docs")
	public void createEvent_Hatroas() throws Exception {
		
		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Develop Test")
				.beginEventDateTime(LocalDateTime.of(2020, 8, 21, 5, 18))
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 28, 5, 18))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 1, 23, 59))
				.endEventDateTime(LocalDateTime.of(2020, 9, 2, 23, 00))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("Seoul Sadangdong Mcdonald")
				.build();
		
		//아이디 설정
		//event.setId(10);
		
		//원래는 테스트 코드를 먼저 만들고 나서 실행 코드를 만들어야 TDD다.
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
			   .andExpect(jsonPath("free").value(Matchers.is(false)))
			   .andExpect(jsonPath("offline").value(Matchers.is(true)))
			   .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
			   .andExpect(jsonPath("_links.self").exists())
			   .andExpect(jsonPath("_links.query-events").exists())
			   .andExpect(jsonPath("_links.update-event").exists())
			   .andDo(document("create-event",
					   links(
							   linkWithRel("self").description("link to self"),
							   linkWithRel("query-events").description("link to query events"),
							   linkWithRel("update-event").description("link to update on exist event"),
							   linkWithRel("profile").description("link to profile")
							   ),
					   requestHeaders(
							   headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
							   headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
							   ),
					   requestFields( 
							   fieldWithPath("name").description("name of new Event"),
							   fieldWithPath("description").description("description of new Event"),
							   fieldWithPath("beginEnrollmentDateTime").description("begin Enrollment Date Time of new Event"),
							   fieldWithPath("closeEnrollmentDateTime").description("close Enrollment Date Time of new Event"),
							   fieldWithPath("beginEventDateTime").description("begin Event Date Time of new Event"),
							   fieldWithPath("endEventDateTime").description("end Event Date Time of new Event"),
							   fieldWithPath("location").description("location of new Event"),
							   fieldWithPath("basePrice").description("base Price of new Event"),
							   fieldWithPath("maxPrice").description("maxP rice of new Event"),
							   fieldWithPath("limitOfEnrollment").description("limit Of Enrollment of new Event")
							   ),
					   responseHeaders(  
							   headerWithName(HttpHeaders.LOCATION).description("location Header"),
							   headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
							   ),
					   //responseFields() 모든 응답에 대한 모든 항목에 대해 문서 링크기술이 필요
					   //relaxedResponseFields() 모든 응답에 대한 모든 항목에 대해 문서 링크기술이 할 필요 없음
					   responseFields(
							   fieldWithPath("id").description("identifier of new Event"),
							   fieldWithPath("name").description("name of new Event"),
							   fieldWithPath("description").description("description of new Event"),
							   fieldWithPath("beginEnrollmentDateTime").description("begin Enrollment Date Time of new Event"),
							   fieldWithPath("closeEnrollmentDateTime").description("close Enrollment Date Time of new Event"),
							   fieldWithPath("beginEventDateTime").description("begin Event Date Time of new Event"),
							   fieldWithPath("endEventDateTime").description("end Event Date Time of new Event"),
							   fieldWithPath("location").description("location of new Event"),
							   fieldWithPath("basePrice").description("base Price of new Event"),
							   fieldWithPath("maxPrice").description("maxP rice of new Event"),
							   fieldWithPath("limitOfEnrollment").description("limit Of Enrollment of new Event"),
							   fieldWithPath("free").description("free of new Event"),
							   fieldWithPath("offline").description("offline of new Event"),
							   fieldWithPath("eventStatus").description("event Status of new Event"),
							   fieldWithPath("_links.self.href").description("link to query self"),
							   fieldWithPath("_links.query-events.href").description("link to query events"),
							   fieldWithPath("_links.update-event.href").description("link to update event."),
							   fieldWithPath("_links.profile.href").description("link to profile")
							   )
					   ));
		
	}
	
	
	
	
	
}
