package yoan.shopping.list.representation;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.list.resource.ShoppingListResource;

/**
 * Shopping list Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "list")
public class ShoppingListRepresentation extends RestRepresentation {
	/** List unique ID */
	private UUID id;
	/** List name */
	private String name;
	/** User owner of the list unique ID */
	private UUID ownerId;
	/** List creation date */
	private LocalDateTime creationDate;
	/** Last time the list was updated */
	private LocalDateTime lastUpdate;
	/** All items in the shopping list */
	private List<ShoppingItemRepresentation> itemList;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListRepresentation.class);
	
	public ShoppingListRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public ShoppingListRepresentation(UUID id, String name, UUID ownerId, LocalDateTime creationDate, LocalDateTime lastUpdate, List<ShoppingItemRepresentation> itemList, List<Link> links) {
		super(links);
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
		this.itemList = itemList;
	}
	
	public ShoppingListRepresentation(ShoppingList list, UriInfo uriInfo) {
		requireNonNull(list);
		requireNonNull(uriInfo);
		URI selfURI = uriInfo.getAbsolutePathBuilder().path(ShoppingListResource.class, "getById").build(list.getId().toString());
		this.links.add(Link.self(selfURI));
		this.id = list.getId();
		this.name = list.getName();
		this.ownerId = list.getOwnerId();
		this.creationDate = list.getCreationDate();
		this.lastUpdate = list.getLastUpdate();
		this.itemList = ShoppingItemRepresentation.extractItemListRepresentations(list.getItemList());
	}
	
	public static ShoppingList toShoppingList(ShoppingListRepresentation representation) {
		requireNonNull(representation, "Unable to create ShoppingList from null ShoppingListRepresentation");
		
		ShoppingList.Builder listBuilder = ShoppingList.Builder.createDefault()
						   .withName(representation.name)
						   .withOwnerId(representation.getOwnerId())
						   .withItemList(ShoppingItemRepresentation.toShoppingItemList(representation.getItemList()));
		//if no ID provided, we let the default one
		if (representation.id != null) {
			listBuilder.withId(representation.id);
		}
		
		ShoppingList list;
		try {
			list = listBuilder.build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("list") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return list;
	}

	@XmlElement(name = "id")
	public UUID getId() {
		return id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "ownerId")
	public UUID getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
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

	@XmlElementWrapper(name = "itemList")
	@XmlElement(name = "item")
	public List<ShoppingItemRepresentation> getItemList() {
		return itemList;
	}

	public void setItemList(List<ShoppingItemRepresentation> itemList) {
		this.itemList = itemList;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, ownerId);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ShoppingListRepresentation that = (ShoppingListRepresentation) obj;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.ownerId, that.ownerId);
    }
	
	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", id).add("name", name)
			.add("ownerId", ownerId)
			.add("created", creationDate)
			.add("lastUpdate", lastUpdate)
			.add("itemList", itemList)
			.toString();
	}
}
