package com.kushi.in.app.model;

import java.time.LocalDateTime;

public class GalleryDTO {

    private Long id;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private String description;

    public GalleryDTO() {
        // ✅ no-args constructor for mapper, frameworks, JSON
    }

    public GalleryDTO(Long id, String fileName, String fileUrl, LocalDateTime uploadedAt, String description) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
        this.description = description;
    }

    // ✅ Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
