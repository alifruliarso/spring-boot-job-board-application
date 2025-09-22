package com.galapea.techblog.jobboardgriddbcloud.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.galapea.techblog.jobboardgriddbcloud.model.CompanyDTO;
import com.galapea.techblog.jobboardgriddbcloud.service.CompanyGridDbService;
import com.galapea.techblog.jobboardgriddbcloud.util.WebUtils;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyGridDbService companyService;

    public CompanyController(final CompanyGridDbService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("companies", companyService.findAll());
        return "company/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add(@ModelAttribute("company") final CompanyDTO companyDTO) {
        return "company/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add(
            @ModelAttribute("company") @Valid final CompanyDTO companyDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "company/add";
        }
        companyService.create(companyDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("company.create.success"));
        return "redirect:/companies";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("company", companyService.get(id));
        return "company/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("company") @Valid final CompanyDTO companyDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "company/edit";
        }
        companyService.update(id, companyDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("company.update.success"));
        return "redirect:/companies";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(
            @PathVariable(name = "id") final String id,
            final RedirectAttributes redirectAttributes) {
        companyService.delete(id);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_INFO, WebUtils.getMessage("company.delete.success"));
        return "redirect:/companies";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable(name = "id") final String id, final Model model) {
        model.addAttribute("company", companyService.get(id));
        return "company/view";
    }
}
