package com.example.streetleague.ServiceImp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIImageService {

    private static final Logger log = LoggerFactory.getLogger(AIImageService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final CloudinaryService cloudinaryService;

    public AIImageService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    public String generateAndUpload(String prompt) throws Exception {
//        log.info("Generating image for prompt: {}", prompt);

        // Pollinations AI — مجاني بدون API key
        String encodedPrompt = prompt.replace(" ", "%20");
        String imageUrl = "https://image.pollinations.ai/prompt/" + encodedPrompt + "?width=512&height=512&nologo=true";

//        log.info("Downloading image from: {}", imageUrl);

        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);

        if (imageBytes == null || imageBytes.length == 0) {
            throw new RuntimeException("Failed to download image from Pollinations");
        }

//        log.info("Image downloaded, size: {} bytes", imageBytes.length);

        String cloudinaryUrl = cloudinaryService.uploadImageBytes(imageBytes);
//        log.info("Uploaded to Cloudinary: {}", cloudinaryUrl);

        return cloudinaryUrl;
    }
}