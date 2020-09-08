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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import org.aspectj.lang.annotation.Before;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import com.hyunkee.account.Account;
import com.hyunkee.account.AccountReopository;
import com.hyunkee.account.AccountRole;
import com.hyunkee.account.AccountService;
import com.hyunkee.common.BaseControllerTest;
import com.hyunkee.events.Event;
import com.hyunkee.events.EventDto;
import com.hyunkee.events.EventRepository;
import com.hyunkee.events.EventStatus;

public class EventControllerTest extends BaseControllerTest{

	//@MockBean //슬라이싱 테스트를 사용할 경우 사용
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountReopository accountReopository;
	
	@BeforeEach// junit4 @Before 대응 -> 테스트 케이스가 실행하면서 이전의 인메모리 db 데이터 삭제
	public void init() {
		this.eventRepository.deleteAll();
		this.accountReopository.deleteAll();
	}
	
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
							//인증키 받기
							.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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

	/**
	 * 인증키 받기 
	 * @return
	 * @throws Exception
	 */
	private String getBearerToken() throws Exception {
		return "Bearer "+getOauth2Token();
	}
	
	/**
	 * 인증키 받기
	 * @return
	 * @throws Exception
	 */
	private String getOauth2Token() throws Exception{
		
		//서버 기동시 걔정 접속 테스트 걔정
		String username = "test@gmail.com";
		String password = "test";
				
		Account test = Account.builder()
							.email(username)
							.password(password)
							.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
							.build();
		
		this.accountService.saveAccount(test);
		
		//인증 서버 아이디 및 키
		String clientId = "nhkApp";
		String clientSecret = "pass";
		
		ResultActions perform = this.mockMvc.perform(post("/oauth/token")
													.with(httpBasic(clientId, clientSecret))
													.param("username", username)
													.param("password", password)
													.param("grant_type", "password"));
		
		var responseBody = perform.andReturn()
								.getResponse()
								.getContentAsString();

		Jackson2JsonParser parser = new Jackson2JsonParser();
		
		return parser.parseMap(responseBody).get("access_token").toString();
		
		
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
							//인증키 받기
							.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
								//인증키 받기
								.header(HttpHeaders.AUTHORIZATION, getBearerToken())        		
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
							//인증키 받기
							.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
							.contentType(MediaType.APPLICATION_JSON)
							.content(this.objectMapper.writeValueAsString(eventDto)))
					.andDo(print())
					.andExpect(status().isBadRequest())
					//테스트 6을 거치며 수정 
					.andExpect(jsonPath("content.fieldError[0].objectName").exists())
					.andExpect(jsonPath("content.fieldError[0].defaultMessage").exists())
					.andExpect(jsonPath("content.fieldError[0].code").exists())
					.andExpect(jsonPath("content.globalError[0].objectName").exists())
					.andExpect(jsonPath("content.globalError[0].defaultMessage").exists())
					.andExpect(jsonPath("content.globalError[0].code").exists())
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
							//인증키 받기
							.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
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
							//인증키 받기
							.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
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
							   fieldWithPath("manager").description("manager of new Event"),
							   fieldWithPath("_links.self.href").description("link to query self"),
							   fieldWithPath("_links.query-events.href").description("link to query events"),
							   fieldWithPath("_links.update-event.href").description("link to update event."),
							   fieldWithPath("_links.profile.href").description("link to profile")
							   )
					   ));
		
	}
	
    //테스트케이스 7 : 입력한 값이 잘못 정의된 값인 경우에 에러가 발생하는 테스트
	@Test
	@DisplayName("입력한 값이 잘못 정의된 값인 경우에 에러가 발생하는 인덱스 테스트")
	public void createEvent_Bad_Request_Wrong_Input_Index() throws Exception{
 		
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
							//인증키 받기
							.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
							.contentType(MediaType.APPLICATION_JSON)
							.content(this.objectMapper.writeValueAsString(eventDto)))
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("content.fieldError[0].objectName").exists())
					.andExpect(jsonPath("content.fieldError[0].defaultMessage").exists())
					.andExpect(jsonPath("content.fieldError[0].code").exists())
					.andExpect(jsonPath("content.globalError[0].objectName").exists())
					.andExpect(jsonPath("content.globalError[0].defaultMessage").exists())
					.andExpect(jsonPath("content.globalError[0].code").exists())
					.andExpect(jsonPath("_links.index").exists())
					;
		
	}
	
	
	@Test
	@DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvent() throws Exception{
		//테스트 이벤트 생성 람다식
		IntStream.range(0, 30).forEach(i -> {
			this.generateEvent(i);
		});
		
		//이벤트 조회 - 페이징 - 정렬
		this.mockMvc.perform(get("/api/events")
								.param("page", "1")
								.param("size", "10")
								.param("sort", "name,DESC"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("page").exists())
					.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
					;
	}
	
	
	@Test
	@DisplayName("기존의 이벤트를 하나 조회하기.")
	public void getEvent() throws Exception{
		Event event = this.generateEvent(100);
		
		this.mockMvc.perform(get("/api/events/{id}", event.getId()))
					.andExpect(status().isOk())
					.andExpect(jsonPath("name").exists())
					.andExpect(jsonPath("id").exists())
					.andExpect(jsonPath("_links.self").exists())
					.andExpect(jsonPath("_links.profile").exists())
					.andDo(document("get-an-event"))
				;	
	}
	
	//테스트 이벤트 생성 메서드
	private Event generateEvent(int i) {
		Event event = Event.builder()
						.name("Spring_Test_"+i)
						.description("REST API Develop Test_" + i)
						.beginEventDateTime(LocalDateTime.of(2020, 8, 21, 5, 18))
						.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 28, 5, 18))
						.closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 1, 23, 59))
						.endEventDateTime(LocalDateTime.of(2020, 9, 2, 23, 00))
						.basePrice(100)
						.maxPrice(200)
						.limitOfEnrollment(100)
						.location("Seoul Sadangdong Mcdonald")
						.free(false)
						.offline(true)
						.eventStatus(EventStatus.DRAFT)
						.build();
		
		return this.eventRepository.save(event);
	}
	
    @Test
    @DisplayName("없는 이벤트는 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound());
    }


	@Test
	@DisplayName("업데이트 이벤트를 정상적으로 수정하기")
	public void updateEvent() throws Exception{
		//Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String eventName = "Updated Event";
		eventDto.setName(eventName);
		
		//when & then
		this.mockMvc.perform(put("/api/events/{id}",event.getId())
								//인증키 받기
								.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").value(eventName))
				.andExpect(jsonPath("_links.self").exists());
		
	}
	
	@Test
	@DisplayName("입력값이 비어있는 업데이트 이벤트를 정상적으로 수정하기")
	public void updateEvent400Empty() throws Exception{
		//Given
		Event event = this.generateEvent(200);
		EventDto eventDto = new EventDto();
		
		//when & then
		this.mockMvc.perform(put("/api/events/{id}",event.getId())
								//인증키 받기
								.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	
	@Test
	@DisplayName("입력값이 잘못된 업데이트 이벤트를 정상적으로 수정하기")
	public void updateEvent400Wrong() throws Exception{
		//Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);
		
		//when & then
		this.mockMvc.perform(put("/api/events/{id}",event.getId())
								//인증키 받기
								.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	
	@Test
	@DisplayName("존재 하지 않는 업데이트 이벤트를 정상적으로 수정하기")
	public void updateEvent404() throws Exception{
		//Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);
		
		//when & then
		this.mockMvc.perform(put("/api/events/12312")
								//인증키 받기
								.header(HttpHeaders.AUTHORIZATION, getBearerToken())     
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
	
	
}
