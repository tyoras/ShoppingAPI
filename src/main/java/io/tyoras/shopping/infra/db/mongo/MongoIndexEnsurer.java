package io.tyoras.shopping.infra.db.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.tyoras.shopping.infra.logging.Markers;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Helper to create different kind of mongo indexes if not already existing on a mongo collection
 *
 * @author yoan
 */
public class MongoIndexEnsurer {

    private static final Marker CONFIG = Markers.CONFIG.getMarker();
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoIndexEnsurer.class);
    private final MongoCollection<?> collection;
    private final String collectionName;

    public MongoIndexEnsurer(MongoCollection<?> collection) {
        this.collection = requireNonNull(collection, "Mongo collection is mandatory");
        collectionName = collection.getNamespace().getCollectionName();
    }

    public void logStartEnsuringIndexes() {
        LOGGER.info(CONFIG, String.format("Ensuring indexes on %s collection", collectionName));
    }

    public void logEndEnsuringIndexes() {
        LOGGER.info(CONFIG, String.format("Finished to ensure indexes on %s collection", collectionName));
    }

    public void ensureTTLIndex(String fieldName, SortOrder sortOrder, long ttl, TimeUnit timeUnit) {
        Document indexKey = new Document(fieldName, sortOrder.getOrder());
        IndexOptions options = new IndexOptions();
        options.expireAfter(ttl, timeUnit);
        collection.createIndex(indexKey, options);
    }

    public void ensureUniqueIndex(String fieldName, SortOrder sortOrder) {
        Document indexKey = new Document(fieldName, sortOrder.getOrder());
        IndexOptions options = new IndexOptions();
        options.unique(true);
        collection.createIndex(indexKey, options);
    }

    public void ensureIndex(String fieldName, SortOrder sortOrder) {
        Document indexKey = new Document(fieldName, sortOrder.getOrder());
        collection.createIndex(indexKey);
    }

    public void ensureMultiKeyIndex(Map<String, SortOrder> index) {
        Document indexKeys = new Document();
        for (Entry<String, SortOrder> e : index.entrySet()) {
            indexKeys.append(e.getKey(), e.getValue().getOrder());
        }
        collection.createIndex(indexKeys);
    }

    public enum SortOrder {
        ASCENDING(1),
        DESCENDING(-1);

        private int order;

        private SortOrder(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }
}
