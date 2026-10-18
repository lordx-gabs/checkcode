package com.edu.checkcode.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI checkcodeOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Checkcode API")
						.description("API for user authentication and management")
						.version("1.0.0")
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0")))
				.servers(List.of(
						new Server().url("http://localhost:8080").description("Local Server"),
						new Server().url("https://checkcode-acok.onrender.com").description("Production Server")
				));
	}
}
