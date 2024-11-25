package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.RelationshipStatus;
import com.gf.yummify.data.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipRequestDTO {
    private User sender;
    private User receiver;
    private RelationshipType relationshipType;
    private RelationshipStatus relationshipStatus;
}
