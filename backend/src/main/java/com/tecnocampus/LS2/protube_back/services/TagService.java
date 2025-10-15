package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Tag;
import com.tecnocampus.LS2.protube_back.persistence.TagRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class TagService {

    public final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDto> getTags() {
        return tagRepository.findAll().stream().map(this::toDTO).toList();
    }

    public TagDto getTagById(Long id) {
        return tagRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public URI createTag(TagDto tagDto) {
        Tag tag = toEntity(tagDto);
        tagRepository.save(tag);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tag.getId())
                .toUri();
    }

    public TagDto deleteTag(Long id) {
        TagDto tagDto = getTagById(id);
        tagRepository.deleteById(id);
        return tagDto;
    }

    public TagDto updateTag(Long id, TagDto tagDto) {
        Tag tag = toEntity(tagDto);
        tag.setId(id);
        tagRepository.save(tag);
        return toDTO(tag);
    }

    private TagDto toDTO(Tag tag) {
        return new TagDto(
                tag.getId(),
                tag.getName()
        );
    }

    private Tag toEntity(TagDto tagDto) {
        Tag tag = new Tag();
        tag.setId(tagDto.id());
        tag.setName(tagDto.name());
        return tag;
    }
}

