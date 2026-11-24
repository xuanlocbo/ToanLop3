# Toán lớp 3

Ứng dụng Android native offline dành cho học sinh lớp 3, nhân vật Mộc Nhiên.
Không HTML, WebView, quảng cáo, tài khoản hoặc quyền Internet.

## 0.1.0 — bản thử nghiệm
- 20 bài từ bài 25 đến 44, Toán 3 tập 1, Kết nối tri thức với cuộc sống.
- Bài luyện tập tự soạn theo chủ đề. Chưa đọc được hai bản sách gốc được cung cấp, chưa xác nhận từng bài tập khớp vở bài tập. Cần hoàn thiện đối chiếu trước khi sử dụng làm bản học chính thức.
- Học ngay trong hành trình: di chuyển, giải toán để mở đường, chia táo, đọc thước và nhiệt kế.
- Tiến độ, vị trí, câu đang làm, đáp án dở, gợi ý và sao lưu trên thiết bị.
- Sao lưu/khôi phục bằng tệp JSON. Không cần mạng.

## Build
Android Studio, JDK 17, Android SDK 35, Gradle 8.7.
GitHub Actions: workflow Android APK, tải artifact APK sau khi build thành công.
Bản debug là bản thử nghiệm, chưa xác nhận chạy trên thiết bị thật. Khi cập nhật phải giữ applicationId và khóa ký. Nếu khóa ký debug của CI mất, xuất bản sao lưu trước khi gỡ/cài mới.

## Mục lục tham khảo
https://ihoc.vn/sgk/lop-3/toan-hoc-lop-3/sach-giao-khoa-toan-lop-3-tap-1-ket-noi-tri-thuc.html
https://www.thivien.org/vbt-toan-3-kn/index.jsp
