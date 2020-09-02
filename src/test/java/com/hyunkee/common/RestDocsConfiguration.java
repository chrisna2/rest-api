package com.hyunkee.common;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RestDocsConfiguration {

	//문서 포맷팅 처리 
	@Bean
	public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcBuilderCustomizer(){
		
		/*
		//익명클래스 변환 방식
		return new RestDocsMockMvcConfigurationCustomizer() {
			
			@Override
			public void customize(MockMvcRestDocumentationConfigurer configurer) {
				
				configurer.operationPreprocessors()
						.withRequestDefaults(prettyPrint())
						.withResponseDefaults(prettyPrint());
				
			}
		};*/
		
		//람다 표현식
		return configurer -> configurer.operationPreprocessors()
									   .withRequestDefaults(prettyPrint()) //요청값 본문 -> 기본 값 포맷팅 처리
									   .withResponseDefaults(prettyPrint());//응답값 본문 
		
		//람다 표현식, stream 쓰는 방법 
		
		
	}
}
