package com.store.elara.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.store.elara.entities.Permissions.*;

@RequiredArgsConstructor
public enum Roles {
    USER(
        Set.of(
                USER_READ,
                USER_UPDATE
        )
    ),
    ADMIN(
        Set.of(
                ADMIN_CREATE,
                ADMIN_DELETE,
                ADMIN_UPDATE,
                ADMIN_READ
        )
    );
    @Getter
    private final Set<Permissions> permissions;
}
