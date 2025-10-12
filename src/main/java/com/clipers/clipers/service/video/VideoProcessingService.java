package com.clipers.clipers.service.video;

import com.clipers.clipers.dto.VideoProcessingResponse;

import java.nio.file.Path;

/**
 * Servicio para encapsular la integración con el microservicio de procesamiento de video.
 * Mantiene compatibilidad binaria delegando desde el servicio existente.
 */
public interface VideoProcessingService {

    /**
     * Sube un video al microservicio para su procesamiento.
     * @param filePath Ruta del archivo de video en el sistema de archivos.
     * @return Respuesta con transcripción y perfil procesado.
     */
    VideoProcessingResponse uploadVideo(Path filePath);

    /**
     * Verifica la salud del microservicio (GET baseUrl + "/").
     * @return true si responde 200 OK, false en otro caso.
     */
    boolean checkHealth();
}