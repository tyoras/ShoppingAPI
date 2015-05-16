/**
 * 
 */
package yoan.shopping.user.representation;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static yoan.shopping.infra.util.error.CommonErrorMessage.PROBLEM_WITH_URL;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.user.User;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

/**
 * User Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "user")
public class UserRepresentation extends RestRepresentation {
	/** User unique ID */
	private UUID id;
	/** User last name */
	private String name;
	/** User email */
	private String email;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRepresentation.class);
	
	public UserRepresentation() {
		super();
	}
	
	public UserRepresentation(UUID id, String name, String email, List<Link> links) {
		super(links);
		this.id = id;
		checkArgument(StringUtils.isNotBlank(name));
		this.name = name;
		checkArgument(StringUtils.isNotBlank(email));
		this.email = email;
	}
	
	public static UserRepresentation fromUser(User user) {
		requireNonNull(user, "Unable to create representation from null User");
		URL selfURL;
		try {
			selfURL = new URL("http://localhost:8080/shopping/user/" + user.getId().toString());
			List<Link> links = Lists.newArrayList(Link.self(selfURL.toString()));
			return new UserRepresentation(user.getId(), user.getName(), user.getEmail(), links);
		} catch (MalformedURLException e) {
			String message = PROBLEM_WITH_URL.getHumanReadableMessage("self");
			LOGGER.error(message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
	}
	
	public static User toUser(UserRepresentation representation) {
		requireNonNull(representation, "Unable to create User from null UserRepresentation");
		
		User.Builder userBuilder = User.Builder.createDefault()
						   .withName(representation.name)
						   .withEmail(representation.email);
		//if no ID provided, we let the default one
		if (representation.id != null) {
			userBuilder.withId(representation.id);
		}
		
		User user;
		try {
			user = userBuilder.build();
		} catch (IllegalArgumentException e) {
			String message = INVALID.getHumanReadableMessage("user") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(Status.BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return user;
	}

	@XmlElement(name = "id")
	public UUID getId() {
		return id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "email")
	public String getEmail() {
		return email;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, email);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserRepresentation that = (UserRepresentation) obj;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.email, that.email);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id)
											   .add("name", name)
											   .add("email", email)
											   .toString();
	}
}
