package vn.toanlop3;
import java.util.*;
public final class Curriculum {
    public static final int FIRST=25,LAST=44,COUNT=8,ADVANCED_COUNT=2;
    public static final String[] TITLES={
        "Phép chia hết, phép chia có dư",
        "Chia số có hai chữ số cho số có một chữ số",
        "Giảm một số đi một số lần",
        "Bài toán giải bằng hai bước tính",
        "Luyện tập chung",
        "Mi-li-mét","Gam","Mi-li-lít","Nhiệt độ. Đơn vị đo nhiệt độ",
        "Thực hành và trải nghiệm với các đơn vị mi-li-mét, gam, mi-li-lít, độ C",
        "Luyện tập chung",
        "Nhân số có ba chữ số với số có một chữ số",
        "Chia số có ba chữ số cho số có một chữ số",
        "Biểu thức số. Tính giá trị của biểu thức số",
        "So sánh số lớn gấp mấy lần số bé",
        "Luyện tập chung",
        "Ôn tập phép nhân, phép chia trong phạm vi 100, 1 000",
        "Ôn tập biểu thức số","Ôn tập hình học và đo lường","Ôn tập chung"
    };
    public static final int[] PAGES={64,66,69,71,74,76,78,80,81,82,85,88,90,94,98,100,102,106,109,113,116};
    public static String title(int n){return TITLES[n-FIRST];}
    public static int world(int n){return n<=29?0:n<=35?1:n<=40?2:3;}
    public static String worldName(int w){return new String[]{"Khu rừng phép thuật","Thành phố đo lường","Vương quốc trên mây","Lâu đài tri thức"}[w];}
    public static String pages(int n){int a=PAGES[n-FIRST],b=PAGES[n-FIRST+1]-1;return a==b?""+a:a+"–"+b;}
    public static String tip(int n){
        switch(n){
            case 25:return "Số dư luôn nhỏ hơn số chia. Số dư bằng 0 là phép chia hết.";
            case 26:case 37:return "Chia từ hàng cao nhất rồi hạ từng chữ số tiếp theo.";
            case 27:return "Muốn giảm một số đi một số lần, lấy số đó chia cho số lần.";
            case 28:return "Tìm điều cần biết trước, rồi tính điều đề bài hỏi.";
            case 30:return "1 cm = 10 mm. Đọc thước từ vạch 0.";
            case 31:return "1 kg = 1 000 g. Gam là đơn vị đo khối lượng.";
            case 32:return "1 l = 1 000 ml. Mi-li-lít là đơn vị đo dung tích.";
            case 33:return "Đọc vạch ngang với đỉnh cột màu của nhiệt kế.";
            case 34:case 35:return "Chọn đúng dụng cụ, đổi về cùng đơn vị rồi tính.";
            case 36:return "Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.";
            case 38:case 42:return "Trong ngoặc trước; nhân, chia trước cộng, trừ. Cùng mức ưu tiên thì tính từ trái sang phải.";
            case 39:return "Lấy số lớn chia số bé để tìm số lần gấp.";
            case 43:return "Trung điểm chia đoạn thẳng thành hai phần bằng nhau. Đường kính bằng hai lần bán kính.";
            default:return "Đọc kỹ đề, chọn phép tính phù hợp và kiểm tra kết quả.";
        }
    }
    public static final int CHOICE=0,NUMBER=1,SHARE=2,RULER=3,THERMO=4;
    public static final class Q {
        public final int kind,a,b;
        public final String text,answer,hint,explain;
        public final String[] opts;
        Q(int k,String t,String ans,String h,String e,String[] o,int a,int b){kind=k;text=t;answer=ans;hint=h;explain=e;opts=o;this.a=a;this.b=b;}
        public boolean check(String input){return answer.equals(input==null?"":input.trim());}
        public boolean shareOK(int[] g){if(kind!=SHARE||g==null||g.length!=b)return false;for(int v:g)if(v!=a/b)return false;return true;}
    }
    private static Q choose(String t,String ans,String x,String y,String h,String e,Random r){
        if(ans.equals(x)||ans.equals(y)||x.equals(y))throw new IllegalArgumentException(t);
        List<String> opts=new ArrayList<>(Arrays.asList(ans,x,y));Collections.shuffle(opts,r);
        return new Q(CHOICE,t,ans,h,e,opts.toArray(new String[0]),0,0);
    }
    private static Q num(String t,int ans,String h,String e){return new Q(NUMBER,t,""+ans,h,e,new String[0],0,0);}
    private static Q ar(String t,int ans,String h,String e,int i,Random r){
        return i%3==1?num(t,ans,h,e):choose(t,""+ans,""+(ans+1),""+(ans+3),h,e,r);
    }
    private static Q share(int a,int b){return new Q(SHARE,"Chia đều "+a+" quả táo vào "+b+" giỏ, nhiều nhất có thể. Để riêng phần dư.",""+a/b,"Mỗi giỏ bằng nhau; số táo còn lại ít hơn số giỏ.",a+" : "+b+" = "+a/b+(a%b==0?"":" (dư "+a%b+")"),new String[0],a,b);}
    private static Q divide(int n,int d,int i,Random r){
        int q=n/d,m=n%d;String e=n+" = "+d+" × "+q+(m==0?"":" + "+m);
        if(m==0)return ar(n+" : "+d+" = ?",q,"Tìm số nhân với "+d+" được "+n+".",e,i,r);
        if(i%3==1)return num(n+" : "+d+". Số dư là bao nhiêu?",m,"Tìm thương rồi lấy số bị chia trừ tích.",e);
        return choose(n+" : "+d+" = ?",q+" dư "+m,(q+1)+" dư "+m,q+" dư "+((m+1)%d),"Số dư nhỏ hơn số chia.",e,r);
    }
    /** Original enrichment questions. Two optional checkpoints per textbook topic. */
    public static Q advanced(int n,int i){
        if(n<FIRST||n>LAST||i<0||i>=ADVANCED_COUNT)throw new IllegalArgumentException();
        Random r=new Random(8000L+n*31L+i);
        switch(n){
            case 25:
                return i==0?num("Chia một số cho 5 được thương 7 và số dư 3. Số đó là bao nhiêu?",38,"Lấy thương nhân với số chia rồi cộng số dư.","7 × 5 + 3 = 38"):
                    num("Có 29 bạn, mỗi thuyền chở nhiều nhất 4 bạn. Cần ít nhất bao nhiêu thuyền?",8,"Sau khi chia, nếu còn bạn chưa lên thuyền thì cần thêm một thuyền.","29 : 4 = 7 (dư 1); cần 7 + 1 = 8 thuyền.");
            case 26:
                return i==0?num("Tìm số lớn nhất có hai chữ số chia hết cho 6.",96,"Bắt đầu từ 99, tìm số gần nhất chia hết cho 6.","6 × 16 = 96; số tiếp theo là 102, có ba chữ số."):
                    num("Chia một số có hai chữ số cho 7 được thương 12, dư 5. Tìm số đó.",89,"Nhân thương với 7 rồi cộng 5.","12 × 7 + 5 = 89");
            case 27:
                return i==0?num("Một sợi dây sau khi giảm đi 4 lần còn 9 cm. Lúc đầu dây dài bao nhiêu cm?",36,"Muốn tìm số ban đầu, làm ngược phép chia.","9 × 4 = 36 cm"):
                    num("Có 72 bông hoa. Giảm số hoa đi 3 lần, rồi giảm tiếp đi 2 lần. Còn bao nhiêu bông?",12,"Chia cho 3, rồi chia cho 2.","72 : 3 = 24; 24 : 2 = 12");
            case 28:
                return i==0?num("Có 24 quả táo, số lê ít hơn số táo 6 quả. Chia đều tất cả vào 6 giỏ. Mỗi giỏ có bao nhiêu quả?",7,"Tìm số lê, rồi tổng số quả; cuối cùng chia đều.","24 − 6 = 18; 24 + 18 = 42; 42 : 6 = 7"):
                    num("Có 8 hộp bánh, mỗi hộp 7 chiếc. Tặng 14 chiếc rồi chia đều phần còn lại cho 6 bạn. Mỗi bạn được bao nhiêu chiếc?",7,"Tính tổng bánh, trừ phần đã tặng rồi chia.","8 × 7 = 56; 56 − 14 = 42; 42 : 6 = 7");
            case 29:
                return i==0?num("Tìm số có hai chữ số nhỏ nhất mà chia cho 8 dư 5.",13,"Số cần tìm bằng 8 nhân với một số rồi cộng 5.","8 × 1 + 5 = 13; số nhỏ hơn cùng dạng là 5."):
                    num("Có 7 túi bi, mỗi túi 9 viên. Sau khi cho đi 7 viên, chia đều số còn lại vào 8 hộp. Mỗi hộp có bao nhiêu viên?",7,"Tính số bi còn lại trước khi chia.","7 × 9 = 63; 63 − 7 = 56; 56 : 8 = 7");
            case 30:
                return i==0?num("Một dải ruy băng dài 9 cm, cắt đi 37 mm. Dải còn lại dài bao nhiêu mm?",53,"Đổi 9 cm sang mm rồi trừ.","9 cm = 90 mm; 90 − 37 = 53 mm"):
                    num("Ba đoạn dây dài bằng nhau, mỗi đoạn 28 mm. Nối sát các đoạn, không chồng lên nhau. Tổng chiều dài bao nhiêu mm?",84,"Cộng ba đoạn bằng nhau hoặc nhân 3.","28 × 3 = 84 mm");
            case 31:
                return i==0?num("Một túi gạo nặng 1 kg. Lấy ra 3 phần, mỗi phần 125 g. Còn bao nhiêu gam gạo?",625,"Đổi kg sang g, tính khối lượng đã lấy rồi trừ.","1 000 − 125 × 3 = 625 g"):
                    num("Hai quả táo giống nhau nặng 180 g. Một quả lê nặng hơn một quả táo 35 g. Quả lê nặng bao nhiêu gam?",125,"Tính khối lượng một quả táo trước.","180 : 2 = 90; 90 + 35 = 125 g");
            case 32:
                return i==0?num("Bình có 1 l nước. Rót đầy 3 cốc, mỗi cốc 180 ml. Bình còn bao nhiêu ml?",460,"Đổi lít sang ml rồi trừ lượng nước đã rót.","1 000 − 180 × 3 = 460 ml"):
                    num("Có 950 ml nước. Rót đầy 4 chai, mỗi chai 200 ml. Còn bao nhiêu ml?",150,"Tính số nước đã rót vào 4 chai rồi trừ.","950 − 200 × 4 = 150 ml");
            case 33:
                return i==0?num("Lúc sáng nhiệt độ là 19 °C. Trưa tăng 9 °C, tối giảm 6 °C so với trưa. Tối bao nhiêu độ C?",22,"Tính nhiệt độ trưa, rồi trừ 6.","19 + 9 − 6 = 22 °C"):
                    num("Nhiệt độ trong phòng sau khi tăng 7 °C rồi giảm 3 °C là 26 °C. Ban đầu bao nhiêu độ C?",22,"Làm ngược các thay đổi: cộng 3 rồi trừ 7.","26 + 3 − 7 = 22 °C");
            case 34:
                return i==0?num("Một cốc rỗng nặng 125 g. Khi đựng nước, cả cốc nặng 345 g. Riêng nước nặng bao nhiêu gam?",220,"Trừ khối lượng cốc rỗng khỏi khối lượng cả cốc.","345 − 125 = 220 g"):
                    num("Đầu dây nằm ở vạch 12 mm, cuối dây ở vạch 57 mm. Dây dài bao nhiêu mm?",45,"Không đo từ vạch 0 thì lấy vạch cuối trừ vạch đầu.","57 − 12 = 45 mm");
            case 35:
                return i==0?num("Mỗi chai nước có 250 ml. Rót hết 3 chai vào bình đang có 100 ml. Trong bình có bao nhiêu ml?",850,"Nhân lượng nước một chai với 3 rồi cộng 100.","250 × 3 + 100 = 850 ml"):
                    num("Một hộp đựng 4 viên đất nặn, mỗi viên 75 g. Hộp rỗng nặng 120 g. Cả hộp nặng bao nhiêu gam?",420,"Tính khối lượng đất nặn rồi cộng khối lượng hộp.","75 × 4 + 120 = 420 g");
            case 36:
                return i==0?num("Tìm số còn thiếu: ? × 4 = 828.",207,"Lấy tích chia cho thừa số đã biết.","828 : 4 = 207; 207 × 4 = 828"):
                    num("Có 4 khay, mỗi khay 203 hạt. Nếu bỏ đi 28 hạt, còn bao nhiêu hạt?",784,"Tính tích trước, rồi trừ số hạt bỏ đi.","203 × 4 − 28 = 784");
            case 37:
                return i==0?num("Một phép chia cho 6 có thương 135, số dư lớn nhất có thể. Số bị chia là bao nhiêu?",815,"Số dư lớn nhất là 5. Nhân thương với 6 rồi cộng số dư.","135 × 6 + 5 = 815"):
                    num("Có 653 viên bi. Chia đều vào 5 hộp, nhiều nhất có thể. Cần thêm ít nhất bao nhiêu viên để chia hết?",2,"Tìm số dư, rồi xem còn thiếu bao nhiêu để đủ 5 viên.","653 : 5 = 130 (dư 3); 5 − 3 = 2");
            case 38:
                return i==0?choose("Đặt ngoặc để 8 + 4 × 5 có giá trị bằng 60.","(8 + 4) × 5","8 + (4 × 5)","(8 × 4) + 5","Thử tính tổng 8 + 4 trước.","(8 + 4) × 5 = 12 × 5 = 60",r):
                    num("Tìm số còn thiếu: 72 : (3 + ?) = 12.",3,"Tìm số chia bằng 72 chia 12, rồi tìm số còn thiếu.","72 : 12 = 6; 6 − 3 = 3");
            case 39:
                return i==0?num("Lan có 6 nhãn vở. Mai có 24 nhãn. Mai tặng Lan 4 nhãn. Sau đó số nhãn của Mai gấp mấy lần Lan?",2,"Tính lại số nhãn của từng bạn rồi lấy số lớn chia số bé.","Mai: 24 − 4 = 20; Lan: 6 + 4 = 10; 20 : 10 = 2"):
                    num("Vườn A có 12 cây. Vườn B gấp 4 lần vườn A. Vườn B trồng thêm 12 cây. Lúc này B gấp mấy lần A?",5,"Tính số cây mới của B rồi chia cho số cây A.","12 × 4 + 12 = 60; 60 : 12 = 5");
            case 40:
                return i==0?num("Một kho có 4 thùng, mỗi thùng 126 quyển vở. Chia đều tất cả cho 7 lớp. Mỗi lớp nhận bao nhiêu quyển?",72,"Tính tổng số vở rồi chia 7.","126 × 4 = 504; 504 : 7 = 72"):
                    num("Tìm số còn thiếu: (240 + ?) : 6 = 50.",60,"Số trong ngoặc bằng 50 nhân 6.","50 × 6 = 300; 300 − 240 = 60");
            case 41:
                return i==0?num("Tìm số có ba chữ số nhỏ nhất chia hết cho 9.",108,"Tìm bội của 9 đầu tiên không nhỏ hơn 100.","9 × 11 = 99; 9 × 12 = 108"):
                    num("Một số nhân với 8 được 816. Nếu lấy số đó chia cho 3 thì được bao nhiêu?",34,"Tìm số ban đầu bằng phép chia rồi chia tiếp cho 3.","816 : 8 = 102; 102 : 3 = 34");
            case 42:
                return i==0?num("Tính: 96 : (4 + 4) × 5 − 17.",43,"Tính trong ngoặc trước, rồi chia và nhân từ trái sang phải.","96 : 8 = 12; 12 × 5 = 60; 60 − 17 = 43"):
                    num("Tìm số còn thiếu: 5 × (? − 7) = 45.",16,"Tìm giá trị trong ngoặc bằng 45 chia 5.","45 : 5 = 9; 9 + 7 = 16");
            case 43:
                return i==0?num("Đoạn AB dài 8 cm. M là trung điểm AB; N là trung điểm AM. AN dài bao nhiêu mm?",20,"Chia đôi AB, rồi chia đôi AM và đổi sang mm.","8 : 2 : 2 = 2 cm = 20 mm"):
                    num("Hai hình tròn có bán kính 3 cm và 5 cm. Tổng độ dài hai đường kính là bao nhiêu cm?",16,"Đường kính của mỗi hình tròn gấp đôi bán kính.","3 × 2 + 5 × 2 = 16 cm");
            case 44:
                return i==0?num("Có 4 bình nước, mỗi bình 225 ml. Rót đều hết vào 6 cốc. Mỗi cốc có bao nhiêu ml?",150,"Nhân số bình với dung tích mỗi bình rồi chia đều.","225 × 4 = 900; 900 : 6 = 150 ml"):
                    num("Có 360 viên gạch. Dùng một phần ba để xây cầu, rồi dùng 75 viên để sửa đường. Còn bao nhiêu viên?",165,"Tìm một phần ba của 360 rồi trừ hai phần đã dùng.","360 : 3 = 120; 360 − 120 − 75 = 165");
            default:throw new IllegalArgumentException();
        }
    }
    public static Q question(int n,int i){
        if(n<FIRST||n>LAST||i<0||i>=COUNT)throw new IllegalArgumentException();
        Random r=new Random(3107L*n+97L*i);int d=2+r.nextInt(7),a=2+r.nextInt(7),b=2+r.nextInt(7),k=i%4;
        switch(n){
            case 25:
                if(i==0)return share(12,3);if(i==2)return share(14,4);if(i==4)return share(17,3);if(i==6)return share(15,3);
                return divide(d*a+(i%4==1?0:1),d,i,r);
            case 26:return divide(10+r.nextInt(90),d,i,r);
            case 27:{int m=d*(3+r.nextInt(8));return ar("Giảm "+m+" đi "+d+" lần.",m/d,"Thực hiện phép chia.",m+" : "+d+" = "+m/d,i,r);}
            case 28:{int m=3+r.nextInt(6),v=2+r.nextInt(4);
                if(i%2==0)return ar("Có "+m+" quả táo đỏ. Táo xanh gấp "+d+" lần táo đỏ. Có tất cả bao nhiêu quả?",m+m*d,"Tính táo xanh trước rồi cộng hai nhóm.",m+" × "+d+" = "+m*d+"; "+m+" + "+m*d+" = "+(m+m*d),i,r);
                return ar("Có "+d+" giỏ, mỗi giỏ "+m+" quả. Tặng đi "+v+" quả. Còn bao nhiêu quả?",m*d-v,"Tính tổng số quả rồi trừ số đã tặng.",d+" × "+m+" − "+v+" = "+(m*d-v),i,r);
            }
            case 29:return question(new int[]{25,26,27,28}[k],i);
            case 30:{int cm=2+r.nextInt(7),mm=1+r.nextInt(9);
                if(k==0){int length=11+r.nextInt(39);return new Q(RULER,"Đoạn màu hồng dài bao nhiêu mi-li-mét?",""+length,"Đọc từ vạch 0; mỗi vạch nhỏ là 1 mm.",length+" mm",new String[0],length,0);}
                if(k==1)return num(cm+" cm = ? mm",cm*10,"1 cm = 10 mm.",cm+" × 10 = "+cm*10+" mm");
                if(k==2)return ar(cm+" cm "+mm+" mm = ? mm",cm*10+mm,"Đổi cm ra mm rồi cộng.",cm*10+" + "+mm+" = "+(cm*10+mm),i,r);
                return ar(cm*10+" mm = ? cm",cm,"10 mm = 1 cm.",cm*10+" : 10 = "+cm,i,r);
            }
            case 31:case 32:{boolean mass=n==31;String u=mass?"g":"ml",big=mass?"kg":"l";int m=100*(2+r.nextInt(7)),h=50*(1+r.nextInt(3));
                if(k==0)return ar("1 "+big+" = ? "+u,1000,"Nhớ cách đổi đơn vị.","1 "+big+" = 1 000 "+u,i,r);
                if(k==1)return ar(m+" "+u+" + "+h+" "+u+" = ? "+u,m+h,"Cộng số đo cùng đơn vị.",m+" + "+h+" = "+(m+h)+" "+u,i,r);
                if(k==2)return ar(m+" "+u+" − "+h+" "+u+" = ? "+u,m-h,"Trừ số đo cùng đơn vị.",m+" − "+h+" = "+(m-h)+" "+u,i,r);
                return choose(mass?"Đơn vị phù hợp để cân một quả táo?":"Đơn vị phù hợp để đong một cốc nước?",u,mass?"ml":"g","mm",mass?"Đang đo khối lượng.":"Đang đo dung tích.","Dùng "+u+".",r);
            }
            case 33:{int t=15+r.nextInt(25);
                if(k<2)return new Q(THERMO,"Nhiệt kế chỉ bao nhiêu độ C?",""+t,"Đọc vạch ngang với đỉnh cột đỏ.",t+" °C",new String[0],t,0);
                if(k==2)return ar("Sáng "+t+" °C, trưa tăng 4 °C. Trưa bao nhiêu độ C?",t+4,"Nhiệt độ tăng thì cộng.",t+" + 4 = "+(t+4)+" °C",i,r);
                return choose("Dụng cụ đo nhiệt độ là gì?","Nhiệt kế","Cân","Thước","Đơn vị thường dùng là °C.","Dùng nhiệt kế.",r);
            }
            case 34:return question(new int[]{30,31,32,33}[k],i);
            case 35:return question(new int[]{33,30,31,32}[k],i);
            case 36:{int m=100+r.nextInt(999/d-99);return ar(m+" × "+d+" = ?",m*d,"Nhân từ hàng đơn vị và nhớ sang hàng tiếp theo.",m+" × "+d+" = "+m*d,i,r);}
            case 37:return divide(100+r.nextInt(900),d,i,r);
            case 38:case 42:
                if(k==0)return ar(a+" + "+b+" × "+d+" = ?",a+b*d,"Nhân trước, cộng sau.",b+" × "+d+" = "+b*d+"; cộng "+a+" được "+(a+b*d),i,r);
                if(k==1)return ar("("+a+" + "+b+") × "+d+" = ?",(a+b)*d,"Tính trong ngoặc trước.",a+" + "+b+" = "+(a+b)+"; nhân "+d+" được "+((a+b)*d),i,r);
                if(k==2)return ar(a*b+" : "+b+" + "+d+" = ?",a+d,"Chia trước, cộng sau.",a*b+" : "+b+" = "+a+"; cộng "+d+" được "+(a+d),i,r);
                return ar((a*b+d)+" − "+a+" × "+b+" = ?",d,"Tính tích trước rồi trừ.",a+" × "+b+" = "+a*b+"; kết quả "+d,i,r);
            case 39:{int m=3+r.nextInt(9);return ar("Nhóm lớn có "+m*d+" bông hoa, nhóm bé có "+m+" bông. Nhóm lớn gấp mấy lần nhóm bé?",d,"Lấy số lớn chia số bé.",m*d+" : "+m+" = "+d,i,r);}
            case 40:return question(new int[]{36,37,38,39}[k],i);
            case 41:return question(new int[]{26,36,37,25}[k],i);
            case 43:
                if(i==0)return choose("Hình có 4 cạnh bằng nhau và 4 góc vuông?","Hình vuông","Hình tam giác","Hình tròn","Nhớ đặc điểm các cạnh và góc.","Hình vuông.",r);
                if(i==1)return num("Đoạn AB dài 16 cm, M là trung điểm. AM = ? cm",8,"Hai đoạn AM và MB bằng nhau.","16 : 2 = 8 cm");
                if(i==2)return num("Bán kính hình tròn là 6 cm. Đường kính dài bao nhiêu cm?",12,"Đường kính gấp đôi bán kính.","6 × 2 = 12 cm");
                if(i==3)return choose("Khối lập phương có bao nhiêu mặt?","6","4","8","Hãy hình dung một khối xúc xắc.","Có 6 mặt.",r);
                return question(new int[]{30,31,32,33}[k],i);
            case 44:return question(new int[]{28,26,36,37,38,39,43,34}[i],i);
            default:throw new IllegalArgumentException();
        }
    }
}
