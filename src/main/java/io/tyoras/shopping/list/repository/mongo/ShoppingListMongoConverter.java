/**
 * 
 */
package io.tyoras.shopping.list.repository.mongo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;

import com.google.common.collect.ImmutableList;

import io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter;
import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.list.ShoppingList;

/**
 * MongoDb codec to convert shopping list to BSON
 * @author yoan
 */
public class ShoppingListMongoConverter extends MongoDocumentConverter<ShoppingList> {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_OWNER_ID = "ownerId";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";
    public static final String FIELD_ITEM_LIST = "itemList";
    
    private ShoppingItemMongoConverter itemConverter;
    
    public ShoppingListMongoConverter() {
		super();
		itemConverter = new ShoppingItemMongoConverter();
	}
	
    public ShoppingListMongoConverter(Codec<Document> codec) {
		super(codec);
		itemConverter = new ShoppingItemMongoConverter(codec);
	}
	
	@Override
	public ShoppingList fromDocument(Document doc) {
		if (doc == null) {
			return null;
		}
		
        UUID id = doc.get(FIELD_ID, UUID.class);
        String name = doc.getString(FIELD_NAME);
        UUID ownerId = doc.get(FIELD_OWNER_ID, UUID.class);
        Date created = doc.getDate(FIELD_CREATED);
        LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
        Date lastUpdated = doc.getDate(FIELD_LAST_UPDATE);
        LocalDateTime lastUpdate = DateHelper.toLocalDateTime(lastUpdated);
        List<ShoppingItem> itemList = extractItemList(doc);
        
        return ShoppingList.Builder.createDefault()
        				   .withId(id)
        				   .withCreationDate(creationDate)
        				   .withLastUpdate(lastUpdate)
        				   .withName(name)
        				   .withOwnerId(ownerId)
        				   .withItemList(itemList)
        				   .build();
	}
	
	private List<ShoppingItem> extractItemList(Document doc) {
		@SuppressWarnings("unchecked")
		List<Document> array = (List<Document>) doc.get(FIELD_ITEM_LIST);
		List<ShoppingItem> itemList = new ArrayList<ShoppingItem>();
		array.forEach(item -> itemList.add(itemConverter.fromDocument(item)));
		return itemList;
	}

	@Override
	public Document toDocument(ShoppingList list) {
		if (list == null) {
			return new Document();
		}
		
		List<Document> itemArray = getItemArray(list.getItemList());
		
		return new Document(FIELD_ID, list.getId())
				.append(FIELD_NAME, list.getName())
				.append(FIELD_OWNER_ID, list.getOwnerId())
				.append(FIELD_ITEM_LIST, itemArray)
				.append(FIELD_CREATED, DateHelper.toDate(list.getCreationDate()))
				.append(FIELD_LAST_UPDATE, DateHelper.toDate(list.getLastUpdate()));
	}

	protected List<Document> getItemArray(ImmutableList<ShoppingItem> itemList) {
		List<Document> itemArray = new ArrayList<>();
		itemList.forEach(item -> itemArray.add(itemConverter.toDocument(item)));
		return itemArray;
	}
	
	@Override
	public Class<ShoppingList> getEncoderClass() {
		return ShoppingList.class;
	}
	
	@Override
	public ShoppingList generateIdIfAbsentFromDocument(ShoppingList list) {
		return documentHasId(list) ? list : ShoppingList.Builder.createFrom(list).withRandomId().build();
	}
	
	public Document getListUpdate(ShoppingList listToUpdate) {
		List<Document> itemArray = getItemArray(listToUpdate.getItemList());
		Document updateDoc = new Document(FIELD_LAST_UPDATE, DateHelper.toDate(listToUpdate.getLastUpdate()))
									.append(FIELD_OWNER_ID, listToUpdate.getOwnerId())
									.append(FIELD_NAME, listToUpdate.getName())
									.append(FIELD_ITEM_LIST, itemArray);
		return new Document("$set", updateDoc);
	}
}