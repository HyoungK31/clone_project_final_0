package com.clone.baemin.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.clone.baemin.exception.base.CommonException;
import com.clone.baemin.jwt.domain.Jwt;
import com.clone.baemin.jwt.enums.exception.EnumSecurityException;
import com.clone.baemin.member.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtComponent {

	private final String ACCESS_SECRET_KEY;
	private final String REFRESH_SECRET_KEY;
	private final long ACCESS_EXPIRE_MINUTES;
	private final long REFRESH_EXPIRE_MINUTES;

	/**
	 * 토큰 초기값 세팅
	 * 
	 * @param aCCESS_SECRET_KEY
	 * @param REFRESH_SECRET_KEY
	 * @param aCCESS_EXPIRE_MINUTES
	 * @param rEFRESH_EXPIRE_MINUTES
	 */
	public JwtComponent(@Value("${jwt.access.token.secure.key}") String aCCESS_SECRET_KEY,
			@Value("${jwt.refresh.token.secure.key}") String REFRESH_SECRET_KEY,
			@Value("${jwt.access.token.expire.time}") long aCCESS_EXPIRE_MINUTES,
			@Value("${jwt.refresh.token.expire.time}") long rEFRESH_EXPIRE_MINUTES) {

		super();
		this.ACCESS_SECRET_KEY = aCCESS_SECRET_KEY;
		this.REFRESH_SECRET_KEY = REFRESH_SECRET_KEY;
		this.ACCESS_EXPIRE_MINUTES = aCCESS_EXPIRE_MINUTES;
		this.REFRESH_EXPIRE_MINUTES = rEFRESH_EXPIRE_MINUTES;
	}

	// 토큰타입
	public enum TOKEN_TYPE {
		ACCESS_TOKEN, REFRESH_TOKEN
	}

	// 토큰 타입 데이터
	private class TokenTypeData {

		private final String key;
		private final long time;

		public TokenTypeData(String key, long time) {
			super();
			this.key = key;
			this.time = time;
		}

		public String getKey() {
			return key;
		}

		public long getTime() {
			return time;
		}
	}

	/**
	 * 토큰 만료시간 추출
	 * 
	 * @param token
	 * @param tokenType
	 * @return
	 * @throws CommonException
	 */
	private Date extractExpiration(String token, TOKEN_TYPE tokenType) throws CommonException {

		return this.extractClaim(token, Claims::getExpiration, tokenType);
	}

	/**
	 * 토큰 파싱 : 토큰을 분해했을때 각각의 부분이 조건에 맞는지 검사하고 안맞을 경우 에러를 발생시킨다
	 * 
	 * @param token
	 * @return
	 * @throws CommonException
	 */
	public Claims extractAllClaims(String token, TokenTypeData ttd) throws CommonException {

		Claims body = null;
		try {

			body = Jwts.parser().setSigningKey(ttd.getKey()).parseClaimsJws(token).getBody();

		} catch (ExpiredJwtException e) {

			throw new CommonException(e, EnumSecurityException.ExpiredJwtException);	// 시간이 만료되었을때 에러
		} catch (UnsupportedJwtException e) {

			throw new CommonException(e, EnumSecurityException.UnsupportedJwtException);
		} catch (MalformedJwtException e) {

			throw new CommonException(e, EnumSecurityException.MalformedJwtException);
		} catch (IllegalArgumentException e) {

			throw new CommonException(e, EnumSecurityException.IllegalArgumentException);
		}

		return body;
	}

	/**
	 * 토큰 만료 확인 : 토큰의 날짜가 현재 날짜(연월일시분초)보다 이전인지 확인
	 * 
	 * @param token
	 * @return
	 * @throws CommonException
	 */
	public Boolean isTokenExpired(String token, TOKEN_TYPE tokenType) throws CommonException {

		Date date = this.extractExpiration(token, tokenType);
		return date.before(new Date());
	}

	/**
	 * 위에 있는 private class TokenTypeData() 메소드에 값을 입력할때 사용함
	 * 
	 * @param tokenType
	 * @return
	 */
	private TokenTypeData makeTokenTypeData(TOKEN_TYPE tokenType) {

		String key = tokenType == TOKEN_TYPE.ACCESS_TOKEN ? this.ACCESS_SECRET_KEY : this.REFRESH_SECRET_KEY;
		long time = tokenType == TOKEN_TYPE.ACCESS_TOKEN ? this.ACCESS_EXPIRE_MINUTES : this.REFRESH_EXPIRE_MINUTES;
		return new TokenTypeData(key, time);
	}

	/**
	 * 토큰생성2 : 로그인할때 사용자의 email을 받아서 Jwt를 생성 (AccessToken과 RefreshToken을 모두 생성함)
	 * 
	 * @param email
	 * @return
	 */
	private Jwt _makeJwt( String email) {
		
		Map<String, Object> claims = new HashMap<String, Object>();
		
		String accessToken = this.createToken(claims, email, TOKEN_TYPE.ACCESS_TOKEN);
		String refreshToken = this.createToken(claims, email, TOKEN_TYPE.REFRESH_TOKEN);
		
		Jwt jwt = Jwt.builder()
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
		
		return jwt;
	}
	
	/**
	 * 토큰 생성1 : JWT라이브러리에서 가져온 jwt token builder -> 토큰 생성2에서 사용됨
	 * 
	 * @param claims
	 * @param subject
	 * @return
	 */
	private String createToken(Map<String, Object> claims, String subject, TOKEN_TYPE tokenType) {

		TokenTypeData ttd = this.makeTokenTypeData(tokenType);

		LocalDateTime d = LocalDateTime.now().plusMinutes(ttd.getTime());

		return Jwts.builder()
					.setClaims(claims)	// Claims 설정
					.setSubject(subject)	// 토큰용도
					.setExpiration(Date.from(d.atZone(ZoneId.systemDefault()).toInstant())) // 만료시간설정
					.setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())) 	// 토큰발급시간
					.signWith(SignatureAlgorithm.HS256, ttd.getKey()) // HS256과 Key로 Sign
					.compact(); // 토큰생성
	}

	/**
	 * 토큰에서 사용자 이름 추출
	 * 
	 * @param token
	 * @return
	 * @throws CommonException
	 */
	public String extractUsername(String token, TOKEN_TYPE tokenType) throws CommonException {
		
		String subject = this.extractClaim(token, Claims::getSubject, tokenType);
		return subject;
	}

	/**
	 * 
	 * @param <T>
	 * @param token
	 * @param claimsResolver
	 * @param tokenType
	 * @return
	 * @throws CommonException
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TOKEN_TYPE tokenType)
			throws CommonException {

		TokenTypeData ttd = this.makeTokenTypeData(tokenType);

		final Claims claims = extractAllClaims(token, ttd);	// 토큰 파싱과 연결됨

		return claimsResolver.apply(claims);
	}

	
	/**
	 * 로그인할때 사용자가 입력한 이메일을 가져와서 토큰 생성2로 전달해서 토큰을 생성함
	 * 다른 메소드가 private로 설정되어있어서 사용자 화면에서 이 메소드를 불러서 토큰을 만들어야 함
	 * 
	 * @param email
	 * @return
	 * @throws CommonException
	 */
	public Jwt makeJwt(String email) throws CommonException {

		return _makeJwt(email);
	}

	
	/**
	 * 토큰 재발생 : 사용자가 입력한 이메일을 가져와서 토큰 생성2로 전달
	 * 사용자 화면에서 이 메소드를 불러서 토큰을 만들어야 함
	 * 
	 * @param email
	 * @return
	 * @throws CommonException
	 */
	public Jwt makeReJwt(String email) throws CommonException {
		// TODO [질문] makeJwt와 중복코드?

		return _makeJwt(email);
	}

	
	/**
	 * 토큰 검증
	 * 
	 * @param token
	 * @param user
	 * @param tokenType
	 * @return true or false
	 * @throws CommonException
	 */
	public Boolean validateToken(String token, Member member, TOKEN_TYPE tokenType) throws CommonException {
		// 토큰이 만료되지 않았어도 공격으로 인해 중간에 토큰 내부의 자료가 변경되었을수 있다
		// TODO [질문] 사용여부? 
		
		final String username = this.extractUsername(token, tokenType);
		return (username.equals(member.getEmail()) && !isTokenExpired(token, tokenType));
	}
	
}
