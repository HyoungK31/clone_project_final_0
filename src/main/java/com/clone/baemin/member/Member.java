package com.clone.baemin.member;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor				// 생성자를 자동으로 추가 => 파라미터가 없는 생성자 생성
@AllArgsConstructor				// 생성자를 자동으로 추가 => 클래스에 존재하는 모든 필드에 대한 생성자 자동 생성
@ToString
@Builder						// 생성자 대신에 빌더를 사용하는 경우 자동으로 해당 클래스에 빌더를 추가
@Entity(name ="baemin") 		// 테이블명
@Table(name="baemin")
public class Member {
	
	@Id @GeneratedValue			// PK, 자동생성
	private Long id;
	
	private String email;
	private String memberName;
	private String password;
	private String memberPhone;

}
