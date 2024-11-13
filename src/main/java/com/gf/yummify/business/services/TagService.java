package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Tag;

public interface TagService {
    Tag findTagByName(String name);
    Tag findOrCreateTag(String name);
}
