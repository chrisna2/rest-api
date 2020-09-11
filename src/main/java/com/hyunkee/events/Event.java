package com.hyunkee.events;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hyunkee.account.Account;
import com.hyunkee.account.AccountSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

	@Id @GeneratedValue
	private Integer id;
	private String name;
	private String description;
	private LocalDateTime beginEnrollmentDateTime;
	private LocalDateTime closeEnrollmentDateTime;
	private LocalDateTime beginEventDateTime;
	private LocalDateTime endEventDateTime;
	private String location; // (optional) 이게 없으면 온라인 모임 
	private int basePrice; // (optional) 
	private int maxPrice; // (optional) 
	private int limitOfEnrollment;
	private boolean offline;
	private boolean free;
	@Enumerated(EnumType.STRING)
	private EventStatus eventStatus = EventStatus.DRAFT;
	//private EventStatus eventStatus = EventStatus.DRAFT;
	
	public void update() {
		//무료 여부 파악
		if(this.basePrice == 0 && this.maxPrice == 0) {
			this.free = true;
		}
		else {
			this.free = false;
		}
		
		//온-오프라인 여부 파악
		//java 11 => String.isBlank 추가되었다. 자바 11부터라는데... 자바 8바라보는 우리 입장에서... 
		if(this.location == null || this.location.isBlank() ||  this.location.isEmpty()) {
			this.offline = false;
		}
		else {
			this.offline = true;
		}
	}
	
	//Event 와 Account 단방향 연결 JPA
	@ManyToOne
	@JsonSerialize(using = AccountSerializer.class)
	private Account manager;
	
	
	
	
}
