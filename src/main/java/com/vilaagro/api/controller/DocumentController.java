package com.vilaagro.api.controller;

import com.vilaagro.api.dto.DocumentResponseDTO;
import com.vilaagro.api.model.DocumentType;
import com.vilaagro.api.service.CustomUserDetailsService;
import com.vilaagro.api.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controller para gerenciamento de documentos dos usuários
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload de documento do usuário autenticado
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") DocumentType type,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        DocumentResponseDTO document = documentService.uploadDocument(
                currentUser.getUser().getId(), 
                type, 
                file
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    /**
     * Lista documentos do usuário autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<List<DocumentResponseDTO>> getMyDocuments(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        List<DocumentResponseDTO> documents = documentService.getUserDocuments(currentUser.getUser().getId());
        return ResponseEntity.ok(documents);
    }

    /**
     * Deleta documento do usuário autenticado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        documentService.deleteDocument(id, currentUser.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
