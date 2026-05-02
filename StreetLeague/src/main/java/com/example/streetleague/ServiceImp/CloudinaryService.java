// service/CloudinaryService.java
package com.example.streetleague.ServiceImp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder",        "streetleague/posts",
                        "resource_type", "image"
                        // ✅ Supprime la transformation — elle causait l'erreur
                )
        );
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String publicId = imageUrl
                .replaceAll("https://res.cloudinary.com/[^/]+/image/upload/(v\\d+/)?", "")
                .replaceAll("\\.[a-zA-Z]+$", "");

        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String uploadImageBytes(byte[] imageBytes) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                imageBytes,
                ObjectUtils.asMap("folder", "streetleague/ai-generated")
        );
        return uploadResult.get("secure_url").toString();
    }
}