package io.tyoras.shopping.infra.healthcheck;

import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import io.tyoras.shopping.infra.db.Dbs;
import io.tyoras.shopping.infra.db.mongo.MongoDbConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import javax.inject.Inject;

public class MongoHealthCheck extends NamedHealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoHealthCheck.class);

    private final MongoDatabase db;

    @Inject
    public MongoHealthCheck(MongoDbConnectionFactory mongoConnectionFactory) {
        db = mongoConnectionFactory.getDB(Dbs.SHOPPING);
    }

    @Override
    public String getName() {
        return "mongo";
    }

    @Override
    protected Result check() throws Exception {
        try {
            accessDB();
        } catch (MongoException e) {
            LOGGER.warn("Mongo is not healthy", e);
            return Result.unhealthy("MongoDb is unreachable");
        }
        return Result.healthy();
    }

    protected void accessDB() {
        db.listCollectionNames().first();
    }

}
