package com.vilaagro.api.config;

import com.vilaagro.api.model.Artist;
import com.vilaagro.api.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageSeeder implements CommandLineRunner {

    private final ArtistRepository artistRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("🖼️ Carregando imagens dos artistas...");
        
        try {
            // Banda Sertanejo Raiz
            artistRepository.findAll().stream()
                .filter(artist -> artist.getName().equals("Banda Sertanejo Raiz") && artist.getBanner() == null)
                .findFirst()
                .ifPresent(artist -> {
                    try {
                        byte[] image = loadImage("static/images/sertanejo-banner.jpg");
                        artist.setBanner(image);
                        artistRepository.save(artist);
                        log.info("✅ Banner carregado: Banda Sertanejo Raiz");
                    } catch (IOException e) {
                        log.warn("⚠️ Erro ao carregar imagem para Banda Sertanejo Raiz: {}", e.getMessage());
                    }
                });

            // Grupo de Forró
            artistRepository.findAll().stream()
                .filter(artist -> artist.getName().equals("Grupo de Forró") && artist.getBanner() == null)
                .findFirst()
                .ifPresent(artist -> {
                    try {
                        byte[] image = loadImage("static/images/forro-banner.jpg");
                        artist.setBanner(image);
                        artistRepository.save(artist);
                        log.info("✅ Banner carregado: Grupo de Forró");
                    } catch (IOException e) {
                        log.warn("⚠️ Erro ao carregar imagem para Grupo de Forró: {}", e.getMessage());
                    }
                });

            // DJ Eletrônico
            artistRepository.findAll().stream()
                .filter(artist -> artist.getName().equals("DJ Eletrônico") && artist.getBanner() == null)
                .findFirst()
                .ifPresent(artist -> {
                    try {
                        byte[] image = loadImage("static/images/eletronico-banner.jpg");
                        artist.setBanner(image);
                        artistRepository.save(artist);
                        log.info("✅ Banner carregado: DJ Eletrônico");
                    } catch (IOException e) {
                        log.warn("⚠️ Erro ao carregar imagem para DJ Eletrônico: {}", e.getMessage());
                    }
                });

            log.info("🎉 Imagens carregadas com sucesso!");
            
        } catch (Exception e) {
            log.error("❌ Erro ao carregar imagens: {}", e.getMessage());
        }
    }

    private byte[] loadImage(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getInputStream().readAllBytes();
    }
}
