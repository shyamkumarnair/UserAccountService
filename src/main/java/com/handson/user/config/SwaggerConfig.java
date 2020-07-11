package com.handson.user.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.VendorExtensionsMapper;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.handson.user")).paths(PathSelectors.any())
				.build()
				.apiInfo(createMetaInfo());

	}
	
	private ApiInfo createMetaInfo()
	{
		return new ApiInfo("User Account Management Service", "This is a spring boot service developed for managing user accounts", "1.0", "Terms of Service", new Contact("Contact", "ContactMeUrl", "contactme@email.com"), "License", "https://www.mylicence.html", Arrays.asList());
	}
	
}
