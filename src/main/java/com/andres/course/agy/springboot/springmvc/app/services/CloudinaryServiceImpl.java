package com.andres.course.agy.springboot.springmvc.app.services;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> upload(MultipartFile file) throws IOException {
        return (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), Map.of(
                "folder", "products"
        ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> delete(String publicId) throws IOException {
        return (Map<String, Object>) cloudinary.uploader().destroy(publicId, Map.of());
    }
}

