package yoan.shopping.infra.db.mongo;

import static java.util.Objects.requireNonNull;
import static yoan.shopping.infra.logging.Markers.CONFIG;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.config.api.Config;
import yoan.shopping.infra.db.Dbs;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Singleton
public class MongoDbConnectionFactory {
	
	private final Config config;
	private final MongoClient mongoClient;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbConnectionFactory.class);
	
	@Inject
	protected MongoDbConnectionFactory(Config config) {
		this.config = requireNonNull(config);
		mongoClient = new MongoClient(getServerAdress(), getCredentials());
	}
	
	public MongoDatabase getDB(Dbs db) {
		return mongoClient.getDatabase(db.getDbName());
	}
	
	public MongoCollection<Document> getCollection(Dbs db, String collectionName) {
		return getDB(db).getCollection(collectionName);
	}
	
	private ServerAddress getServerAdress() {
		String host = config.getMongoHost();
		int port = config.getMongoPort();
		return new ServerAddress(host, port);
	}
	
	protected List<MongoCredential> getCredentials() {
		String user = config.getMongoUser();
		String password = config.getMongoPass();
		if (StringUtils.isBlank(user) || StringUtils.isBlank(password)) {
			LOGGER.warn(CONFIG.getMarker(), "Using MongoDb without credentials");
			return ImmutableList.<MongoCredential>of();
		}
		LOGGER.info(CONFIG.getMarker(), "Using MongoDb with user : " + user);
		return ImmutableList.<MongoCredential>of(MongoCredential.createCredential(user, Dbs.SHOPPING.getDbName(), password.toCharArray()));
	}
}
