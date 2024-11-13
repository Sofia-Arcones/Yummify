package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Tag;
import com.gf.yummify.data.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag findTagByName(String name) {
        Optional<Tag> tagOptional = tagRepository.findByName(normalizeTagName(name));
        if (tagOptional.isPresent()) {
            return tagOptional.get();
        } else {
            throw new NoSuchElementException("El tag no existe");
        }
    }

    @Override
    public Tag findOrCreateTag(String name) {
        String normalizedTagName = normalizeTagName(name);
        Optional<Tag> existingTag = tagRepository.findByName(normalizedTagName);

        if (existingTag.isPresent()) {
            return existingTag.get();
        } else {
            Tag tag = new Tag();
            tag.setName(normalizedTagName);
            return tagRepository.save(tag);
        }
    }

    private String normalizeTagName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.trim().toLowerCase()
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-z0-9# ]", "");
        if (!normalized.startsWith("#")) {
            normalized = "#" + normalized;
        }
        return normalized;
    }
}
