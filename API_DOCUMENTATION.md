# API Documentation - E-Wallet Backend System

**Last Updated**: 2025-01-07
**Version**: 3.1

**Major Updates in v3.1**:
- ✅ **Enhanced Card Deposit Validation**: Full validation with min/max amounts, status checks
- ✅ **Comprehensive Error Handling**: Detailed error responses with context
- ✅ **Transaction Safety**: @Transactional with rollback on errors
- ✅ **Complete API Coverage**: All 35 endpoints fully documented
- ✅ **Flexible Authentication**: Support User object and String JWT identity
- ✅ **Real Database Storage**: Card deposits with complete history tracking

**Previous Major Updates (v3.0)**:
- ✅ **Dashboard APIs**: Financial Summary & Spending Analytics
- ✅ **Card & Deposit System**: Complete card balance tracking & deposit history
- ✅ **Enhanced User Profile**: Profile update & avatar management
- ✅ **Transfer & Withdraw APIs**: Full E-Wallet operations

**Key Features**:
- **Phone Number = Account Number** throughout entire system
- **QR Code contains accountNumber and amount (no note)**
- **Base64 QR format**: `data:image/png;base64,iVBORw0KGgo...`
- **Consistent DTO responses across all APIs**
- **Flexible JWT Authentication**: Handle both User object and String identity
- **Complete Card Management**: Balance tracking & deposit history
- **Dashboard Analytics**: Income/expense & spending charts
- **Full CRUD Operations**: Users, Wallets, Transactions, Cards
- **Robust Validation**: Comprehensive input validation for all APIs
- **Transaction Safety**: Atomic operations with rollback support

**System Statistics**:
- **Total APIs**: 35 endpoints across 14 controllers
- **New Files**: 8 (entities, DTOs, services, repositories)
- **Enhanced Files**: 15+ with validation and error handling
- **Documentation**: 1000+ lines with complete API specs

---

## API Summary (Tổng quan tất cả APIs)

### Authentication APIs
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập

### User & Profile APIs  
- `GET /api/user/profile` - Lấy thông tin cá nhân
- `PUT /api/user/profile` - Cập nhật thông tin cá nhân
- `PUT /api/user/profile/avatar` - Cập nhật avatar
- `GET /api/me` - Lấy thông tin người dùng hiện tại

### Wallet APIs
- `GET /api/wallet/balance` - Xem số dư
- `GET /api/wallet/me` - Lấy thông tin ví đầy đủ **[QR Controller]**

### QR Code APIs **[NEW]**
- `GET /api/qr/wallet` - Tạo QR Code cho ví (Base64)
- `GET /api/qr/wallet/download` - Tải QR Image
- `POST /api/qr/wallet/with-amount` - Tạo QR với số tiền cố định
- `POST /api/qr/resolve` - Giải mã QR Payload
- `POST /api/qr/read-image` - Đọc ảnh QR thành JSON **[NEW]**

### Transaction APIs
- `GET /api/transactions` - Lịch sử giao dịch (phân trang)
- `POST /api/transactions` - Tạo giao dịch (topup)
- `POST /api/transactions/transfer` - Chuyển tiền
- `GET /api/transactions/incoming` - Giao dịch đến gần đây **[NEW]**

### E-Wallet Operations
- `POST /api/E-Wallet/deposits` - Nạp tiền vào ví
- `GET /api/E-Wallet/deposits/wallet/{id}` - Xem thông tin ví
- `GET /api/E-Wallet/deposits/wallet-by-username/{userName}` - Lấy ví theo username
- `GET /api/E-Wallet/deposits/wallet/{id}/recent-deposits` - Lịch sử nạp tiền gần đây

### Dashboard APIs **[NEW]**
- `GET /api/wallet/summary` - Financial Summary (Income/Expense)
- `GET /api/analytics/spending` - Spending Analytics (Chart Data)

### Card & Deposit APIs  
- `GET /api/cards` - Danh sách thẻ (với balanceCard)
- `POST /api/cards` - Thêm thẻ mới (balance mặc định 100,000)
- `POST /api/cards/deposit` - Nạp tiền từ thẻ vào ví
- `GET /api/cards/deposit/history` - Lịch sử nạp tiền từ thẻ

### Transfer APIs **[E-Wallet]**
- `GET /api/E-Wallet/transfers/wallet/{walletId}/history` - Lịch sử chuyển tiền
- `POST /api/E-Wallet/transfers` - Tạo chuyển tiền
- `GET /api/E-Wallet/transfers/wallet/{walletId}` - Thông tin ví
- `GET /api/E-Wallet/transfers/{transferId}` - Chi tiết chuyển tiền

### Withdraw APIs
- `POST /api/wallets/{walletId}/withdraw` - Rút tiền từ ví

