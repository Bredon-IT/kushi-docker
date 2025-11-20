package com.kushi.in.app.mapper;

import com.kushi.in.app.entity.Gallery;
import com.kushi.in.app.model.GalleryDTO;

public final class GalleryMapper {
    private GalleryMapper() {}

    public static GalleryDTO toDto(Gallery g) {
        if (g == null) return null;
        GalleryDTO dto = new GalleryDTO();
        dto.setId(g.getId());
        dto.setFileName(g.getFileName());
        dto.setFileUrl(g.getFileUrl());
        dto.setUploadedAt(g.getUploadedAt());

        return dto;
    }
}
