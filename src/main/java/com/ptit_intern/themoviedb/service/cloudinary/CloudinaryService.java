package com.ptit_intern.themoviedb.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private final Cloudinary cloudinary;
    public record UploadResult(String secureUrl,String publicId){

    }

    public void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Ảnh không được rỗng.");
        }
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Định dạng ảnh không hợp lệ. Hỗ trợ: JPG, PNG, WEBP.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Ảnh vượt quá 5MB.");
        }
    }

    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("URL ảnh không hợp lệ");
        }
        try {
            // bỏ query params
            int qIdx = imageUrl.indexOf('?');
            String cleaned = (qIdx != -1) ? imageUrl.substring(0, qIdx) : imageUrl;

            // tìm /upload/
            int uploadIdx = cleaned.indexOf("/upload/");
            String after = (uploadIdx != -1)
                    ? cleaned.substring(uploadIdx + "/upload/".length())
                    : cleaned.substring(cleaned.lastIndexOf('/') + 1);

            // nếu có version /v12345/ thì lấy phần sau version
            Pattern versionPattern = Pattern.compile("^.*?/v\\d+/(.*)$");
            Matcher m = versionPattern.matcher(after);
            if (m.matches()) {
                after = m.group(1);
            }

            // bỏ extension
            int dot = after.lastIndexOf('.');
            if (dot != -1) after = after.substring(0, dot);

            return after;
        } catch (Exception e) {
            log.error("Lỗi khi extract public id từ url: {}", imageUrl, e);
            throw new IllegalArgumentException("Lỗi khi extract public id từ url");
        }
    }

    public UploadResult uploadFileWithPublicId(MultipartFile multipartFile, UploadOptions uploadOptions) throws IOException {
        validateImage(multipartFile);
        if (uploadOptions == null) uploadOptions = new UploadOptions();

        File tempFile = File.createTempFile("temp_upload_", multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(tempFile);
            @SuppressWarnings("unchecked")
            Map<String,Object> result = cloudinary.uploader().upload(tempFile, uploadOptions.toMap());
            String secureUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            return new UploadResult(secureUrl, publicId);
        } catch (IOException ex) {
            log.error("Upload ảnh thất bại", ex);
            throw new IOException("Upload ảnh thất bại", ex);
        } finally {
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (Exception e) {
                log.warn("Không thể xóa temp file {}", tempFile.getAbsolutePath(), e);
                throw new IOException("Không thể xóa temp file"+tempFile.getAbsolutePath());
            }
        }
    }

    public void deleteImageByPublicId(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            log.error("Xóa ảnh theo publicId thất bại: {}", publicId, e);
            throw new RuntimeException("Xóa ảnh thất bại", e);
        }
    }
    public void deleteImageByUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null && !publicId.isBlank()) {
                deleteImageByPublicId(publicId);
            } else {
                log.warn("Không thể extract publicId từ URL: {}", imageUrl);
                throw new RuntimeException("Không thể extract publicId từ URL");
            }
        } catch (Exception e) {
            log.error("Xoá ảnh theo URL thất bại: {}", imageUrl, e);
            throw new RuntimeException("Xoá ảnh thất bại", e);
        }
    }
}
