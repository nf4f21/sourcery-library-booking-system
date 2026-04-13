package com.example.demo.util;

import com.example.demo.exception.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidator {
    private static final List<String> ACCEPTABLE_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "application/octet-stream");

    public boolean isCorrectTypeFile(MultipartFile coverImage) {
        String contentType = coverImage.getContentType();
        if (contentType == null) {
            throw new InvalidFileException("The file not exist.");
        }
        if (!ACCEPTABLE_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException("The file has not correct type, only acceptable types are jpg, png, jpeg");
        }
        return true;
    }

}
