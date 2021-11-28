package com.zendeskCodingChallenge.service;

import java.util.Scanner;

import com.zendeskCodingChallenge.entity.UserDetailsEntity;

public interface UserService {
	public UserDetailsEntity initialLogin(boolean repeatFlag);
	public void displayIndividualTicket(UserDetailsEntity userDetailsEntity, String ticketId);
	public void displayTicketCollection(UserDetailsEntity userDetailsEntity);
	public boolean checkAuthentication(UserDetailsEntity userDetailsEntity);
	public void displayViewOptions();
}
