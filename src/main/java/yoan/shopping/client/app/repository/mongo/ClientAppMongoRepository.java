package yoan.shopping.client.app.repository.mongo;

import static yoan.shopping.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_CREATION_CLIENT_APP;
import static yoan.shopping.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_DELETE_CLIENT_APP;
import static yoan.shopping.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_READ_CLIENT_APP;
import static yoan.shopping.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_UPDATE_CLIENT_APP;
import static yoan.shopping.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_UPDATE_CLIENT_APP_SECRET;
import static yoan.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;

import java.util.UUID;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.client.app.ClientApp;
import yoan.shopping.client.app.repository.ClientAppRepository;
import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.infra.util.helper.MongoRepositoryHelper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * Mongo implementation of the client application repository
 * @author yoan
 */
@Singleton
public class ClientAppMongoRepository extends ClientAppRepository {

	public static final String CLIENT_APP_COLLECTION = "clientApps";
	
	private final MongoCollection<ClientApp> clientAppCollection;
	private final ClientAppMongoConverter appConverter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientAppMongoRepository.class);
	
	@Inject
	public ClientAppMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		clientAppCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING, CLIENT_APP_COLLECTION, ClientApp.class);
		appConverter = new ClientAppMongoConverter();
	}
	
	@Override
	protected void processCreate(ClientApp appToCreate) {
		try {
			clientAppCollection.insertOne(appToCreate);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_CLIENT_APP);
		}
	}

	@Override
	protected ClientApp processGetById(UUID clientAppId) {
		Bson filter = Filters.eq(FIELD_ID, clientAppId);
		ClientApp foundApp = null;
		try {
			foundApp = clientAppCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_CLIENT_APP);
		}
		return foundApp;
	}
	
	@Override
	protected void processUpdate(ClientApp clientApp) {
		Bson filter = Filters.eq(FIELD_ID, clientApp.getId());
		Bson update = ClientAppMongoConverter.getClientAppUpdate(clientApp);
		try {
			clientAppCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_CLIENT_APP);
		}
	}

	@Override
	protected void processDeleteById(UUID listId) {
		Bson filter = Filters.eq(FIELD_ID, listId);
		try {
			clientAppCollection.deleteOne(filter);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_CLIENT_APP);
		}
	}

	@Override
	protected void processChangeSecret(ClientApp clientAppToUpdate) {
		Bson filter = Filters.eq(FIELD_ID, clientAppToUpdate.getId());
		Bson update = appConverter.getChangeSecretUpdate(clientAppToUpdate);
		try {
			clientAppCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_CLIENT_APP_SECRET);
		}
	}
}