### Bank Account APIs
- `GET /api/bank-account` - Danh sách tài khoản ngân hàng

### Contact APIs
- `GET /api/contacts/frequent` - Danh sách chuyển tiền nhanh

---

## 1. Authentication (Xác thực)

### Đăng ký tài khoản (Register)
- **Mô tả**: Tạo tài khoản người dùng mới với số tài khoản tự động từ số điện thoại.
- **Endpoint**: `POST /api/auth/register`
- **Đầu vào (Request Body)**:
  ```json
  {
    "userName": "newuser",
    "email": "newuser@example.com",
    "phone": "0987654321",
    "fullName": "Nguyen Van New",
    "passwordHash": "123456" 
  }
  ```
  *(Lưu ý: Trường `passwordHash` chứa mật khẩu dạng raw text từ client, server sẽ mã hóa sau)*
- **Đầu ra**:
  ```json
  {
    "message": "User registered successfully",
    "userId": 123,
    "accountNumber": "0987654321",
    "walletId": "WALLET123"
  }
  ```
- **Logic**:
  - Kiểm tra trùng username, email, phone.
  - Mã hóa mật khẩu.
  - Tạo User mới với role USER.
  - **Tự động tạo số tài khoản = số điện thoại đăng ký**
  - Tạo Wallet liên kết với User.
  - Trả về thông tin tài khoản vừa tạo.
- **Data Transfer**: Sử dụng `RegisterForm` DTO thay vì `User` entity trực tiếp.

### Đăng nhập (Login)
- **Mô tả**: Xác thực người dùng và trả về JWT token để truy cập các API khác.
- **Endpoint**: `POST /api/auth/login`
- **Đầu vào (Request Body)**:
  ```json
  {
    "userName": "user",
    "password": "123456"
  }
  ```
- **Đầu ra (Response Body)**:
  ```json
  {
    "token": "eyJhbGciOiJIUz...",
    "type": "Bearer",
    "expiresIn": 86400000,
    "userName": "user",
    "email": "user@example.com",
    "fullName": "Nguyen Van User",
    "roles": ["USER"]
  }
  ```
- **Logic**: 
  - Kiểm tra userName và password trong database.
  - Nếu đúng, sinh JWT token.
  - Nếu sai, trả về lỗi 401.

### Cập nhật thông tin cá nhân
- **Mô tả**: Cập nhật thông tin profile của người dùng (firstName, lastName, phone, dateOfBirth, address).
- **Endpoint**: `PUT /api/user/profile`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "firstName": "Nguyen Van",
    "lastName": "Updated",
    "phone": "0987654321",
    "dateOfBirth": "1990-01-15",
    "address": "123 Nguyen Hue, Q1, HCMC"
  }
  ```
- **Validation**:
  - `firstName`, `lastName`: Optional, có thể null
  - `phone`: Optional, format phone number
  - `dateOfBirth`: Optional, format `yyyy-MM-dd`
  - `address`: Optional, text address
- **Đầu ra**:
  ```json
  {
    "message": "Profile updated successfully"
  }
  ```
- **Logic**:
  - Combine firstName + lastName → fullName
  - Update các field được cung cấp
  - Auto set updatedAt timestamp

### Cập nhật Avatar (Base64 Image)
- **Mô tả**: Cập nhật ảnh đại diện của người dùng. Hình ảnh được lưu dưới dạng Base64 thuần.
- **Endpoint**: `PUT /api/user/profile/avatar`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Request Body Options**:

#### **Option 1: Base64 String (Recommended)**
```json
{
  "avatar": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg=="
}
```

#### **Option 2: URL String**
```json
{
  "avatar": "https://example.com/new-avatar.jpg"
}
```

#### **Option 3: Full Base64 with Prefix (Auto-stripped)**
```json
{
  "avatar": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg=="
}
```

- **Validation**:
  - `avatar`: Required, not blank
  - Auto-strip `data:image/...;base64,` prefix nếu có
  - Lưu vào database dưới dạng Base64 thuần
- **Đầu ra**:
  ```json
  {
    "message": "Avatar updated successfully"
  }
  ```
- **Error Responses**:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Avatar URL is required",
    "path": "/api/user/profile/avatar"
  }
  ```
- **Logic**:
  - Check avatar không null/blank
  - Strip prefix `data:image/...;base64,` nếu có
  - Lưu Base64 thuần vào database
  - Update timestamp

