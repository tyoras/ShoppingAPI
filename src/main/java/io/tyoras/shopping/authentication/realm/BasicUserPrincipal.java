package io.tyoras.shopping.authentication.realm;

import io.tyoras.shopping.user.User;

import java.security.Principal;
import java.util.UUID;

public final class BasicUserPrincipal implements Principal {

    private final UUID userId;

    public BasicUserPrincipal(User user) {
        userId = user.getId();
    }

    @Override
    public String getName() {
        return userId.toString();
    }

    public UUID getUserId() {
        return userId;
    }


}
