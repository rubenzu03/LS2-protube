package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Tag;
import com.tecnocampus.LS2.protube_back.persistence.TagRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDto> getTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    public TagDto getTagById(Long id) {
        return tagRepository.findById(id)
                .map(tag -> new TagDto(tag.getId(), tag.getName()))
                .orElse(null);
    }

    public TagDto createTag(TagDto tagDto) {
        Tag tag = new Tag(tagDto);
        Tag saved = tagRepository.save(tag);
        return new TagDto(saved.getId(), saved.getName());
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    public TagDto updateTag(Long id, TagDto tagDto) {
        Tag tag = new Tag(tagDto);
        tag.setId(id);
        Tag saved = tagRepository.save(tag);
        return new TagDto(saved.getId(), saved.getName());
    }
}
