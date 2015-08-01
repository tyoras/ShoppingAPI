package yoan.shopping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import yoan.shopping.infra.config.api.Config;
import yoan.shopping.infra.config.api.repository.ConfigRepository;
import yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesRepository;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.list.repository.mongo.ShoppingListMongoRepository;

import com.google.common.collect.ImmutableList;

public class Main {
	private static final Config configAppli;
	private static Random rand = new Random();
	private static final ImmutableList<String> SYLLABS = ImmutableList.<String>of("yo", "an", "ad", "ri", "en", "e", "mi", "li", "en");
	
	static {
		ConfigRepository configRepo = new ConfigPropertiesRepository();
		configAppli = configRepo.readConfig();
	}
	
	public static void main(String[] args) {
		MongoDbConnectionFactory fac = new MongoDbConnectionFactory(configAppli);
		ShoppingListMongoRepository repo = new ShoppingListMongoRepository(fac);
		ShoppingList create = generateRandomShoppingList();
		repo.create(create);
		
		ShoppingList read = repo.getById(create.getId());
		
		System.out.println(read);
	}
	
	public static ShoppingList generateRandomShoppingList() {
		int nbItem = generateRandomInt(1, 3);
		List<ShoppingItem> itemList = new ArrayList<>();
		for (int i = 0; i < nbItem; i++) {
			itemList.add(generateRandomShoppingItem());
		}
		return ShoppingList.Builder.createDefault()
								   .withRandomId()
								   .withName(generateRandomName())
								   .withItemList(itemList)
								   .withOwnerId(UUID.randomUUID())
								   .build();
	}
	
	public static String generateRandomName() {
		return generateString(generateRandomInt(1, 3));
	}
	
	public static int generateRandomInt(int min, int max) {
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	public static String generateString(int length) {
	    StringBuilder name = new StringBuilder();
	    for (int i = 0; i < length; i++) {
	    	name.append(SYLLABS.get(rand.nextInt(SYLLABS.size())));
	    }
	    return name.toString();
	}
	
	public static ShoppingItem generateRandomShoppingItem() {
		return ShoppingItem.Builder.createDefault()
								   .withRandomId()
								   .withName(generateRandomName())
								   .withQuantity(generateRandomInt(0, 10))
								   .build();
	}
}
