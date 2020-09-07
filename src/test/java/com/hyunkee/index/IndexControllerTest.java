package com.hyunkee.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyunkee.common.BaseControllerTest;

public class IndexControllerTest  extends BaseControllerTest{

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
