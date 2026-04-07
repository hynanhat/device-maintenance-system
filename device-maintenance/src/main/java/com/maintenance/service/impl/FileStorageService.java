package com.maintenance.service.impl;

import com.maintenance.dto.response.ApiResponse;
import com.maintenance.entity.MediaFile;
import com.maintenance.entity.User;
import com.maintenance.exception.BadRequestException;
import com.maintenance.repository.MediaFileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.maintenance.exception.ResourceNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    private final MediaFileRepository mediaFileRepository;
    private final UserService userService;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg","image/png","image/gif","image/webp");
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(uploadDir + "/images"));
            Files.createDirectories(Paths.get(uploadDir + "/avatars"));
            log.info("Upload directories initialized at: {}", uploadDir);
        } catch (IOException e) {
            log.error("Could not create upload directories: {}", e.getMessage());
        }
    }

    @Transactional
    public Map<String, String> uploadFile(MultipartFile file, String entityType, Long entityId) {
        validateFile(file);
        try {
            User currentUser = userService.getCurrentUser();
            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
            String storedName = UUID.randomUUID() + ext;
            String subDir = ALLOWED_IMAGE_TYPES.contains(file.getContentType()) ? "/images/" : "/files/";
            Path targetPath = Paths.get(uploadDir + subDir + storedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = baseUrl + "/files/" + storedName;
            MediaFile mediaFile = MediaFile.builder()
                    .originalName(originalName)
                    .storedName(storedName)
                    .filePath(targetPath.toString())
                    .fileUrl(fileUrl)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .entityType(entityType)
                    .entityId(entityId)
                    .uploadedBy(currentUser)
                    .build();
            mediaFileRepository.save(mediaFile);

            Map<String, String> result = new HashMap<>();
            result.put("fileName", storedName);
            result.put("fileUrl", fileUrl);
            result.put("originalName", originalName);
            return result;
        } catch (IOException e) {
            throw new BadRequestException("Lỗi khi lưu file: " + e.getMessage());
        }
    }

    public Path loadFile(String fileName) {
        try {
            // 1. Chuẩn hóa đường dẫn thư mục gốc (uploads)
            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // 2. Danh sách các thư mục con cho phép
            String[] subDirs = {"/images/", "/avatars/", "/files/"};

            for (String subDir : subDirs) {
                // Chuẩn hóa đường dẫn file được yêu cầu
                Path filePath = Paths.get(uploadDir + subDir + fileName).toAbsolutePath().normalize();

                // 3. CHỐT CHẶN BẢO MẬT: Đường dẫn file phải bắt đầu bằng đường dẫn gốc
                // Nếu hacker cố tình dùng ../ để lùi thư mục, startWith sẽ trả về false
                if (!filePath.startsWith(basePath)) {
                    throw new BadRequestException("Bảo mật: Tên file chứa đường dẫn không hợp lệ!");
                }

                if (Files.exists(filePath)) {
                    return filePath;
                }
            }
            throw new ResourceNotFoundException("Không tìm thấy file: " + fileName);
        } catch (Exception e) {
            throw new BadRequestException("Lỗi truy xuất file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("File không được để trống");
        if (file.getSize() > MAX_SIZE) throw new BadRequestException("File vượt quá 10MB");
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Chỉ chấp nhận file ảnh (JPEG, PNG, GIF, WebP)");
        }
    }
}