### Lấy thông tin cá nhân (With Avatar)
- **Mô tả**: Lấy thông tin chi tiết của người dùng đang đăng nhập để hiển thị trên Dashboard.
- **Endpoint**: `GET /api/user/profile`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "fullName": "Nguyen Van Updated",
    "email": "user@vti.com",
    "userName": "user",
    "avatarUrl": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg==",
    "membership": "Gold"
  }
  ```
- **Logic**:
  - Lấy username từ Security Context (token)
  - Truy vấn bảng `users` để lấy thông tin
  - Return Base64 string trong `avatarUrl` field
  - Frontend có thể hiển thị: `data:image/png;base64,{avatarUrl}`

### Lấy thông tin người dùng hiện tại
- **Mô tả**: Lấy thông tin đầy đủ của người dùng đang đăng nhập.
- **Endpoint**: `GET /api/me`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "id": 2,
    "email": "user@vti.com",
    "fullName": "Nguyen Van User",
    "phone": "0987654321",
    "username": "user",
    "wallet": {
      "id": 2,
      "balance": 1500000.0,
      "currency": "VND",
      "status": "ACTIVE"
    }
  }
  ```
- **Logic**: Direct User object mapping with wallet information.

---

## 3. Wallet (Ví điện tử)

### Xem số dư
- **Mô tả**: Lấy số dư hiện tại của ví và phần trăm thay đổi (dummy) để hiển thị biểu đồ.
- **Endpoint**: `GET /api/wallet/balance`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "balance": 1500000.0,
    "monthlyChangePercent": 2.5
  }
  ```
- **Logic**:
  - Tìm ví (`wallets`) thuộc về người dùng hiện tại.
  - Trả về `balance` và số liệu thống kê.

---

## 4. QR Code APIs

### Lấy thông tin ví đầy đủ
- **Mô tả**: Lấy thông tin chi tiết ví của người dùng hiện tại.
- **Endpoint**: `GET /api/wallet/me`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "accountName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "currency": "VND",
    "balance": 1500000.0
  }
  ```
- **Logic**:
  - Lấy username từ SecurityContext
  - Join users → wallets
  - Mapping DTO với account number (số điện thoại đăng ký)
  - Không trả internal ID

### Tạo QR Code cho ví (Base64)
- **Mô tả**: Tạo QR code chứa thông tin nhận tiền, trả về dạng Base64.
- **Endpoint**: `GET /api/qr/wallet`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **QR Payload Format**:
  ```
  walletapp://pay?version=1&walletId=WALLET001&name=Nguyen%20Van%20User&accountNumber=0987654321
  ```
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "accountName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "qrBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
  ```
- **Logic**:
  - Auth user từ JWT token
  - Lấy wallet information với account number (số điện thoại)
  - Build payload string bao gồm accountNumber
  - Generate QR image và encode Base64 với prefix `data:image/png;base64,`

### Tải QR Image
- **Mô tả**: Tải QR code dạng file ảnh PNG.
- **Endpoint**: `GET /api/qr/wallet/download`
- **Đầu ra**: 
  - Content-Type: `image/png`
  - Content-Disposition: `attachment; filename="wallet-qr.png"`
  - Binary image data
- **Logic**:
  - Reuse logic tạo QR
  - Stream image về client (không Base64)

### Tạo QR với số tiền cố định
- **Mô tả**: Tạo QR code có kèm số tiền.
- **Endpoint**: `POST /api/qr/wallet/with-amount`
- **Đầu vào (Request Body)**:
  ```json
  {
    "amount": 50000
  }
  ```
- **QR Payload**:
  ```
  walletapp://pay?version=1&walletId=WALLET001&name=Nguyen%20Van%20User&accountNumber=0987654321&amount=50000
  ```
- **Đầu ra**:
  ```json
  {
    "qrBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
  ```
- **Logic**:
  - Validate amount > 0
  - **Không cần ghi chú (note)**
  - Generate QR runtime với accountNumber và amount
  - Return proper DTO with qrBase64 field

### Giải mã QR Payload
- **Mô tả**: Giải mã và xác thực thông tin từ QR code khi scan.
- **Endpoint**: `POST /api/qr/resolve`
- **Đầu vào (Request Body)**:
  ```json
  {
    "qrPayload": "walletapp://pay?walletId=WALLET001&accountNumber=0987654321&amount=50000"
  }
  ```
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "receiverName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "amount": 50000,
    "currency": "VND",
    "valid": true
  }
  ```
- **Logic**:
  - Parse payload parameters
  - Validate scheme và version
  - Check wallet tồn tại và ACTIVE
  - Return info bao gồm accountNumber

### Đọc ảnh QR thành JSON
- **Mô tả**: Đọc file ảnh QR (.png) và chuyển thành thông tin JSON để chuyển khoản.
- **Endpoint**: `POST /api/qr/read-image`
- **Đầu vào**: Multipart form data với file ảnh QR
  ```
  Content-Type: multipart/form-data
  file: [QR image file .png]
  ```
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "receiverName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "amount": 50000,
    "currency": "VND",
    "valid": true,
    "transferReady": true
  }
  ```
- **Logic**:
  - Nhận file ảnh QR từ client
  - Decode QR image để extract payload
  - Parse và validate payload
  - Return JSON thông tin sẵn sàng để chuyển khoản
  - Nếu amount có sẵn, set transferReady = true

---

## 5. Incoming Transactions (Widget)

### Lấy giao dịch đến gần đây
- **Mô tả**: Lấy danh sách giao dịch đến cho widget bên phải.
- **Endpoint**: `GET /api/transactions/incoming?limit=5`
- **Đầu vào**: 
  - Query param `limit` (mặc định 5)
  - Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    {
      "id": 1,
      "type": "TRANSFER_IN",
      "amount": 50000.0,
      "date": "2024-01-15T10:30:00",
      "status": "COMPLETED",
      "description": "Chuyển tiền vào"
    },
    {
      "id": 2,
      "type": "DEPOSIT",
      "amount": 100000.0,
      "date": "2024-01-14T15:20:00",
      "status": "COMPLETED",
      "description": "Nạp tiền vào ví"
    }
  ]
  ```
