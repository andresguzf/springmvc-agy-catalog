package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Company;
import com.andres.course.agy.springboot.springmvc.app.repositories.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Company getCompany() {
        List<Company> list = companyRepository.findAll();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return new Company(
                "TIENDA ONLINE E-COMMERCE",
                "76.543.210-9",
                "Av. Principal 123, Santiago, Chile",
                "+56 9 1234 5678",
                "contacto@tienda.com",
                "www.tienda.com"
        );
    }

    @Override
    @Transactional
    public Company save(Company company) {
        List<Company> list = companyRepository.findAll();
        if (!list.isEmpty()) {
            Company existing = list.get(0);
            existing.setName(company.getName());
            existing.setTaxId(company.getTaxId());
            existing.setAddress(company.getAddress());
            existing.setPhone(company.getPhone());
            existing.setEmail(company.getEmail());
            existing.setWebsite(company.getWebsite());
            return companyRepository.save(existing);
        }
        return companyRepository.save(company);
    }
}
