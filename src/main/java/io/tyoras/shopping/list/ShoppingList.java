/**
 * 
 */
package io.tyoras.shopping.list;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import io.tyoras.shopping.infra.db.WithId;
import io.tyoras.shopping.infra.util.GenericBuilder;
import io.tyoras.shopping.user.User;

/**
 * Shopping list
 * @author yoan
 */
public class ShoppingList implements Bson, WithId {
	/** Default list ID */
	public static final UUID DEFAULT_ID = UUID.fromString("da8c92f3-0c95-4879-b892-47015e694ead");
	/** Default empty list instance instance */
	public static final ShoppingList EMPTY = Builder.createDefault().build();
	
	/** List unique ID */
	private final UUID id;
	/** List name */
	private final String name;
	/** User owner of the list unique ID */
	private final UUID ownerId;
	/** List creation date */
	private final LocalDateTime creationDate;
	/** Last time the list was updated */
	private final LocalDateTime lastUpdate;
	/** All items in the shopping list */
	private final ImmutableList<ShoppingItem> itemList; 
	
	
	protected ShoppingList(UUID id, String name, UUID ownerId, LocalDateTime creationDate, LocalDateTime lastUpdate, ImmutableList<ShoppingItem> itemList) {
		this.id = requireNonNull(id, "List Id is mandatory");
		checkArgument(StringUtils.isNotBlank(name), "Invalid list name");
		this.name = name;
		this.ownerId = requireNonNull(ownerId, "List owner Id is mandatory");
		this.creationDate = requireNonNull(creationDate, "Creation date is mandatory");
		this.lastUpdate = requireNonNull(lastUpdate, "Last update date is mandatory");
		this.itemList = requireNonNull(itemList, "Item list is mandatory");
	}
	
	public static class Builder implements GenericBuilder<ShoppingList> {
		private UUID id = DEFAULT_ID;
		private String name = "Default name";
		private UUID ownerId = User.DEFAULT_ID;
		private LocalDateTime creationDate = LocalDateTime.now();
		private LocalDateTime lastUpdate = LocalDateTime.now();
		private List<ShoppingItem> itemList = new ArrayList<>();
		
		private Builder() { }
		
		/**
         * The default list is EMPTY
         *
         * @return EMPTY list
         */
        public static Builder createDefault() {
            return new Builder();
        }
        
        /**
         * Duplicate an existing builder
         *
         * @param otherBuilder
         * @return builder
         */
        public static Builder createFrom(final Builder otherBuilder) {
            Builder builder = new Builder();

            builder.id = otherBuilder.id;
            builder.name = otherBuilder.name;
            builder.ownerId = otherBuilder.ownerId;
            builder.creationDate = otherBuilder.creationDate;
            builder.lastUpdate = otherBuilder.lastUpdate;
            builder.itemList = otherBuilder.itemList;
            
            return builder;
        }
        
        /**
         * Get a builder based on an existing ShoppingList instance
         *
         * @param user
         * @return builder
         */
        public static Builder createFrom(final ShoppingList list) {
            Builder builder = new Builder();

            builder.id = list.id;
            builder.name = list.name;
            builder.ownerId = list.ownerId;
            builder.creationDate = list.creationDate;
            builder.lastUpdate = list.lastUpdate;
            builder.itemList = list.itemList;
            
            return builder;
        }
        
		@Override
		public ShoppingList build() {
			ImmutableList<ShoppingItem> finalItemList = ImmutableList.<ShoppingItem>copyOf(itemList);
			return new ShoppingList(id, name, ownerId, creationDate, lastUpdate, finalItemList);
		}
		
		public Builder withId(UUID id) {
            this.id = requireNonNull(id);
            return this;
        }

        /**
         * Set a random user ID
         *
         * @return builder
         */
        public Builder withRandomId() {
            this.id = UUID.randomUUID();
            return this;
        }
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withOwnerId(UUID ownerId) {
            this.ownerId = requireNonNull(ownerId);
            return this;
        }
        
        public Builder withCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }
        
        public Builder withLastUpdate(LocalDateTime lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }
        
        public Builder withItemList(List<ShoppingItem> itemList) {
            this.itemList = requireNonNull(itemList);
            return this;
        }
        
        public Builder withItem(ShoppingItem item) {
            itemList.add(item);
            return this;
        }
        
        public Builder withoutItem(ShoppingItem item) {
            itemList.remove(item);
            return this;
        }
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public UUID getOwnerId() {
		return ownerId;
	}
	
	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public ImmutableList<ShoppingItem> getItemList() {
		return itemList;
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
        ShoppingList that = (ShoppingList) obj;
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
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<ShoppingList>(this, codecRegistry.get(ShoppingList.class));
	}
}
