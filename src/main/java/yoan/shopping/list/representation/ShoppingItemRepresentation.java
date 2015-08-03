package yoan.shopping.list.representation;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.list.ItemState;
import yoan.shopping.list.ShoppingItem;

/**
 * Shopping item Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "item")
public class ShoppingItemRepresentation {
	/** Item unique ID */
	private UUID id;
	/** Item name */
	private String name;
	/** Quantity of this item */
	private int quantity;
	/** Current item state */
	private String state;
	/** Item creation date */
	private LocalDateTime creationDate;
	/** Last time the item was updated */
	private LocalDateTime lastUpdate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingItemRepresentation.class);
	
	public ShoppingItemRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public ShoppingItemRepresentation(UUID id, String name, int quantity, String state, LocalDateTime creationDate, LocalDateTime lastUpdate) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.state = state;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
	}
	
	public ShoppingItemRepresentation(ShoppingItem item) {
		super();
		requireNonNull(item);
		this.id = item.getId();
		this.name = item.getName();
		this.quantity = item.getQuantity();
		this.state = item.getState().name();
		this.creationDate = item.getCreationDate();
		this.lastUpdate = item.getLastUpdate();
	}
	
	public static ShoppingItem toShoppingItem(ShoppingItemRepresentation representation) {
		requireNonNull(representation, "Unable to create ShoppingItem from null ShoppingItemRepresentation");
		
		ShoppingItem.Builder itemBuilder = ShoppingItem.Builder.createDefault()
						   .withName(representation.name)
						   .withQuantity(representation.quantity)
						   .withState(ItemState.of(representation.state))
						   .withCreationDate(representation.creationDate)
						   .withLastUpdate(representation.lastUpdate);
		//if no ID provided, we let the default one
		if (representation.id != null) {
			itemBuilder.withId(representation.id);
		}
		
		ShoppingItem item;
		try {
			item = itemBuilder.build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("item") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return item;
	}
	
	public static List<ShoppingItem> toShoppingItemList(List<ShoppingItemRepresentation> representations) {
		requireNonNull(representations, "Unable to create ShoppingItems from null ShoppingItemRepresentations");
		
		List<ShoppingItem> items = new ArrayList<>();
		representations.forEach(representation -> items.add(toShoppingItem(representation)));
		
		return items;
	}
	
	public static  List<ShoppingItemRepresentation> extractItemListRepresentations(ImmutableList<ShoppingItem> items) {
		List<ShoppingItemRepresentation> itemList = new ArrayList<>();
		items.forEach(item -> itemList.add(new ShoppingItemRepresentation(item)));
		return itemList;
	}

	@XmlElement(name = "id")
	public UUID getId() {
		return id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "quantity")
	public int getQuantity() {
		return quantity;
	}
	
	@XmlElement(name = "state")
	public String getState() {
		return state;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, quantity, state);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ShoppingItemRepresentation that = (ShoppingItemRepresentation) obj;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.quantity, that.quantity)
                && Objects.equals(this.state, that.state);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id)
											   .add("name", name)
											   .add("quantity", quantity)
											   .add("state", state)
											   .toString();
	}
}