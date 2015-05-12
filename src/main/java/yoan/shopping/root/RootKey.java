/**
 * 
 */
package yoan.shopping.root;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.user.resource.UserResource;

/**
 * Enumeration of all the root keys and associated resource
 * @author yoan
 */
public enum RootKey {
	USER("user", UserResource.class);
	
	private final String key;
	private final Class<?> resourceClass;
	
	private RootKey(String key, Class<?> resourceClass) {
		checkArgument(StringUtils.isNotBlank(key), "A root key should not be empty");
		this.key = key;
		this.resourceClass = requireNonNull(resourceClass);
	}
	
	public String getKey() {
		return key;
	}

	public Class<?> getResourceClass() {
		return resourceClass;
	}
	
	public Link getlink(UriInfo uriInfo) {
		URI apiURI = uriInfo.getBaseUriBuilder().path(resourceClass).build();
		return new Link(key, apiURI);
	}
}
