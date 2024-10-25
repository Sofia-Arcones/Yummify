package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
}
