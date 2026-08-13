package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Company;

public interface CompanyService {

    Company getCompany();

    Company save(Company company);
}
