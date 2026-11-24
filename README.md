# Toán lớp 3 — Android native v0.5.0 (development candidate)

Giữ applicationId vn.toanlop3, lưu tiến độ theo ID câu hỏi và đọc các bản sao lưu v3. Một hành trình học toán và chơi tích hợp, bài 25–44. Không HTML, không WebView, không Internet.

## Thay đổi v0.5.0
- Bản đồ minh họa 1648×928; 20 nút bài và số sao hiển thị dữ liệu thật.
- Bốn nền cảnh riêng 1440×810, sprite bé heo Mộc Nhiên, rương thưởng, bốn vòng nhạc nền gốc và năm hiệu ứng âm thanh. Không chèn tệp vô dụng để tăng dung lượng.
- Màn chơi native theo tọa độ, nút luôn nằm trong vùng chơi, thu thập khối số bằng chuyển động, chọn đáp án và mở đường bằng kết quả toán.
- Mô hình chia 20 quả táo có kéo thả/tap, lưu cả vị trí quả táo. Đọc dụng cụ tương tác; giữ câu hỏi, lời giải và tiến độ cũ.
- 483 nhiệm vụ chuyển thể theo VBT, 40 câu nâng cao tự soạn. Ngân hàng có chỉ mục trang/tiết; chưa xác minh khớp tuyệt đối toàn bộ từng câu và hình trong hai PDF.
- Không coi build thành công là xác nhận chất lượng đồ họa. Kiểm thử giả lập và xem ảnh chụp thực tế trước khi bàn giao.

## Build
JDK 17, SDK 35, Gradle 8.7. Workflow tạo APK, kiểm tra tài nguyên, chữ ký, ngân hàng và kiểm thử native. APK được đưa lên artifact sau kiểm thử. Khóa debug được cache; sao lưu nếu phải cài lại do khóa thay đổi.
