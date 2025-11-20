package com.kushi.in.app.service;

import com.kushi.in.app.model.GalleryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GalleryService {

    /**
     * Save a file uploaded locally or via URL
     * @param file MultipartFile (optional, can be null if fileUrl is provided)
     * @param fileUrl URL of the file (optional, can be null if local file is provided)
     * @param description Optional description of the file
     * @throws IOException
     */
    void saveFile(MultipartFile file, String fileUrl, String description) throws IOException;

    /**
     * Get all gallery files
     * @return list of GalleryDTO
     */
    List<GalleryDTO> getAllFiles();

    /**
     * Delete a gallery file by id
     * @param id
     */
    void deleteFile(Long id);

    /**
     * Update description of a gallery file
     * @param id
     * @param description
     * @return true if update successful, false if file not found
     */
    boolean updateDescription(Long id, String description);
}
