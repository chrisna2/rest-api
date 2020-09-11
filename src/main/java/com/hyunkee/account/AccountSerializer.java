package com.hyunkee.account;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

//민감한 정보는 보여 주지 않도록 처리하는 클래스!
public class AccountSerializer extends JsonSerializer<Account>{

	@Override
	public void serialize(Account value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		// TODO Auto-generated method stub
		gen.writeStartObject();
		gen.writeNumberField("id", value.getId());
		gen.writeEndObject();
		
	}

}
