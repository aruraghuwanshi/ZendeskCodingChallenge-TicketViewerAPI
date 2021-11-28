package com.zendeskCodingChallenge.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zendeskCodingChallenge.controller.Controller;
import com.zendeskCodingChallenge.entity.UserDetailsEntity;

public class UserServiceImpl implements UserService {
	
	Common common = new Common();
	
	final int PAGELIMIT = 25;
	public UserDetailsEntity initialLogin(boolean repeatFlag) {

		UserDetailsEntity userDetailsEntity = new UserDetailsEntity();

		if (!repeatFlag) {
			System.out.println("\nWelcome to the ticket viewer");
			System.out.println("Please enter your username and password to view further menu options:\n");
		}

		try {
			System.out.println("Username:");
			String input = common.getUserInput();
			userDetailsEntity.setUsername(input);
			System.out.println("\nPassword:");
			input = common.getUserInput();
			userDetailsEntity.setPassword(input);

		} catch (InputMismatchException inputMismatchException) {
			System.out.println("Your input is not the correct type!");
			inputMismatchException.printStackTrace();
		}
 
		return userDetailsEntity;
	}  

	public void displayIndividualTicket(UserDetailsEntity userDetailsEntity, String ticketId) {

		String[] command = ("curl https://zcczendeskcodingchallenge2056.zendesk.com/api/v2/tickets/" + ticketId
				+ ".json -v -u " + userDetailsEntity.getUsername() + ":" + userDetailsEntity.getPassword()
				+ " -X GET -H \"Content-Type: application/json\"").split(" ");
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process;

		try {
			process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			JSONObject APIResponse = new JSONObject(result);

			if (!validateEndpoint(APIResponse)) {
				System.exit(0);
			}

			JSONObject ticketDetails = (JSONObject) APIResponse.get("ticket");

			List<String> resultStringList = new ArrayList<String>();
			StringBuilder sbLine = new StringBuilder();

			sbLine.append("Ticket with ID: "+ticketDetails.get("id")+" ");
			sbLine.append("and subject '" + String.valueOf(ticketDetails.get("subject")) + "' ");
			sbLine.append("opened by " + String.valueOf(ticketDetails.get("requester_id") + " "));
			sbLine.append("on " + convertDate(String.valueOf(ticketDetails.get("created_at"))));
			resultStringList.add(sbLine.toString());

			for (String resultString : resultStringList) {
				System.out.println(resultString);
			}

		} catch (IOException e) {
			System.out.print("Error occured while trying to read the input");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.print("An unknown error has occured.");
			e.printStackTrace();
		}
	}

	public void displayTicketCollection(UserDetailsEntity userDetailsEntity) {
		JSONArray ticketsArray = new JSONArray();
		JSONArray ticketsArrayCurrent = new JSONArray();
		int pageCounter = 1;

		do {
			try {
				String[] newCommand = ("curl https://zcczendeskcodingchallenge2056.zendesk.com/api/v2/tickets.json?page="
						+ pageCounter++ + " -v -u " + userDetailsEntity.getUsername() + ":"
						+ userDetailsEntity.getPassword() + " -X GET -H \"Content-Type: application/json\"").split(" ");

				ProcessBuilder process = new ProcessBuilder(newCommand);
				Process p;

				p = process.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append(System.getProperty("line.separator"));
				}
				String result = builder.toString();
				JSONObject APIResponse = new JSONObject(result);

				if (!validateEndpoint(APIResponse)) {
					System.exit(0);
				}

				ticketsArrayCurrent = (JSONArray) APIResponse.get("tickets");
				ticketsArray.put(ticketsArrayCurrent);

			} catch (IOException e) {
				System.out.print("Error occured while trying to read the input");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.print("An unknown error has occured.");
				e.printStackTrace();
			}
		} while (ticketsArrayCurrent.length() == 100);

