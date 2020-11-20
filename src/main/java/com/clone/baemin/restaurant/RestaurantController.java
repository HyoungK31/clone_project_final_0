package com.clone.baemin.restaurant;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/baemin/restaurant")
public class RestaurantController {
	
	@Autowired
	private RestaurantService restauarntService;
	
	@GetMapping("/foodlist")
	public  ResponseEntity foodTypeList(@RequestParam(value="lat") String lat, @RequestParam(value="lot") String lot, @RequestParam(value="foodType") String foodType) {
		
		System.out.println("===============================================================================================");
		System.out.println(lat);
		System.out.println(lot);
		System.out.println(foodType);
		
		List<Restaurant> restaurantList  = this.restauarntService.foodTypeList(lat, lot, foodType);
		return ResponseEntity.ok().body(restaurantList);
	}
	
	@GetMapping("{foodType}")
	public ResponseEntity findByFoodType(@PathVariable("foodType") String foodType) {
		List<Restaurant> foodList = this.restauarntService.findByFoodType(foodType);
		return ResponseEntity.ok().body(foodList);
	}

}
