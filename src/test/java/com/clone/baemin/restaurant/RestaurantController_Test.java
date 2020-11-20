package com.clone.baemin.restaurant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc	// mockMvc 사용 하려면 꼭 추가
@TestMethodOrder(OrderAnnotation.class)	// 순서 정할때 사용
public class RestaurantController_Test {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	// ====================== 카테고리 목록 Test ===============================
	// 1. 성공 => 한식, 중식, 양식, 분식
	@ParameterizedTest
	@ValueSource(strings = {"한식", "분식", "양식", "중식"})
	public void 조회_성공_테스트(String foodType) throws Exception {
				this.mockMvc.perform(
				get("/baemin/restaurant/foodlist")
				.contentType(MediaType.APPLICATION_JSON)
				.param("lat", "37.511649")
				.param("lot", "127.021453")
				.param("foodType", foodType)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}
	// 2. 실패 => 잘못된 keyword
	@ParameterizedTest
	@ValueSource(strings = {"야식", "배달"})
	public void 조회_실패_테스트(String foodType) throws Exception {
		this.mockMvc.perform(
		get("/baemin/restaurant/foodlist")
		.contentType(MediaType.APPLICATION_JSON)
		.param("lat", "37.511649")
		.param("lot", "127.021453")
		.param("foodType", foodType)
	)
	.andDo(print())
	.andExpect(status().isBadRequest());
}
}
