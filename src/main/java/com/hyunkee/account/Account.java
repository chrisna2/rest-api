package com.hyunkee.account;

import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 스프링 시큐리티 - 계정 (Account)
 * 웹시큐리티(필터 기반 시큐리티)
 * 	-> 서블릿 기반
 *  -> webflux 기반
 * 메서드시큐리티
 * Security Interceptor를 통해 기능이 작동
 * 	-> method SecurityInterceptor
 * 	-> Filter SecurityInterceptor
 * 
 * java TreadLocal
 * -> 한 쓰레드에서 공유 하는 자원
 * -> 쓰레드안에서 데이터 저장하고 다른 메서드에서 공유
 * 
 * Security Interceptor -> 로그인 매니저 -> '인증' 확인 , 비밀번호 인코딩 정보 확인 등등 -> 로그인 완료
 * 						-> SecurityContextHolder에 계정 인증 정보 저장
 * 
 * Security Interceptor -> 권한 확인 매니저 -> '인가' 확인, 사용자의 role을 확인 -> 인가 완료
 * 
 * 의존성 추가 <oath2>
 * -> 앞으로 오는 모든 접속 설정이 인증 및 인가를 필요로 하게 된다.
 * -> 기존에 테스트가 모두 깨지게 됨
 * -> 따라서 스프링 시큐리티에 대한 설정이 필요
 * @author Hyun Kee Na
 *
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	
	@Id @GeneratedValue
	private Integer id;
	
	private String email;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<AccountRole> roles;
}
