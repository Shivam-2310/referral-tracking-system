package com.shivam.referral_tracking_system;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Referral Tracking API", version = "1.0", description = "API documentation for the Referral Tracking System"))
public class ReferralTrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReferralTrackingSystemApplication.class, args);
	}

}
