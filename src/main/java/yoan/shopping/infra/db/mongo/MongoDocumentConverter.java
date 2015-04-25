/**
 * 
 */
package yoan.shopping.infra.db.mongo;

import org.bson.Document;

/**
 * Converter between mongo document and POJO
 * @param <POJO> Type of the POJO
 * @author yoan
 */
public interface MongoDocumentConverter<POJO> {
	
	/**
	 * Convert a mongo document to a POJO
	 * @param doc : mongo document
	 * @return an instance of the POJO
	 */
	public POJO fromDocument(Document doc);
	
	/**
	 * Converte a POJO to a mongo document
	 * @param obj : POJO
	 * @return a mongo document
	 */
	public Document toDocument(POJO obj);
}
