package com.hyunkee.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 입력값 제한 방법 -이만큼의 데이터만 입력가능
 * @author Hyun Kee Na
 *
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventDto {
	
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

}
