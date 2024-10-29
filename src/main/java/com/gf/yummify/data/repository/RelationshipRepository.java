package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {
}
