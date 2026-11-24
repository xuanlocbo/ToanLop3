package vn.toanlop3;
import java.util.*;
public final class Curriculum {
    public static final int FIRST=25,LAST=44,COUNT=8;
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
