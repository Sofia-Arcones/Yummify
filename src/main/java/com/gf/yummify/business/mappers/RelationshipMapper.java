package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.Relationship;
import com.gf.yummify.presentation.dto.RelationshipRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RelationshipMapper {
    Relationship toRelationship(RelationshipRequestDTO relationshipRequestDTO);
}
