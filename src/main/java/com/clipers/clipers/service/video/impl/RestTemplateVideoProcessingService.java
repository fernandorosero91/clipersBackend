package com.clipers.clipers.service.video.impl;

import com.clipers.clipers.dto.VideoProcessingResponse;
import com.clipers.clipers.exception.BusinessException;
import com.clipers.clipers.service.video.VideoProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;

/**
 * Implementación de VideoProcessingService usando RestTemplate.
 * Resuelve base URL de forma tolerante a properties y variables de entorno.
 */
@Service
public class RestTemplateVideoProcessingService implements VideoProcessingService {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateVideoProcessingService.class);
    private static final String DEFAULT_BASE_URL = "https://micoservicioprocesarvideo.onrender.com";

    private final RestTemplate restTemplate;
    private final Environment env;

    public RestTemplateVideoProcessingService(RestTemplate restTemplate, Environment env) {
        this.restTemplate = restTemplate;
        this.env = env;
    }

    @Override
    public boolean checkHealth() {
        String baseUrl = resolveBaseUrl();
        String url = ensureNoTrailingSlash(baseUrl) + "/";
        try {
            log.debug("Verificando salud del microservicio de video en endpoint: {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            boolean ok = response.getStatusCode().is2xxSuccessful();
            log.info("Health check microservicio de video: status={}, ok={}", response.getStatusCode(), ok);
            return ok;
        } catch (RestClientResponseException e) {
            log.error("Fallo en health check del microservicio de video. endpoint={}, status={}, body={}",
                    url, e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new BusinessException("Error verificando salud del microservicio de video", e);
        } catch (ResourceAccessException e) {
            log.error("No se pudo acceder al microservicio de video. endpoint={}, causa={}", url, e.getMessage(), e);
            throw new BusinessException("No se pudo acceder al microservicio de video", e);
        } catch (Exception e) {
            log.error("Error inesperado en health check del microservicio de video. endpoint={}, causa={}", url, e.getMessage(), e);
            throw new BusinessException("Error inesperado en health check del microservicio de video", e);
        }
    }

    @Override
    public VideoProcessingResponse uploadVideo(Path filePath) {
        String baseUrl = resolveBaseUrl();
        String url = ensureNoTrailingSlash(baseUrl) + "/upload-video";

        try {
            log.info("Subiendo video para procesamiento. endpoint={}, file={}", url, filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(filePath.toFile()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<VideoProcessingResponse> response = restTemplate.postForEntity(
                    url, requestEntity, VideoProcessingResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Microservicio de video respondió 2xx. status={}", response.getStatusCode());
                return response.getBody();
            }

            log.error("Respuesta no exitosa del microservicio de video. endpoint={}, status={}", url, response.getStatusCode());
            throw new BusinessException("El microservicio de video respondió con estado no exitoso: " + response.getStatusCode());
        } catch (RestClientResponseException e) {
            log.error("Error HTTP al llamar microservicio de video. endpoint={}, status={}, body={}",
                    url, e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new BusinessException("Error HTTP del microservicio de video: " + e.getRawStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Error de acceso al recurso microservicio de video. endpoint={}, causa={}", url, e.getMessage(), e);
            throw new BusinessException("No se pudo acceder al microservicio de video", e);
        } catch (Exception e) {
            log.error("Error inesperado llamando microservicio de video. endpoint={}, causa={}", url, e.getMessage(), e);
            throw new BusinessException("Error inesperado llamando microservicio de video", e);
        }
    }

    private String resolveBaseUrl() {
        String value = env.getProperty("video.processing.service.base-url");
        if (isBlank(value)) {
            value = env.getProperty("VIDEO_PROCESSING_SERVICE_URL");
        }
        if (isBlank(value)) {
            value = DEFAULT_BASE_URL;
        }
        return value;
    }

    private String ensureNoTrailingSlash(String url) {
        if (url == null) return null;
        if (url.endsWith("/")) return url.substring(0, url.length() - 1);
        return url;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}