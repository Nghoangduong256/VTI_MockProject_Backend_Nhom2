III. DANH SÁCH API CẦN TẠO MỚI
API	Bắt buộc	Mục đích
GET /api/wallet/me	✅	Lấy thông tin ví đầy đủ
GET /api/qr/wallet	✅	Tạo QR cơ bản
GET /api/qr/wallet/download	✅	Tải QR
POST /api/qr/wallet/with-amount	✅	QR có amount
POST /api/qr/resolve	✅	Giải mã QR
GET /api/transactions/incoming	⚠️	Widget bên phải
IV. THIẾT KẾ CHI TIẾT TỪNG API (THEO TASK)
TASK 1 – API lấy thông tin ví hiện tại
Mục tiêu

Frontend cần:

Wallet ID

Account Name

Account Number (hiển thị)

Balance

API
GET /api/wallet/me

Input

JWT Token

Output
{
  "walletId": "WALLET001",
  "accountName": "Nguyen Van User",
  "accountNumber": "0x71C...9A23",
  "currency": "VND",
  "balance": 1500000.0
}

Logic cần làm

Lấy username từ SecurityContext

Join users → wallets

Mapping DTO

Không trả internal ID

TASK 2 – API tạo QR Code cho ví (Base64)
Mục tiêu

Load QR hiển thị trên HTML

QR encode thông tin nhận tiền

API
GET /api/qr/wallet

QR Payload Format (CHUẨN)
walletapp://pay
?version=1
&walletId=WALLET001
&name=Nguyen%20Van%20User

Output
{
  "walletId": "WALLET001",
  "accountName": "Nguyen Van User",
  "accountNumber": "0x71C...9A23",
  "qrBase64": "data:image/png;base64,iVBORw0KGgoAAA..."
}

Logic cần làm

Auth user

Lấy wallet

Build payload string

Generate QR image

Encode Base64

Return DTO

TASK 3 – API Download QR Image
Mục tiêu

Nút Save Img

API
GET /api/qr/wallet/download

Output

Content-Type: image/png

Content-Disposition: attachment; filename="wallet-qr.png"

Logic

Reuse logic tạo QR

Stream image về client

Không Base64

TASK 4 – API tạo QR với số tiền cố định
Mục tiêu

Nút Set Amount

API
POST /api/qr/wallet/with-amount

Input
{
  "amount": 50000,
  "note": "Tien cafe"
}

QR Payload
walletapp://pay
?version=1
&walletId=WALLET001
&amount=50000
&note=Tien%20cafe

Output
{
  "qrBase64": "data:image/png;base64,..."
}

Logic

Validate amount > 0

Không lưu DB

Generate QR runtime

TASK 5 – API Resolve QR Payload (Scan)
Mục tiêu

Khi scan QR → xác thực người nhận

Trước khi chuyển tiền

API
POST /api/qr/resolve

Input
{
  "qrPayload": "walletapp://pay?walletId=WALLET001&amount=50000"
}

Output
{
  "walletId": "WALLET001",
  "receiverName": "Nguyen Van User",
  "amount": 50000,
  "currency": "VND",
  "valid": true
}

Logic

Parse payload

Validate scheme + version

Check wallet tồn tại

Check wallet ACTIVE

Return info (KHÔNG balance)

TASK 6 – API Recent Incoming Transactions
Mục tiêu

Widget bên phải HTML

API
GET /api/transactions/incoming?limit=5

Logic

Filter:

direction = IN

Order by createdAt DESC

Limit

V. STRUCTURE CODE ĐỀ XUẤT (CHO AI AGENT)
controller/
 └─ QrController
service/
 └─ QrService
 └─ WalletService
dto/
 └─ QrResponseDTO
 └─ WalletInfoDTO
 └─ ResolveQrRequest
 └─ ResolveQrResponse
util/
 └─ QrGeneratorUtil