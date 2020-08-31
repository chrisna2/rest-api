package com.hyunkee.events;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


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
	
	//값에 대한 검증 수행	
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	@NotNull
	private LocalDateTime beginEnrollmentDateTime;
	@NotNull
	private LocalDateTime closeEnrollmentDateTime;
	@NotNull
	private LocalDateTime beginEventDateTime;
	@NotNull
	private LocalDateTime endEventDateTime;
	private String location; // (optional) 이게 없으면 온라인 모임 
	@Min(0)
	private int basePrice; // (optional) 
	@Min(0)
	private int maxPrice; // (optional) 
	@Min(0)
	private int limitOfEnrollment;

}
