package yoan.shopping.list.representation;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import io.swagger.annotations.ApiModel;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.list.ShoppingList;

/**
 * Shopping list Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "list")
@ApiModel(value = "Shopping list write")
public class ShoppingListWriteRepresentation {
	/** List name */
	private String name;
	/** User owner of the list unique ID */
	private UUID ownerId;
	/** All items in the shopping list */
	private List<ShoppingItemWriteRepresentation> itemList;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListWriteRepresentation.class);
	
	public ShoppingListWriteRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public ShoppingListWriteRepresentation(String name, UUID ownerId, List<ShoppingItemWriteRepresentation> itemList) {
		this.name = name;
		this.ownerId = ownerId;
		this.itemList = itemList;
	}
	
	public ShoppingListWriteRepresentation(ShoppingList list) {
		requireNonNull(list);
		this.name = list.getName();
		this.ownerId = list.getOwnerId();
		this.itemList = ShoppingItemWriteRepresentation.extractItemListRepresentations(list.getItemList());
	}
	
	public static ShoppingList toShoppingList(ShoppingListWriteRepresentation representation, UUID listId) {
		requireNonNull(representation, "Unable to create ShoppingList from null ShoppingListWriteRepresentation");
		
		ShoppingList list;
		try {
			list = ShoppingList.Builder.createDefault()
			           .withId(listId)
					   .withName(representation.name)
					   .withOwnerId(representation.getOwnerId())
					   .withItemList(ShoppingItemWriteRepresentation.toShoppingItemList(representation.getItemList()))
					   .build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("list") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return list;
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

	@XmlElementWrapper(name = "itemList")
	@XmlElement(name = "item")
	public List<ShoppingItemWriteRepresentation> getItemList() {
		return itemList;
	}

	public void setItemList(List<ShoppingItemWriteRepresentation> itemList) {
		this.itemList = itemList;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, ownerId);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ShoppingListWriteRepresentation that = (ShoppingListWriteRepresentation) obj;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.ownerId, that.ownerId);
    }
	
	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this)
			.add("ownerId", ownerId)
			.add("itemList", itemList)
			.toString();
	}
}
