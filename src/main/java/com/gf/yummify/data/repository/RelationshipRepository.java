package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Relationship;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.RelationshipStatus;
import com.gf.yummify.data.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {
    Optional<Relationship> findBySenderAndReceiverAndRelationshipType(User sender, User receiver, RelationshipType relationshipType);

    Optional<Relationship> findBySenderAndReceiverAndRelationshipStatus(User sender, User receiver, RelationshipStatus relationshipStatus);

    List<Relationship> findBySenderAndRelationshipStatusAndRelationshipType(User sender, RelationshipStatus relationshipStatus, RelationshipType relationshipType);
    List<Relationship> findByReceiverAndRelationshipStatusAndRelationshipType(User receiver, RelationshipStatus relationshipStatus, RelationshipType relationshipType);

}
