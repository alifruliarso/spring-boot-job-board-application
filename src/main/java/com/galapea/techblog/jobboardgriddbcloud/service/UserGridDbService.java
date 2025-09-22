package com.galapea.techblog.jobboardgriddbcloud.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.galapea.techblog.jobboardgriddbcloud.model.UserDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.UserRole;
import com.galapea.techblog.jobboardgriddbcloud.util.NotImplementedException;
import com.github.f4b6a3.tsid.TsidCreator;

@Service
public class UserGridDbService {

    private final UserContainer userContainer;

    public UserGridDbService(UserContainer userContainer) {
        this.userContainer = userContainer;
    }

    public void createTable() {
        userContainer.createTable();
    }

    public String nextId() {
        return TsidCreator.getTsid().format("usr_%s");
    }

    public List<UserDTO> findAll() {
        return userContainer.getAll().stream().map(this::mapToDTO).toList();
    }

    public Optional<UserDTO> get(String id) {
        return userContainer.getOne(id).map(this::mapToDTO);
    }

    public Optional<UserDTO> getByEmail(String email) {
        return userContainer.getOneByEmail(email).map(this::mapToDTO);
    }

    public String create(UserDTO userDTO) {
        String id =
                userDTO.getId() != null && !userDTO.getId().isBlank() ? userDTO.getId() : nextId();
        UserRecord rec =
                new UserRecord(
                        id,
                        userDTO.getEmail(),
                        userDTO.getFullName(),
                        userDTO.getRole(),
                        userDTO.getCompanyId());
        userContainer.saveRecords(List.of(rec));
        return id;
    }

    public void createAll(List<UserDTO> users) {
        List<UserRecord> records =
                users.stream()
                        .map(
                                u ->
                                        new UserRecord(
                                                u.getId() != null && !u.getId().isBlank()
                                                        ? u.getId()
                                                        : nextId(),
                                                u.getEmail(),
                                                u.getFullName(),
                                                u.getRole(),
                                                u.getCompanyId()))
                        .toList();
        userContainer.saveRecords(records);
    }

    public void update(String id, UserDTO userDTO) {
        UserRecord rec =
                new UserRecord(
                        id,
                        userDTO.getEmail(),
                        userDTO.getFullName(),
                        userDTO.getRole(),
                        userDTO.getCompanyId());
        userContainer.saveRecords(List.of(rec));
    }

    private UserDTO mapToDTO(UserRecord record) {
        UserDTO dto = new UserDTO();
        dto.setId(record.id());
        dto.setEmail(record.email());
        dto.setFullName(record.fullName());
        dto.setRole(record.role() == null ? UserRole.valueOf("USER") : record.role());
        dto.setCompanyId(record.companyId());
        return dto;
    }

    public void delete(final String id) {
        throw new NotImplementedException("Delete operation is not implemented yet.");
    }

    public boolean idExists(final String id) {
        return userContainer.getOne(id).isPresent();
    }

    public boolean emailExists(final String name) {
        return userContainer.getOneByEmail(name).isPresent();
    }
}
