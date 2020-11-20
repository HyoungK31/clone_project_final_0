package com.clone.baemin.restaurant;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {

	@Autowired
	private RestaurantRepository restaurantRepository;

	public List<Restaurant> foodTypeList(String lat, String lot, String foodType) {
		
		List<Restaurant> foodTypeChk = this.findByFoodType(foodType);
		System.out.println("=============="+foodTypeChk);
		if(foodTypeChk.isEmpty()) {
			throw new IllegalArgumentException("잘못 선택 하셨습니다.");
		}else {
			List<Restaurant> restaurantList = this.restaurantRepository.FoodTypeList(lat, lot, foodType);
			return restaurantList;
		}
	}
	
	public List<Restaurant> findByFoodType(String foodType){
		return this.restaurantRepository.findByFoodType(foodType);
	}


}
