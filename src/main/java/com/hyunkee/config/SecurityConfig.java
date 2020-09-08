package com.hyunkee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.hyunkee.account.AccountService;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	AccountService accountService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(accountService)
			.passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		//API 문서 경로 재외
		web.ignoring().mvcMatchers("/docs/index.html");
		//화면 공통 영역 (정적 자산) 제외
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
		
	}
	
	/*
	@Override //http 서버를 통해 접근 감지
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.mvcMatchers("/docs/index.html").anonymous() //도드
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}*/
	
	@Override //인증 서버 설정
	public void configure(HttpSecurity http) throws Exception {
		http.anonymous()
			.and()
			.formLogin()
			.and()
			.authorizeRequests()
			//get으로 받는 모든 요청에 대해 모든 접속을 허용한다.
			//.mvcMatchers(HttpMethod.GET, "/api/**").anonymous()
			.mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
			.anyRequest().authenticated();
	}
	
	
	
	
}
