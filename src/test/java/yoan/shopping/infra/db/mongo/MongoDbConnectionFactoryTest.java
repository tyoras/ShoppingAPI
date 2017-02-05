package yoan.shopping.infra.db.mongo;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.mongodb.MongoCredential;

import yoan.shopping.infra.config.ShoppingApiConfiguration;

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
