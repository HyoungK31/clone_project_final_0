package com.clone.baemin.restaurant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.clone.baemin.member.Member;
import com.clone.baemin.member.Member.MemberBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter @Getter
@NoArgsConstructor				// 생성자를 자동으로 추가 => 파라미터가 없는 생성자 생성
@AllArgsConstructor				// 생성자를 자동으로 추가 => 클래스에 존재하는 모든 필드에 대한 생성자 자동 생성
@ToString
@Builder						// 생성자 대신에 빌더를 사용하는 경우 자동으로 해당 클래스에 빌더를 추가
@Entity(name ="test00") 	// 테이블명
@Table(name="test00")
public class Restaurant {
	
	@Id @GeneratedValue
	private Long Id;
	
	// 상호명
	private String name;
	// 주소
	private String address;
	// 도로명
	private String roadAddress;
	// 위도
	private String latitude;
	// 경도
	private String longitude;
	// 전화번호
	private String phone;
	// 메인사진
	private String pictureUrl;
	// 평점
	private Double rating;
	// 음식
	private String foodType;

//	@Transient
//	private double distance;
}
