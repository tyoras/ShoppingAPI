package io.tyoras.shopping.list.repository.mongo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;

import io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter;
import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.list.ItemState;
import io.tyoras.shopping.list.ShoppingItem;

/**
 * MongoDb codec to convert shopping item to BSON
 * @author yoan
 */
public class ShoppingItemMongoConverter extends MongoDocumentConverter<ShoppingItem> {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";
	
    public ShoppingItemMongoConverter() {
		super();
	}
	
    public ShoppingItemMongoConverter(Codec<Document> codec) {
		super(codec);
	}
    
	@Override
	public ShoppingItem fromDocument(Document doc) {
		if (doc == null) {
			return null;
		}
		
        UUID id = doc.get(FIELD_ID, UUID.class);
        String name = doc.getString(FIELD_NAME);
        int quantity = doc.getInteger(FIELD_QUANTITY);
        String stateCode = doc.getString(FIELD_STATE);
        ItemState state = ItemState.valueOfOrNull(stateCode);
        Date created = doc.getDate(FIELD_CREATED);
        LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
        Date lastUpdated = doc.getDate(FIELD_LAST_UPDATE);
        LocalDateTime lastUpdate = DateHelper.toLocalDateTime(lastUpdated);
        
        return ShoppingItem.Builder.createDefault()
        				   .withId(id)
        				   .withName(name)
        				   .withQuantity(quantity)
        				   .withState(state)
        				   .withCreationDate(creationDate)
        				   .withLastUpdate(lastUpdate)
        				   .build();
	}

	@Override
	public Document toDocument(ShoppingItem item) {
		if (item == null) {
			return new Document();
		}
		
		return new Document(FIELD_ID, item.getId())
				.append(FIELD_NAME, item.getName())
				.append(FIELD_QUANTITY, item.getQuantity())
				.append(FIELD_STATE, item.getState().name())
				.append(FIELD_CREATED, DateHelper.toDate(item.getCreationDate()))
				.append(FIELD_LAST_UPDATE, DateHelper.toDate(item.getLastUpdate()));
	}
	
	@Override
	public Class<ShoppingItem> getEncoderClass() {
		return ShoppingItem.class;
	}
	
	@Override
	public ShoppingItem generateIdIfAbsentFromDocument(ShoppingItem item) {
		return documentHasId(item) ? item : ShoppingItem.Builder.createFrom(item).withRandomId().build();
	}
	
	public Document getItemUpdate(ShoppingItem itemToUpdate) {
		return new Document(FIELD_LAST_UPDATE, DateHelper.toDate(itemToUpdate.getLastUpdate()))
									.append(FIELD_QUANTITY, itemToUpdate.getQuantity())
									.append(FIELD_NAME, itemToUpdate.getName())
									.append(FIELD_STATE, itemToUpdate.getState().name());
	}
}
