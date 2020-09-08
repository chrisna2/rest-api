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
import com.hyunkee.common.AppProperties;
import com.hyunkee.common.BaseControllerTest;

class AuthServerConfigTest extends BaseControllerTest{

	@Autowired
	AccountService accountService;

	@Autowired
	AppProperties appProperties;
	
	@Test
	@DisplayName("인증 토큰을 발급 받는 테스트")
	public void getAuthToken() throws Exception{
		
		/* (이미 AppConfig에서 생성함)
		Account test = Account.builder()
							.email(appProperties.getUserUsername())
							.password(appProperties.getUserPassword())
							.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
							.build();
		this.accountService.saveAccount(test);
		*/
		
		this.mockMvc.perform(post("/oauth/token")
							.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
							.param("username", appProperties.getUserUsername())
							.param("password", appProperties.getUserPassword())
							.param("grant_type", "password"))
		            .andDo(print())
		            .andExpect(status().isOk())
		            .andExpect(jsonPath("access_token").exists());
	}

}


