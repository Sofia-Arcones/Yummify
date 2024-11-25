package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.RelationshipMapper;
import com.gf.yummify.data.entity.Relationship;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.RelationshipStatus;
import com.gf.yummify.data.enums.RelationshipType;
import com.gf.yummify.data.repository.RelationshipRepository;
import com.gf.yummify.presentation.dto.RelationshipRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    private RelationshipRepository relationshipRepository;
    private UserService userService;
    private RelationshipMapper relationshipMapper;

    public RelationshipServiceImpl(RelationshipRepository relationshipRepository, UserService userService, RelationshipMapper relationshipMapper) {
        this.relationshipRepository = relationshipRepository;
        this.userService = userService;
        this.relationshipMapper = relationshipMapper;
    }

    @Override
    public void followOrUnfollow(Authentication authentication, String username) {
        User sender = userService.findUserByUsername(authentication.getName());
        User receiver = userService.findUserByUsername(username);
        validateRequest(sender, receiver);
        Relationship relationship = findRelathionshipByType(sender, receiver, RelationshipType.FOLLOW);
        if (relationship != null) {
            if (relationship.getRelationshipStatus() == RelationshipStatus.FOLLOWING) {
                relationship.setRelationshipStatus(RelationshipStatus.UNFOLLOWED);
                relationshipRepository.save(relationship);
            } else if (relationship.getRelationshipStatus() == RelationshipStatus.UNFOLLOWED) {
                relationship.setRelationshipStatus(RelationshipStatus.FOLLOWING);
                relationshipRepository.save(relationship);
            }
        } else {
            RelationshipRequestDTO relationshipRequestDTO = new RelationshipRequestDTO(sender, receiver, RelationshipType.FOLLOW, RelationshipStatus.FOLLOWING);
            relationshipRepository.save(relationshipMapper.toRelationship(relationshipRequestDTO));
        }
    }

    @Override
    public Boolean isFriend(Authentication authentication, String username) {
        User user = userService.findUserByUsername(authentication.getName());
        User requestedUser = userService.findUserByUsername(username);
        Boolean isFriend = hasValidRelationship(user, requestedUser, RelationshipType.FRIEND, RelationshipStatus.ACCEPTED);
        return isFriend != null ? isFriend : false;
    }

    @Override
    public Boolean isPending(Authentication authentication, String username) {
        User user = userService.findUserByUsername(authentication.getName());
        User requestedUser = userService.findUserByUsername(username);
        Boolean isPending = hasValidRelationship(user, requestedUser, RelationshipType.FRIEND, RelationshipStatus.PENDING);
        return isPending != null ? isPending : false;
    }

    @Override
    public Boolean isBlocked(Authentication authentication, String username) {
        User user = userService.findUserByUsername(authentication.getName());
        User requestedUser = userService.findUserByUsername(username);
        Boolean isBlocked = hasValidRelationship(user, requestedUser, RelationshipType.FRIEND, RelationshipStatus.BLOCKED);
        return isBlocked != null ? isBlocked : false;
    }

    @Override
    public Boolean isFollowed(Authentication authentication, String username) {
        User user = userService.findUserByUsername(authentication.getName());
        User requestedUser = userService.findUserByUsername(username);
        Boolean isFollowed = hasValidRelationship(user, requestedUser, RelationshipType.FOLLOW, RelationshipStatus.FOLLOWING);
        return isFollowed != null ? isFollowed : false;
    }


    private Boolean hasValidRelationship(User sender, User receiver, RelationshipType type, RelationshipStatus status) {
        Optional<Relationship> relationship = relationshipRepository.findBySenderAndReceiverAndRelationshipType(sender, receiver, type);
        if (relationship.isPresent() && relationship.get().getRelationshipStatus() == status) {
            return true;
        }
        return false;
    }

    private Relationship findRelathionshipByType(User sender, User receiver, RelationshipType relationshipType) {
        Optional<Relationship> optionalRelationship = relationshipRepository.findBySenderAndReceiverAndRelationshipType(sender, receiver, relationshipType);
        if (optionalRelationship.isPresent()) {
            return optionalRelationship.get();
        }
        return null;
    }

    private Relationship findRelathionshipByStatus(User sender, User receiver, RelationshipStatus relationshipStatus) {
        Optional<Relationship> optionalRelationship = relationshipRepository.findBySenderAndReceiverAndRelationshipStatus(sender, receiver, relationshipStatus);
        if (optionalRelationship.isPresent()) {
            return optionalRelationship.get();
        }
        return null;
    }

    private void validateRequest(User sender, User receiver) {
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (sender == receiver) {
            throw new IllegalArgumentException("No puedes mandarte una petición a ti mismo");
        }
        Relationship relationship = findRelathionshipByStatus(sender, receiver, RelationshipStatus.BLOCKED);
        if (relationship != null) {
            throw new IllegalArgumentException("No puedes mandar una petición a este usuario");
        }
        relationship = findRelathionshipByStatus(receiver, sender, RelationshipStatus.BLOCKED);
        if (relationship != null) {
            throw new IllegalArgumentException("No puedes mandar una petición a un usuario que has bloqueado");
        }
    }
}
