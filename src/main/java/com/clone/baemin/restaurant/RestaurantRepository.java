package com.clone.baemin.restaurant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>{
	
	@Query(value = "select * , "
					+ " (6371 * acos(cos(radians(:lat))*cos(radians(latitude))*cos(radians(longitude)-radians(:lot))+sin(radians(:lat))*sin(radians(latitude)))) as distance "
					+ " from test01.test00 "
					+ " where food_type=:foodType "
					+ " having distance<=0.3 "
					+ " order by distance limit 0,300", nativeQuery = true)
//	@Query(value = "select r , "
//			+ " (6371 * acos(cos(radians(:lat))*cos(radians(r.latitude))*cos(radians(r.longitude)-radians(:lot))"
//			+ " 							+ sin(radians(:lat))*sin(radians(r.latitude)))) as distance "
//			+ " from Restaurant r"
//			+ " where r.foodType=:foodType "
//			+ " having distance<=0.3 "
//			+ " order by distance limit 0,300")
	List<Restaurant> FoodTypeList( @Param("lat") String lat, @Param("lot") String lot, @Param("foodType") String foodType);
	
	List<Restaurant> findByFoodType(String foodType);
	
	
	
}
