package io.tyoras.shopping.list.repository.mongo;

import static io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_CREATED;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_ITEM_LIST;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_LAST_UPDATE;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_NAME;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_OWNER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.junit.Test;

import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.list.ShoppingList;
import io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter;
import io.tyoras.shopping.test.TestHelper;

public class ShoppingListMongoConverterTest {
	
	@Test
	public void fromDocument_should_return_null_with_null_document() {
		//given
		Document nullDoc = null;
		ShoppingListMongoConverter testedConverter = new ShoppingListMongoConverter();
		
		//when
		ShoppingList result = testedConverter.fromDocument(nullDoc);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void fromDocument_should_work_with_valid_doc() {
		//given
		ShoppingList expectedList = TestHelper.generateRandomShoppingList();
		ShoppingListMongoConverter testedConverter = new ShoppingListMongoConverter();
		List<Document> itemArray = testedConverter.getItemArray(expectedList.getItemList());
		Document doc = new Document(FIELD_ID, expectedList.getId())
							.append(FIELD_NAME, expectedList.getName())
							.append(FIELD_OWNER_ID, expectedList.getOwnerId())
							.append(FIELD_CREATED, DateHelper.toDate(expectedList.getCreationDate()))
							.append(FIELD_LAST_UPDATE, DateHelper.toDate(expectedList.getLastUpdate()))
							.append(FIELD_ITEM_LIST, itemArray);
		
		//when
		ShoppingList result = testedConverter.fromDocument(doc);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedList.getId());
		assertThat(result.getName()).isEqualTo(expectedList.getName());
		assertThat(result.getOwnerId()).isEqualTo(expectedList.getOwnerId());
		assertThat(result.getCreationDate()).isEqualTo(expectedList.getCreationDate());
		assertThat(result.getLastUpdate()).isEqualTo(expectedList.getLastUpdate());
		assertThat(result.getItemList()).isEqualTo(expectedList.getItemList());
	}
	
	@Test
	public void toDocument_should_return_empty_doc_with_null_list() {
		//given
		ShoppingList nullList = null;
		ShoppingListMongoConverter testedConverter = new ShoppingListMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(nullList);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(new Document());
	}
	
	@Test
	public void toDocument_should_work_with_valid_list() {
		//given
		ShoppingList list = TestHelper.generateRandomShoppingList();
		ShoppingListMongoConverter testedConverter = new ShoppingListMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(list);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.get(FIELD_ID, UUID.class)).isEqualTo(list.getId());
		assertThat(result.getString(FIELD_NAME)).isEqualTo(list.getName());
		assertThat(result.get(FIELD_OWNER_ID, UUID.class)).isEqualTo(list.getOwnerId());
		assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_CREATED))).isEqualTo(list.getCreationDate());
		assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_LAST_UPDATE))).isEqualTo(list.getLastUpdate());
		@SuppressWarnings("unchecked")
		List<Document> itemArray = (List<Document>) result.get(FIELD_ITEM_LIST);
		assertThat(itemArray).isNotNull();
		assertThat(itemArray).hasSameSizeAs(list.getItemList());
	}
}