- **Logic**:
  - Get user from JWT token
  - Find wallet by user id
  - Filter transactions with direction = IN
  - Order by createdAt DESC
  - Apply limit parameter
  - Map description theo transaction type:
    - `TRANSFER_IN` → "Chuyển tiền vào"
    - `DEPOSIT` → "Nạp tiền vào ví"
    - Default → "Giao dịch vào"

---

## 6. Cards (Thẻ ngân hàng)

### Danh sách thẻ
- **Mô tả**: Lấy danh sách các thẻ/tài khoản ngân hàng đã liên kết.
- **Endpoint**: `GET /api/cards`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    {
      "id": "uuid...",
      "last4": "3210",
      "holderName": "NGUYEN VAN USER",
      "type": "Debit",
      "bankName": "TPBank",
      "status": "ACTIVE",
      "expirationDate": "12/25",
      "balanceCard": 95000.0
    }
  ]
  ```
- **Logic**:
  - Get user from JWT token
  - Return cards thuộc về user đó
  - Include `balanceCard` field (số dư thẻ)

### Thêm thẻ mới
- **Mô tả**: Thêm thẻ ngân hàng mới vào tài khoản.
- **Endpoint**: `POST /api/cards`
- **Đầu vào**:
  ```json
  {
    "cardNumber": "4111111111111234",
    "holderName": "Nguyen Van A",
    "expiryDate": "12/25",
    "cvv": "123",
    "type": "Debit",
    "bankName": "Vietcombank"
  }
  ```
- **Đầu ra**: Card object với `balanceCard = 100000.0` (mặc định)
- **Logic**:
  - Validate card info
  - Set default balance = 100,000
  - Link to authenticated user

### Nạp tiền từ thẻ vào ví
- **Mô tả**: Nạp tiền từ thẻ đã liên kết vào ví điện tử.
- **Endpoint**: `POST /api/cards/deposit`
- **Đầu vào**:
  ```json
  {
    "cardId": 1,
    "amount": 50000.50,
    "description": "Nạp tiền từ thẻ VCB"
  }
  ```
- **Validation Rules**:
  - `cardId`: Required, phải thuộc về user đang đăng nhập
  - `amount`: Required, phải > 0, maximum 5,000,000 USD
  - Card phải ở trạng thái ACTIVE
  - Card balance phải >= amount
  - Wallet phải ở trạng thái ACTIVE
- **Success Response**:
  ```json
  {
    "transactionId": 123,
    "cardId": 1,
    "cardNumber": "**** **** **** 1234",
    "amount": 50000.50,
    "previousCardBalance": 1000000.00,
    "newCardBalance": 950000.00,
    "previousWalletBalance": 1000000.00,
    "newWalletBalance": 1050000.50,
    "description": "Nạp tiền từ thẻ VCB",
    "timestamp": "2024-01-15T10:30:00",
    "status": "SUCCESS",
    "message": "Deposit successful"
  }
  ```
- **Error Responses**:
  ```json
  {
    "transactionId": null,
    "cardId": 1,
    "cardNumber": "**** **** **** 1234",
    "amount": null,
    "previousCardBalance": null,
    "newCardBalance": null,
    "previousWalletBalance": null,
    "newWalletBalance": null,
    "description": null,
    "timestamp": "2024-01-15T10:30:00",
    "status": "FAILED",
    "message": "Insufficient card balance. Available: 30000.00 USD"
  }
  ```
- **Logic**:
  - Validate tất cả input parameters
  - Check card ownership và status
  - Validate card balance >= amount
  - Validate wallet status
  - Update card balance (trừ tiền)
  - Update wallet balance (cộng tiền)
  - Transaction atomic (@Transactional)
  - Lưu cả SUCCESS và FAILED records
  - Error handling với detailed messages
  - **Currency**: USD throughout system

### Lịch sử nạp tiền từ thẻ
- **Mô tả**: Lấy lịch sử các lần nạp tiền từ thẻ vào ví.
- **Endpoint**: `GET /api/cards/deposit/history`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    {
      "transactionId": 1,
      "cardId": 1,
      "cardNumber": "**** **** **** 1234",
      "bankName": "Vietcombank",
      "amount": 50000,
      "description": "Nạp tiền từ thẻ VCB",
      "timestamp": "2024-01-15T10:30:00",
      "status": "SUCCESS"
    },
    {
      "transactionId": 2,
      "cardId": 1,
      "cardNumber": "**** **** **** 1234",
      "bankName": "Vietcombank",
      "amount": 30000,
      "description": "Nạp tiền lần 2",
      "timestamp": "2024-01-14T15:20:00",
      "status": "FAILED"
    }
  ]
  ```
