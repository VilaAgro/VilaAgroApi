package com.vilaagro.api.service;

import com.vilaagro.api.dto.DocumentResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.Document;
import com.vilaagro.api.model.DocumentType;
import com.vilaagro.api.model.User;
import com.vilaagro.api.repository.DocumentRepository;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public DocumentResponseDTO uploadDocument(UUID userId, DocumentType documentType, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        // Verifica se já existe documento desse tipo para o usuário
        documentRepository.findByUserIdAndType(userId, documentType)
                .ifPresent(existingDoc -> {
                    // Remove arquivo antigo
                    fileStorageService.deleteFile(existingDoc.getFilePath());
                    // Remove registro antigo
                    documentRepository.delete(existingDoc);
                });

        // Salva novo arquivo
        String filePath = fileStorageService.storeFile(file, "documents/" + userId);
        
        Document document = Document.builder()
                .user(user)
                .type(documentType)
                .filePath(filePath)
                .originalFileName(file.getOriginalFilename())
                .fileExtension(getFileExtension(file.getOriginalFilename()))
                .fileSize(file.getSize())
                .build();

        Document saved = documentRepository.save(document);
        return DocumentResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getUserDocuments(UUID userId) {
        return documentRepository.findByUserId(userId)
                .stream()
                .map(DocumentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteDocument(UUID documentId, UUID userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", "id", documentId));

        if (!document.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Você não tem permissão para deletar este documento");
        }

        fileStorageService.deleteFile(document.getFilePath());
        documentRepository.delete(document);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
