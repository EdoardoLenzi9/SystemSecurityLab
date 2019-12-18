package com.gieseckedevrient.applets.myfirst;

/*
 * User data container class 
 */
public class UserDataManager {
   	public static byte[] firstName;
   	public static byte[] lastName;
   	public static DateTime dateOfbirth;
   	
   	public UserDataManager(){
   		initUserData();
   	}
   	
   	private void initUserData(){
	   	// empty user data
	   	firstName = new byte[(short) 30];
	   	lastName = new byte[(short) 30];
	   	dateOfbirth = new DateTime((short)(15), (short)(12), (short)(2000));
   	}
}
