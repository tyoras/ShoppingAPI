package yoan.shopping.infra.util.helper;

import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.crypto.hash.Sha256Hash;

import yoan.shopping.infra.config.guice.ShiroSecurityModule;

/**
 * Utility methods related to security
 * @author yoan
 */
public final class SecurityHelper {
	
	private SecurityHelper() { }
	
	/**
	 * Basic way to get a salted hash
	 * @param password
	 * @param salt
	 * @return salted hash base 64 encoded
	 */
	public static String hash(String password, Object salt) {
		return new Sha256Hash(password, salt, ShiroSecurityModule.NB_HASH_ITERATION).toBase64();
	}
	
	/**
	 * Generate a JSON Web Token for a given user
	 * @param userId
	 * @return JWT as String
	 */
	public static String generateJWT(UUID userId) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + TimeUnit.SECONDS.toMillis(3600));
		return Jwts.builder()
			.setSubject(userId.toString())
			.setIssuedAt(now)
			.setExpiration(expiration)
			.compact();
	}
}
