# Toán lớp 3 — Android native v0.6.1 (development candidate)

Giữ applicationId vn.toanlop3, lưu tiến độ theo ID câu hỏi và đọc các bản sao lưu v3. Một hành trình học toán và chơi tích hợp, bài 25–44. Không HTML, không WebView, không Internet.

## Thay đổi v0.5.0
- Bản đồ minh họa 1648×928; 20 nút bài và số sao hiển thị dữ liệu thật.
- Bốn nền cảnh riêng 1440×810, sprite bé heo Mộc Nhiên, rương thưởng, bốn vòng nhạc nền gốc và năm hiệu ứng âm thanh. Không chèn tệp vô dụng để tăng dung lượng.
- Màn chơi native theo tọa độ, nút luôn nằm trong vùng chơi, thu thập khối số bằng chuyển động, chọn đáp án và mở đường bằng kết quả toán.
- Mô hình chia 20 quả táo có kéo thả/tap, lưu cả vị trí quả táo. Đọc dụng cụ tương tác; giữ câu hỏi, lời giải và tiến độ cũ.
- 493 nhiệm vụ chuyển thể theo VBT, 40 câu nâng cao tự soạn. Ngân hàng có chỉ mục trang/tiết; chưa xác minh khớp tuyệt đối toàn bộ từng câu và hình trong hai PDF.
- Không coi build thành công là xác nhận chất lượng đồ họa. Kiểm thử giả lập và xem ảnh chụp thực tế trước khi bàn giao.

## Build
JDK 17, SDK 35, Gradle 8.7. Workflow tạo APK, kiểm tra tài nguyên, chữ ký, ngân hàng và kiểm thử native. APK được đưa lên artifact sau kiểm thử. Khóa debug được cache; sao lưu nếu phải cài lại do khóa thay đổi.

## Bản 0.6.0
- Sửa bài cân trang 85 theo VBT gốc; bổ sung ô trung gian bài 29 và 41, chuyển hai bài đong nước sang mô phỏng ca có thể rót, đổ đầy, đổ hết; lưu trạng thái ca nước.
- Sửa đường đi và thứ tự animation cầu, khôi phục vị trí nhân vật, hiển thị lựa chọn đã lưu, tự co chữ ở màn hình nhỏ.
- APK được tải lên ngay sau kiểm tra ngân hàng, tài nguyên, biên dịch, Lint và chữ ký; kiểm thử giả lập chạy sau đó. Artifact trước khi smoke test hoàn tất là bản kiểm tra, không phải chứng nhận chất lượng.
- Chưa đối chiếu 100% tất cả câu và hình trong hai sách.

## 0.6.1 — kiểm soát chất lượng và chữ ký
- Sửa hình học VBT trang 109–110, 113: vẽ hình, đọc sơ đồ hình tròn, đếm khối, chọn góc. Chỉ 2 bài cần dụng cụ/vật thật được xác nhận thủ công.
- Giữ mã bài, đánh giá lại những câu cũ chuyển từ xác nhận sang chấm tự động.
- Ký APK release bằng khóa riêng cố định; không lưu khóa riêng trong Git/cache. Secret TOANLOP3_SIGNING phải chứa JSON {keystore_b64,password}, alias toanlop3. Chứng thư được kiểm tra bằng SHA-256 đã ghim. Khi chưa cài secret, chỉ xuất bản kiểm thử/unsigned, không tự nhận là bản phát hành ổn định.
- Các APK cũ có chứng thư khác nhau: phải sao lưu trong app trước khi gỡ, cài bản mới rồi khôi phục. Không thể cài đè qua chứng thư khác. Bản release ký ổn định sau này cập nhật được mà không cần xóa dữ liệu.
- Chưa đối chiếu tuyệt đối mọi câu phụ trong cả hai PDF.
