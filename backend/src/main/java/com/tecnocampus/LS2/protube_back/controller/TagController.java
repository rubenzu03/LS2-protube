package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.persistence.dto.TagDto;
import com.tecnocampus.LS2.protube_back.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    TagService tagService;

    @GetMapping()
    public ResponseEntity<List<TagDto>> getTags() {
        return ResponseEntity.ok().body(tagService.getTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTag(@PathVariable Long id) {
        return ResponseEntity.ok().body(tagService.getTagById(id));
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto tagDto) {
        return ResponseEntity.created(tagService.createTag(tagDto)).body(tagDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TagDto> deleteTag(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.deleteTag(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag(@PathVariable Long id, @RequestBody TagDto tagDto) {
        return ResponseEntity.ok(tagService.updateTag(id, tagDto));
    }
}

