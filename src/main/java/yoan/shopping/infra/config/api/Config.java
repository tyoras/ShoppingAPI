package yoan.shopping.infra.config.api;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.apache.commons.lang3.StringUtils;

import yoan.shopping.infra.util.GenericBuilder;

/**
 * Application configuration
 * @author yoan
 */
public class Config {
	
	private final String apiHost;
	private final Integer apiPort;
	
	private final String mongoHost;
	private final Integer mongoPort;
	private final String mongoUser;
	private final String mongoPass;
	
	private final String swaggerBasePath;

	protected Config(String apiHost, Integer apiPort, String mongoHost, Integer mongoPort, String mongoUser, String mongoPass, String swaggerBasePath) {
		checkArgument(StringUtils.isNotBlank(apiHost), "API host is mandatory");
		this.apiHost = apiHost;
		this.apiPort = requireNonNull(apiPort, "API port is mandatory");
		checkArgument(StringUtils.isNotBlank(mongoHost), "Mongo host is mandatory");
		this.mongoHost = mongoHost;
		this.mongoPort = requireNonNull(mongoPort);
		this.mongoUser = mongoUser;
		this.mongoPass = mongoPass;
		checkArgument(StringUtils.isNotBlank(swaggerBasePath), "Swagger base path is mandatory");
		this.swaggerBasePath = swaggerBasePath;
	}

	public static class Builder implements GenericBuilder<Config> {
		private String apiHost = "localhost";
		private Integer apiPort = 8080;
		private String mongoHost = "localhost";
		private Integer mongoPort = 27017;
		private String mongoUser = null;
		private String mongoPass = null;
		private String swaggerBasePath = "/shopping/api";
		
		private Builder() { }
		
		/**
         * The default Config
         *
         * @return DEFAULT User
         */
        public static Builder createDefault() {
            return new Builder();
        }
        
        /**
         * Duplicate an existing builder
         *
         * @param otherBuilder
         * @return builder
         */
        public static Builder createFrom(final Builder otherBuilder) {
            Builder builder = new Builder();

            builder.apiHost = otherBuilder.apiHost;
            builder.apiPort = otherBuilder.apiPort;
            builder.mongoHost = otherBuilder.mongoHost;
            builder.mongoPort = otherBuilder.mongoPort;
            builder.mongoUser = otherBuilder.mongoUser;
            builder.mongoPass = otherBuilder.mongoPass;
            builder.swaggerBasePath = otherBuilder.swaggerBasePath;

            return builder;
        }
        
        /**
         * Get a builder based on an existing User instance
         *
         * @param user
         * @return builder
         */
        public static Builder createFrom(final Config config) {
            return createDefault()
	        		.withApiHost(config.apiHost)
	            	.withApiPort(config.apiPort)
	            	.withMongoHost(config.mongoHost)
	            	.withMongoPass(config.mongoPass)
	            	.withMongoPort(config.mongoPort)
	            	.withMongoUser(config.mongoUser)
	            	.withSwaggerBasePath(config.swaggerBasePath);
        }
        
        @Override
        public Config build() {
            return new Config(apiHost, apiPort, mongoHost, mongoPort, mongoUser, mongoPass, swaggerBasePath);
        }
        
		public Builder withApiHost(String apiHost) {
			this.apiHost = apiHost;
			return this;
		}

		public Builder withApiPort(Integer apiPort) {
			this.apiPort = apiPort;
			return this;
		}

		public Builder withMongoHost(String mongoHost) {
			this.mongoHost = mongoHost;
			return this;
		}

		public Builder withMongoPort(Integer mongoPort) {
			this.mongoPort = mongoPort;
			return this;
		}

		public Builder withMongoUser(String mongoUser) {
			this.mongoUser = mongoUser;
			return this;
		}

		public Builder withMongoPass(String mongoPass) {
			this.mongoPass = mongoPass;
			return this;
		}

		public Builder withSwaggerBasePath(String swaggerBasePath) {
			this.swaggerBasePath = swaggerBasePath;
			return this;
		}

	}
	
	public String getApiHost() {
		return apiHost;
	}

	public Integer getApiPort() {
		return apiPort;
	}

	public String getMongoHost() {
		return mongoHost;
	}

	public Integer getMongoPort() {
		return mongoPort;
	}

	public String getMongoUser() {
		return mongoUser;
	}

	public String getMongoPass() {
		return mongoPass;
	}

	public String getSwaggerBasePath() {
		return swaggerBasePath;
	}
}
