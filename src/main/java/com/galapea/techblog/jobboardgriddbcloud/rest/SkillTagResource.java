package com.galapea.techblog.jobboardgriddbcloud.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galapea.techblog.jobboardgriddbcloud.model.SkillTagDTO;
import com.galapea.techblog.jobboardgriddbcloud.service.SkillTagGridDbService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/skillTags", produces = MediaType.APPLICATION_JSON_VALUE)
public class SkillTagResource {

    private final SkillTagGridDbService skillTagService;

    public SkillTagResource(final SkillTagGridDbService skillTagService) {
        this.skillTagService = skillTagService;
    }

    @GetMapping
    public ResponseEntity<List<SkillTagDTO>> getAllSkillTags() {
        return ResponseEntity.ok(skillTagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillTagDTO> getSkillTag(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(skillTagService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> createSkillTag(
            @RequestBody @Valid final SkillTagDTO skillTagDTO) {
        final String createdId = skillTagService.create(skillTagDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSkillTag(
            @PathVariable(name = "id") final String id,
            @RequestBody @Valid final SkillTagDTO skillTagDTO) {
        skillTagService.update(id, skillTagDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkillTag(@PathVariable(name = "id") final String id) {
        skillTagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
