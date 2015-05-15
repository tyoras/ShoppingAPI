package yoan.shopping.infra.db.mongo;

import java.util.List;

import org.bson.Document;

import yoan.shopping.infra.db.Dbs;

import com.google.common.collect.ImmutableList;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Singleton
public class MongoDbConnectionFactory {
	
	
	private static final MongoClient mongoClient = new MongoClient(getServerAdress(), getCredentials());
	
	
	public static MongoDatabase getDB(Dbs db) {
		return mongoClient.getDatabase(db.getDbName());
	}
	
	public static MongoCollection<Document> getCollection(Dbs db, String collectionName) {
		return getDB(db).getCollection(collectionName);
	}
	
	private static ServerAddress getServerAdress() {
		return new ServerAddress(ServerAddress.defaultHost(), ServerAddress.defaultPort());
	}
	
	private static List<MongoCredential> getCredentials() {
		return ImmutableList.<MongoCredential>of(MongoCredential.createCredential("", Dbs.SHOPPING.getDbName(), "".toCharArray()));
	}
}
