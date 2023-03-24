package com.example.testproject;

import com.example.testproject.models.ParserServer;
import com.example.testproject.services.ServerCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class TestProjectApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext configurableApplicationContext =
				SpringApplication.run(TestProjectApplication.class, args);
		ServerCommand serverCommand = configurableApplicationContext.getBean(ServerCommand.class);
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String input = scanner.nextLine();
			Map<String, String> res = ParserServer.parseInput(input);
			if (res == null) {
				System.out.println("Incorrect data");
			} else {
				if (res.size() == 0) {
					break;
				}
				if (res.get("save") != null) {
					serverCommand.save(res.get("save"));
				}
				if (res.get("load") != null) {
					serverCommand.load(res.get("load"));
				}
			}
		}
		scanner.close();
		System.out.println("Outside!");
		configurableApplicationContext.close();
	}

}
