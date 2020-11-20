package com.clone.baemin.util;

import javax.servlet.http.HttpServletRequest;

public class TokenUtil {
	
	public static String getTokenFromRequest(HttpServletRequest request) {
		
		String headerToken = request.getHeader("Authorization");
		System.out.println("request header : " + headerToken);
		
		/* 
	    	request의 header에는 아래처럼 담겨있고
			header 'Authorization: Bearer eyJiJ.eyJAzfQ.zZgA'
	
	   		headerToken에는 아래처럼 담겨있음
	   		Bearer eyJiJ.eyJAzfQ.zZgA
	
		 */
		
		//if(headerToken == null || headerToken.trim().length() == 0) {
		if(headerToken == null) {
			throw new NullPointerException();
		}
		
				
		//String token = headerToken.split(" ")[1];	// 인증값 있고, Bearer 헤더가 있다
		
		
		String[] tokenArr = headerToken.split(" ");
		if( tokenArr.length != 2) {
			throw new IllegalArgumentException("Bearer 토큰데이터 확인");
		}
		
		String token = tokenArr[1];
		System.out.println("순수한 토큰"+token);
		
		return token;
		
		
		
	}
	

}
