package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Relationship;
import com.gf.yummify.presentation.dto.RelationshipResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

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

    void acceptFriendRequest(Authentication authentication, UUID relationshipId);

    void rejectFriendRequest(Authentication authentication, UUID relationshipId);

    void removeFollower(Authentication authentication, UUID relationshipId);

    Relationship findRelationshipById(UUID relationshipId);
}
