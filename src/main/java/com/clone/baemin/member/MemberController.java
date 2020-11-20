package com.clone.baemin.member;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clone.baemin.exception.base.CommonException;
import com.clone.baemin.jwt.JwtComponent;
import com.clone.baemin.jwt.JwtComponent.TOKEN_TYPE;
import com.clone.baemin.jwt.domain.Jwt;
import com.clone.baemin.util.TokenUtil;

@RestController
@RequestMapping(value="/baemin/member")
public class MemberController {
	
	@Autowired
	private MemberService memberSerivce;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private JwtComponent jwtComponent;
	
	@PostMapping("/join")
	public ResponseEntity save(@Valid @RequestBody MemberJoinDto join) {
		Member newMember = modelMapper.map(join, Member.class);
		newMember = this.memberSerivce.save(newMember);
		return ResponseEntity.created(URI.create("/join")).body(newMember);
	}
	
	@GetMapping("{email}")
	public ResponseEntity findByEmail(@PathVariable("email") String email) {
		System.out.println("CONTROLLER=================================");
		Member member = this.memberSerivce.findByEmail(email);
		return ResponseEntity.ok().body(member);
		
	}
	
	@PostMapping("/login")
	public ResponseEntity member(@Valid @RequestBody MemberLoginDto loginDto) throws CommonException {
		Member member = modelMapper.map(loginDto, Member.class);
		TokenDto ckMember = this.memberSerivce.memberLogin(member);
		return ResponseEntity.ok().body(ckMember);
	}
	
	@PostMapping("/get_accesstoken")
	public ResponseEntity get_accesstoken(HttpServletRequest request) throws CommonException {
		// 
		System.out.println(request);
		// 순수한 토큰값 추출
		String token = TokenUtil.getTokenFromRequest(request);
		System.out.println(token);
		// refreshToken 만료 확인
		Boolean isExpired = this.jwtComponent.isTokenExpired(token, TOKEN_TYPE.REFRESH_TOKEN);
		// refreshToken 만료 아님
		if(isExpired != true) {
			String exData = this.jwtComponent.extractUsername(token, TOKEN_TYPE.REFRESH_TOKEN);
			Jwt newJwt = this.jwtComponent.makeReJwt(exData);
			return ResponseEntity.ok().body(newJwt);
		}
		// refreshToken 만료
		return ResponseEntity.badRequest().body("다시 로그인해 주세요");
	}

}
