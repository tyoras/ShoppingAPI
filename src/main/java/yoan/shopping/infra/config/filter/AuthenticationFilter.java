/**
 * 
 */
package yoan.shopping.infra.config.filter;

import static yoan.shopping.infra.config.guice.ShoppingWebModule.CONNECTED_USER;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import yoan.shopping.user.User;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Filter which authenticate the currently connected user
 * @author yoan
 */
@Singleton
public class AuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		//we add the authenticated user infos to the request
		httpRequest.setAttribute(Key.get(User.class, Names.named(CONNECTED_USER)).toString(), User.DEFAULT);
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() { }

}
