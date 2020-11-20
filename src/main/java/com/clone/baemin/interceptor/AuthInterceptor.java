package com.clone.baemin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.clone.baemin.jwt.JwtComponent;
import com.clone.baemin.jwt.JwtComponent.TOKEN_TYPE;
import com.clone.baemin.member.MemberService;
import com.clone.baemin.util.TokenUtil;


// Spring 최신버전  HandlerInterceptorAdapter => HandlerInterceptor 변경해서 사용
@Component
public class AuthInterceptor implements HandlerInterceptor{
	private Logger LOG = LoggerFactory.getLogger(AuthInterceptor.class);
	
	@Autowired
	private JwtComponent jwtComponent;
	
	@Autowired
	private MemberService memberService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
			
		System.out.println("INTERCEPTOR()()()()()()()()()()()()()()()()()()");

			// 공통으로 쓰이는 부분
			String token = TokenUtil.getTokenFromRequest(request);
			// @Get/{email} 때 accessToken를 같이 보내면
			
			// 1. 로그인한 사용자 이메일
			String exData = jwtComponent.extractUsername(token, TOKEN_TYPE.ACCESS_TOKEN);
			System.out.println(exData); // 사용자가 입력한 이메일 출력
			// 2. 로그인한 사용자의 accessToken만료 여부 -> 1에서 에러를 보내주기 때문에 쓸필요없음
			//Boolean isExpired = jwtComponent.isTokenExpired(token, TOKEN_TYPE.ACCESS_TOKEN);
			//System.out.println("토큰의 만료여부 : " + isExpired);
			
			// 리턴값이 false일 경우 controller 이후 진행안됨
			return true;
		}

	
}
	
	
	
	

