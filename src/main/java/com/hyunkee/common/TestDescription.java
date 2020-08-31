package com.hyunkee.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)//이 어노테이션을 붙인 코드를 어디까지 유지 할 건가? 소스까지, 컴파일 이후에는 사용안함
public @interface TestDescription {
//이런 식으로 어노테이션을 직접 만들어서 사용가능하다.
//주석 처럼 사용하려고 했던 모양이다. junit4 이전에 이야기 오늘날에는 @DisplayName으로 대체
	String value();
}
