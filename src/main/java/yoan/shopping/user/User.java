/**
 * 
 */
package yoan.shopping.user;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import yoan.shopping.infra.util.GenericBuilder;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * User of the application
 * @author yoan
 */
public class User {
	/** Default user ID */
	public static final UUID DEFAULT_ID = UUID.fromString("718f729c-c7ef-4f95-9f74-4a332cb82794");
	/** Default user instance */
	public static final User DEFAULT = Builder.createDefault().build();
	
	/** User unique ID */
	private final UUID id;
	/** User last name */
	private final String name;
	/** User email */
	private final String email;
	
	public User() {
		id = null;
		name = null;
		email = null;
	}
	
	protected User(UUID id, String name, String email) {
		this.id = Objects.requireNonNull(id, "User Id is mandatory");
		checkArgument(StringUtils.isNotBlank(name), "Invalid user name");
		this.name = name;
		checkArgument(StringUtils.isNotBlank(email), "Invalid user email");
		this.email = email;
	}
	
	public static class Builder implements GenericBuilder<User> {
		private UUID id = DEFAULT_ID;
		private String name = "Default name";
		private String email = "default@default.com";
		
		private Builder() { }
		
		/**
         * The default user is DEFAULT
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

            builder.id = otherBuilder.id;
            builder.name = otherBuilder.name;
            builder.email = otherBuilder.email;

            return builder;
        }
        
        /**
         * Get a builder based on an existing User instance
         *
         * @param user
         * @return builder
         */
        public static Builder createFrom(final User user) {
            Builder builder = new Builder();

            builder.id = user.id;
            builder.name = user.name;
            builder.email = user.email;
            
            return builder;
        }
        
        @Override
        public User build() {
            return new User(id, name, email);
        }
        
        public Builder withId(UUID id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        /**
         * Set a random user ID
         *
         * @return builder
         */
        public Builder withRandomId() {
            this.id = UUID.randomUUID();
            return this;
        }
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }
	}
	
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, email);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User that = (User) obj;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
        		&& Objects.equals(this.email, that.email);
    }
	
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this).add("id", id)
				   .add("name", name)
				   .add("email", email);
	   }

	   @Override
	   public final String toString() {
	     return toStringHelper().toString();
	   }
}
