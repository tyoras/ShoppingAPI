package yoan.shopping.list.repository.mongo;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.infra.db.Dbs.SHOPPING;
import static yoan.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static yoan.shopping.list.repository.ShoppingListRepositoryErrorMessage.PROBLEM_CREATION_LIST;
import static yoan.shopping.list.repository.mongo.ShoppingListMongoRepository.LIST_COLLECTION;

import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.mockito.InjectMocks;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.test.TestHelper;
import yoan.shopping.test.fongo.FongoBackedTest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class ShoppingListMongoRepositoryTest extends FongoBackedTest {
	
	private final ShoppingListMongoConverter converter = new ShoppingListMongoConverter();
	private final MongoCollection<Document> listCollection = getFongoDbConnectionFactory().getCollection(SHOPPING, LIST_COLLECTION);
	
	@InjectMocks
	ShoppingListMongoRepository testedRepo;
	
	@Test
	public void create_should_work() {
		//given
		ShoppingList expectedShoppingList = TestHelper.generateRandomShoppingList();

		//when
		testedRepo.create(expectedShoppingList);
		
		//then
		Bson filter = Filters.eq(FIELD_ID, expectedShoppingList.getId());
		Document result = listCollection.find().filter(filter).first();
		ShoppingList list = converter.fromDocument(result);
		assertThat(list).isEqualTo(expectedShoppingList);
		assertThat(list.getCreationDate()).isEqualTo(list.getLastUpdate());
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_message_with_already_existing_list() {
		//given
		ShoppingList alreadyExistingShoppingList = TestHelper.generateRandomShoppingList();
		testedRepo.create(alreadyExistingShoppingList);
		
		//when
		try {
			testedRepo.create(alreadyExistingShoppingList);
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_LIST.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing list still exists
			Bson filter = Filters.eq("_id", alreadyExistingShoppingList.getId());
			Document result = listCollection.find().filter(filter).first();
			ShoppingList list = converter.fromDocument(result);
			assertThat(list).isEqualTo(alreadyExistingShoppingList);
		}
	}
	
	@Test
	public void getById_should_return_null_with_not_existing_list_id() {
		//given
		UUID notExistingShoppingListId = UUID.randomUUID();

		//when
		ShoppingList result = testedRepo.getById(notExistingShoppingListId);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getById_should_work_with_existing_list_id() {
		//given
		ShoppingList expectedShoppingList = TestHelper.generateRandomShoppingList();
		testedRepo.create(expectedShoppingList);

		//when
		ShoppingList result = testedRepo.getById(expectedShoppingList.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedShoppingList);
	}
	
	@Test
	public void update_should_work_with_existing_list() {
		//given
		ShoppingList originalShoppingList = TestHelper.generateRandomShoppingList();
		testedRepo.create(originalShoppingList);
		originalShoppingList = testedRepo.getById(originalShoppingList.getId());
		String modifiedName = "new " + originalShoppingList.getName();
		ShoppingList modifiedShoppingList = ShoppingList.Builder.createFrom(originalShoppingList).withName(modifiedName).build();

		//when
		testedRepo.update(modifiedShoppingList);
		
		//then
		ShoppingList result = testedRepo.getById(originalShoppingList.getId());
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(modifiedName);
		assertThat(result).isEqualTo(modifiedShoppingList);
		//creation date should not change
		assertThat(result.getCreationDate()).isEqualTo(originalShoppingList.getCreationDate());
		//last update date should have changed
		assertThat(result.getLastUpdate().isAfter(originalShoppingList.getLastUpdate())).isTrue();
	}
	
	@Test
	public void update_should_be_able_to_update_items() {
		//given
		ShoppingList originalShoppingList = TestHelper.generateRandomShoppingList();
		testedRepo.create(originalShoppingList);
		originalShoppingList = testedRepo.getById(originalShoppingList.getId());
		List<ShoppingItem> modifiedList = Lists.newArrayList(originalShoppingList.getItemList());
		ShoppingItem modifiedItem = modifiedList.get(0);
		modifiedItem = ShoppingItem.Builder.createFrom(modifiedItem).withName("new " + modifiedItem.getName()).build();
		modifiedList.set(0, modifiedItem);
		ShoppingList modifiedShoppingList = ShoppingList.Builder.createFrom(originalShoppingList).withItemList(modifiedList).build();

		//when
		testedRepo.update(modifiedShoppingList);
		
		//then
		ShoppingList result = testedRepo.getById(originalShoppingList.getId());
		assertThat(result).isNotNull();
		assertThat(result.getItemList().get(0)).isEqualTo(modifiedItem);
		assertThat(result).isEqualTo(modifiedShoppingList);
		//creation date should not change
		assertThat(result.getCreationDate()).isEqualTo(originalShoppingList.getCreationDate());
		//last update date should have changed
		assertThat(result.getLastUpdate().isAfter(originalShoppingList.getLastUpdate())).isTrue();
	}
	
	@Test
	public void deleteById_should_not_fail_with_not_existing_list_id() {
		//given
		UUID notExistingShoppingListId = UUID.randomUUID();

		//when
		testedRepo.deleteById(notExistingShoppingListId);
		
		//then
		//should not have failed
	}
	
	@Test
	public void deleteById_should_work_with_existing_list_id() {
		//given
		ShoppingList existingShoppingList = TestHelper.generateRandomShoppingList();
		testedRepo.create(existingShoppingList);

		//when
		testedRepo.deleteById(existingShoppingList.getId());
		
		//then
		ShoppingList result = testedRepo.getById(existingShoppingList.getId());
		assertThat(result).isNull();
	}
	
	@Test
	public void getByOwner_should_return_empty_list_if_no_list_found() {
		//given
		UUID ownerIdWithoutList = UUID.randomUUID();

		//when
		ImmutableList<ShoppingList> result = testedRepo.getByOwner(ownerIdWithoutList);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(ImmutableList.<ShoppingList>of());
	}
	
	@Test
	public void getByOwner_should_work_with_owner_with_list() {
		//given
		UUID ownerIdWithTwoLists = UUID.randomUUID();
		ShoppingList list1 = TestHelper.generateRandomShoppingList();
		ShoppingList expectedShoppingList1 = ShoppingList.Builder.createFrom(list1).withOwnerId(ownerIdWithTwoLists).build();
		ShoppingList list2 = TestHelper.generateRandomShoppingList();
		ShoppingList expectedShoppingList2 = ShoppingList.Builder.createFrom(list2).withOwnerId(ownerIdWithTwoLists).build();
		
		testedRepo.create(expectedShoppingList1);
		testedRepo.create(expectedShoppingList2);

		//when
		ImmutableList<ShoppingList> result = testedRepo.getByOwner(ownerIdWithTwoLists);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(expectedShoppingList1, expectedShoppingList2);
	}
}