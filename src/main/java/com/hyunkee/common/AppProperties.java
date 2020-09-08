package com.hyunkee.common;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "nhk-app")
public class AppProperties {
	
	@NotEmpty
	private String adminUsername;
	@NotEmpty
	private String adminPassword;
	@NotEmpty
	private String userUsername;
	@NotEmpty
	private String userPassword;
	@NotEmpty
	private String clientId;
	@NotEmpty
	private String clientSecret;
}
