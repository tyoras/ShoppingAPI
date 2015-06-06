/**
 * 
 */
package yoan.shopping.test;

import static java.util.Objects.requireNonNull;
import yoan.shopping.infra.config.api.Config;
import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoDatabase;

/**
 * Fongo implementation of the MongoDb connection factory
 * @author yoan
 */
public class FongoDbConnectionFactory extends MongoDbConnectionFactory {
	private final Fongo fongo;
	
	public FongoDbConnectionFactory(Fongo fongo) {
		super(Config.DEFAULT);
		this.fongo = requireNonNull(fongo);
	}
	
	@Override
	public MongoDatabase getDB(Dbs db) {
		return fongo.getDatabase(db.getDbName());
	}
}
