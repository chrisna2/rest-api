package com.hyunkee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.hyunkee.account.AccountService;
import com.hyunkee.common.AppProperties;

/**
 * OAuth2 인증 서버  
 * oauth 토큰을 가지고 인증을 처리 할때 토큰으 발급과 인증 처리 절차를 수행하는 서버
 * @author Hyun Kee Na
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AccountService accountService;
	
	@Autowired
	TokenStore tokenStore;
	
	@Autowired
	AppProperties appProperties;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		// security -> passwordEncoder 설정 , 클라이언트의 Secret을 확인하기 위해 사용
		security.passwordEncoder(passwordEncoder);
	}
	
	
	/**
	 * 여기서 메모리에 클라이언트 아이디와 시클릿을 설정한다. 이걸 설정하지 않고 테스트로 바로 넘어 간서 에러가 발생했다.
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// clients
		clients.inMemory()
			.withClient(appProperties.getClientId())
			.authorizedGrantTypes("password", "refresh_token")
			.scopes("read", "write")//읽기 쓰기 권한 부여
			.secret(this.passwordEncoder.encode(appProperties.getClientSecret()))
			.accessTokenValiditySeconds(10*60)
			.refreshTokenValiditySeconds(6*10*60);
		
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//endpoints
		endpoints.authenticationManager(authenticationManager)
				.userDetailsService(accountService)
				.tokenStore(tokenStore);
	}
	
	
	
	
}
