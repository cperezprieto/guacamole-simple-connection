
import static org.junit.Assert.assertEquals;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.guacamole.net.simpleConnector.JWT;

public class test_guacamole_servlet {

	@Test
	public void testParseJWT() {
		String secret = System.getenv("JWT_SECRET");
		
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
		
		Instant ini = Instant.now().minus(Duration.ofMinutes(10));		
		Instant exp = Instant.now().plus(Duration.ofMinutes(20));
		
		System.out.println("Ini: " + ini.toString() + " - Exp: " + exp.toString());
		
		String hostname = "<host-name>";
		String username = "<username>";
		String password = new String(DatatypeConverter.parseBase64Binary("<base64-user-password>"));
		
		String s = Jwts.builder()
				.setIssuedAt(Date.from(ini))
				.setExpiration(Date.from(exp))
				.claim("hostname", hostname)
				.claim("username", username)
				.claim("password", password)
				.signWith(SignatureAlgorithm.HS256, signingKey)
				.compact();
		
		
		Claims c = JWT.parse(s);
		
		assertEquals(hostname, c.get("hostname").toString());
		assertEquals(username, c.get("username").toString());
		assertEquals(password, c.get("password").toString());
	}
}
