package io.tyoras.shopping.user.representation;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestRepresentation;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.user.ProfileVisibility;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * User Rest Representation
 *
 * @author yoan
 */
@XmlRootElement(name = "user")
@ApiModel(value = "User")
public class UserRepresentation extends RestRepresentation {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepresentation.class);
    /**
     * User unique ID
     */
    private UUID id;
    /**
     * User last name
     */
    private String name;
    /**
     * User email
     */
    private String email;
    /**
     * Profile visibility level
     */
    private String profileVisibility;
    /**
     * User creation date
     */
    private LocalDateTime creationDate;
    /**
     * Last time the user was updated
     */
    private LocalDateTime lastUpdate;

    public UserRepresentation() {
        super();
    }

    /**
     * Test Purpose only
     */
    @Deprecated
    public UserRepresentation(UUID id, String name, String email, String profileVisibility, List<Link> links) {
        super(links);
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileVisibility = profileVisibility;
    }

    public UserRepresentation(User user, UriInfo uriInfo) {
        super();
        requireNonNull(user);
        requireNonNull(uriInfo);
        URI selfURI = uriInfo.getBaseUriBuilder().path(UserResource.class).path(UserResource.class, "getById").build(user.getId());
        this.links.add(Link.self(selfURI));
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileVisibility = user.getProfileVisibility().name();
        this.creationDate = user.getCreationDate();
        this.lastUpdate = user.getLastUpdate();
    }

    public static User toUser(UserRepresentation representation) {
        requireNonNull(representation, "Unable to create User from null UserRepresentation");

        User.Builder userBuilder = User.Builder.createDefault()
                .withName(representation.name)
                .withEmail(representation.email)
                .withProfileVisibility(ProfileVisibility.valueOfOrNull(representation.profileVisibility));
        //if no ID provided, we let the default one
        if (representation.id != null) {
            userBuilder.withId(representation.id);
        }

        User user;
        try {
            user = userBuilder.build();
        } catch (NullPointerException | IllegalArgumentException e) {
            String message = INVALID.getDevReadableMessage("user") + " : " + e.getMessage();
            LOGGER.error(message, e);
            throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
        }
        return user;
    }

    @XmlElement(name = "id")
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "profileVisibility")
    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    @XmlElement(name = "creationDate")
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @XmlElement(name = "lastUpdate")
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, profileVisibility, creationDate, lastUpdate);
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
                && Objects.equals(this.email, that.email)
                && Objects.equals(this.profileVisibility, that.profileVisibility)
                && Objects.equals(this.creationDate, that.creationDate)
                && Objects.equals(this.lastUpdate, that.lastUpdate);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id)
                .add("name", name)
                .add("email", email)
                .add("profileVisibility", profileVisibility)
                .add("created", creationDate)
                .add("lastUpdate", lastUpdate)
                .toString();
    }
}
