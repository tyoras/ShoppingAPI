/**
 *
 */
package io.tyoras.shopping.root.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestRepresentation;
import io.tyoras.shopping.root.BuildInfo;
import io.tyoras.shopping.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Rest Root representation
 *
 * @author yoan
 */
public class RootRepresentation extends RestRepresentation {
    /**
     * Current build version
     */
    private String version;
    /**
     * Maven build date
     */
    private LocalDateTime buildDate;
    /**
     * Connected user Id
     */
    private UUID connectedUserId;

    public RootRepresentation() {
        super();
        this.version = "version";
        this.buildDate = LocalDateTime.now();
        this.connectedUserId = User.DEFAULT_ID;
    }

    public RootRepresentation(BuildInfo buildInfo, UUID connectedUserId, List<Link> links) {
        super(links);
        if (buildInfo != null) {
            this.version = buildInfo.getVersion();
            this.buildDate = buildInfo.getBuildDate();
        }
        this.connectedUserId = connectedUserId;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("buildDate")
    public LocalDateTime getBuildDate() {
        return buildDate;
    }

    @JsonProperty("connectedUserId")
    public UUID getConnectedUserId() {
        return connectedUserId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), version, buildDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RootRepresentation that = (RootRepresentation) obj;
        return Objects.equals(this.version, that.version)
                && Objects.equals(this.buildDate, that.buildDate)
                && Objects.equals(this.connectedUserId, that.connectedUserId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("version", version)
                .add("buildDate", buildDate)
                .add("connectedUserId", connectedUserId)
                .toString();
    }
}
