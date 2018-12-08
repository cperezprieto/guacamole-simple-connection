package org.apache.guacamole.net.simpleConnector;

import java.time.Instant;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;


public final class JWT {
	public static Claims parse(String jwt) {
		String secret = System.getenv("JWT_SECRET");
		
		logger.info("Secret: " + secret);
				
		Claims claims = null;

		claims = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(secret))
				.parseClaimsJws(jwt).getBody();
		
		if(claims.getExpiration().before(Date.from(Instant.now())))
				throw new JwtException("Expired Token");
		
		return claims;
	}
	
	private static Logger logger = Logger.getLogger(JWT.class);
}