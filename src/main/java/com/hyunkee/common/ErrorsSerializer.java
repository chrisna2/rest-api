package com.hyunkee.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/**
 * Errors 데이터는 JSON으로 바로 변형이 되지 않기 때문에 해당 Errors 데이터를 JSON으로 변형 시킴
 * 글로벌 에러와 필드에러 분리
 * @author Hyun Kee Na
 *
 */
@JsonComponent // Object Mapper에 등록 => 앞으로 모든 Errors의 데이터는 여기서 JSON으로  변형되어 만들어 지게 됨
public class ErrorsSerializer extends JsonSerializer<Errors>{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		
		//배열로 시작
		//gen.writeStartObject();
		
		//필드 에러 (수정 연습)
		gen.writeStartObject();
		
		
		gen.writeFieldName("fieldError");
		gen.writeStartArray();
		//필드 에러 처리 배열 담기
		errors.getFieldErrors().forEach(e -> {
			try {
				gen.writeStartObject();
				gen.writeStringField("field", e.getField());
				gen.writeStringField("objectName", e.getObjectName());
				gen.writeStringField("code", e.getCode());
				gen.writeStringField("defaultMessage", e.getDefaultMessage());
				Object rejectedValue = e.getRejectedValue();
				if(rejectedValue != null) {
					gen.writeStringField("rejectedValue", rejectedValue.toString());
				}
				gen.writeEndObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		gen.writeEndArray();
		
		//// End로 끝나고 그 뒤로 추가가 되면 중간에 ,가 생긴다. ////
		
		//글로벌 에러
		gen.writeFieldName("globalError");
		gen.writeStartArray();
		//글로벌 에러 처리 배열 담기
		errors.getGlobalErrors().forEach(ge -> {
			try {
				gen.writeStartObject();
				gen.writeStringField("objectName", ge.getObjectName());
				gen.writeStringField("code", ge.getCode());
				gen.writeStringField("defaultMessage", ge.getDefaultMessage());
				gen.writeEndObject();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		gen.writeEndArray();
		
		//오브젝트 끝
		gen.writeEndObject();
		
		//배열 끝 -> 나는 배열 보다 오브젝트 안에 여러개 배열을 나열하는게 더 좋다.
		//gen.writeEndObject();
	}

}
