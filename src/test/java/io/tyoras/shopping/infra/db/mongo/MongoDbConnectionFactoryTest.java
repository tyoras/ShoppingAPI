package io.tyoras.shopping.infra.db.mongo;

import com.mongodb.MongoCredential;
import io.tyoras.shopping.infra.config.ShoppingApiConfiguration;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoDbConnectionFactoryTest {

    @Test
    public void getCredentials_should_return_empty_list_if_no_mongo_credentials_in_config() {
        //given
        ShoppingApiConfiguration configWithoutMongoCredentials = new ShoppingApiConfiguration();
        MongoDbConnectionFactory tested = new MongoDbConnectionFactory(configWithoutMongoCredentials);

        //when
        List<MongoCredential> result = tested.getCredentials();

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    public void getCredentials_should_return_credential_list_if_mongo_credentials_in_config() {
        //given
        ShoppingApiConfiguration configWithMongoCredentials = new ShoppingApiConfiguration();
        configWithMongoCredentials.mongo.user = "user";
        configWithMongoCredentials.mongo.password = "pass";
        MongoDbConnectionFactory tested = new MongoDbConnectionFactory(configWithMongoCredentials);

        //when
        List<MongoCredential> result = tested.getCredentials();

        //then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }
}