- **Logic**:
  - Query `card_deposits` table cho authenticated user
  - Order by `timestamp DESC` (mới nhất trước)
  - Return masked card info (**** **** **** 1234)
  - Include cả SUCCESS và FAILED transactions
  - **Real database storage** - không phải empty list

---

## 7. Contacts (Danh bạ thụ hưởng)

### Danh sách chuyển tiền nhanh
- **Mô tả**: Lấy danh sách người nhận thường xuyên (Frequent Contacts) cho chức năng Quick Transfer.
- **Endpoint**: `GET /api/contacts/frequent?limit=5`
- **Đầu vào**: 
  - Query param `limit` (mặc định 5).
  - Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    {
      "id": "uuid...",
      "name": "Friend One",
      "avatarUrl": "https://..."
    }
  ]
  ```
- **Logic**:
  - Truy vấn bảng `contacts` của user, giới hạn số lượng trả về.

---

## 8. Transactions (Giao dịch)

### Lịch sử giao dịch
- **Mô tả**: Lấy danh sách giao dịch có phân trang.
- **Endpoint**: `GET /api/transactions?page=0&size=10`
- **Đầu vào**: Params `page`, `size`.
- **Đầu ra**:
  ```json
  {
    "content": [
      {
        "id": "uuid...",
        "type": "DEPOSIT",
        "category": "General",
        "amount": 500000.0,
        "date": "2024-12-20T...",
        "status": "COMPLETED",
        "direction": "IN"
      }
    ],
    ...
  }
  ```
- **Logic**:
  - Lấy tất cả giao dịch trong bảng `transactions` thuộc ví của user.
  - Sắp xếp theo ngày tạo mới nhất.

### Chuyển tiền (Transfer)
- **Mô tả**: Chuyển tiền từ ví người dùng sang người dùng khác trong hệ thống.
- **Endpoint**: `POST /api/transactions/transfer`
- **Đầu vào**:
  ```json
  {
    "toUserId": 123,
    "amount": 50000.0,
    "note": "Tien an trua"
  }
  ```
  *(Lưu ý: `toUserId` là ID (Integer) của người nhận, không phải username)*
- **Đầu ra**: 200 OK (Empty Body)
- **Logic**:
  - Kiểm tra số dư ví người gửi (`balance` >= `amount`).
  - Trừ tiền ví người gửi, cộng tiền ví người nhận.
  - Tạo 2 bản ghi `Transaction`: 1 bản ghi `TRANSFER_OUT` cho người gửi, 1 bản ghi `TRANSFER_IN` cho người nhận.
  - Sử dụng `@Transactional`.

### Nạp tiền (Topup)
- **Mô tả**: Nạp tiền vào ví từ thẻ liên kết.
- **Endpoint**: `POST /api/transactions`
- **Đầu vào**:
  ```json
  {
    "type": "topup",
    "amount": 100000.0,
    "sourceCardId": "uuid-card"
  }
  ```
- **Đầu ra**: 200 OK
- **Logic**:
  - Cộng tiền vào ví, tạo bản ghi `Transaction` loại `DEPOSIT`.
  - Chỉ hỗ trợ `type`: "topup" hiện tại.

---

## 9. E-Wallet Operations (Ví điện tử)

### Nạp tiền vào ví (Deposit)
- **Mô tả**: Nạp tiền vào ví từ nguồn bên ngoài.
- **Endpoint**: `POST /api/E-Wallet/deposits`
- **Đầu vào (Request Body)**:
  ```json
  {
    "walletId": 1,
    "amount": 100000.0
  }
  ```
- **Đầu ra**:
  ```json
  {
    "message": "Deposit successful",
    "newBalance": 1600000.0
  }
  ```
- **Logic**: 
  - Cộng tiền vào ví balance
  - Tạo transaction record loại DEPOSIT

### Xem thông tin ví
- **Mô tả**: Lấy thông tin chi tiết của ví theo ID.
- **Endpoint**: `GET /api/E-Wallet/deposits/wallet/{id}`
- **Đầu ra**:
  ```json
  {
    "id": 1,
    "code": "WALLET001",
    "currency": "VND",
    "balance": 1500000.0,
    "availableBalance": 1500000.0,
    "status": "ACTIVE"
  }
  ```

### Lịch sử nạp tiền gần đây
- **Mô tả**: Lấy lịch sử các giao dịch nạp tiền gần đây của ví.
- **Endpoint**: `GET /api/E-Wallet/deposits/wallet/{id}/recent-deposits`
- **Đầu ra**:
  ```json
  [
    {
      "id": 1,
      "amount": 500000.0,
      "referenceId": "DEMO_DEPOSIT_1640995200000",
      "status": "COMPLETED",
      "createdAt": "2024-01-15T10:30:00"
    }
  ]
  ```

---

## 10. User Profile & Account Management

### Lấy thông tin người dùng hiện tại
- **Mô tả**: Lấy thông tin chi tiết của người dùng đang đăng nhập.
- **Endpoint**: `GET /api/me`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "id": 2,
    "email": "user@vti.com",
    "fullName": "Nguyen Van User",
    "phone": "0987654321",
    "username": "user",
    "wallet": {
      "id": 2,
      "balance": 1500000.0,
      "currency": "VND",
      "status": "ACTIVE"
    }
  }
  ```

