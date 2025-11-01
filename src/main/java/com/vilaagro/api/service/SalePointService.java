package com.vilaagro.api.service;

import com.vilaagro.api.dto.SalePointAllocateDTO;
import com.vilaagro.api.dto.SalePointCreateDTO;
import com.vilaagro.api.dto.SalePointResponseDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.AccountStatus;
import com.vilaagro.api.model.SalePoint;
import com.vilaagro.api.model.User;
import com.vilaagro.api.repository.AddressRepository;
import com.vilaagro.api.repository.SalePointRepository;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalePointService {

    private final SalePointRepository salePointRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserService userService; // Para converter User -> UserResponseDTO

    /**
     * Cria um novo Ponto de Venda (RF-D.4.2)
     */
    public SalePointResponseDTO createSalePoint(SalePointCreateDTO createDTO) {
        // Valida se o endereço existe (se fornecido)
        if (createDTO.getAddressId() != null) {
            addressRepository.findById(createDTO.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Endereço", "id", createDTO.getAddressId()));
        }

        SalePoint salePoint = SalePoint.builder()
                .name(createDTO.getName())
                .addressId(createDTO.getAddressId())
                .build();

        SalePoint saved = salePointRepository.save(salePoint);
        return convertToResponseDTO(saved);
    }

    /**
     * Lista todos os Pontos de Venda (RF-D.4.1)
     */
    @Transactional(readOnly = true)
    public List<SalePointResponseDTO> getAllSalePoints() {
        return salePointRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um Ponto de Venda por ID
     */
    @Transactional(readOnly = true)
    public SalePointResponseDTO getSalePointById(UUID id) {
        SalePoint salePoint = salePointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de Venda", "id", id));
        return convertToResponseDTO(salePoint);
    }

    /**
     * Atualiza um Ponto de Venda (RF-D.4.2)
     */
    public SalePointResponseDTO updateSalePoint(UUID id, SalePointCreateDTO updateDTO) {
        SalePoint salePoint = salePointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de Venda", "id", id));

        // Valida o endereço
        if (updateDTO.getAddressId() != null) {
            addressRepository.findById(updateDTO.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Endereço", "id", updateDTO.getAddressId()));
            salePoint.setAddressId(updateDTO.getAddressId());
        }
        if (updateDTO.getName() != null) {
            salePoint.setName(updateDTO.getName());
        }

        SalePoint updated = salePointRepository.save(salePoint);
        return convertToResponseDTO(updated);
    }

    /**
     * Deleta um Ponto de Venda (RF-D.4.2)
     */
    public void deleteSalePoint(UUID id) {
        SalePoint salePoint = salePointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de Venda", "id", id));

        // Regra de Negócio: RN-D.4.1
        // Verifica se há um usuário alocado antes de deletar
        User allocatedUser = findUserBySalePointId(id);
        if (allocatedUser != null) {
            throw new IllegalStateException("Não é possível excluir um ponto de venda que está ocupado.");
        }

        salePointRepository.delete(salePoint);
    }

    /**
     * Aloca um usuário a um Ponto de Venda (RF-D.4.4, RF-D.4.5)
     */
    public UserResponseDTO allocateUserToSalePoint(UUID salePointId, SalePointAllocateDTO allocateDTO) {
        SalePoint salePoint = salePointRepository.findById(salePointId)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de Venda", "id", salePointId));

        User user = userRepository.findById(allocateDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", allocateDTO.getUserId()));

        // Verifica se o ponto já está ocupado
        if (findUserBySalePointId(salePointId) != null) {
            throw new IllegalStateException("Este ponto de venda já está alocado.");
        }

        // Verifica se o usuário já está em outro ponto
        if (user.getSalePointId() != null) {
            throw new IllegalStateException("Este usuário já está alocado em outro ponto de venda.");
        }

        // Regra de Negócio: Usuário deve estar 'APROVADO' (na fila) para ser alocado
        if (user.getDocumentsStatus() != AccountStatus.APPROVED) {
            throw new IllegalStateException("Usuário não está na fila de espera (Status: APROVADO) e não pode ser alocado.");
        }

        // Alocação
        user.setSalePointId(salePoint.getId());

        // Regra de Negócio: RN-D.4.2
        user.setDocumentsStatus(AccountStatus.ACTIVE);

        User allocatedUser = userRepository.save(user);
        return userService.convertToResponseDTO(allocatedUser);
    }

    /**
     * Remove um usuário de um Ponto de Venda
     */
    public UserResponseDTO removeUserFromSalePoint(UUID salePointId) {
        SalePoint salePoint = salePointRepository.findById(salePointId)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de Venda", "id", salePointId));

        User user = findUserBySalePointId(salePoint.getId());

        if (user == null) {
            throw new ResourceNotFoundException("Nenhum usuário encontrado para este Ponto de Venda", "id", salePointId);
        }

        user.setSalePointId(null);
        // Regra de Negócio: Ao desalocar, usuário volta para fila (APROVADO) ou fica INATIVO?
        // Vamos definir como INATIVO por segurança.
        user.setDocumentsStatus(AccountStatus.INACTIVE);

        User updatedUser = userRepository.save(user);
        return userService.convertToResponseDTO(updatedUser);
    }

    // Método auxiliar para converter
    private SalePointResponseDTO convertToResponseDTO(SalePoint salePoint) {
        UserResponseDTO userDTO = null;

        // Como 'allocatedUser' é LAZY, buscamos manualmente
        User user = findUserBySalePointId(salePoint.getId());
        if (user != null) {
            userDTO = userService.convertToResponseDTO(user);
        }

        return SalePointResponseDTO.builder()
                .id(salePoint.getId())
                .name(salePoint.getName())
                .addressId(salePoint.getAddressId())
                .allocatedUser(userDTO)
                .createdAt(salePoint.getCreatedAt())
                .updatedAt(salePoint.getUpdatedAt())
                .build();
    }

    // Método auxiliar para buscar usuário pelo Ponto de Venda
    @Transactional(readOnly = true)
    private User findUserBySalePointId(UUID salePointId) {
        // O repositório de usuário não tem 'findBySalePointId'
        // Precisamos adicioná-lo
        return userRepository.findBySalePointId(salePointId).stream().findFirst().orElse(null);
    }
}