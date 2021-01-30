package com.ecnucrowdsourcing.croudsourcingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class CroudsourcingbackendApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(CroudsourcingbackendApplication.class);
		application.addListeners(new ApplicationPidFileWriter());
		application.run(args);
	}

}
