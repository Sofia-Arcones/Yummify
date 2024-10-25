package com.gf.yummify.business.services;

import com.gf.yummify.data.repository.RatingRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService{
    private RatingRepository ratingRepository;

    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }
}
