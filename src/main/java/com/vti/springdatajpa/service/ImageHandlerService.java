package com.vti.springdatajpa.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;

public interface ImageHandlerService {
    private String stripBase64Prefix(String base64) {
        if (base64 == null) return null;

        int commaIndex = base64.indexOf(",");
        return commaIndex != -1
                ? base64.substring(commaIndex + 1)
                : base64;
    }

    private void validateBase64Size(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        int sizeInBytes = decodedBytes.length;

        if (sizeInBytes > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Avatar exceeds 2MB");
        }
    }

    private void validateImageType(byte[] imageBytes) throws IOException {
        String mimeType = URLConnection.guessContentTypeFromStream(
                new ByteArrayInputStream(imageBytes)
        );

        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image format");
        }
    }

}
