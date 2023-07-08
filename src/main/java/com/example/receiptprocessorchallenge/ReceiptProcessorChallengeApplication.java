package com.example.receiptprocessorchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;

@SpringBootApplication
public class ReceiptProcessorChallengeApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(ReceiptProcessorChallengeApplication.class, args);
		String serverPort = configurableApplicationContext.getEnvironment().getProperty("server.port");
		WebApplicationContext webApplicationContext = (WebApplicationContext) configurableApplicationContext;
		webApplicationContext.getServletContext().log("ReceiptProcessorChallenge API Application ready at port: "+serverPort);
	}

}
