package com.clone.baemin.member;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor				// 생성자를 자동으로 추가 => 파라미터가 없는 생성자 생성
@AllArgsConstructor				// 생성자를 자동으로 추가 => 클래스에 존재하는 모든 필드에 대한 생성자 자동 생성
@Setter @Getter
@ToString
@Builder	
public class MemberJoinDto {
	
	//이메일 형식 @ . 포함
	@Pattern(regexp="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message="email 형식이 아닙니다")
	@Email(message = "email 형식이 아닙니다")
	@NotNull(message = "email을 입력해 주세요")
	private String email;
	
	// 영어 또는 한글
	@Pattern(regexp="^[a-zA-Z]*$|^[가-힣]*$", message="영어 또는 한글로 입력해주세요")
	@Size(min = 2, max = 5)
	@NotNull(message = "이름을 입력해 주세요")
	private String memberName;
	
	//최소8자리에 숫자,문자,특수문자 각각 1개 포함
	@Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", message = "비밀번호를 다시 입력해 주세요")
	@NotNull(message = "비밀번호를 다시 입력해 주세요")
	private String password;
	
	// 숫자만
	@Pattern(regexp="^[0-9]*$", message="숫자만 입력해 주세요")
	@NotNull(message = "숫자만 입력해 주세요")
	private String memberPhone;

}
