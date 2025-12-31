package com.vti.springdatajpa.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class QrGeneratorUtil {

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;

    public static String generateQrBase64(String text) throws WriterException, IOException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        byte[] imageBytes = outputStream.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    public static byte[] generateQrBytes(String text) throws WriterException, IOException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    public static String buildQrPayload(String walletId, String name, String accountNumber, Double amount, String note) {
        StringBuilder payload = new StringBuilder("walletapp://pay?version=1");

        payload.append("&walletId=").append(urlEncode(walletId));
        payload.append("&name=").append(urlEncode(name));
        payload.append("&accountNumber=").append(urlEncode(accountNumber));

        if (amount != null && amount > 0) {
            payload.append("&amount=").append(amount);
        }

        return payload.toString();
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}