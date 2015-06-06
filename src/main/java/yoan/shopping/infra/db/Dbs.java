package yoan.shopping.infra.db;

public enum Dbs {
	SHOPPING("shopping");
	
	private final String dbName;
	
	private Dbs(String dbName) {
		this.dbName = dbName;
	}
	
	public String getDbName() {
		return dbName;
	}
}
