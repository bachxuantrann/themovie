package com.ptit_intern.themoviedb.service.impl;

import com.ptit_intern.themoviedb.dto.dtoClass.CompanyDTO;
import com.ptit_intern.themoviedb.dto.dtoClass.GenreDTO;
import com.ptit_intern.themoviedb.dto.request.CreateCompanyRequest;
import com.ptit_intern.themoviedb.dto.request.UpdateCompanyRequest;
import com.ptit_intern.themoviedb.dto.response.ResultPagination;
import com.ptit_intern.themoviedb.entity.Company;
import com.ptit_intern.themoviedb.entity.Genre;
import com.ptit_intern.themoviedb.exception.InvalidExceptions;
import com.ptit_intern.themoviedb.repository.CompanyRepository;
import com.ptit_intern.themoviedb.service.CompanyService;
import com.ptit_intern.themoviedb.service.cloudinary.CloudinaryService;
import com.ptit_intern.themoviedb.service.cloudinary.UploadOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
            return;
        }
        throw new InvalidExceptions("company is not found");
    }

    @Override
    public ResultPagination searchCompanies(int page, int size, String keyword, boolean desc) {
        Sort sort = Sort.by("created_at");
        if (desc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Company> companies = companyRepository.searchCompanies(keyword, pageable);
        List<CompanyDTO> companyDTOS = new ArrayList<>();
        companyDTOS = companies.stream().map(genre -> genre.toDTO(CompanyDTO.class)).toList();
        ResultPagination resultPagination = new ResultPagination();
        resultPagination.setResults(companyDTOS);
        ResultPagination.MetaInfo metaInfo = new ResultPagination.MetaInfo();
        metaInfo.setTotal(companies.getTotalElements());
        metaInfo.setPage(page);
        metaInfo.setSize(size);
        metaInfo.setTotalPages(companies.getTotalPages());
        resultPagination.setMetaInfo(metaInfo);
        return resultPagination;
    }

    @Override
    public void updateCompany(UpdateCompanyRequest request) throws InvalidExceptions, IOException {
        if (companyRepository.existsByNameAndIdNot(request.getName(),request.getId())) {
            throw new InvalidExceptions("Company name is existed");
        }
        Company company = companyRepository.findById(request.getId()).orElseThrow(
                () -> new InvalidExceptions("Company is not existed")
        );
        company.setName(request.getName());
        boolean hasLogo = request.getLogo() != null && !request.getLogo().isEmpty();
        boolean removeOldLogo = request.isRemoveLogo();
        String oldLogoPath = company.getLogoPath();
        String oldPublicId = company.getLogoPublicId();
        if (hasLogo) {
            UploadOptions options = new UploadOptions();
            options.setFolder("companies");
            options.setTags(List.of("companies", "logo"));
            var uploadRes = cloudinaryService.uploadFileWithPublicId(request.getLogo(),options);
            String newUrl = uploadRes.secureUrl();
            String newPublicId = uploadRes.publicId();
            company.setLogoPath(newUrl);
            company.setLogoPublicId(newPublicId);
            if (removeOldLogo){
                try {
                    if (oldPublicId != null && !oldPublicId.isEmpty()) {
                        cloudinaryService.deleteImageByPublicId(oldPublicId);
                    } else if (oldLogoPath != null && !oldLogoPath.isEmpty()) {
                        cloudinaryService.deleteImageByUrl(oldLogoPath);
                    }
                } catch (Exception e) {
                    log.warn("Xoá ảnh trên cloudinary thất bại: companyId ={}, url = {}, public_id = {}",company.getId(), oldLogoPath, oldPublicId);
                }
            }
        } else if (removeOldLogo) {
            if (oldPublicId != null && !oldPublicId.isEmpty()) {
                cloudinaryService.deleteImageByPublicId(oldPublicId);
            } else if (oldLogoPath != null && !oldLogoPath.isEmpty()) {
                cloudinaryService.deleteImageByUrl(oldLogoPath);
            }
            company.setLogoPath(logoDefault);
            company.setLogoPublicId(cloudinaryService.extractPublicIdFromUrl(logoDefault));
        }
        companyRepository.save(company);
    }

    private boolean isExisted(String name) {
        return companyRepository.existsByName(name);
    }
}
