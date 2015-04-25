package yoan.shopping.infra.db.mongo;

import org.bson.Document;

import yoan.shopping.infra.db.Dbs;

import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Singleton
public class MongoDbConnectionFactory {
	
	private static final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/"));
	
	
	public static MongoDatabase getDB(Dbs db) {
		return mongoClient.getDatabase(db.getDbName());
	}
	
	public static MongoCollection<Document> getCollection(Dbs db, String collectionName) {
		return getDB(db).getCollection(collectionName);
	}
}
