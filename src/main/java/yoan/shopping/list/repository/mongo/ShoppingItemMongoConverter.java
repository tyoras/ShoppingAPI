package yoan.shopping.list.repository.mongo;

import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;

import yoan.shopping.infra.db.mongo.MongoDocumentConverter;
import yoan.shopping.list.ItemState;
import yoan.shopping.list.ShoppingItem;

/**
 * MongoDb codec to convert shopping item to BSON
 * @author yoan
 */
public class ShoppingItemMongoConverter extends MongoDocumentConverter<ShoppingItem> {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_STATE = "state";
	
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
        ItemState state = ItemState.of(stateCode);
        
        return ShoppingItem.Builder.createDefault()
        				   .withId(id)
        				   .withName(name)
        				   .withQuantity(quantity)
        				   .withState(state)
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
				.append(FIELD_STATE, item.getState().name());
	}
	
	@Override
	public Class<ShoppingItem> getEncoderClass() {
		return ShoppingItem.class;
	}
	
	@Override
	public ShoppingItem generateIdIfAbsentFromDocument(ShoppingItem item) {
		return documentHasId(item) ? item : ShoppingItem.Builder.createFrom(item).withRandomId().build();
	}
}
