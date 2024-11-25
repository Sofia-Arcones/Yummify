package com.gf.yummify.business.services;

import com.gf.yummify.presentation.dto.RelationshipResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface RelationshipService {
    void followOrUnfollow(Authentication authentication, String username);

    Boolean isFollowed(Authentication authentication, String username);

    Boolean isFriend(Authentication authentication, String username);

    Boolean isBlocked(Authentication authentication, String username);

    Boolean isPending(Authentication authentication, String username);

    void addOrChangeFriend(Authentication authentication, String username);

    List<RelationshipResponseDTO> findReceivedFriendRequests(Authentication authentication);

    List<RelationshipResponseDTO> findFriends(Authentication authentication);
    List<RelationshipResponseDTO> findFolloweds(Authentication authentication);
    List<RelationshipResponseDTO> findFollowers(Authentication authentication);
}
