package yoan.shopping.infra.util.helper;

import static yoan.shopping.authentication.repository.OAuth2AccessTokenRepository.ACCESS_TOKEN_TTL_IN_MINUTES;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.crypto.hash.Sha256Hash;

import io.jsonwebtoken.Jwts;

/**
 * Utility methods related to security
 * @author yoan
 */
public final class SecurityHelper {
	
	public static final int NB_HASH_ITERATION = 2;
	
	private SecurityHelper() { }
	
	/**
	 * Basic way to get a salted hash
	 * @param password
	 * @param salt
	 * @return salted hash base 64 encoded
	 */
	public static String hash(String password, Object salt) {
		return new Sha256Hash(password, salt, NB_HASH_ITERATION).toBase64();
	}
	
	/**
	 * Generate a JSON Web Token for a given user
	 * @param userId
	 * @return JWT as String
	 */
	public static String generateJWT(UUID userId) {
		Date now = new Date();
		long expiresInSeconds = ACCESS_TOKEN_TTL_IN_MINUTES * 60;
		Date expiration = new Date(now.getTime() + TimeUnit.SECONDS.toMillis(expiresInSeconds));
		return Jwts.builder()
			.setSubject(userId.toString())
			.setIssuedAt(now)
			.setExpiration(expiration)
			.compact();
	}
}
