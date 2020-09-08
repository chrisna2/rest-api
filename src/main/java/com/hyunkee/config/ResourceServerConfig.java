package com.hyunkee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

/**
 * 리소스 서버 설정
 * @author Hyun Kee Na
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// resources -> 리소스 접근 권한
		resources.resourceId("event");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// http -> 중요한 설정 처리
		http.anonymous()
				.and()
			.authorizeRequests()
				.mvcMatchers(HttpMethod.GET, "/api/**")
					//.anonymous()//이렇게 처리 하면 로그인 하지 않은 사람들만 사용이 가능하다.
					.permitAll()//이렇게 처리를 해야 모든 사용자가 사용 가능하다.
				.anyRequest()
					.authenticated()
				.and()
			.exceptionHandling()
				.accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}

}
