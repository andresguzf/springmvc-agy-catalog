package com.andres.course.agy.springboot.springmvc.app.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    private CloudinaryServiceImpl cloudinaryService;

    @BeforeEach
    void setUp() {
        cloudinaryService = new CloudinaryServiceImpl(cloudinary);
    }

    @Test
    @DisplayName("Should successfully upload image to Cloudinary")
    void uploadImageSuccess() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        Map<String, Object> expectedResponse = Map.of(
                "secure_url", "https://res.cloudinary.com/demo/image/upload/v12345/products/test-image.jpg",
                "public_id", "products/test-image"
        );

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class))).thenReturn(expectedResponse);

        Map<String, Object> result = cloudinaryService.upload(mockFile);

        assertNotNull(result);
        assertEquals("https://res.cloudinary.com/demo/image/upload/v12345/products/test-image.jpg", result.get("secure_url"));
        assertEquals("products/test-image", result.get("public_id"));
        verify(uploader, times(1)).upload(any(byte[].class), any(Map.class));
    }

    @Test
    @DisplayName("Should successfully delete image from Cloudinary by public ID")
    void deleteImageSuccess() throws IOException {
        String publicId = "products/test-image";
        Map<String, Object> expectedResponse = Map.of("result", "ok");

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq(publicId), any(Map.class))).thenReturn(expectedResponse);

        Map<String, Object> result = cloudinaryService.delete(publicId);

        assertNotNull(result);
        assertEquals("ok", result.get("result"));
        verify(uploader, times(1)).destroy(eq(publicId), any(Map.class));
    }
}
