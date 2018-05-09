package io.tyoras.shopping.test;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.tyoras.shopping.infra.config.ShoppingApiConfiguration;
import io.tyoras.shopping.infra.db.mongo.MongoDbConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Base class for unit tests backed by local mongo
 *
 * @author yoan
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class LocalMongoBackedTest {
    private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private static MongodExecutable _mongodExe;
    @Spy
    protected MongoDbConnectionFactory connectionFactory = getMongoDbConnectionFactory();
    private MongodProcess _mongod;
    private MongoClient _mongo;

    @After
    public void afterTest() {
        for (DB db : _mongo.getUsedDatabases()) {
            db.dropDatabase();
        }
    }

    protected MongoDbConnectionFactory getMongoDbConnectionFactory() {
        ShoppingApiConfiguration config = new ShoppingApiConfiguration();
        config.mongo.port = 12345;
        return new MongoDbConnectionFactory(config);
    }

    @Before
    public void setUp() throws Exception {
        _mongodExe = starter.prepare(new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(12345, Network.localhostIsIPv6())).build());
        _mongod = _mongodExe.start();

        _mongo = new MongoClient("localhost", 12345);
    }

    @After
    public void tearDown() throws Exception {
        _mongod.stop();
        _mongodExe.stop();
    }

    public Mongo getMongo() {
        return _mongo;
    }
}
