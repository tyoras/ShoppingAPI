package io.tyoras.shopping.list.repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.list.ShoppingList;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.test.fongo.FongoBackedTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.UUID;

import static io.tyoras.shopping.infra.db.Dbs.SHOPPING;
import static io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.NOT_FOUND;
import static io.tyoras.shopping.infra.util.error.RepositoryErrorCode.ALREADY_EXISTING;
import static io.tyoras.shopping.list.repository.ShoppingItemRepositoryErrorMessage.PROBLEM_CREATION_ITEM_ALREADY_EXISTS;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoRepository.LIST_COLLECTION;
import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingItemMongoRepositoryTest extends FongoBackedTest {

    private final ShoppingListMongoConverter listConverter = new ShoppingListMongoConverter();
    private final MongoCollection<Document> listCollection = getFongoDbConnectionFactory().getCollection(SHOPPING, LIST_COLLECTION);

    @InjectMocks
    ShoppingListMongoRepository listRepo;

    @Test
    public void create_should_work() throws InterruptedException {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        ShoppingList existingList = TestHelper.generateRandomShoppingList();
        listRepo.create(existingList);
        Thread.sleep(1);
        ShoppingItem expectedShoppingItem = TestHelper.generateRandomShoppingItem();

        //when
        testedRepo.create(existingList.getId(), expectedShoppingItem);

        //then
        Bson filter = Filters.eq(FIELD_ID, existingList.getId());
        Document result = listCollection.find().filter(filter).first();
        ShoppingList list = listConverter.fromDocument(result);
        ShoppingItem createdItem = list.getItemList().get(list.getItemList().size() - 1);
        //last update date should have been updated
        assertThat(list.getLastUpdate().isAfter(existingList.getLastUpdate())).isTrue();
        assertThat(createdItem).isEqualTo(expectedShoppingItem);
        assertThat(createdItem.getCreationDate()).isEqualTo(createdItem.getLastUpdate());
    }

    @Test(expected = ApplicationException.class)
    public void create_should_fail_with_message_with_already_existing_item() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        ShoppingList existingList = TestHelper.generateRandomShoppingList();
        ShoppingItem alreadyExistingShoppingItem = existingList.getItemList().get(0);
        listRepo.create(existingList);
        existingList = listRepo.getById(existingList.getId());
        String expectedMessage = PROBLEM_CREATION_ITEM_ALREADY_EXISTS.getDevReadableMessage(alreadyExistingShoppingItem.getId());

        //when
        try {
            testedRepo.create(existingList.getId(), alreadyExistingShoppingItem);
        } catch (ApplicationException ae) {
            //then
            TestHelper.assertApplicationException(ae, INFO, ALREADY_EXISTING, expectedMessage);
            throw ae;
        } finally {
            //checking if the already existing item still exists
            Bson filter = Filters.eq(FIELD_ID, existingList.getId());
            Document result = listCollection.find().filter(filter).first();
            ShoppingList list = listConverter.fromDocument(result);
            assertThat(list).isNotNull();
            assertThat(list.getItemList().get(0)).isEqualTo(alreadyExistingShoppingItem);
            assertThat(list.getLastUpdate()).isEqualTo(existingList.getLastUpdate());
        }
    }

    @Test(expected = ApplicationException.class)
    public void create_should_fail_with_message_with_not_existing_list() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        UUID unknownListId = UUID.randomUUID();
        ShoppingItem item = TestHelper.generateRandomShoppingItem();

        //when
        try {
            testedRepo.create(unknownListId, item);
        } catch (ApplicationException ae) {
            //then
            assertThat(ae.getMessage()).contains(NOT_FOUND.getDevReadableMessage("List"));
            throw ae;
        } finally {
            //checking if the list was not created
            Bson filter = Filters.eq("_id", unknownListId);
            Document result = listCollection.find().filter(filter).first();
            ShoppingList list = listConverter.fromDocument(result);
            assertThat(list).isNull();
        }
    }

    @Test
    public void getById_should_return_null_with_not_existing_item_id() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        ShoppingList existingList = TestHelper.generateRandomShoppingList();
        listRepo.create(existingList);
        UUID notExistingShoppingItemId = UUID.randomUUID();

        //when
        ShoppingItem result = testedRepo.getById(existingList.getId(), notExistingShoppingItemId);

        //then
        assertThat(result).isNull();
    }

    @Test(expected = ApplicationException.class)
    public void getById_should_fail_with_not_existing_list_id() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        UUID notExistingShoppingListId = UUID.randomUUID();
        UUID notExistingShoppingItemId = UUID.randomUUID();

        //when
        try {
            testedRepo.getById(notExistingShoppingListId, notExistingShoppingItemId);
        } catch (ApplicationException ae) {
            //then
            assertThat(ae.getMessage()).contains(NOT_FOUND.getDevReadableMessage("List"));
            throw ae;
        }
    }

    @Test
    public void getById_should_work_with_existing_item_id() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        ShoppingList existingList = TestHelper.generateRandomShoppingList();
        listRepo.create(existingList);
        ShoppingItem expectedShoppingItem = existingList.getItemList().get(0);

        //when
        ShoppingItem result = testedRepo.getById(existingList.getId(), expectedShoppingItem.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedShoppingItem);
    }

    @Test
    @Ignore // passe avec embed mongo mais pas avec Fongo :(
    public void update_should_work_with_existing_item() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        ShoppingList existingList = TestHelper.generateRandomShoppingList();
        listRepo.create(existingList);
        existingList = listRepo.getById(existingList.getId());
        ShoppingItem originalShoppingItem = existingList.getItemList().get(0);
        String modifiedName = "new " + originalShoppingItem.getName();
        ShoppingItem modifiedShoppingItem = ShoppingItem.Builder.createFrom(originalShoppingItem).withName(modifiedName).build();

        //when
        testedRepo.update(existingList.getId(), modifiedShoppingItem);

        //then
        ShoppingItem result = testedRepo.getById(existingList.getId(), originalShoppingItem.getId());
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(modifiedName);
        assertThat(result).isEqualTo(modifiedShoppingItem);
        //creation date should not change
        assertThat(result.getCreationDate()).isEqualTo(originalShoppingItem.getCreationDate());
        //last update date should have changed
        assertThat(result.getLastUpdate().isAfter(originalShoppingItem.getLastUpdate())).isTrue();
    }

    @Test
    public void deleteById_should_not_fail_with_not_existing_item_id() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        ShoppingList existingList = TestHelper.generateRandomShoppingList();
        listRepo.create(existingList);
        UUID notExistingShoppingItemId = UUID.randomUUID();

        //when
        testedRepo.deleteById(existingList.getId(), notExistingShoppingItemId);

        //then
        //should not have failed
    }

    @Test
    public void deleteById_should_not_fail_with_not_existing_list_id() {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        UUID notExistingShoppingListId = UUID.randomUUID();
        UUID notExistingShoppingItemId = UUID.randomUUID();

        //when
        testedRepo.deleteById(notExistingShoppingListId, notExistingShoppingItemId);

        //then
        //should not have failed
    }

    @Test
    public void deleteById_should_work_with_existing_item_id() throws InterruptedException {
        //given
        ShoppingItemMongoRepository testedRepo = new ShoppingItemMongoRepository(connectionFactory, listRepo);
        ShoppingList existingList = TestHelper.generateRandomShoppingList();
        listRepo.create(existingList);
        Thread.sleep(1);
        ShoppingItem existingShoppingItem = existingList.getItemList().get(0);

        //when
        testedRepo.deleteById(existingList.getId(), existingShoppingItem.getId());

        //then
        ShoppingList list = listRepo.getById(existingList.getId());
        assertThat(list).isNotNull();
        //last update date should have been updated
        assertThat(list.getLastUpdate().isAfter(existingList.getLastUpdate())).isTrue();
        ShoppingItem result = testedRepo.getById(existingList.getId(), existingShoppingItem.getId());
        assertThat(result).isNull();
    }
}