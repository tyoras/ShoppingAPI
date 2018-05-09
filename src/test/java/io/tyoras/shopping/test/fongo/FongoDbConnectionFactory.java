/**
 *
 */
package io.tyoras.shopping.test.fongo;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.tyoras.shopping.infra.config.ShoppingApiConfiguration;
import io.tyoras.shopping.infra.db.Dbs;
import io.tyoras.shopping.infra.db.mongo.MongoDbConnectionFactory;
import org.bson.Document;

import static java.util.Objects.requireNonNull;

/**
 * Fongo implementation of the MongoDb connection factory
 *
 * @author yoan
 */
public class FongoDbConnectionFactory extends MongoDbConnectionFactory {
    private final Fongo fongo;

    public FongoDbConnectionFactory(Fongo fongo) {
        super(new ShoppingApiConfiguration());
        this.fongo = requireNonNull(fongo);
    }

    @Override
    public MongoDatabase getDB(Dbs db) {
        return fongo.getDatabase(db.getDbName()).withCodecRegistry(generateFinalCodecRegistry());
    }

    public MongoCollection<Document> getCollection(Dbs db, String collectionName) {
        return getDB(db).getCollection(collectionName);
    }

    public <TDOC> MongoCollection<TDOC> getCollection(Dbs db, String collectionName, Class<TDOC> documentClass) {
        return getDB(db).getCollection(collectionName, documentClass);
    }
}
