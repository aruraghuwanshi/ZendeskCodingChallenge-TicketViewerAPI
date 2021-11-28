package com.zendeskCodingChallenge.service;

import java.util.Scanner;

public class Common {
	Scanner scanner;
	
	public String getUserInput() {
		if(scanner == null)
			scanner = new Scanner(System.in);
		
		String input = scanner.nextLine();
		return input;
	}
}
