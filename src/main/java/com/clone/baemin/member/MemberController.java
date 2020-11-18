package com.clone.baemin.member;

import java.net.URI;

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

@RestController
@RequestMapping(value="/baemin/member")
public class MemberController {
	
	@Autowired
	private MemberService memberSerivce;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping("/join")
	public ResponseEntity save(@Valid @RequestBody MemberJoinDto join) {
		Member newMember = modelMapper.map(join, Member.class);
		newMember = this.memberSerivce.save(newMember);
		return ResponseEntity.created(URI.create("/join")).body(newMember);
	}
	
	@GetMapping("{email}")
	public ResponseEntity findByEmail(@PathVariable("email") String email) {
		
		Member member = this.memberSerivce.findByEmail(email);
		return ResponseEntity.ok().body(member);
		
	}
	
	@PostMapping("/login")
	public ResponseEntity member(@Valid @RequestBody MemberLoginDto loginDto) throws CommonException {
		Member member = modelMapper.map(loginDto, Member.class);
		TokenDto ckMember = this.memberSerivce.memberLogin(member);
		return ResponseEntity.ok().body(ckMember);
	}
	

}
