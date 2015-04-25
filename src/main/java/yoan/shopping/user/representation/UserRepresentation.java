/**
 * 
 */
package yoan.shopping.user.representation;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.rest.BasicRepresentation;
import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.util.ApplicationException;
import yoan.shopping.user.User;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

/**
 * User Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "user")
public class UserRepresentation extends BasicRepresentation {
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
		Objects.requireNonNull(user, "Unable to create representation from null User");
		URL selfURL;
		try {
			selfURL = new URL("http://localhost:8080/shopping/user/" + user.getId().toString());
			List<Link> links = Lists.newArrayList(Link.self(selfURL));
			return new UserRepresentation(user.getId(), user.getName(), user.getEmail(), links);
		} catch (MalformedURLException e) {
			LOGGER.error("Problem with self URL", e);
			throw new ApplicationException("Problem with self URL", e);
		}
	}
	
	public static User toUser(UserRepresentation representation) {
		Objects.requireNonNull(representation, "Unable to create User from null UserRepresentation");
		
		User.Builder user = User.Builder.createDefault()
						   .withName(representation.name)
						   .withEmail(representation.email);
		//if no ID provided, we generate one
		if (representation.id == null) {
			user.withRandomId();
		} else {
			user.withId(representation.id);
		}
		return user.build();
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
