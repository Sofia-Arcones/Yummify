package com.gf.yummify.business.services;

import com.gf.yummify.data.repository.RelationshipRepository;
import org.springframework.stereotype.Service;

@Service
public class RelationshipServiceImpl implements RelationshipService{

    private RelationshipRepository relationshipRepository;

    public RelationshipServiceImpl(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }
}
