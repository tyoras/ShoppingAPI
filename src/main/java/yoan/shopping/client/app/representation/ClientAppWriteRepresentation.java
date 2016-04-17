package yoan.shopping.client.app.representation;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import io.swagger.annotations.ApiModel;
import yoan.shopping.client.app.ClientApp;
import yoan.shopping.infra.rest.error.WebApiException;

/**
 * Client application Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "clientApp")
@ApiModel(value = "Client app write")
public class ClientAppWriteRepresentation {
	/** Client app last name */
	private String name;
	/** Oauth2 redirect URI */
	private String redirectURI;
	/** Client app owner ID */
	private UUID ownerId;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientAppWriteRepresentation.class);
	
	public ClientAppWriteRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public ClientAppWriteRepresentation(String name, UUID ownerId, String redirectURI) {
		this.name = name;
		this.ownerId = ownerId;
		this.redirectURI = redirectURI;
	}
	
	public ClientAppWriteRepresentation(ClientApp clientApp) {
		requireNonNull(clientApp);
		this.name = clientApp.getName();
		this.ownerId = clientApp.getOwnerId();
		this.redirectURI = clientApp.getRedirectURI().toString();
	}
	
	public static ClientApp toClientApp(ClientAppWriteRepresentation representation, UUID appId) {
		requireNonNull(representation, "Unable to create client application from null ClientAppWriteRepresentation");
		
		ClientApp app;
		try {
			app =  ClientApp.Builder.createDefault()
								   .withId(appId)
								   .withName(representation.name)
								   .withOwnerId(representation.ownerId)
								   .withRedirectURI(URI.create(representation.redirectURI))
								   .build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("client application") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return app;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	@XmlElement(name = "ownerid")
	public UUID getOwnerId() {
		return ownerId;
	}
	
	@XmlElement(name = "redirectURI")
	public String getRedirectURI() {
		return redirectURI;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
	}
	
	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, ownerId, redirectURI);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClientAppWriteRepresentation that = (ClientAppWriteRepresentation) obj;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.ownerId, that.ownerId)
                && Objects.equals(this.redirectURI, that.redirectURI);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", name)
											   .add("ownerId", ownerId)
											   .add("redirectURI", redirectURI)
											   .toString();
	}
}
