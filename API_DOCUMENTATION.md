# API Documentation - E-Wallet Backend System

**Last Updated**: 2026-01-14
**Version**: 3.1

---

## API Summary

### Authentication APIs
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập

### User & Profile APIs  
- `GET /api/user/profile` - Lấy thông tin cá nhân
- `GET /api/me` - Lấy thông tin người dùng hiện tại

### Card APIs
- `GET /api/cards` - Danh sách thẻ
- `POST /api/cards` - Thêm thẻ mới

### Wallet APIs
- `GET /api/wallet/balance` - Xem số dư ví

### Wallet Transfer APIs
- `POST /api/wallet/transfers` - Chuyển tiền giữa các ví
- `GET /api/wallet/transfers/recent` - Lịch sử giao dịch
- `GET /api/wallet/transfers/lookup/{accountNumber}` - Tìm kiếm tài khoản

### Transaction APIs
- `GET /api/transactions` - Lịch sử giao dịch (phân trang)
- `GET /api/transactions/incoming` - Giao dịch đến gần đây
- `POST /api/transactions/transfer` - Chuyển tiền
- `POST /api/transactions` - Nạp tiền (topup)

### Admin APIs
- `GET /api/admin/transactions` - Xem tất cả giao dịch (Admin)

### QR Code APIs
- `GET /api/qr/wallet` - Tạo QR Code cho ví
- `GET /api/qr/wallet/download` - Tải QR Image
- `POST /api/qr/wallet/with-amount` - Tạo QR với số tiền
- `POST /api/qr/resolve` - Giải mã QR Payload
- `POST /api/qr/read-image` - Đọc ảnh QR thành JSON

### E-Wallet Operations
- `POST /api/E-Wallet/deposits` - Nạp tiền vào ví
- `GET /api/E-Wallet/deposits/wallet/{id}` - Xem thông tin ví
- `GET /api/E-Wallet/deposits/wallet/{id}/recent-deposits` - Lịch sử nạp tiền

### Bank Account APIs
- `GET /api/bank-account` - Danh sách tài khoản ngân hàng

---

## 1. Authentication APIs

### 1.1 Register
**Endpoint**: `POST /api/auth/register`

**Request Body**:
```json
{
  "userName": "newuser",
  "email": "newuser@example.com", 
  "phone": "0987654321",
  "fullName": "Nguyen Van New",
  "passwordHash": "123456"
}
```

**Response**:
```json
{
  "message": "User registered successfully",
  "userId": 123,
  "accountNumber": "0987654321"
}
```

### 1.2 Login
**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
  "userName": "testuser",
  "passwordHash": "123456"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 36000000,
  "userId": 123,
  "userName": "testuser",
  "role": "USER"
}
```

---

## 2. Card APIs

### 2.1 Get Cards
**Endpoint**: `GET /api/cards`

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
[
  {
    "id": 1,
    "cardNumber": "**** **** **** 2103",
    "holderName": "NGUYEN VAN A",
    "expiryDate": "05/25",
    "type": "DEBIT",
    "bankName": "Vietcombank",
    "balanceCard": 100000000000000000000.0,
    "status": "ACTIVE",
    "last4": "2103"
  }
]
```

### 2.2 Add Card
**Endpoint**: `POST /api/cards`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "cardNumber": "5702676235112103",
  "holderName": "NGUYEN VAN A",
  "expiryDate": "05/25",
  "cvv": "477",
  "type": "DEBIT",
  "bankName": "Vietcombank",
  "balanceCard": 500000.0
}
```

**Response**:
```json
{
  "id": 123,
  "cardNumber": "**** **** **** 2103",
  "holderName": "NGUYEN VAN A",
  "expiryDate": "05/25",
  "type": "DEBIT",
  "bankName": "Vietcombank",
  "balanceCard": 500000.0,
  "status": "ACTIVE",
  "last4": "2103"
}
```

---

## 3. Wallet APIs

### 3.1 Get Balance
**Endpoint**: `GET /api/wallet/balance`

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
{
  "balance": 5000000.0,
  "monthlyChangePercent": 2.5
}
```

---

## 4. Wallet Transfer APIs

