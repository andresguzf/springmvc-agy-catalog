package com.andres.course.agy.springboot.springmvc.app.services;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map<String, Object> upload(MultipartFile file) throws IOException;
    Map<String, Object> delete(String publicId) throws IOException;
}

