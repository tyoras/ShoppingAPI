/**
 *
 */
package io.tyoras.shopping.user;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import io.tyoras.shopping.infra.db.WithId;
import io.tyoras.shopping.infra.util.GenericBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static java.util.Objects.requireNonNull;

/**
 * User of the application
 *
 * @author yoan
 */
public class User implements Principal, Bson, WithId {
    /**
     * Default user ID
     */
    public static final UUID DEFAULT_ID = UUID.fromString("718f729c-c7ef-4f95-9f74-4a332cb82794");
    /**
     * Default user instance
     */
    public static final User DEFAULT = Builder.createDefault().build();

    /**
     * User unique ID
     */
    private final UUID id;
    /**
     * User last name
     */
    private final String name;
    /**
     * User email
     */
    private final String email;
    /**
     * Profile visibility
     **/
    private final ProfileVisibility profileVisibility;
    /**
     * user creation date
     */
    private final LocalDateTime creationDate;
    /**
     * Last time the user was updated
     */
    private final LocalDateTime lastUpdate;

    public User() {
        id = null;
        name = null;
        email = null;
        profileVisibility = null;
        creationDate = null;
        lastUpdate = null;
    }

    protected User(UUID id, String name, String email, ProfileVisibility profileVisibility, LocalDateTime creationDate, LocalDateTime lastUpdate) {
        this.id = requireNonNull(id, "User Id is mandatory");
        checkArgument(StringUtils.isNotBlank(name), "Invalid user name");
        this.name = name;
        checkArgument(StringUtils.isNotBlank(email), "Invalid user email");
        this.email = email;
        this.profileVisibility = requireNonNull(profileVisibility, "Profile visibility is mandatory");
        this.creationDate = requireNonNull(creationDate, "Creation date is mandatory");
        this.lastUpdate = requireNonNull(lastUpdate, "Last update date is mandatory");
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ProfileVisibility getProfileVisibility() {
        return profileVisibility;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, profileVisibility);
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
                && Objects.equals(this.email, that.email)
                && this.profileVisibility == that.profileVisibility;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("email", email)
                .add("profileVisibility", profileVisibility)
                .add("created", creationDate)
                .add("lastUpdate", lastUpdate);
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
        return new BsonDocumentWrapper<User>(this, codecRegistry.get(User.class));
    }

    public static class Builder implements GenericBuilder<User> {
        private UUID id = DEFAULT_ID;
        private String name = "Default name";
        private String email = "default@default.com";
        private ProfileVisibility profileVisibility = PUBLIC;
        private LocalDateTime creationDate = LocalDateTime.now();
        private LocalDateTime lastUpdate = LocalDateTime.now();

        private Builder() {
        }

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
            builder.profileVisibility = otherBuilder.profileVisibility;
            builder.creationDate = otherBuilder.creationDate;
            builder.lastUpdate = otherBuilder.lastUpdate;

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
            builder.profileVisibility = user.profileVisibility;
            builder.creationDate = user.creationDate;
            builder.lastUpdate = user.lastUpdate;

            return builder;
        }

        @Override
        public User build() {
            return new User(id, name, email, profileVisibility, creationDate, lastUpdate);
        }

        public Builder withId(UUID id) {
            this.id = id;
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

        public Builder withProfileVisibility(ProfileVisibility profileVisibility) {
            this.profileVisibility = profileVisibility;
            return this;
        }

        public Builder withCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder withLastUpdate(LocalDateTime lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }
    }
}
