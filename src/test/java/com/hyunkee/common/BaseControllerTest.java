package com.hyunkee.common;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 기본 설정 들을 한개의 상속 클래스로 정리
 * @author Hyun Kee Na
 *
 */
@SpringBootTest //통합테스트, 웹 쪽으로 관련된 테스트를 진행하기 가장 좋다.
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@AutoConfigureRestDocs // API 문서 작성
@Import(RestDocsConfiguration.class) // API 문서 포맷팅 처리
@ActiveProfiles("test")//기본 application.properties (OverRiding) application-test.properties (DB소스가 다름)
@Disabled//테스트 케이스를 가지고 있지 않은 클래스로 처리 (junit4 -> @Ignore 에서 변경)
public class BaseControllerTest {

	@Autowired
	protected MockMvc mockMvc; //웹서버를 띄우지 않고 디스패처 서블릿을 생성, 속도는 단위테스트보다는 좀 걸림
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected ModelMapper modelMapper;
	

}
