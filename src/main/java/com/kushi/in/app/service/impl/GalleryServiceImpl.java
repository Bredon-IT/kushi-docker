package com.kushi.in.app.service.impl;

import com.kushi.in.app.entity.Gallery;
import com.kushi.in.app.model.GalleryDTO;
import com.kushi.in.app.dao.GalleryRepository;
import com.kushi.in.app.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GalleryServiceImpl implements GalleryService {

    private final String UPLOAD_DIR = "uploads/"; // ✅ adjust path

    @Autowired
    private GalleryRepository galleryRepository;

    @Override
    public void saveFile(MultipartFile file, String fileUrl, String description) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String fileName;
        if (file != null) {
            fileName = file.getOriginalFilename();
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
        } else if (fileUrl != null && !fileUrl.isEmpty()) {
            fileName = Paths.get(new java.net.URL(fileUrl).getPath()).getFileName().toString();
            try (InputStream in = new java.net.URL(fileUrl).openStream()) {
                Files.copy(in, uploadPath.resolve(fileName));
            }
        } else {
            throw new IllegalArgumentException("No file or URL provided");
        }

        Gallery item = new Gallery();
        item.setFileName(fileName);
        item.setFileUrl("uploads/" + fileName);
        item.setDescription(description);
        item.setUploadedAt(LocalDateTime.now());
        galleryRepository.save(item);
    }


    @Override
    public List<GalleryDTO> getAllFiles() {
        return galleryRepository.findAll()
                .stream()
                .map(item -> new GalleryDTO(
                        item.getId(),
                        item.getFileName(),
                        item.getFileUrl(),
                        item.getUploadedAt(),
                        item.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(Long id) {
        galleryRepository.deleteById(id);
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
