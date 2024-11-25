package com.gf.yummify.business.services;

import org.springframework.security.core.Authentication;

public interface RelationshipService {
    void followOrUnfollow(Authentication authentication, String username);

    Boolean isFollowed(Authentication authentication, String username);

    Boolean isFriend(Authentication authentication, String username);

    Boolean isBlocked(Authentication authentication, String username);

    Boolean isPending(Authentication authentication, String username);

    void addOrChangeFriend(Authentication authentication, String username);
}
