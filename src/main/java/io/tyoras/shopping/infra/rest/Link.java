/**
 *
 */
package io.tyoras.shopping.infra.rest;

import com.google.common.base.MoreObjects;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Hypermedia link
 *
 * @author yoan
 */
public class Link {
    public static final String SELF_REL = "self";

    private final String href;
    private final String rel;

    public Link() {
        this.href = "href";
        this.rel = "rel";
    }

    public Link(String rel, String href) {
        this.href = requireNonNull(href);
        this.rel = requireNonNull(rel);
    }

    public Link(String rel, URI href) {
        requireNonNull(href);
        this.href = href.toString();
        this.rel = requireNonNull(rel);
    }

    public static Link self(String url) {
        return new Link(SELF_REL, url);
    }

    public static Link self(URI uri) {
        return new Link(SELF_REL, uri);
    }

    public static Link self(UriInfo uriInfo) {
        return new Link(SELF_REL, uriInfo.getAbsolutePath());
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, rel);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Link that = (Link) obj;
        return Objects.equals(this.href, that.href)
                && Objects.equals(this.rel, that.rel);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("href", href)
                .add("rel", rel)
                .toString();
    }
}
