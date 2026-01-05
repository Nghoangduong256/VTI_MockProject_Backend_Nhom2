package com.vti.springdatajpa.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.WalletStatus;
import com.vti.springdatajpa.repository.WalletRepository;
import com.vti.springdatajpa.util.QrGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QRService {

    private final WalletRepository walletRepository;
    private final WalletService walletService;

    public QrResponseDTO generateQrForWallet(Object identity) throws WriterException, IOException {
        var walletInfo = walletService.getWalletInfo(identity);
        
        String payload = QrGeneratorUtil.buildQrPayload(
            walletInfo.getWalletId(),
            walletInfo.getAccountName(),
            walletInfo.getAccountNumber(),
            null,
            null
        );
        
        String qrBase64 = QrGeneratorUtil.generateQrBase64(payload);
        
        QrResponseDTO response = new QrResponseDTO();
        response.setWalletId(walletInfo.getWalletId());
        response.setAccountName(walletInfo.getAccountName());
        response.setAccountNumber(walletInfo.getAccountNumber());
        response.setQrBase64(qrBase64);
        
        return response;
    }

    public byte[] generateQrForDownload(Object identity) throws WriterException, IOException {
        var walletInfo = walletService.getWalletInfo(identity);
        
        String payload = QrGeneratorUtil.buildQrPayload(
            walletInfo.getWalletId(),
            walletInfo.getAccountName(),
            walletInfo.getAccountNumber(),
            null,
            null
        );
        
        return QrGeneratorUtil.generateQrBytes(payload);
    }

    public String generateQrWithAmount(Object identity, QrWithAmountRequest request) throws WriterException, IOException {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        
        var walletInfo = walletService.getWalletInfo(identity);
        
        String payload = QrGeneratorUtil.buildQrPayload(
            walletInfo.getWalletId(),
            walletInfo.getAccountName(),
            walletInfo.getAccountNumber(),
            request.getAmount(),
            null // Remove note
        );
        
        return QrGeneratorUtil.generateQrBase64(payload);
    }

    public ResolveQrResponse resolveQrPayload(ResolveQrRequest request) {
        String payload = request.getQrPayload();
        
        // Validate scheme and version
        if (!payload.startsWith("walletapp://pay?version=1")) {
            return createInvalidResponse();
        }
        
        // Parse query parameters
        Map<String, String> params = parseQueryParams(payload);
        
        String walletId = params.get("walletId");
        if (walletId == null) {
            return createInvalidResponse();
        }
        
        // Find wallet
        Wallet wallet = walletRepository.findByCode(walletId).orElse(null);
        if (wallet == null || wallet.getStatus() != WalletStatus.ACTIVE) {
            return createInvalidResponse();
        }
        
        // Build response
        ResolveQrResponse response = new ResolveQrResponse();
        response.setWalletId(walletId);
        response.setReceiverName(wallet.getUser().getFullName());
        response.setAccountNumber(wallet.getAccountNumber());
        response.setCurrency(wallet.getCurrency() != null ? wallet.getCurrency() : "VND");
        response.setValid(true);
        
        // Parse amount if present
        String amountStr = params.get("amount");
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                response.setAmount(Double.parseDouble(amountStr));
            } catch (NumberFormatException e) {
                response.setAmount(null);
            }
        }
        
        return response;
    }

    public QrImageReadResponse readQrImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Read image
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new RuntimeException("Invalid image format");
        }

        // Decode QR
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        try {
            Result result = new MultiFormatReader().decode(bitmap, null);
            String payload = result.getText();
            
            // Use existing resolve logic
            ResolveQrRequest resolveRequest = new ResolveQrRequest();
            resolveRequest.setQrPayload(payload);
            
            ResolveQrResponse resolveResponse = resolveQrPayload(resolveRequest);
            
            // Convert to QrImageReadResponse
            QrImageReadResponse response = new QrImageReadResponse();
            response.setWalletId(resolveResponse.getWalletId());
            response.setReceiverName(resolveResponse.getReceiverName());
            response.setAccountNumber(resolveResponse.getAccountNumber());
            response.setAmount(resolveResponse.getAmount());
            response.setCurrency(resolveResponse.getCurrency());
            response.setValid(resolveResponse.getValid());
            
            // Set transferReady based on amount presence
            response.setTransferReady(resolveResponse.getValid() && resolveResponse.getAmount() != null);
            
            return response;
            
        } catch (NotFoundException e) {
            // QR not found or unreadable
            QrImageReadResponse response = new QrImageReadResponse();
            response.setValid(false);
            response.setTransferReady(false);
            return response;
        }
    }

    private Map<String, String> parseQueryParams(String payload) {
        Map<String, String> params = new HashMap<>();
        
        // Remove scheme and get query part
        String queryPart = payload.substring(payload.indexOf('?') + 1);
        String[] pairs = queryPart.split("&");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (Exception e) {
                    // Skip invalid pairs
                }
            }
        }
        
        return params;
    }

    private ResolveQrResponse createInvalidResponse() {
        ResolveQrResponse response = new ResolveQrResponse();
        response.setValid(false);
        return response;
    }
}
