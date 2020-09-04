package com.hyunkee.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import com.hyunkee.index.IndexController;

public class ErrorsResource extends EntityModel<Errors>{

	public ErrorsResource(Errors content, Link... links) {
		super(content, links);
		add(linkTo(methodOn(IndexController.class).index()).withRel("index")); // index 링크 생성
	}

}
