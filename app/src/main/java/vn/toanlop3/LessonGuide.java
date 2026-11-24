package vn.toanlop3;
/** Original short teaching summaries, based on the exact provided SGK printed pages. */
final class LessonGuide {
 static final int[] SGK={72,75,79,81,83,85,87,89,91,93,95,97,99,104,109,111,113,116,118,120,122};
 static final String[] RULE={
 "Chia hết có số dư 0. Chia có dư: số dư bé hơn số chia.",
 "Chia lần lượt từ trái sang phải: chia, nhân, trừ, rồi hạ chữ số tiếp theo.",
 "Muốn giảm một số đi một số lần, lấy số đó chia cho số lần.",
 "Tìm đại lượng chưa biết ở bước một, rồi dùng kết quả ấy để trả lời câu hỏi ở bước hai.",
 "Ôn nhân, chia, gấp một số lên nhiều lần và giảm một số đi nhiều lần.",
 "Mi-li-mét (mm) đo độ dài. 1 cm = 10 mm; 1 m = 1 000 mm.",
 "Gam (g) đo khối lượng. 1 kg = 1 000 g.",
 "Mi-li-lít (ml) đo dung tích. 1 l = 1 000 ml.",
 "Nhiệt độ đọc theo vạch ngang đỉnh cột màu. Đơn vị thường dùng là độ C (°C).",
 "Dùng thước, cân, ca đong và nhiệt kế đúng mục đích. Đọc đúng vạch chia.",
 "Đổi về cùng đơn vị trước khi tính hoặc so sánh.",
 "Nhân từ hàng đơn vị, đến hàng chục, hàng trăm; nhớ sang hàng liền trước.",
 "Chia từ trái sang phải. Nếu một lượt chia không đủ, nhớ viết 0 ở thương khi cần.",
 "Cộng trừ hoặc nhân chia cùng mức ưu tiên: từ trái sang phải. Nhân chia trước cộng trừ; có ngoặc thì tính trong ngoặc trước.",
 "Muốn biết số lớn gấp mấy lần số bé, lấy số lớn chia số bé.",
 "Ôn nhân chia trong phạm vi 1 000, biểu thức và so sánh số lần.",
 "Ôn bảng nhân chia, đặt tính, tìm thành phần chưa biết và một phần mấy.",
 "Chú ý dấu ngoặc, thứ tự phép tính và nhóm số thuận tiện.",
 "Trung điểm chia đoạn thành hai phần bằng nhau. Đường kính gấp hai bán kính; ôn hình khối, góc và đơn vị đo.",
 "Tổng hợp phép tính, hình học, đo lường và giải toán có lời văn."};
 static final String[] EXAMPLE={
 "6 : 2 = 3; 7 : 2 = 3 (dư 1).",
 "48 : 2 = 24; 74 : 3 = 24 (dư 2).",
 "6 con thỏ giảm 3 lần còn 2 con.",
 "Ngăn trên 10 quyển, ngăn dưới hơn 3 quyển. Ngăn dưới: 13; cả hai: 23 quyển.",
 "30 × 3 = 90; 60 : 2 = 30.",
 "2 cm = 20 mm.",
 "100 g + 50 g = 150 g.",
 "1 l = 1 000 ml.",
 "30 °C cao hơn 26 °C.",
 "200 g + 500 g = 700 g.",
 "Đọc số đo rồi chọn phép tính tương ứng.",
 "215 × 4 = 860.",
 "714 : 7 = 102; 102 : 5 = 20 (dư 2).",
 "10 − 2 × 3 = 10 − 6 = 4.",
 "Số 18 gấp số 6 ba lần.",
 "Nhân, chia và biểu thức: kiểm tra bằng phép tính ngược.",
 "Tìm thừa số bằng tích chia thừa số đã biết.",
 "Tính trong ngoặc trước.",
 "Đường kính bằng hai lần bán kính.",
 "Đọc yêu cầu, giải từng bước, kiểm tra kết quả."};
 static String pages(int l){return SGK[l-25]+"–"+(SGK[l-24]-1);}
}