		if (((JSONArray) ticketsArray.get(0)).length() <= 25) {
			List<String> resultStringList = new ArrayList<String>();
			for (Object obj : ticketsArray) {
				JSONArray jsonPageArray = (JSONArray) obj;
				for (Object ticketObject : jsonPageArray) {
					StringBuilder sbLine = new StringBuilder();
					JSONObject singleTicketJSON = (JSONObject) ticketObject;
					sbLine.append("Ticket with ID: "+singleTicketJSON.get("id")+" ");
					sbLine.append("and subject '" + String.valueOf(singleTicketJSON.get("subject")) + "' ");
					sbLine.append("opened by " + String.valueOf(singleTicketJSON.get("requester_id") + " "));
					sbLine.append("on " + convertDate(String.valueOf(singleTicketJSON.get("created_at"))));
					resultStringList.add(sbLine.toString());
				}
			}

			for (String res : resultStringList) {
				System.out.println(res);
			}
		} else {
			System.out.println("pagination to be implemented");
			paginateTickets(ticketsArray);
		}
	}
	
	private void paginateTickets(JSONArray ticketsArray)
	{
		int ticketIndex = 0;
		int pageIndex = 0;
		int APIPageIndex = 0;
		int totalTicketCount = ((ticketsArray.length() - 1) * 100) + ((JSONArray)ticketsArray.get(ticketsArray.length()-1)).length();
		
		while(ticketIndex < totalTicketCount)
		{
			int rangeStart = pageIndex * PAGELIMIT;
			int rangeEnd = (pageIndex+1) * PAGELIMIT;
			if(rangeEnd > 100)
			{
				rangeStart = rangeStart%100;
				rangeEnd = rangeEnd%100;
			}
			
			JSONArray jsonPageArray = null;
			
			if(APIPageIndex < ticketsArray.length())
				jsonPageArray = (JSONArray) ticketsArray.get(APIPageIndex);
			else
			{
				System.out.println("No next page exists");
				break;
			}
			
			if(rangeEnd%100 == 0)
			{
				APIPageIndex++;
			}
			
			List<String> resultStringList = new ArrayList<>();
			
			if(jsonPageArray.length() < rangeStart)
			{
				System.out.println("No next page exists");
				break;
			}
			
			for(ticketIndex = rangeStart; ticketIndex < Math.min(jsonPageArray.length(),rangeEnd); ticketIndex++)
			{
				StringBuilder sbLine = new StringBuilder();
				JSONObject singleTicketJSON = (JSONObject) jsonPageArray.get(ticketIndex);
				sbLine.append("Ticket with ID: "+singleTicketJSON.get("id")+" ");
				sbLine.append("and subject '" + String.valueOf(singleTicketJSON.get("subject")) + "' ");
				sbLine.append("opened by " + String.valueOf(singleTicketJSON.get("requester_id") + " "));
				sbLine.append("on " + convertDate(String.valueOf(singleTicketJSON.get("created_at"))));
				resultStringList.add(sbLine.toString());
			}
			
			for(String ticketString: resultStringList)
				System.out.println(ticketString);
			
			Scanner scanner = new Scanner(System.in);
			
			System.out.println("\nEnter 'next' for displaying the next 25 entries");
			System.out.println("Enter 'previous' for displaying the previous 25 entries");
			System.out.println("Enter 'back' for displaying the previous menu");
			
			String input = scanner.nextLine();
			boolean whileBreakFlag = false;
			
			switch(input.trim())
			{
			case("next"):
				pageIndex++;
				break;
			case("previous"):
				if(pageIndex == 0)
				{
					System.out.println("No pages exist prior to the current page.");
					whileBreakFlag = true;
				}
				else
					pageIndex--;
				break;
			case("back"):
				whileBreakFlag = true;
			break;
			
			default:
				System.out.println("Incorrect choice");
				whileBreakFlag = true;
			}
			
			if(whileBreakFlag)
				break;
			
		}
		Controller.viewOptions();
		
	}

	public boolean checkAuthentication(UserDetailsEntity userDetailsEntity) {
		String[] command = ("curl https://zcczendeskcodingchallenge2056.zendesk.com/api/v2/tickets.json -v -u "
				+ userDetailsEntity.getUsername() + ":" + userDetailsEntity.getPassword()
				+ " -X GET -H \"Content-Type: application/json\"").split(" ");
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process;

		try {
			process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			JSONObject APIResponse = new JSONObject(result);

			if (APIResponse.has("error")) {
				System.out
						.println("Unable to authenticate user. Please check your username and password and try again");
				return false;
			}
		} catch (IOException e) {
			System.out.print("Error occured while trying to read input");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.print("An unknown error has occured");
			e.printStackTrace();
		}

		return true;
	}

	public void displayViewOptions() {
		System.out.println("\n\t Select view options:" + "\n\t* Press 1 to view all tickets"
				+ "\n\t* Press 2 to view a ticket" + "\n\t* Type 'quit' to exit");
	}

	public String convertDate(String dateTime) {

		try {

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
			LocalDate date = LocalDate.parse(dateTime, inputFormatter);
			String formattedDate = outputFormatter.format(date);

			SimpleDateFormat dateFormatFinal = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date dateFinal = sdf.parse(formattedDate);
			String convertedDateTime = dateFormatFinal.format(dateFinal);

			return convertedDateTime;

		} catch (ParseException e) {
			System.out.print("Error occured while parsing the date and time");
			e.printStackTrace();
			System.exit(0);
		}

		return null;
	}

	public boolean validateEndpoint(JSONObject APIResponse) {
		if (APIResponse.has("error")) {
			if (APIResponse.get("error").equals("InvalidEndpoint")) {
				System.out.println("Endpoint unavailable");
			} else {
				System.out.println(APIResponse.get("error"));
			}
			return false; 
		}

		return true;
	}

}
