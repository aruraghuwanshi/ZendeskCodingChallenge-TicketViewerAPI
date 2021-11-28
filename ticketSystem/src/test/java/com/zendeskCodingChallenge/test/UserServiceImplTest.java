package com.zendeskCodingChallenge.test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.zendeskCodingChallenge.controller.Controller;
import com.zendeskCodingChallenge.entity.UserDetailsEntity;
import com.zendeskCodingChallenge.service.Common;
import com.zendeskCodingChallenge.service.UserService;
import com.zendeskCodingChallenge.service.UserServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Mock
	Common common;
	
	@Mock
	BufferedReader reader;
	
	@Mock
	JSONObject APIResponse;
	
	@Mock
	Process process;
	
	@InjectMocks
	UserServiceImpl userServiceImpl;

	@Test
	public void initialLoginTest() {
		
		UserDetailsEntity userDetailsEntityTest = new UserDetailsEntity();
		userDetailsEntityTest.setPassword("test");
		userDetailsEntityTest.setUsername("test");
		
		when(common.getUserInput()).thenReturn("test");
		
		UserDetailsEntity userDetailsEntity = userServiceImpl.initialLogin(false);
		
		
		
		assertEquals(userDetailsEntity.getPassword(),userDetailsEntityTest.getPassword());
		assertEquals(userDetailsEntity.getUsername(),userDetailsEntityTest.getUsername());
		
	}
	
	@Test
	public void initialLoginTestException() {
		
		UserDetailsEntity userDetailsEntityTest = new UserDetailsEntity();
		userDetailsEntityTest.setPassword("test");
		userDetailsEntityTest.setUsername("test");
		
		doThrow(InputMismatchException.class)
	      .when(common)
	      .getUserInput();
		
		UserDetailsEntity userDetailsEntity = userServiceImpl.initialLogin(false);
		
	}
	
	
	@Test
	public void displayIndividualTicketTest() throws Exception
	{
	
		Map<String,String> map = new HashMap<>();
		map.put("id","1");
		map.put("subject", "test ticket");
		map.put("requester_id", "test_id");
		map.put("created_at", "test_date");
		
		JSONObject jsonObject = new JSONObject(map);
		
		UserDetailsEntity userDetailsEntityTest = new UserDetailsEntity();
		userDetailsEntityTest.setPassword("abc1234");
		userDetailsEntityTest.setUsername("aruraghuwanshi3@gmail.com");
		
		userServiceImpl.displayIndividualTicket(userDetailsEntityTest, "1");
		
	}
	
	@Test
	public void convertDate() throws Exception
	{
		String testDate = userServiceImpl.convertDate("2021-11-23T07:10:39Z");
		
		assertEquals(testDate,"23 Nov 2021");
		
	}
}
