package yoan.shopping.user.representation;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import io.swagger.annotations.ApiModel;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.user.User;

/**
 * User Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "user")
@ApiModel(value = "User write", subTypes = {SecuredUserWriteRepresentation.class})
public class UserWriteRepresentation {
	/** User last name */
	private String name;
	/** User email */
	private String email;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserWriteRepresentation.class);
	
	public UserWriteRepresentation() { }
	
	/** Test Purpose only */
	@Deprecated 
	public UserWriteRepresentation(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	public UserWriteRepresentation(User user) {
		super();
		requireNonNull(user);
		this.name = user.getName();
		this.email = user.getEmail();
	}
	
	public static User toUser(UserWriteRepresentation representation, UUID userId) {
		requireNonNull(representation, "Unable to create User from null UserWriteRepresentation");
		
		User user;
		try {
			user = User.Builder.createDefault()
					   .withId(userId)
					   .withName(representation.name)
					   .withEmail(representation.email)
					   .build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("user") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return user;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "email")
	public String getEmail() {
		return email;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash( name, email);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserWriteRepresentation that = (UserWriteRepresentation) obj;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.email, that.email);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", name)
											   .add("email", email)
											   .toString();
	}
}
