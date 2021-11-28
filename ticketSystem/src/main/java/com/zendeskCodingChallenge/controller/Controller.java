package com.zendeskCodingChallenge.controller;

import java.util.Scanner;

import com.zendeskCodingChallenge.entity.UserDetailsEntity;
import com.zendeskCodingChallenge.service.Common;
import com.zendeskCodingChallenge.service.UserService;
import com.zendeskCodingChallenge.service.UserServiceImpl;

public class Controller {
	static UserDetailsEntity userDetailsEntity;
	static UserService userService = new UserServiceImpl();
	static Common common = new Common();
	
	public static void applicationStart(boolean repeatFlag)
	{
		userDetailsEntity = userService.initialLogin(repeatFlag);
		viewOptions();
	}
	
	public static void viewOptions()
	{ 
		
		String input = "";
		while (!input.equals("quit")) {
			userService.displayViewOptions();
			input = common.getUserInput();

			switch (input.trim()) {
			case "1":
				userService.displayTicketCollection(userDetailsEntity);
				break;
			case "2":
				System.out.println("Enter ticker number:");
				String ticketId = common.getUserInput();
				userService.displayIndividualTicket(userDetailsEntity, ticketId);
				break;
			case "quit":
				System.out.println("Program now shutting down");
				System.exit(0);
				break;
			default:
				System.out.println("please enter a valid choice");
				break;
			}
		}
	}
}
