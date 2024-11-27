package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.RelationshipMapper;
import com.gf.yummify.data.entity.Relationship;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import com.gf.yummify.data.enums.RelationshipStatus;
import com.gf.yummify.data.enums.RelationshipType;
import com.gf.yummify.data.repository.RelationshipRepository;
import com.gf.yummify.presentation.dto.ActivityLogRequestDTO;
import com.gf.yummify.presentation.dto.RelationshipRequestDTO;
import com.gf.yummify.presentation.dto.RelationshipResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    private RelationshipRepository relationshipRepository;
    private UserService userService;
    private RelationshipMapper relationshipMapper;
    private ActivityLogService activityLogService;

    public RelationshipServiceImpl(RelationshipRepository relationshipRepository, UserService userService, RelationshipMapper relationshipMapper, ActivityLogService activityLogService) {
        this.relationshipRepository = relationshipRepository;
        this.userService = userService;
        this.relationshipMapper = relationshipMapper;
        this.activityLogService = activityLogService;
    }

    @Override
    public void acceptFriendRequest(Authentication authentication, UUID relationshipId) {
        User user = userService.findUserByUsername(authentication.getName());
        Relationship relationship = findRelationshipById(relationshipId);
        relationship.setRelationshipStatus(RelationshipStatus.ACCEPTED);
        relationshipRepository.save(relationship);
        Relationship relationshipInverse = findRelationshipByType(user, relationship.getSender(), RelationshipType.FRIEND);
        if (relationshipInverse != null) {
            relationshipInverse.setRelationshipStatus(RelationshipStatus.ACCEPTED);
            relationshipRepository.save(relationshipInverse);
        } else {
            RelationshipRequestDTO reverseRelationshipDTO = new RelationshipRequestDTO(user, relationship.getSender(), RelationshipType.FRIEND, RelationshipStatus.ACCEPTED);
            relationshipRepository.save(relationshipMapper.toRelationship(reverseRelationshipDTO));
        }
        String description = "El usuario '" + user.getUsername() + "' ha aceptado la relación de amistad con el usuario '" + relationship.getSender().getUsername() + "' (ID: " + relationship.getSender().getUserId() + ")";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, relationship.getSender().getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_ACCEPTED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
    }

    @Override
    public void removeFollower(Authentication authentication, UUID relationshipId) {
        User user = userService.findUserByUsername(authentication.getName());
        Relationship relationship = findRelationshipById(relationshipId);
        relationship.setRelationshipStatus(RelationshipStatus.UNFOLLOWED);
        relationshipRepository.save(relationship);
        String description = "El usuario '" + user.getUsername() + "' ha retirado el follow al usuario '" + relationship.getSender().getUsername() + "' (ID: " + relationship.getSender().getUserId() + ")";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, relationship.getSender().getUserId(), RelatedEntity.USER, ActivityType.FOLLOW_RETIRED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
    }

    @Override
    public void rejectFriendRequest(Authentication authentication, UUID relationshipId) {
        User user = userService.findUserByUsername(authentication.getName());
        Relationship relationship = findRelationshipById(relationshipId);
        relationship.setRelationshipStatus(RelationshipStatus.REJECTED);
        relationshipRepository.save(relationship);
        String description = "El usuario '" + user.getUsername() + "' ha rechazado la relación de amistad con el usuario '" + relationship.getSender().getUsername() + "' (ID: " + relationship.getSender().getUserId() + ")";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO(user, relationship.getSender().getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_REJECTED, description);
        activityLogService.createActivityLog(activityLogRequestDTO);
    }

    @Override
    public Relationship findRelationshipById(UUID relationshipId) {
        return relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new NoSuchElementException("La relación con id: " + relationshipId + " no encontrada"));
    }

    @Override
    public List<RelationshipResponseDTO> findReceivedFriendRequests(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<Relationship> friendRequestsList = relationshipRepository.findByReceiverAndRelationshipStatusAndRelationshipType(user, RelationshipStatus.PENDING, RelationshipType.FRIEND);
        List<RelationshipResponseDTO> relationshipResponseDTOS = new ArrayList<>();
        for (Relationship friendRequest : friendRequestsList) {
            RelationshipResponseDTO relationshipResponseDTO = new RelationshipResponseDTO(friendRequest.getRelationshipId(), friendRequest.getSender().getAvatar(), friendRequest.getSender().getUsername());
            relationshipResponseDTOS.add(relationshipResponseDTO);
        }
        return relationshipResponseDTOS;
    }

    @Override
    public List<RelationshipResponseDTO> findFriends(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<Relationship> friendsList = relationshipRepository.findByReceiverAndRelationshipStatusAndRelationshipType(user, RelationshipStatus.ACCEPTED, RelationshipType.FRIEND);
        List<RelationshipResponseDTO> relationshipResponseDTOS = new ArrayList<>();
        for (Relationship friend : friendsList) {
            RelationshipResponseDTO relationshipResponseDTO = new RelationshipResponseDTO(friend.getRelationshipId(), friend.getSender().getAvatar(), friend.getSender().getUsername());
            relationshipResponseDTOS.add(relationshipResponseDTO);
        }
        return relationshipResponseDTOS;
    }

    @Override
    public List<RelationshipResponseDTO> findFolloweds(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<Relationship> followedList = relationshipRepository.findBySenderAndRelationshipStatusAndRelationshipType(user, RelationshipStatus.FOLLOWING, RelationshipType.FOLLOW);
        List<RelationshipResponseDTO> relationshipResponseDTOS = new ArrayList<>();
        for (Relationship followed : followedList) {
            RelationshipResponseDTO relationshipResponseDTO = new RelationshipResponseDTO(followed.getRelationshipId(), followed.getReceiver().getAvatar(), followed.getReceiver().getUsername());
            relationshipResponseDTOS.add(relationshipResponseDTO);
        }
        return relationshipResponseDTOS;
    }

    @Override
    public List<RelationshipResponseDTO> findFollowers(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<Relationship> followersList = relationshipRepository.findByReceiverAndRelationshipStatusAndRelationshipType(user, RelationshipStatus.FOLLOWING, RelationshipType.FOLLOW);
        List<RelationshipResponseDTO> relationshipResponseDTOS = new ArrayList<>();
        for (Relationship follower : followersList) {
            RelationshipResponseDTO relationshipResponseDTO = new RelationshipResponseDTO(follower.getRelationshipId(), follower.getSender().getAvatar(), follower.getSender().getUsername());
            relationshipResponseDTOS.add(relationshipResponseDTO);
        }
        return relationshipResponseDTOS;
    }


    @Override
    public void followOrUnfollow(Authentication authentication, String username) {
        User sender = userService.findUserByUsername(authentication.getName());
        User receiver = userService.findUserByUsername(username);
        validateRequest(sender, receiver);
        Relationship relationship = findRelationshipByType(sender, receiver, RelationshipType.FOLLOW);
        String description = "";
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO();
        if (relationship != null) {
            if (relationship.getRelationshipStatus() == RelationshipStatus.FOLLOWING) {
                description = "El usuario '" + sender.getUsername() + "' ha dejado de seguir al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.UNFOLLOWED, description);
                relationship.setRelationshipStatus(RelationshipStatus.UNFOLLOWED);
                relationshipRepository.save(relationship);
            } else if (relationship.getRelationshipStatus() == RelationshipStatus.UNFOLLOWED) {
                description = "El usuario '" + sender.getUsername() + "' ha seguido al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FOLLOWED, description);
                relationship.setRelationshipStatus(RelationshipStatus.FOLLOWING);
                relationshipRepository.save(relationship);
            }
        } else {
            RelationshipRequestDTO relationshipRequestDTO = new RelationshipRequestDTO(sender, receiver, RelationshipType.FOLLOW, RelationshipStatus.FOLLOWING);
            relationshipRepository.save(relationshipMapper.toRelationship(relationshipRequestDTO));
            description = "El usuario '" + sender.getUsername() + "' ha seguido al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
            activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FOLLOWED, description);
        }
        activityLogService.createActivityLog(activityLogRequestDTO);
    }

    @Override
    public void addOrChangeFriend(Authentication authentication, String username) {
        User sender = userService.findUserByUsername(authentication.getName());
        User receiver = userService.findUserByUsername(username);
        validateRequest(sender, receiver);

        Relationship relationship = findRelationshipByType(sender, receiver, RelationshipType.FRIEND);
        Relationship relationshipInverse = findRelationshipByType(receiver, sender, RelationshipType.FRIEND);

        String description;
        ActivityLogRequestDTO activityLogRequestDTO = new ActivityLogRequestDTO();

        if (relationship != null && relationshipInverse != null) {
            if (relationship.getRelationshipStatus() == RelationshipStatus.UNFRIENDED &&
                    relationshipInverse.getRelationshipStatus() == RelationshipStatus.UNFRIENDED) {
                relationship.setRelationshipStatus(RelationshipStatus.PENDING);
                description = "El usuario '" + sender.getUsername() + "' ha solicitado amistad al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_REQUESTED, description);
                relationshipRepository.save(relationship);
            } else if (relationship.getRelationshipStatus() == RelationshipStatus.ACCEPTED &&
                    relationshipInverse.getRelationshipStatus() == RelationshipStatus.ACCEPTED) {
                relationship.setRelationshipStatus(RelationshipStatus.UNFRIENDED);
                relationshipInverse.setRelationshipStatus(RelationshipStatus.UNFRIENDED);
                description = "El usuario '" + sender.getUsername() + "' ha dejado de ser amigo del usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_ENDED, description);
                relationshipRepository.save(relationship);
                relationshipRepository.save(relationshipInverse);
            } else if (relationship.getRelationshipStatus() == RelationshipStatus.PENDING) {
                relationship.setRelationshipStatus(RelationshipStatus.REJECTED);
                description = "El usuario '" + sender.getUsername() + "' ha retirado la solicitud de amistad al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_RETIRED, description);
                relationshipRepository.save(relationship);
            } else if (relationship.getRelationshipStatus() == RelationshipStatus.REJECTED) {
                relationship.setRelationshipStatus(RelationshipStatus.PENDING);
                description = "El usuario '" + sender.getUsername() + "' ha solicitado amistad al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_REQUESTED, description);
                relationshipRepository.save(relationship);
            }
        } else if (relationship != null) {
            if (relationship.getRelationshipStatus() == RelationshipStatus.REJECTED) {
                relationship.setRelationshipStatus(RelationshipStatus.PENDING);
                description = "El usuario '" + sender.getUsername() + "' ha solicitado amistad al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_REQUESTED, description);
                relationshipRepository.save(relationship);
            } else if (relationship.getRelationshipStatus() == RelationshipStatus.PENDING) {
                relationship.setRelationshipStatus(RelationshipStatus.REJECTED);
                description = "El usuario '" + sender.getUsername() + "' ha retirado la solicitud de amistad al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
                activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_RETIRED, description);
                relationshipRepository.save(relationship);
            }
        } else {
            RelationshipRequestDTO relationshipDTO = new RelationshipRequestDTO(sender, receiver, RelationshipType.FRIEND, RelationshipStatus.PENDING);
            relationshipRepository.save(relationshipMapper.toRelationship(relationshipDTO));
            description = "El usuario '" + sender.getUsername() + "' ha solicitado amistad al usuario '" + receiver.getUsername() + "' (ID: " + receiver.getUserId() + ")";
            activityLogRequestDTO = new ActivityLogRequestDTO(sender, receiver.getUserId(), RelatedEntity.USER, ActivityType.FRIENDSHIP_REQUESTED, description);
        }
        activityLogService.createActivityLog(activityLogRequestDTO);
    }


    @Override
    public int followersNumber(String username) {
        User user = userService.findUserByUsername(username);
        List<Relationship> followersList = relationshipRepository.findByReceiverAndRelationshipStatusAndRelationshipType(user, RelationshipStatus.FOLLOWING, RelationshipType.FOLLOW);
        return followersList.size();
    }

    @Override
    public int friendsNumber(String username) {
        User user = userService.findUserByUsername(username);
        List<Relationship> friendsList = relationshipRepository.findByReceiverAndRelationshipStatusAndRelationshipType(user, RelationshipStatus.ACCEPTED, RelationshipType.FRIEND);
        return friendsList.size();
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

    private Boolean hasValidRelationship(User sender, User receiver, RelationshipType type, RelationshipStatus
            status) {
        Optional<Relationship> relationship = relationshipRepository.findBySenderAndReceiverAndRelationshipType(sender, receiver, type);
        if (relationship.isPresent() && relationship.get().getRelationshipStatus() == status) {
            return true;
        }
        return false;
    }

    private Relationship findRelationshipByType(User sender, User receiver, RelationshipType relationshipType) {
        Optional<Relationship> optionalRelationship = relationshipRepository.findBySenderAndReceiverAndRelationshipType(sender, receiver, relationshipType);
        if (optionalRelationship.isPresent()) {
            return optionalRelationship.get();
        }
        return null;
    }

    private Relationship findRelathionshipByStatus(User sender, User receiver, RelationshipStatus
            relationshipStatus) {
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
