package yoan.shopping.client.app.repository.mongo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;

import yoan.shopping.client.app.ClientApp;
import yoan.shopping.infra.db.mongo.MongoDocumentConverter;
import yoan.shopping.infra.util.helper.DateHelper;

/**
 * MongoDb codec to convert client app to BSON
 * @author yoan
 */
public class ClientAppMongoConverter extends MongoDocumentConverter<ClientApp> {
	public static final String FIELD_NAME = "name";
	public static final String FIELD_OWNER_ID = "ownerId";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";
    public static final String FIELD_SECRET = "secret";
    public static final String FIELD_SALT = "salt";
    
    public ClientAppMongoConverter() {
		super();
	}
	
    public ClientAppMongoConverter(Codec<Document> codec) {
		super(codec);
	}
    
    @Override
	public ClientApp fromDocument(Document doc) {
		if (doc == null) {
			return null;
		}
		
        UUID id = doc.get(FIELD_ID, UUID.class);
        String name = doc.getString(FIELD_NAME);
        UUID ownerId = doc.get(FIELD_OWNER_ID, UUID.class);
        Date created = doc.getDate(FIELD_CREATED);
        LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
        Date lastUpdated = doc.getDate(FIELD_LAST_UPDATE);
        LocalDateTime lastUpdate = DateHelper.toLocalDateTime(lastUpdated);
        String secret = doc.getString(FIELD_SECRET);
        Object salt = doc.get(FIELD_SALT);
        
        return ClientApp.Builder.createDefault()
        				   .withId(id)
        				   .withCreationDate(creationDate)
        				   .withLastUpdate(lastUpdate)
        				   .withName(name)
        				   .withOwnerId(ownerId)
        				   .withSecret(secret)
        				   .withSalt(salt)
        				   .build();
	}

	@Override
	public Document toDocument(ClientApp app) {
		if (app == null) {
			return new Document();
		}
		
		return new Document(FIELD_ID, app.getId())
				.append(FIELD_NAME, app.getName())
				.append(FIELD_OWNER_ID, app.getOwnerId())
				.append(FIELD_CREATED, DateHelper.toDate(app.getCreationDate()))
				.append(FIELD_LAST_UPDATE, DateHelper.toDate(app.getLastUpdate()))
				.append(FIELD_SECRET, app.getSecret())
				.append(FIELD_SALT, app.getSalt());
	}
    
    @Override
	public Class<ClientApp> getEncoderClass() {
		return ClientApp.class;
	}
	
	@Override
	public ClientApp generateIdIfAbsentFromDocument(ClientApp app) {
		return documentHasId(app) ? app : ClientApp.Builder.createFrom(app).withRandomId().build();
	}
	
	public Document getChangeSecretUpdate(ClientApp appToUpdate) {
		Document updateDoc = new Document(FIELD_LAST_UPDATE, DateHelper.toDate(appToUpdate.getLastUpdate()))
									.append(FIELD_SECRET, appToUpdate.getSecret())
									.append(FIELD_SALT, appToUpdate.getSalt());
		return new Document("$set", updateDoc);
	}
}
