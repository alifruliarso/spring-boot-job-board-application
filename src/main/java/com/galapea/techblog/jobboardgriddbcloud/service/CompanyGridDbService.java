package com.galapea.techblog.jobboardgriddbcloud.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.galapea.techblog.jobboardgriddbcloud.model.CompanyDTO;
import com.galapea.techblog.jobboardgriddbcloud.util.NotFoundException;
import com.galapea.techblog.jobboardgriddbcloud.util.NotImplementedException;
import com.github.f4b6a3.tsid.TsidCreator;

@Service
public class CompanyGridDbService {

    private final CompanyContainer companyContainer;

    public CompanyGridDbService(CompanyContainer companyContainer) {
        this.companyContainer = companyContainer;
    }

    public static String nextId() {
        return TsidCreator.getTsid().format("com_%s");
    }

    public List<CompanyDTO> findAll() {
        final List<CompanyRecord> companies = companyContainer.getAll();
        return companies.stream()
                .map(company -> mapToDTO(company, new CompanyDTO()))
                .collect(Collectors.toList());
    }

    public CompanyDTO get(final String id) {
        return companyContainer
                .getOne(id)
                .map(company -> mapToDTO(company, new CompanyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final CompanyDTO companyDTO) {
        String id = (companyDTO.getId() != null) ? companyDTO.getId() : nextId();
        CompanyRecord newCompany =
                new CompanyRecord(
                        id,
                        companyDTO.getName(),
                        companyDTO.getWebsiteUrl(),
                        companyDTO.getDescription());
        companyContainer.saveRecords(List.of(newCompany));
        return id;
    }

    public void createAll(List<CompanyDTO> companyDTOs) {
        List<CompanyRecord> companyRecords =
                companyDTOs.stream()
                        .map(
                                companyDTO ->
                                        new CompanyRecord(
                                                (companyDTO.getId() != null)
                                                        ? companyDTO.getId()
                                                        : nextId(),
                                                companyDTO.getName(),
                                                companyDTO.getWebsiteUrl(),
                                                companyDTO.getDescription()))
                        .collect(Collectors.toList());
        if (!companyRecords.isEmpty()) {
            companyContainer.saveRecords(companyRecords);
        }
    }

    public void update(final String id, final CompanyDTO companyDTO) {
        CompanyRecord updatedCompany =
                new CompanyRecord(
                        id,
                        companyDTO.getName(),
                        companyDTO.getWebsiteUrl(),
                        companyDTO.getDescription());
        companyContainer.saveRecords(List.of(updatedCompany));
    }

    public void delete(final String id) {
        throw new NotImplementedException("Delete operation is not implemented yet.");
    }

    private CompanyDTO mapToDTO(final CompanyRecord company, final CompanyDTO companyDTO) {
        companyDTO.setId(company.id());
        companyDTO.setName(company.name());
        companyDTO.setWebsiteUrl(company.websiteUrl());
        companyDTO.setDescription(company.description());
        return companyDTO;
    }

    public boolean idExists(final String id) {
        return companyContainer.getOne(id).isPresent();
    }

    public boolean nameExists(final String name) {
        return companyContainer.getOneByName(name).isPresent();
    }

    public void createTable() {
        companyContainer.createTable();
    }
}
