package io.tyoras.shopping.infra.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Basic representation with the minimum fields
 *
 * @author yoan
 */
public class RestRepresentation {
    /**
     * Rest navigation links
     */
    protected final List<Link> links;

    public RestRepresentation() {
        links = Lists.newArrayList();
    }

    public RestRepresentation(List<Link> links) {
        this.links = requireNonNull(links, "A rest representation should always have navigation links");
    }

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

}
