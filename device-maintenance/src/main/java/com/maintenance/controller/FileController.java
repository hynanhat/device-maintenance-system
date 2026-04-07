package com.maintenance.controller;

import com.maintenance.service.impl.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            Path path = fileStorageService.loadFile(fileName);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) return ResponseEntity.notFound().build();
            String contentType = "application/octet-stream";
            String fn = fileName.toLowerCase();
            if (fn.endsWith(".jpg") || fn.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (fn.endsWith(".png")) contentType = "image/png";
            else if (fn.endsWith(".gif")) contentType = "image/gif";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