### 4.1 Transfer Money
**Endpoint**: `POST /api/wallet/transfers`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "toAccountNumber": "0987654321",
  "amount": 1000.00,
  "description": "Thanh toán hóa đơn"
}
```

**Response**:
```json
{
  "transactionId": 12345,
  "fromAccountNumber": "0123456789",
  "toAccountNumber": "0987654321",
  "toAccountName": "Nguyen Van B",
  "amount": 1000.00,
  "previousBalance": 5000.00,
  "newBalance": 4000.00,
  "description": "Thanh toán hóa đơn",
  "timestamp": "2026-01-14T01:00:00",
  "status": "SUCCESS",
  "message": "Transfer successful",
  "transactionType": "WALLET_TRANSFER"
}
```

### 4.2 Get Recent Transactions
**Endpoint**: `GET /api/wallet/transfers/recent?limit=10`

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `limit`: Số lượng record (default: 10, max: 100)

**Response**:
```json
[
  {
    "transactionId": 12345,
    "transactionType": "TRANSFER_OUT",
    "direction": "OUT",
    "amount": 1000.00,
    "balanceBefore": 5000.00,
    "balanceAfter": 4000.00,
    "partnerAccountNumber": "0987654321",
    "partnerAccountName": "Nguyen Van B",
    "description": "Thanh toán hóa đơn",
    "timestamp": "2026-01-14T01:00:00",
    "status": "COMPLETED",
    "referenceId": "0987654321"
  }
]
```

### 4.3 Lookup Account
**Endpoint**: `GET /api/wallet/transfers/lookup/{accountNumber}`

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `accountNumber`: Số tài khoản cần tìm

**Response**:
```json
{
  "accountNumber": "0987654321",
  "accountName": "Nguyen Van B",
  "accountType": "WALLET",
  "active": true,
  "found": true,
  "message": "Account found"
}
```

---

## 5. Transaction APIs

### 5.1 Get Transactions
**Endpoint**: `GET /api/transactions?page=0&size=10`

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `page`: Số trang (default: 0)
- `size`: Số lượng record (default: 10)

**Response**:
```json
{
  "content": [
    {
      "id": 123,
      "type": "TRANSFER",
      "amount": 1000.00,
      "date": "2026-01-14T01:00:00",
      "status": "COMPLETED",
      "category": "General",
      "direction": "OUT"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### 5.2 Transfer Money
**Endpoint**: `POST /api/transactions/transfer`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "toWalletId": 456,
  "amount": 1000.00,
  "description": "Chuyển tiền"
}
```

**Response**:
```json
{
  "transactionId": 12345,
  "fromWalletId": 123,
  "toWalletId": 456,
  "amount": 1000.00,
  "description": "Chuyển tiền",
  "timestamp": "2026-01-14T01:00:00",
  "status": "SUCCESS"
}
```

---

## 6. Admin APIs

### 6.1 Get All Transactions
**Endpoint**: `GET /api/admin/transactions`

**Query Parameters**:
- `page`: Số trang (default: 0)
- `size`: Số lượng record (default: 50)

**Response**:
```json
[
  {
    "transactionId": "TXN-882941",
    "walletId": "WAL-00923",
    "amount": 1200,
    "status": "COMPLETE",
    "type": "DEPOSIT",
    "createdAt": "2023-11-20T14:35:00"
  }
]
```

---

## 7. QR Code APIs

### 7.1 Generate QR Code
**Endpoint**: `GET /api/qr/wallet`

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
{
  "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "walletCode": "WALLET123",
  "accountNumber": "0987654321"
}
```

### 7.2 Generate QR with Amount
**Endpoint**: `POST /api/qr/wallet/with-amount`

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "amount": 100000.00
}
```

**Response**:
```json
{
  "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "walletCode": "WALLET123",
  "accountNumber": "0987654321",
  "amount": 100000.00
}
```

### 7.3 Resolve QR
**Endpoint**: `POST /api/qr/resolve`

**Request Body**:
```json
{
  "qrData": "WALLET123"
}
```

**Response**:
```json
{
  "walletCode": "WALLET123",
  "accountNumber": "0987654321",
  "amount": null
}
```

---

## 8. Error Responses

### Common Error Format
```json
{
  "status": "FAILED",
  "message": "Error description",
  "timestamp": 1641748400000
}
```

### HTTP Status Codes
- `200 OK`: Request thành công
- `400 Bad Request`: Dữ liệu đầu vào không hợp lệ
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn
- `403 Forbidden`: Không có quyền truy cập
- `404 Not Found`: Resource không tồn tại
- `500 Internal Server Error`: Lỗi server

---

## 9. Testing Examples

### Add Card
```bash
curl -X POST http://localhost:8080/api/cards \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "5702676235112103",
    "holderName": "NGUYEN VAN A",
    "expiryDate": "05/25",
    "cvv": "477",
    "type": "DEBIT",
    "bankName": "Vietcombank"
  }'
```

### Wallet Transfer
```bash
curl -X POST http://localhost:8080/api/wallet/transfers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "toAccountNumber": "0987654321",
    "amount": 100000,
    "description": "Chuyển tiền test"
  }'
```

### Admin Get All Transactions
```bash
curl -X GET "http://localhost:8080/api/admin/transactions?page=0&size=50" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

---

**API Documentation Updated: 2026-01-14**
**Version: 3.1**
