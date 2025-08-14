package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.CompanyDTO;
import com.ptit_intern.themoviedb.dto.request.CreateCompanyRequest;
import com.ptit_intern.themoviedb.entity.Company;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.CompanyRepository;
import com.ptit_intern.themoviedb.service.CompanyService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CloudinaryService cloudinaryService;
    private String logoDefault = "https://res.cloudinary.com/dpioj21ib/image/upload/v1755157208/blue-building-circle-by-simplepixelsl-brandcrowd_bnkasz.png";

    @Override
    public void createCompany(CreateCompanyRequest request) throws InvalidExceptions, IOException {
        if (isExisted(request.getName())) {
            throw new InvalidExceptions("Company name is existed");
        }
        Company newCompany = new Company();
        newCompany.setName(request.getName());
        boolean hasLogo = request.getLogo() != null && !request.getLogo().isEmpty();
        if (hasLogo) {
            UploadOptions options = new UploadOptions();
            options.setFolder("companies");
            options.setTags(List.of("companies", "logo"));
            var uploadRes = cloudinaryService.uploadFileWithPublicId(request.getLogo(), options);
            String newUrl = uploadRes.secureUrl();
            String newPublicId = uploadRes.publicId();
            if (newUrl != null && !newUrl.isEmpty()) {
                newCompany.setLogoPath(newUrl);
            }
            if (newPublicId != null && !newPublicId.isEmpty()) {
                newCompany.setLogoPublicId(newPublicId);
            }
        } else {
            newCompany.setLogoPath(logoDefault);
            newCompany.setLogoPublicId(cloudinaryService.extractPublicIdFromUrl(logoDefault));
        }
        this.companyRepository.save(newCompany);
    }

    @Override
    public CompanyDTO getCompany(Long id) throws InvalidExceptions {
        return companyRepository.findById(id).orElseThrow(
                () -> new InvalidExceptions("user not found")).toDTO(CompanyDTO.class);
    }

    @Override
    public void deleteCompany(Long id) throws InvalidExceptions {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
        }
        throw new InvalidExceptions("company is not found");
    }

    private boolean isExisted(String name) {
        return companyRepository.existsByName(name);
    }
}
