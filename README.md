# Toán lớp 3 — Android native v0.3.1

Ứng dụng offline Android native, không HTML/WebView. Nhân vật nữ bé heo hồng Mộc Nhiên, bản đồ ngang 20 bài, 4 mục Hành trình / Ôn tập / Thành tích / Cài đặt. Một màn phiêu lưu tích hợp học toán, không có chế độ game tách rời. Giao diện được dựng lại bằng native Views và Canvas vector theo bố cục đã duyệt; chưa phải ảnh thiết kế được chuyển nguyên pixel thành UI.

## Căn cứ nội dung
- Hai PDF người dùng cung cấp: SGK Toán 3 tập một (tái bản lần thứ tư), trang 72–121; VBT Toán 3 tập một, trang 64–115.
- 480 nhiệm vụ chuyển thể theo số bài/tiết/trang/câu VBT, gồm 9 nhiệm vụ thực hành xác nhận thủ công. 40 câu nâng cao tự soạn (2/bài).
- Các dạng đặt tính (thương và dư), phép nhân, nhiều lựa chọn, chọn nhiều đáp án, đọc thước/cân/ca/nhiệt kế, sơ đồ trung điểm, toán có lời văn.
- Những bài cần đo trực tiếp/vẽ theo hình gốc và một số hình khó đọc được hướng dẫn thực hành, không tự bịa đáp án; chưa tuyên bố chuyển thể 100% mọi thao tác hình học nguyên bản hay toàn bộ câu phụ.
- Có hướng dẫn kiến thức ngắn theo SGK, chỉ mục nguồn cho câu cơ bản.
- Tự động lưu theo ID bài tập, vị trí, câu trả lời dở, gợi ý và câu sai; sao lưu JSON. Tiến độ nâng cao cũ được di trú tương ứng; dữ liệu ngân hàng tự soạn cũ được lưu trữ riêng vì không tương đương câu VBT mới.

## 0.3.1
- Sửa lỗi biên dịch do trùng tên hàm Activity.finish(); giữ nguyên nội dung, giao diện và dữ liệu tiến độ.

## Build
JDK 17, SDK 35, Gradle 8.7, Android Gradle Plugin 8.6.1. GitHub Actions tạo APK debug, kiểm tra nội dung và chữ ký. Duy trì applicationId vn.toanlop3. Khóa debug được cache; nếu khóa cũ mất, xuất backup trước khi cài mới. Chưa xác nhận chạy trên thiết bị thật chỉ từ kết quả build.
