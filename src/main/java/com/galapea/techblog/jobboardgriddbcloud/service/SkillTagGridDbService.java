package com.galapea.techblog.jobboardgriddbcloud.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.galapea.techblog.jobboardgriddbcloud.model.SkillTagDTO;
import com.galapea.techblog.jobboardgriddbcloud.util.NotFoundException;
import com.galapea.techblog.jobboardgriddbcloud.util.NotImplementedException;
import com.github.f4b6a3.tsid.TsidCreator;

@Service
public class SkillTagGridDbService {

    private final SkillTagContainer skillTagContainer;

    public SkillTagGridDbService(SkillTagContainer skillTagContainer) {
        this.skillTagContainer = skillTagContainer;
    }

    public static String nextId() {
        return TsidCreator.getTsid().format("skt_%s");
    }

    public List<SkillTagDTO> findAll() {
        return findAll(50L);
    }

    public List<SkillTagDTO> findAll(Long limit) {
        final List<SkillTagRecord> skillTags = skillTagContainer.getAll(limit);
        return skillTags.stream()
                .map(skillTag -> mapToDTO(skillTag, new SkillTagDTO()))
                .collect(Collectors.toList());
    }

    public SkillTagDTO get(final String id) {
        return skillTagContainer
                .getOne(id)
                .map(skillTag -> mapToDTO(skillTag, new SkillTagDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<SkillTagDTO> searchByName(final String namePattern) {
        final List<SkillTagRecord> skillTags = skillTagContainer.searchByName(namePattern);
        return skillTags.stream()
                .map(skillTag -> mapToDTO(skillTag, new SkillTagDTO()))
                .collect(Collectors.toList());
    }

    public String create(final SkillTagDTO skillTagDTO) {
        String id = (skillTagDTO.getId() != null) ? skillTagDTO.getId() : nextId();
        SkillTagRecord newSkillTag = new SkillTagRecord(id, skillTagDTO.getName());
        skillTagContainer.saveRecords(List.of(newSkillTag));
        return id;
    }

    public void createAll(List<SkillTagDTO> skillTagDTOs) {
        List<SkillTagRecord> skillTagRecords =
                skillTagDTOs.stream()
                        .map(
                                skillTagDTO ->
                                        new SkillTagRecord(
                                                (skillTagDTO.getId() != null)
                                                        ? skillTagDTO.getId()
                                                        : nextId(),
                                                skillTagDTO.getName()))
                        .collect(Collectors.toList());
        if (!skillTagRecords.isEmpty()) {
            skillTagContainer.saveRecords(skillTagRecords);
        }
    }

    public void update(final String id, final SkillTagDTO skillTagDTO) {
        SkillTagRecord updatedSkillTag = new SkillTagRecord(id, skillTagDTO.getName());
        skillTagContainer.saveRecords(List.of(updatedSkillTag));
    }

    public void delete(final String id) {
        throw new NotImplementedException("Delete operation is not implemented yet.");
    }

    private SkillTagDTO mapToDTO(final SkillTagRecord skillTag, final SkillTagDTO skillTagDTO) {
        skillTagDTO.setId(skillTag.id());
        skillTagDTO.setName(skillTag.name());
        return skillTagDTO;
    }

    public boolean idExists(final String id) {
        return skillTagContainer.getOne(id).isPresent();
    }

    public boolean nameExists(final String name) {
        return skillTagContainer.getOneByName(name).isPresent();
    }

    public void createTable() {
        skillTagContainer.createTable();
    }
}