---

## 11. Bank Account Management

### Lấy danh sách tài khoản ngân hàng
- **Mô tả**: Lấy danh sách các tài khoản ngân hàng của người dùng theo userId.
- **Endpoint**: `GET /api/bank-account?userId={userId}`
- **Đầu ra**:
  ```json
  [
    {
      "id": 1,
      "code": "BANK001",
      "bankCode": "TPB",
      "bankName": "TPBank",
      "accountNumber": "9876543210",
      "accountName": "NGUYEN VAN USER",
      "status": "ACTIVE",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
  ```

---

## 13. Dashboard APIs **[NEW]**

### Financial Summary (Income/Expense)
- **Mô tả**: Returns total income and expense for a specific period.
- **Endpoint**: `GET /api/wallet/summary?period=current`
- **Đầu vào**: 
  - Query param `period` (default: "current", format: "2024-01")
  - Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "income": 12340.00,
    "expense": 5670.00,
    "period": "2024-01"
  }
  ```
- **Logic**:
  - Get user wallet from JWT token
  - Filter transactions by date range (current month or custom period)
  - Calculate income (IN direction) and expense (OUT direction)
  - Return summary with period label

### Spending Analytics
- **Mô tả**: Returns spending data for chart visualization.
- **Endpoint**: `GET /api/analytics/spending?range=7days`
- **Đầu vào**: 
  - Query param `range` (default: "7days", options: "7days", "30days", "90days")
  - Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    { "label": "Mon", "value": 65 },
    { "label": "Tue", "value": 45 },
    { "label": "Wed", "value": 80 },
    { "label": "Thu", "value": 55 },
    { "label": "Fri", "value": 90 },
    { "label": "Sat", "value": 70 },
    { "label": "Sun", "value": 60 }
  ]
  ```
- **Logic**:
  - Get user wallet from JWT token
  - Filter transactions by date range based on `range` parameter
  - Only include expense transactions (OUT direction)
  - Group by day/week and calculate totals
  - Return chart-ready data format

---

