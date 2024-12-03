package com.gf.yummify.business.services;

import com.gf.yummify.business.mappers.NotificationMapper;
import com.gf.yummify.data.entity.*;
import com.gf.yummify.data.enums.Role;
import com.gf.yummify.data.repository.NotificationRepository;
import com.gf.yummify.presentation.dto.NotificationRequestDTO;
import com.gf.yummify.presentation.dto.NotificationResponseDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;
    private UserService userService;
    private NotificationMapper notificationMapper;
    private ChallengeService challengeService;
    private CommentService commentService;
    private IngredientService ingredientService;
    private RatingService ratingService;
    private RelationshipService relationshipService;
    private RecipeService recipeService;
    private ChallengeParticipationService challengeParticipationService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, @Lazy UserService userService, NotificationMapper notificationMapper, @Lazy ChallengeService challengeService, @Lazy CommentService commentService, @Lazy IngredientService ingredientService, @Lazy RatingService ratingService, @Lazy RelationshipService relationshipService, @Lazy RecipeService recipeService, @Lazy ChallengeParticipationService challengeParticipationService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.notificationMapper = notificationMapper;
        this.challengeService = challengeService;
        this.commentService = commentService;
        this.ingredientService = ingredientService;
        this.ratingService = ratingService;
        this.relationshipService = relationshipService;
        this.recipeService = recipeService;
        this.challengeParticipationService = challengeParticipationService;
    }

    @Override
    public void sendNotifications(ActivityLog activityLog) {
        List<Notification> notificationList = buildNotifications(activityLog);
        for (Notification notification : notificationList) {
            notificationRepository.save(notification);
        }
    }

    @Override
    public Page<NotificationResponseDTO> getNotificationFromLastMonth(Authentication authentication, int page, int size) {
        User user = userService.findUserByUsername(authentication.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.MONTHS);

        Page<Notification> notificationPage = notificationRepository.findByUserAndCreationDateBetween(user, startDate, endDate, pageable);
        List<NotificationResponseDTO> notificationResponseDTOS = notificationPage
                .stream()
                .map(notificationMapper::toNotificationResponseDTO)
                .toList();
        return new PageImpl<>(notificationResponseDTOS, pageable, notificationPage.getTotalElements());
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("La notificación con id: " + notificationId + " no existe"));
        if (notification.getIsRead()) {
            notification.setIsRead(false);
        } else {
            notification.setIsRead(true);
        }
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        List<Notification> notificationList = notificationRepository.findByUserAndIsRead(user, false);
        for (Notification notification : notificationList) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public long countUnreadNotifications(String username) {
        User user = userService.findUserByUsername(username);
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    private List<Notification> buildNotifications(ActivityLog activityLog) {
        if (activityLog == null) {
            throw new IllegalArgumentException("No se ha referenciado ninguna actividad");
        }
        List<Notification> notificationList = new ArrayList<>();
        String content;

        User user;
        List<User> userList;
        Comment comment;
        Ingredient ingredient;
        Rating rating;
        Challenge challenge;
        Recipe recipe;
        ChallengeParticipation challengeParticipation;
        switch (activityLog.getActivityType()) {
            case FRIENDSHIP_REQUESTED:
                user = findRelatedUser(activityLog);
                content = "Has recibido una solicitud de amistad de " + activityLog.getUser().getUserId() + ".";
                createNotification(user, content, activityLog, notificationList);
                break;
            case FRIENDSHIP_ACCEPTED:
                user = findRelatedUser(activityLog);
                content = user.getUsername() + " ha aceptado tu solicitud de amistad.";
                createNotification(activityLog.getUser(), content, activityLog, notificationList);
                break;
            case FRIENDSHIP_BLOCKED:
                user = findRelatedUser(activityLog);
                content = "Has bloqueado al usuario '" + user.getUsername() + "'.";
                createNotification(activityLog.getUser(), content, activityLog, notificationList);
                break;
            case FRIENDSHIP_REJECTED:
                user = findRelatedUser(activityLog);
                content = user.getUsername() + " ha rechazado tu solicitud de amistad.";
                createNotification(activityLog.getUser(), content, activityLog, notificationList);
                break;
            case FRIENDSHIP_ENDED:
                user = findRelatedUser(activityLog);
                content = activityLog.getUser().getUsername() + "y tu habeis dejado de ser amigos.";
                createNotification(user, content, activityLog, notificationList);
                break;
            case FRIENDSHIP_RETIRED:
                user = findRelatedUser(activityLog);
                content = "Has cancelado la solicitud de amistad a " + user.getUsername() + ".";
                createNotification(activityLog.getUser(), content, activityLog, notificationList);
                break;
            case FOLLOWED:
                user = findRelatedUser(activityLog);
                content = "Te ha seguido " + activityLog.getUser().getUsername() + ".";
                createNotification(user, content, activityLog, notificationList);
                break;
            case UNFOLLOWED:
                user = findRelatedUser(activityLog);
                content = "Te ha dejado de seguir  " + activityLog.getUser().getUsername() + ".";
                createNotification(user, content, activityLog, notificationList);
                break;
            case CHALLENGE_CREATED:
                userList = userService.findAllUsersByRole(Role.ROLE_USER);
                content = "¡Hay un nuevo desafío disponible, échale un vistazo!";
                createNotificationsForUsers(userList, content, activityLog, notificationList);
                break;
            case CHALLENGE_JOINED:
                userList = userService.findAllUsersByRole(Role.ROLE_ADMIN);
                challenge = challengeService.findChallengeById(activityLog.getRelatedEntityId());
                content = "¡Hay una nueva participación en el desafío '" + challenge.getTitle() + "'!";
                createNotificationsForUsers(userList, content, activityLog, notificationList);
                break;
            case CHALLENGE_WINNER:
                challengeParticipation = challengeParticipationService.findChallengeParticipationById(activityLog.getRelatedEntityId());
                content = "!Has ganado el reto '" + challengeParticipation.getChallenge().getTitle() + "'¡ Enhorabuena, contacta con un administrador para recibir tu premio.";
                createNotification(activityLog.getUser(), content, activityLog, notificationList);
                break;
            case CHALLENGE_FINISHED:
                challenge = challengeService.findChallengeById(activityLog.getRelatedEntityId());
                content = "Ya se ha finalizado la seleccion de ganadores para el desafío '" + challenge.getTitle() + "'. ¡Si no has sido elegido, te deseamos suerte para la proxima vez!";
                List<User> participants = new ArrayList<>();
                for (ChallengeParticipation participation : challenge.getParticipations()) {
                    participants.add(participation.getUser());
                }
                createNotificationsForUsers(participants, content, activityLog, notificationList);
                break;
            case COMMENT_ADDED:
                comment = commentService.findCommentById(activityLog.getRelatedEntityId());
                content = "¡Hay un nuevo comentario en tu valoración!";
                createNotification(comment.getRate().getUser(), content, activityLog, notificationList);

                content = "¡Hay un nuevo comentario en tu receta '" + comment.getRate().getRecipe().getTitle() + "'!.";
                createNotification(comment.getRate().getRecipe().getUser(), content, activityLog, notificationList);
                break;
            case INGREDIENT_SUGGESTED:
                userList = userService.findAllUsersByRole(Role.ROLE_ADMIN);
                content = "Hay nuevos ingredientes sugeridos.";
                createNotificationsForUsers(userList, content, activityLog, notificationList);
                break;
            case INGREDIENT_UPDATE:
                userList = userService.findAllUsersByRole(Role.ROLE_ADMIN);
                ingredient = ingredientService.findIngredientById(activityLog.getRelatedEntityId());
                content = "El administrador '" + activityLog.getUser().getUsername() + "' ha actualizado el ingrediente '" + ingredient.getIngredientName() + "'.";
                createNotificationsForUsers(userList, content, activityLog, notificationList);
                break;
            case INGREDIENT_APPROVED:
                userList = userService.findAllUsersByRole(Role.ROLE_ADMIN);
                ingredient = ingredientService.findIngredientById(activityLog.getRelatedEntityId());
                content = "El administrador '" + activityLog.getUser().getUsername() + "' ha aprobado el ingrediente '" + ingredient.getIngredientName() + "'.";
                createNotificationsForUsers(userList, content, activityLog, notificationList);
                break;
            case RATING_ADDED:
                rating = ratingService.findRateById(activityLog.getRelatedEntityId());
                content = "!Has recibido una nueva valoración en tu receta '" + rating.getRecipe().getTitle() + "!.";
                createNotification(rating.getRecipe().getUser(), content, activityLog, notificationList);
                break;
            case RECIPE_CREATED:
                content = "¡" + activityLog.getUser().getUsername() + " ha subido una nueva receta, échale un vistazo!.";
                List<Relationship> relationships = relationshipService.findFollowersAndFriends(activityLog.getUser());
                for (Relationship relationship : relationships) {
                    createNotification(relationship.getSender(), content, activityLog, notificationList);
                }
                break;
            case RECIPE_UPDATED:
                recipe = recipeService.findRecipeById(activityLog.getRelatedEntityId());
                content = "Has actualizado tu receta '" + recipe.getTitle() + "' correctamente.";
                createNotification(activityLog.getUser(), content, activityLog, notificationList);
            case PROFILE_EDITED:
                content = "¡Has actualizado correctamente tu perfil!";
                createNotification(activityLog.getUser(), content, activityLog, notificationList);
                break;
        }
        return notificationList;
    }

    private void createNotification(User user, String content, ActivityLog activityLog, List<Notification> notificationList) {
        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO(user, content, false, activityLog);
        notificationList.add(notificationMapper.toNotification(notificationRequestDTO));
    }

    private void createNotificationsForUsers(List<User> users, String content, ActivityLog activityLog, List<Notification> notificationList) {
        for (User user : users) {
            createNotification(user, content, activityLog, notificationList);
        }
    }

    private User findRelatedUser(ActivityLog activityLog) {
        return userService.findUserById(activityLog.getRelatedEntityId());
    }
}
