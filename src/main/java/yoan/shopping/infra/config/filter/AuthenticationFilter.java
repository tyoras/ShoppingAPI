/**
 * 
 */
package yoan.shopping.infra.config.filter;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;
import static yoan.shopping.infra.logging.Markers.AUTHENTICATION;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.*;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Filter which authenticate the currently connected user
 * @author yoan
 */
@Singleton
public class AuthenticationFilter implements Filter {
	
	public static final String USER_ID_HEADER = "USER-ID";
	private final UserRepository userRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	@Inject
	public AuthenticationFilter(UserRepository userRepo) {
		userRepository = requireNonNull(userRepo);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			UUID connectedUserId = extractUserIdFromHeaders(httpRequest);
			User connectedUser = findConnectedUser(connectedUserId);
			//we add the authenticated user infos to the request
			httpRequest.setAttribute(Key.get(User.class, Names.named(CONNECTED_USER)).toString(), connectedUser);
		} catch(WebApiException wae) {
			httpResponse.sendError(wae.getStatus().getStatusCode(), wae.getMessage());
			httpRequest.setAttribute(Key.get(User.class, Names.named(CONNECTED_USER)).toString(), User.DEFAULT);
		} finally {
			chain.doFilter(httpRequest, httpResponse);
		}
	}
	
	private UUID extractUserIdFromHeaders(HttpServletRequest httpRequest) {
		String userId = null;
        if (httpRequest.getHeaderNames() != null) {
        	userId = httpRequest.getHeader(USER_ID_HEADER);
        }
        if (StringUtils.isBlank(userId)) {
        	String message = MISSING.getHumanReadableMessage("header " + USER_ID_HEADER);
			LOGGER.debug(AUTHENTICATION.getMarker(), message);
			throw new WebApiException(UNAUTHORIZED, ERROR, API_RESPONSE, message);
        }
        return getUserId(userId);
	}
	
	public static UUID getUserId(String userId) {
		UUID id;
		try {
			id = UUID.fromString(userId);
		} catch(IllegalArgumentException e) {
			String message = INVALID.getHumanReadableMessage("header " + USER_ID_HEADER + " : " + userId);
			LOGGER.debug(AUTHENTICATION.getMarker(), message);
			throw new WebApiException(UNAUTHORIZED, ERROR, API_RESPONSE, message, e);
		}
		return id;
	}
	
	private User findConnectedUser(UUID connectedUserID) {
		if (connectedUserID.equals(User.DEFAULT_ID)) {
			return User.DEFAULT;
		}
		
		User connectedUser = userRepository.getById(connectedUserID);
		if (connectedUser == null) {
        	String message = "Trying to connect with unknown user : " + connectedUserID;
			LOGGER.debug(AUTHENTICATION.getMarker(), message);
			throw new WebApiException(UNAUTHORIZED, ERROR, API_RESPONSE, message);
        }
		return connectedUser;
	}

	@Override
	public void destroy() { }

}