### Common Error Format
- **Đầu ra (Error Response)**:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid username or password",
    "path": "/api/auth/login"
  }
  ```

### Common Error Codes
- `400 Bad Request`: Dữ liệu đầu vào không hợp lệ
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn
- `403 Forbidden`: Không có quyền truy cập
- `404 Not Found`: Resource không tồn tại
- `500 Internal Server Error`: Lỗi server

---

## 14. Transfer APIs **[E-Wallet]**

### Lịch sử chuyển tiền
- **Mô tả**: Lấy lịch sử các giao dịch chuyển tiền của một ví.
- **Endpoint**: `GET /api/E-Wallet/transfers/wallet/{walletId}/history`
- **Đầu vào**: Path param `walletId`, query params for pagination/filter
- **Đầu ra**: Paginated transfer history
- **Logic**: Get transfer transactions with filters.

### Tạo chuyển tiền
- **Mô tả**: Tạo một giao dịch chuyển tiền mới.
- **Endpoint**: `POST /api/E-Wallet/transfers`
- **Đầu vào**: Transfer request details
- **Đầu ra**: Transfer creation confirmation
- **Logic**: Create new transfer transaction.

### Thông tin ví (Transfer)
- **Mô tả**: Lấy thông tin ví cho mục đích chuyển tiền.
- **Endpoint**: `GET /api/E-Wallet/transfers/wallet/{walletId}`
- **Đầu ra**: Wallet information
- **Logic**: Return wallet details for transfer operations.

### Chi tiết chuyển tiền
- **Mô tả**: Lấy chi tiết một giao dịch chuyển tiền.
- **Endpoint**: `GET /api/E-Wallet/transfers/{transferId}`
- **Đầu ra**: Transfer transaction details
- **Logic**: Return specific transfer information.

---

## 15. Withdraw APIs

### Rút tiền từ ví
- **Mô tả**: Tạo yêu cầu rút tiền từ ví về tài khoản ngân hàng.
- **Endpoint**: `POST /api/wallets/{walletId}/withdraw`
- **Đầu vào**: 
  - Path param `walletId`
  - Header `Idempotency-Key` (để chống duplicate)
  - Request body với amount và bank info
- **Đầu ra**: Withdraw response with transaction details
- **Logic**:
  - Validate wallet ownership and balance
  - Create pending withdraw transaction
  - Hold amount from available balance
  - Process bank transfer asynchronously

---

## 17. Validation & Error Handling

### Global Validation Rules

#### **Amount Validations**
- **Minimum Amount**: > 0 USD (cho tất cả giao dịch)
- **Maximum Amount**: 5,000,000 USD (cho card deposit)
- **Positive Values**: Tất cả amount phải > 0

#### **Status Validations**
- **Card Status**: Must be ACTIVE for operations
- **Wallet Status**: Must be ACTIVE for operations  
- **User Status**: Must be ACTIVE for operations

#### **Ownership Validations**
- **Card Ownership**: Card phải thuộc về user đang đăng nhập
- **Wallet Ownership**: Wallet phải thuộc về user đang đăng nhập
- **Transaction Access**: Chỉ xem được transactions của chính mình

### Error Response Format

#### **Standard Error Response**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/cards/deposit",
  "details": [
    {
      "field": "amount",
      "message": "Minimum deposit amount is 1000 VND"
    }
  ]
}
```

#### **Card Deposit Error Response**
```json
{
  "transactionId": null,
  "cardId": 1,
  "cardNumber": "**** **** **** 1234",
  "amount": null,
  "previousCardBalance": null,
  "newCardBalance": null,
  "previousWalletBalance": null,
  "newWalletBalance": null,
  "description": null,
  "timestamp": "2024-01-15T10:30:00",
  "status": "FAILED",
  "message": "Insufficient card balance. Available: 30000 VND"
}
```

### Validation Examples

#### **Card Deposit Validation**
```json
// Valid Request
{
  "cardId": 1,
  "amount": 50000.50,
  "description": "Nạp tiền từ thẻ"
}

// Invalid Requests
{
  "cardId": null,           // Error: Card ID is required
  "amount": 0,              // Error: Amount must be greater than 0
  "amount": -100,            // Error: Amount must be greater than 0
  "amount": 6000000.00      // Error: Maximum deposit amount is 5,000,000 USD
}
```

#### **QR Code Validation**
```json
// Valid Request
{
  "amount": 50000
}

// Invalid Request
{
  "amount": 0               // Error: Amount must be greater than 0
}
```

### Transaction Safety

#### **Atomic Operations**
```java
@Transactional
public CardDepositResponse depositFromCard(String username, CardDepositRequest request) {
    try {
        // Update card balance
        card.setBalanceCard(card.getBalanceCard() - amount);
        // Update wallet balance  
        wallet.setBalance(wallet.getBalance() + amount);
        
        cardRepository.save(card);
        walletRepository.save(wallet);
        
    } catch (Exception e) {
        // Automatic rollback on any error
        throw new TransactionException("Deposit failed: " + e.getMessage());
    }
}
```

#### **Failed Transaction Handling**
- **Failed deposits** are still logged to database
- **Status**: FAILED with detailed error message
- **Rollback**: All balance changes are reverted
- **Audit Trail**: Complete history of failed attempts

### Common Error Messages

| Error Type | Message | Context |
|-------------|---------|---------|
| **Authentication** | "Invalid or expired token" | JWT token issues |
| **Authorization** | "Access denied" | User not authorized |
| **Validation** | "Card ID is required" | Missing required field |
| **Business Rule** | "Insufficient card balance. Available: X USD" | Card balance insufficient |
| **Status** | "Card is not active. Please contact support." | Card status invalid |
| **System** | "Service temporarily unavailable" | System errors |

### Debug Information

#### **Enhanced Logging**
```java
// Authentication logging
System.out.println("JWT identity: " + identity);
System.out.println("Identity type: " + identity.getClass().getName());

// Transaction logging
System.out.println("Found user: " + user);
System.out.println("Found wallet: " + wallet);
System.out.println("Created DTO: " + dto);
```

