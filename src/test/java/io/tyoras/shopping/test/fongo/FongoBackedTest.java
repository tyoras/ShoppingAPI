/**
 *
 */
package io.tyoras.shopping.test.fongo;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import io.tyoras.shopping.infra.db.mongo.MongoDbConnectionFactory;
import org.junit.After;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Base class for unit tests backed by FongoDb
 *
 * @author yoan
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class FongoBackedTest {
    protected static final Fongo FONGO = new Fongo("unit test server");
    private final FongoDbConnectionFactory fongoConnectionFactory = new FongoDbConnectionFactory(FONGO);

    @Spy
    protected MongoDbConnectionFactory connectionFactory = getFongoDbConnectionFactory();

    @After
    public void afterTest() {
        for (DB db : FONGO.getUsedDatabases()) {
            db.dropDatabase();
        }
    }

    protected FongoDbConnectionFactory getFongoDbConnectionFactory() {
        return fongoConnectionFactory;
    }
}
