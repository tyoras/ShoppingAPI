/**
 * 
 */
package yoan.shopping.test;

import org.junit.After;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;

/**
 * Base class for unit tests backed by FongoDb
 * @author yoan
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class FongoBackedTest {
	private static final Fongo FONGO = new Fongo("unit test server");
	private final FongoDbConnectionFactory connectionFactory = new FongoDbConnectionFactory(FONGO);
	
	@Spy
	protected MongoDbConnectionFactory ConnectionFactory = getFongoDbConnectionFactory();
	
	@After
    public void afterTest() {
        for (DB db : FONGO.getUsedDatabases()) {
            db.dropDatabase();
        }
    }
	
	protected FongoDbConnectionFactory getFongoDbConnectionFactory() {
		return connectionFactory;
	}
}
