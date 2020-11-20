package com.clone.baemin.member;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clone.baemin.exception.base.CommonException;
import com.clone.baemin.jwt.JwtComponent;
import com.clone.baemin.jwt.domain.Jwt;

@Service
public class MemberService {
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private JwtComponent jwtComponent;
	
	
	/**
	 * 회원가입 용
	 * @param member
	 * @return
	 */
	public Member save(Member member) {
		
		Member memberIdDoubleChk = this.findByEmail(member.getEmail());
		if(memberIdDoubleChk == null) {
			
			Member newMember = this.memberRepository.save(member);
			return newMember;
		}else {
			throw new IllegalArgumentException("중복된 이메일 입니다.");
		}
		
	}
	
	/**
	 * 아이디 확인용(로그인,회원가입 중복체크)
	 * @param email
	 * @return
	 */
	public Member findByEmail(String email) {
		return this.memberRepository.findByEmail(email);
	}
	
	/**
	 * 로그인용
	 * @param memberLogin
	 * @return
	 * @throws CommonException
	 */
	public TokenDto memberLogin(Member memberLogin) throws CommonException {
		TokenDto newMember = null;
		Member member = this.findByEmail(memberLogin.getEmail());
		//System.out.println(member);
		if(member != null) {
			if(member.getPassword().equals(memberLogin.getPassword())) {
				
				Jwt jwt = jwtComponent.makeJwt(memberLogin.getEmail());
				
				newMember = new ModelMapper().map(member, TokenDto.class);
				
				newMember.setTokens(jwt);
				System.out.println(newMember);
				return newMember;
			}else {
				throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
			}
		}else {
			throw new IllegalArgumentException("해당 이메일이 없습니다.");
		}
	}
	
	
	
	
}
