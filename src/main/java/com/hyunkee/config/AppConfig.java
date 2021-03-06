package com.hyunkee.config;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hyunkee.account.Account;
import com.hyunkee.account.AccountReopository;
import com.hyunkee.account.AccountRole;
import com.hyunkee.account.AccountService;
import com.hyunkee.common.AppProperties;

@Configuration
public class AppConfig {
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	//페스워드 인코더 추가
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	
	@Bean
	public ApplicationRunner applicationRunner() {
		
		return new ApplicationRunner() {
			
			@Autowired
			AccountService accountService;
			
			@Autowired
			AppProperties appProperties;
			
			@Override
			public void run(ApplicationArguments args) throws Exception {
				
				//서버 기동시 걔정 접속 테스트 걔정 (관리자)
				Account admin = Account.builder()
									.email(appProperties.getAdminUsername())
									.password(appProperties.getAdminPassword())
									.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
									.build();
				accountService.saveAccount(admin);
				
				//서버 기동시 걔정 접속 테스트 걔정 (사용자)
				Account user = Account.builder()
									.email(appProperties.getUserUsername())
									.password(appProperties.getUserPassword())
									.roles(Set.of(AccountRole.USER))
									.build();
				accountService.saveAccount(user);
			}
		};
	}
	
	
}
