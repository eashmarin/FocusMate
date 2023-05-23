package com.api.focusmate;

import com.api.focusmate.controller.UserController;
import com.api.focusmate.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FocusmateApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FocusmateApplication.class, args);
	}
}