#### **Error Context**
- **Available balance** included in insufficient balance errors
- **Card number** masked in responses
- **Transaction ID** provided for tracking
- **Timestamp** for all operations

---

## 20. Base64 Image Handling Guide

### 🖼️ Avatar Storage Format

#### **Database Storage:**
- **Format**: Base64 thuần (KHÔNG prefix)
- **Field**: `users.avatar` (TEXT/VARCHAR)
- **Size**: Limit ~1MB per avatar

#### **Supported Input Formats:**
```javascript
// 1. Pure Base64 (Recommended)
"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg=="

// 2. Full Data URL (Auto-stripped)
"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg=="

// 3. HTTP URL (Stored as-is)
"https://example.com/avatar.jpg"
```

### 🔧 Backend Processing

#### **Auto-Strip Logic:**
```java
// UserServiceImpl.updateAvatar()
if (avatarBase64.startsWith("data:image")) {
    avatarBase64 = avatarBase64.substring(
        avatarBase64.indexOf(",") + 1
    );
}
// Lưu Base64 thuần vào database
user.setAvatar(avatarBase64);
```

#### **Validation Rules:**
```java
@NotBlank(message = "Avatar URL is required")
private String avatar; // DTO validation
```

### 📱 Frontend Integration

#### **Display Avatar:**
```javascript
// GET /api/user/profile returns:
{
  "avatarUrl": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg=="
}

// Display in HTML/React:
const avatarSrc = user.avatarUrl.startsWith('data:') 
  ? user.avatarUrl 
  : `data:image/png;base64,${user.avatarUrl}`;

<img src={avatarSrc} alt="User Avatar" />
```

#### **Upload Avatar:**
```javascript
// Convert File to Base64
const fileToBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = error => reject(error);
  });
};

// Upload to API
const uploadAvatar = async (file) => {
  const base64 = await fileToBase64(file);
  
  const response = await fetch('/api/user/profile/avatar', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ avatar: base64 })
  });
  
  return response.json();
};
```

### 🎯 Best Practices

#### **Image Size:**
- **Recommended**: < 500KB
- **Maximum**: 1MB
- **Resolution**: 200x200 to 400x400 pixels

#### **Image Format:**
- **Preferred**: PNG (lossless)
- **Acceptable**: JPEG, WebP
- **Not Recommended**: BMP, TIFF

#### **Frontend Optimization:**
```javascript
// Compress before upload
const compressImage = (file, maxWidth = 200, quality = 0.8) => {
  return new Promise((resolve) => {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const img = new Image();
    
    img.onload = () => {
      const ratio = Math.min(maxWidth / img.width, maxWidth / img.height);
      canvas.width = img.width * ratio;
      canvas.height = img.height * ratio;
      
      ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
      resolve(canvas.toDataURL('image/jpeg', quality));
    };
    
    img.src = URL.createObjectURL(file);
  });
};
```

### 🚨 Common Issues & Solutions

#### **Issue 1: "Avatar URL is required"**
```javascript
// ❌ Wrong
{
  "avatarUrl": "base64string"  // Field name incorrect
}

// ✅ Correct
{
  "avatar": "base64string"     // Correct field name
}
```

#### **Issue 2: Large image upload**
```javascript
// ❌ Too large
const hugeBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA..."; // 2MB+

// ✅ Compressed
const compressedBase64 = await compressImage(file); // < 500KB
```

#### **Issue 3: Invalid Base64**
```javascript
// ❌ Invalid
{
  "avatar": "not-base64-string"
}

// ✅ Valid
{
  "avatar": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg=="
}
```

---

## 21. Security Notes

- **JWT Token**: Có hiệu lực 15 ngày
- **Password Encryption**: Sử dụng BCrypt
- **PIN Generation**: Tự động tạo 6 chữ số ngẫu nhiên khi đăng ký
- **Role-based Access**: USER, ADMIN, SUPPORT
- **Transaction Security**: Sử dụng @Transactional và pessimistic locking

---

## 22. Database Schema Summary

### Main Entities:
- **users**: Thông tin người dùng
- **wallets**: Ví điện tử (1-1 với users)
- **transactions**: Lịch sử giao dịch
- **bank_accounts**: Tài khoản ngân hàng liên kết
- **cards**: Thẻ tín dụng/ghi nợ
- **contacts**: Danh bạ chuyển tiền

### Enums:
- **Role**: USER, ADMIN, SUPPORT
- **WalletStatus**: ACTIVE, FROZEN, CLOSED
- **TransactionType**: DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
- **TransactionStatus**: PENDING, COMPLETED, FAILED
- **TransactionDirection**: IN, OUT
- **BankAccountStatus**: ACTIVE, PENDING, REVOKED
- **CardStatus**: ACTIVE, INACTIVE, LOCKED

---

