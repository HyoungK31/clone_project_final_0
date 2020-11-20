package com.clone.baemin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.clone.baemin.interceptor.AuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Autowired
	private AuthInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// Interceptor 사용조건 등록
		registry.addInterceptor(authInterceptor)
		//.addPathPatterns("/**/*")
		.excludePathPatterns("/baemin/member/join"
							, "/baemin/member/login"
							, "/baemin/member/get_accesstoken"
							, "/baemin/restaurant/foodlist"
							, "/baemin/restaurant/{foodType}"
							);
		
	}

}
