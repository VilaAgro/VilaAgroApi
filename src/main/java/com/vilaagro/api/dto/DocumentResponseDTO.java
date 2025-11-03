package com.vilaagro.api.dto;

import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.model.Document;
import com.vilaagro.api.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDTO {
    private UUID id;
    private UUID userId;
    private DocumentType type;
    private String filePath;
    private String originalFileName;
    private String fileExtension;
    private Long fileSize;
    private AccountStatus status;
    private LocalDateTime uploadedAt;

    public static DocumentResponseDTO fromEntity(Document document) {
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .userId(document.getUser().getId())
                .type(document.getType())
                .filePath(document.getFilePath())
                .originalFileName(document.getOriginalFileName())
                .fileExtension(document.getFileExtension())
                .fileSize(document.getFileSize())
                .status(document.getStatus())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
