package io.tyoras.shopping.list.repository.mongo;

import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.list.ItemState;
import io.tyoras.shopping.list.ShoppingItem;
import org.bson.Document;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static io.tyoras.shopping.list.ItemState.TO_BUY;
import static io.tyoras.shopping.list.repository.mongo.ShoppingItemMongoConverter.*;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_CREATED;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_LAST_UPDATE;
import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingItemMongoConverterTest {

    @Test
    public void fromDocument_should_return_null_with_null_document() {
        //given
        Document nullDoc = null;
        ShoppingItemMongoConverter testedConverter = new ShoppingItemMongoConverter();

        //when
        ShoppingItem result = testedConverter.fromDocument(nullDoc);

        //then
        assertThat(result).isNull();
    }

    @Test
    public void fromDocument_should_work_with_valid_doc() {
        //given
        UUID expectId = UUID.randomUUID();
        String expectedName = "name";
        int expectedQuantity = 4;
        ItemState expectedState = TO_BUY;
        LocalDateTime expectedCreationDate = LocalDateTime.now();
        LocalDateTime expectedUpdateDate = LocalDateTime.now();

        Document doc = new Document(FIELD_ID, expectId)
            .append(FIELD_NAME, expectedName)
            .append(FIELD_QUANTITY, expectedQuantity)
            .append(FIELD_STATE, expectedState.name())
            .append(FIELD_CREATED, DateHelper.toDate(expectedCreationDate))
            .append(FIELD_LAST_UPDATE, DateHelper.toDate(expectedUpdateDate));
        ShoppingItemMongoConverter testedConverter = new ShoppingItemMongoConverter();

        //when
        ShoppingItem result = testedConverter.fromDocument(doc);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectId);
        assertThat(result.getName()).isEqualTo(expectedName);
        assertThat(result.getQuantity()).isEqualTo(expectedQuantity);
        assertThat(result.getState()).isEqualTo(expectedState);
        assertThat(result.getCreationDate()).isEqualToIgnoringNanos(expectedCreationDate);
        assertThat(result.getLastUpdate()).isEqualToIgnoringNanos(expectedUpdateDate);
    }

    @Test
    public void toDocument_should_return_empty_doc_with_null_item() {
        //given
        ShoppingItem nullitem = null;
        ShoppingItemMongoConverter testedConverter = new ShoppingItemMongoConverter();

        //when
        Document result = testedConverter.toDocument(nullitem);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(new Document());
    }

    @Test
    public void toDocument_should_work_with_valid_item() {
        //given
        ShoppingItem item = ShoppingItem.Builder.createDefault().withRandomId().build();
        ShoppingItemMongoConverter testedConverter = new ShoppingItemMongoConverter();

        //when
        Document result = testedConverter.toDocument(item);

        //then
        assertThat(result).isNotNull();
        assertThat(result.get(FIELD_ID, UUID.class)).isEqualTo(item.getId());
        assertThat(result.getString(FIELD_NAME)).isEqualTo(item.getName());
        assertThat(result.getInteger(FIELD_QUANTITY)).isEqualTo(item.getQuantity());
        assertThat(ItemState.valueOfOrNull(result.getString(FIELD_STATE))).isEqualTo(item.getState());
        assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_CREATED))).isEqualToIgnoringNanos(item.getCreationDate());
        assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_LAST_UPDATE))).isEqualToIgnoringNanos(item.getLastUpdate());
    }
}
