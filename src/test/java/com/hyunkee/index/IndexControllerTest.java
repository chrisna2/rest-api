package com.hyunkee.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.hyunkee.common.RestDocsConfiguration;

//@WebMvcTest //슬라이싱 테스트 (mocktest)
@SpringBootTest //통합테스트, 웹 쪽으로 관련된 테스트를 진행하기 가장 좋다.
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@AutoConfigureRestDocs // API 문서 작성
@Import(RestDocsConfiguration.class) // API 문서 포맷팅 처리
@ActiveProfiles("test")//기본 application.properties (OverRiding) application-test.properties (DB소스가 다름)
public class IndexControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Test
	@DisplayName("인덱스 생성 확인 테스트")
	public void index() throws Exception {
		
		this.mockMvc.perform(get("/api/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("_links.events").exists())
				.andDo(print())
				//jsonPath("_links.events").exists()
				;
	}
	
	
	
	
}
