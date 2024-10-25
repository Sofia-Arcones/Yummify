package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
