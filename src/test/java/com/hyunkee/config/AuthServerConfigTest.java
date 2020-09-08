package com.hyunkee.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyunkee.account.Account;
import com.hyunkee.account.AccountRole;
import com.hyunkee.account.AccountService;
import com.hyunkee.common.BaseControllerTest;

class AuthServerConfigTest extends BaseControllerTest{

	@Autowired
	AccountService accountService;

	@Test
	@DisplayName("인증 토큰을 발급 받는 테스트")
	public void getAuthToken() throws Exception{
		
		
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
		
		this.mockMvc.perform(post("/oauth/token")
							.with(httpBasic(clientId, clientSecret))
							.param("username", username)
							.param("password", password)
							.param("grant_type", "password"))
		            .andDo(print())
		            .andExpect(status().isOk())
		            .andExpect(jsonPath("access_token").exists());
	}

}


