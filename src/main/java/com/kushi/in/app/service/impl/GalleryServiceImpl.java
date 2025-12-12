package com.kushi.in.app.service.impl;

import com.kushi.in.app.entity.Gallery;
import com.kushi.in.app.model.GalleryDTO;
import com.kushi.in.app.dao.GalleryRepository;
import com.kushi.in.app.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private software.amazon.awssdk.services.s3.presigner.S3Presigner s3Presigner;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public void saveFile(MultipartFile file, String fileUrl, String description) throws IOException {
        String fileName;
        String s3Key;

        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // Generate unique filename to prevent collisions
            fileName = UUID.randomUUID().toString() + extension;
            s3Key = fileName; // Key is the filename

            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } else if (fileUrl != null && !fileUrl.isEmpty()) {
            fileName = "external-link";
            s3Key = fileUrl; // For external, key is the URL itself
        } else {
            throw new IllegalArgumentException("No file or URL provided");
        }

        Gallery item = new Gallery();
        item.setFileName(fileName);
        item.setFileUrl(s3Key);
        item.setDescription(description);
        item.setUploadedAt(LocalDateTime.now());
        galleryRepository.save(item);
    }

    @Override
    public List<GalleryDTO> getAllFiles() {
        return galleryRepository.findAll()
                .stream()
                .map(item -> {
                    String url = item.getFileUrl();
                    // If it's a key in our bucket (simple check: doesn't start with http), generate
                    // presigned URL
                    if (url != null && !url.startsWith("http")) {
                        try {
                            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest
                                    .builder()
                                    .bucket(bucketName)
                                    .key(url)
                                    .build();

                            software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest presignRequest = software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
                                    .builder()
                                    .signatureDuration(java.time.Duration.ofMinutes(60)) // URL valid for 60 mins
                                    .getObjectRequest(getObjectRequest)
                                    .build();

                            software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest presignedRequest = s3Presigner
                                    .presignGetObject(presignRequest);

                            url = presignedRequest.url().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (url != null && url.contains("s3.amazonaws.com")) {
                        // Should handle legacy absolute S3 URLs if any were saved during the brief
                        // window
                        try {
                            String key = url.substring(url.lastIndexOf("/") + 1);
                            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest
                                    .builder()
                                    .bucket(bucketName)
                                    .key(key)
                                    .build();

                            software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest presignRequest = software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
                                    .builder()
                                    .signatureDuration(java.time.Duration.ofMinutes(60))
                                    .getObjectRequest(getObjectRequest)
                                    .build();

                            url = s3Presigner.presignGetObject(presignRequest).url().toString();
                        } catch (Exception e) {
                            // Fallback
                        }
                    }

                    return new GalleryDTO(
                            item.getId(),
                            item.getFileName(),
                            url,
                            item.getUploadedAt(),
                            item.getDescription());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(Long id) {
        Optional<Gallery> optional = galleryRepository.findById(id);
        if (optional.isPresent()) {
            Gallery item = optional.get();
            String key = item.getFileUrl();
            if (key != null) {
                if (key.startsWith("http") && key.contains("/")) {
                    key = key.substring(key.lastIndexOf("/") + 1);
                }

                if (!item.getFileUrl().startsWith("http") || item.getFileUrl().contains("s3.amazonaws.com")) {
                    try {
                        DeleteObjectRequest deleteOb = DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build();
                        s3Client.deleteObject(deleteOb);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            galleryRepository.deleteById(id);
        }
    }

    @Override
    public boolean updateDescription(Long id, String description) {
        Optional<Gallery> optional = galleryRepository.findById(id);
        if (optional.isPresent()) {
            Gallery item = optional.get();
            item.setDescription(description);
            galleryRepository.save(item);
            return true;
        }
        return false;
    }
}
