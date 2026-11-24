package vn.toanlop3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** Native UI and checkpoint orchestration. All content and progress stay on device. */
public final class MainActivity extends Activity {
    private static final int PINK=0xffd94c91,BG=0xfffff0f6,INK=0xff4d354b,MUTED=0xff835f75,PALE=0xfffbe1ed,WHITE=0xfffffdfd,GREEN=0xff23794f;
    private static final int EXPORT=81,IMPORT=82;
    private SharedPreferences prefs;
    private SaveState[] data=new SaveState[20], bonus=new SaveState[20];
    private boolean advanced=false;
    private int lesson=25;
    private String screen="home";
    private LinearLayout root;
    private AdventureView scene;
    private boolean sound=true;
    private int dp(float x){return Math.round(x*getResources().getDisplayMetrics().density);}
    private boolean landscape(){return getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE;}
    private SaveState state(){return (advanced?bonus:data)[lesson-25];}
    @Override public void onCreate(Bundle b){
        super.onCreate(b);prefs=getSharedPreferences("toanlop3.progress.v1",MODE_PRIVATE);
        lesson=SaveState.bound(prefs.getInt("lesson",25),25,44);screen=prefs.getString("screen","home");sound=prefs.getBoolean("sound",true);
        advanced=prefs.getBoolean("advanced",false);
        if(!screen.equals("game")&&!screen.equals("map"))screen="home";
        for(int n=25;n<=44;n++){
            try{data[n-25]=SaveState.read(new JSONObject(prefs.getString("lesson"+n,"{}")),n);}catch(Exception e){data[n-25]=new SaveState();}
            try{bonus[n-25]=SaveState.read(new JSONObject(prefs.getString("bonus"+n,"{}")),n,true);}catch(Exception e){bonus[n-25]=new SaveState(true);}
        }
        render();
    }
    @Override public void onConfigurationChanged(Configuration c){super.onConfigurationChanged(c);persist();render();}
    @Override protected void onPause(){persist();super.onPause();}
    private void persist(){
        SharedPreferences.Editor e=prefs.edit().putInt("lesson",lesson).putString("screen",screen).putBoolean("sound",sound).putBoolean("advanced",advanced);
        try{for(int n=25;n<=44;n++){e.putString("lesson"+n,data[n-25].json().toString());e.putString("bonus"+n,bonus[n-25].json().toString());}e.commit();}
        catch(JSONException ex){throw new IllegalStateException(ex);}
    }
    private int stars(){int t=0;for(SaveState s:data)t+=s.stars();return t;}
    private int completed(){int t=0;for(SaveState s:data)if(s.complete())t++;return t;}
    private GradientDrawable shape(int col,int rad){GradientDrawable g=new GradientDrawable();g.setColor(col);g.setCornerRadius(dp(rad));return g;}
    private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    private TextView text(String s,int sp,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(color);
        t.setFontFeatureSettings("tnum");t.setTypeface(Typeface.create("sans-serif-medium",bold?Typeface.BOLD:Typeface.NORMAL));t.setIncludeFontPadding(true);
        return t;
    }
    private TextView center(String s,int sp,int color,boolean bold){TextView t=text(s,sp,color,bold);t.setGravity(Gravity.CENTER);return t;}
    private Button btn(String label,int color,int fg,int sp,Runnable action){
        Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setTextColor(fg);b.setTextSize(sp);b.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));
        b.setMinHeight(dp(54));b.setMinimumHeight(dp(54));b.setPadding(dp(12),dp(9),dp(12),dp(9));b.setElevation(dp(1));
        b.setBackground(new RippleDrawable(ColorStateList.valueOf(0x22834474),shape(color,18),null));
        b.setSoundEffectsEnabled(sound);b.setOnClickListener(v->action.run());return b;
    }
    private void add(LinearLayout parent,View v,int height,int top){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,height<0?height:dp(height));lp.topMargin=dp(top);parent.addView(v,lp);
    }
    private void weight(LinearLayout parent,View v,int height,int side){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,height<0?height:dp(height),1);lp.leftMargin=dp(side);lp.rightMargin=dp(side);parent.addView(v,lp);
    }
    private void padding(LinearLayout l,int x){l.setPadding(dp(x),dp(x),dp(x),dp(x));}
    private LinearLayout card(){LinearLayout l=column();l.setBackground(shape(WHITE,22));padding(l,14);return l;}
    private void progress(LinearLayout l,int n,int total){
        ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(total);p.setProgress(n);
        p.setProgressTintList(ColorStateList.valueOf(PINK));p.setProgressBackgroundTintList(ColorStateList.valueOf(PALE));add(l,p,10,7);
    }
    private void navigate(String s){screen=s;persist();render();}
    private void start(int n){start(n,false);}
    private void start(int n,boolean enrichment){lesson=n;advanced=enrichment;screen="game";persist();render();}
    private void render(){
        root=column();root.setBackgroundColor(BG);padding(root,12);
        setContentView(root);
        if(screen.equals("home"))home();
        else if(screen.equals("map"))map();
        else game(-1,false);
    }
    private LinearLayout scroller(LinearLayout parent){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);scroll.setClipToPadding(false);
        LinearLayout l=column();l.setPadding(dp(2),0,dp(2),dp(10));scroll.addView(l,new ScrollView.LayoutParams(-1,-2));
        parent.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));return l;
    }
    private void home(){
        LinearLayout head=row();TextView title=text("Toán lớp 3",30,PINK,true);
        head.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        head.addView(btn("⚙",PALE,INK,23,this::settings),new LinearLayout.LayoutParams(dp(56),dp(54)));add(root,head,-2,0);
        LinearLayout l=scroller(root);
        if(landscape()){
            LinearLayout two=row();two.setGravity(Gravity.TOP);
            LinearLayout a=column(),b=column();weight(two,a,-2,5);weight(two,b,-2,5);
            add(a,new AdventureView(this,Curriculum.world(lesson),0,0,false,true),226,6);homeControls(b);add(l,two,-2,3);
        }else{add(l,new AdventureView(this,Curriculum.world(lesson),0,0,false,true),220,8);homeControls(l);}
    }
    private void homeControls(LinearLayout l){
        add(l,center("Mộc Nhiên",28,INK,true),-2,8);
        add(l,center("⭐ "+stars()+"     •     "+completed()+"/20 bài",20,MUTED,true),-2,2);
        LinearLayout box=card();
        add(box,text((advanced?"Nâng cao · ":"")+"Bài "+lesson+" · "+Curriculum.title(lesson),22,INK,true),-2,0);
        add(box,text(state().stage>=state().count?"Đã hoàn thành!":"Thử thách "+(state().stage+1)+"/"+state().count,17,MUTED,false),-2,5);
        progress(box,state().stars(),state().count);add(l,box,-2,12);
        add(l,btn(advanced?"▶  Tiếp tục nâng cao":"▶  Học tiếp",PINK,WHITE,25,()->start(lesson,advanced)),66,13);
        add(l,btn("Bản đồ bài học",PALE,INK,22,()->navigate("map")),58,10);
    }
    private void map(){
        LinearLayout h=row();h.addView(btn("‹",PALE,INK,28,()->navigate("home")),new LinearLayout.LayoutParams(dp(54),dp(54)));
        TextView title=center("Bản đồ bài học",25,PINK,true);h.addView(title,new LinearLayout.LayoutParams(0,-2,1));add(root,h,-2,0);
        LinearLayout l=scroller(root);
        add(l,center("⭐ "+stars()+"  •  "+completed()+"/20 bài hoàn thành",19,MUTED,true),-2,8);
        int[] start={25,30,36,41},end={29,35,40,44};
        int cols=landscape()?4:2;
        for(int w=0;w<4;w++){
            add(l,text(Curriculum.worldName(w),23,INK,true),-2,16);
            LinearLayout group=row();group.setGravity(Gravity.TOP);
            int count=0;
            for(int n=start[w];n<=end[w];n++){
                final int num=n;SaveState s=data[n-25];String mark=s.complete()?"✓":"⭐ "+s.stars()+"/"+s.count;
                LinearLayout cell=card();add(cell,text("Bài "+n,22,PINK,true),-2,0);
                add(cell,text(Curriculum.title(n),17,INK,true),-2,3);add(cell,text(mark,16,MUTED,true),-2,5);
                add(cell,btn("Vào bài",n==lesson&&!advanced?PINK:PALE,n==lesson&&!advanced?WHITE:INK,17,()->start(num)),54,6);
                add(cell,btn("Nâng cao "+bonus[n-25].stars()+"/2",0xffffefd3,INK,16,()->start(num,true)),54,6);
                weight(group,cell,-2,3);count++;
                if(count==cols||n==end[w]){
                    for(int k=count;k<cols;k++)weight(group,new View(this),1,3);
                    add(l,group,-2,8);group=row();group.setGravity(Gravity.TOP);count=0;
                }
            }
        }
    }
    private void move(int direction,boolean jump){
        SaveState s=state();if(s.solved||s.stage>=s.count)return;
        int old=s.position;
        if(direction<0){if(s.position>0)s.position--;}
        else if(jump){if(s.position==2)s.position=3;}
        else if(s.position<4&&s.position!=2)s.position++;
        persist();root.removeAllViews();game(old,jump);
    }
    private void game(int old,boolean jump){
        SaveState s=state();
        LinearLayout header=row();
        header.addView(btn("‹",PALE,INK,28,()->navigate("home")),new LinearLayout.LayoutParams(dp(54),dp(54)));
        LinearLayout label=column();add(label,text((advanced?"Nâng cao · ":"")+"Bài "+lesson+"   ·   ⭐ "+s.stars()+"/"+s.count,21,PINK,true),-2,0);
        add(label,text(Curriculum.title(lesson),16,INK,true),-2,0);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.leftMargin=dp(10);header.addView(label,lp);add(root,header,-2,0);progress(root,s.stars(),s.count);
        if(s.stage>=s.count){finishLesson();return;}
        LinearLayout sceneBox=column(),taskBox=card();
        if(landscape()){
            LinearLayout two=row();two.setGravity(Gravity.TOP);
            LinearLayout.LayoutParams a=new LinearLayout.LayoutParams(0,-1,0.98f);a.rightMargin=dp(6);two.addView(sceneBox,a);
            ScrollView right=new ScrollView(this);right.setFillViewport(false);right.addView(taskBox,new ScrollView.LayoutParams(-1,-2));
            LinearLayout.LayoutParams b=new LinearLayout.LayoutParams(0,-1,1.02f);b.leftMargin=dp(6);two.addView(right,b);
            LinearLayout.LayoutParams t=new LinearLayout.LayoutParams(-1,0,1);t.topMargin=dp(10);root.addView(two,t);
        }else{
            LinearLayout all=scroller(root);add(all,sceneBox,-2,9);add(all,taskBox,-2,10);
        }
        scene=new AdventureView(this,Curriculum.world(lesson),s.stage,s.position,s.solved,false,s.count);
        int sceneH=landscape()?Math.max(110,Math.min(190,(int)(getResources().getDisplayMetrics().heightPixels/getResources().getDisplayMetrics().density)-210)):180;
        add(sceneBox,scene,sceneH,0);if(old>=0)scene.animateFrom(old,jump);
        if(s.position<4&&!s.solved){
            LinearLayout controls=row();
            Button back=btn("◀",PALE,INK,25,()->move(-1,false));
            Button forward=btn("▶",PALE,INK,25,()->move(1,false));
            Button hop=btn("Nhảy ↑",PINK,WHITE,20,()->move(1,true));
            back.setContentDescription("Đi sang trái");forward.setContentDescription("Đi sang phải");
            weight(controls,back,55,3);weight(controls,forward,55,3);weight(controls,hop,55,3);add(sceneBox,controls,-2,7);
            add(taskBox,center(s.position==2?"Nhảy qua chướng ngại vật!":"Đi tới cổng phép thuật!",25,INK,true),-2,4);
            add(taskBox,center("Thử thách "+(s.stage+1)+"/"+s.count,19,MUTED,false),-2,8);
            if(s.stage==0){add(taskBox,text("Bí kíp",20,PINK,true),-2,14);add(taskBox,text(Curriculum.tip(lesson),20,INK,false),-2,6);}
            return;
        }
        Curriculum.Q q=advanced?Curriculum.advanced(lesson,s.stage):Curriculum.question(lesson,s.stage);
        if(s.solved){
            add(taskBox,center("⭐ Chính xác!",30,GREEN,true),-2,8);
            add(taskBox,center(q.explain,24,INK,true),-2,12);
            add(taskBox,center("Cầu đã mở!",19,MUTED,false),-2,7);
            add(taskBox,btn("Qua cầu  ▶",PINK,WHITE,23,()->{s.next();persist();render();}),62,13);return;
        }
        add(taskBox,text(advanced?"Thử thách nâng cao":"Mở đường bằng toán",18,PINK,true),-2,0);
        add(taskBox,center(q.text,q.text.length()>65?23:29,INK,true),-2,9);
        if(q.kind==Curriculum.RULER||q.kind==Curriculum.THERMO)add(taskBox,new MeasureView(this,q),q.kind==Curriculum.RULER?105:172,8);
        if(q.kind==Curriculum.CHOICE){
            for(String option:q.opts)add(taskBox,btn(option,PALE,INK,option.length()>16?21:27,()->submit(q.check(option))),57,7);
        }else if(q.kind==Curriculum.SHARE)share(taskBox,q);
        else keypad(taskBox,q);
        if(s.wrong>0)add(taskBox,text("Thử lại nhé, không sao cả!",19,PINK,true),-2,10);
        if(s.hint)add(taskBox,text(q.hint,19,INK,false),-2,8);
        if(!s.hint)add(taskBox,btn("Gợi ý  💡",PALE,INK,19,()->{s.hint=true;persist();render();}),54,10);
    }
    private void submit(boolean ok){
        SaveState s=state();if(s.solved||s.stage>=s.count||s.position!=4)return;
        if(ok)s.correct();
        else{s.wrong++;s.hint=true;}
        persist();render();
    }
    private void keypad(LinearLayout l,Curriculum.Q q){
        SaveState s=state();TextView field=center(s.draft.isEmpty()?"?":s.draft,36,PINK,true);field.setBackground(shape(PALE,16));add(l,field,60,8);
        for(int r=0;r<4;r++){
            LinearLayout line=row();
            for(int col=0;col<3;col++){
                int v=r*3+col;
                if(v==9){weight(line,btn("Xóa",PALE,INK,18,()->{s.draft="";persist();field.setText("?");}),54,3);}
                else if(v==11){weight(line,btn("⌫",PALE,INK,22,()->{if(!s.draft.isEmpty())s.draft=s.draft.substring(0,s.draft.length()-1);persist();field.setText(s.draft.isEmpty()?"?":s.draft);}),54,3);}
                else{String digit=v==10?"0":""+(v+1);weight(line,btn(digit,WHITE,INK,26,()->{if(s.draft.length()<4){s.draft=s.draft.equals("0")?digit:s.draft+digit;persist();field.setText(s.draft);}}),54,3);}
            }add(l,line,-2,5);
        }
        add(l,btn("Kiểm tra",PINK,WHITE,22,()->{if(!s.draft.isEmpty())submit(q.check(s.draft));}),56,9);
    }
    private void share(LinearLayout l,Curriculum.Q q){
        SaveState s=state();TextView pool=center("",23,PINK,true);pool.setBackground(shape(PALE,16));add(l,pool,58,8);
        TextView[] baskets=new TextView[q.b];
        Runnable refresh=()->{int used=0;for(int i=0;i<q.b;i++){used+=s.groups[i];if(baskets[i]!=null)baskets[i].setText("🧺 "+s.groups[i]);}pool.setText("🍎 Còn lại: "+(q.a-used));};
        pool.setOnLongClickListener(v->{ClipData data=ClipData.newPlainText("apple","apple");if(android.os.Build.VERSION.SDK_INT>=24)v.startDragAndDrop(data,new View.DragShadowBuilder(v),null,0);else v.startDrag(data,new View.DragShadowBuilder(v),null,0);return true;});
        for(int r=0;r<(q.b+1)/2;r++){
            LinearLayout line=row();
            for(int col=0;col<2;col++){
                final int idx=r*2+col;if(idx>=q.b){weight(line,new View(this),1,3);continue;}
                LinearLayout b=column();b.setBackground(shape(0xfffff4da,14));padding(b,4);
                baskets[idx]=center("🧺 0",26,INK,true);add(b,baskets[idx],45,0);
                Runnable more=()->{int total=Arrays.stream(s.groups).sum();if(total<q.a){s.groups[idx]++;persist();refresh.run();}};
                baskets[idx].setOnClickListener(v->more.run());
                baskets[idx].setOnDragListener((v,event)->{if(event.getAction()==DragEvent.ACTION_DROP){more.run();return true;}return true;});
                LinearLayout buttons=row();weight(buttons,btn("−",WHITE,INK,23,()->{if(s.groups[idx]>0){s.groups[idx]--;persist();refresh.run();}}),54,2);
                weight(buttons,btn("+",PINK,WHITE,25,more),54,2);add(b,buttons,-2,0);weight(line,b,-2,3);
            }add(l,line,-2,6);
        }
        refresh.run();add(l,btn("Kiểm tra",PINK,WHITE,22,()->submit(q.shareOK(Arrays.copyOf(s.groups,q.b)))),56,9);
    }
    private void finishLesson(){
        LinearLayout l=scroller(root);LinearLayout box=card();
        add(box,center(advanced?"Hoàn thành nâng cao! 🎉":"Hoàn thành! 🎉",32,PINK,true),-2,0);
        add(box,new AdventureView(this,Curriculum.world(lesson),state().count-1,4,true,true),landscape()?145:190,10);
        add(box,center("⭐ "+state().stars()+"/"+state().count,27,INK,true),-2,8);
        if(!advanced){
            add(box,btn("Thử thách nâng cao "+bonus[lesson-25].stars()+"/2",0xffffefd3,INK,21,()->start(lesson,true)),64,10);
            add(box,text("2 câu tùy chọn",17,MUTED,false),-2,4);
        }
        if(lesson<44)add(box,btn("Bài tiếp theo  ▶",PINK,WHITE,23,()->start(lesson+1)),62,10);
        else add(box,btn("Về bản đồ",PINK,WHITE,23,()->navigate("map")),62,10);
        if(advanced)add(box,btn("Về bài cơ bản",PALE,INK,20,()->start(lesson)),54,8);
        add(box,btn(advanced?"Chơi lại nâng cao":"Chơi lại bài "+lesson,PALE,INK,20,()->{state().replay();persist();render();}),54,8);add(l,box,-2,12);
    }
    private void settings(){
        String[] items={sound?"Tắt âm thanh nút":"Bật âm thanh nút","Sao lưu tiến độ","Khôi phục tiến độ","Thông tin phiên bản"};
        new AlertDialog.Builder(this).setTitle("Cài đặt").setItems(items,(d,i)->{
            if(i==0){sound=!sound;persist();render();}
            else if(i==1){Intent in=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/json").putExtra(Intent.EXTRA_TITLE,"ToanLop3-tien-do.json");startActivityForResult(in,EXPORT);}
            else if(i==2){Intent in=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/json");startActivityForResult(in,IMPORT);}
            else new AlertDialog.Builder(this).setTitle("Toán lớp 3 · 0.2.0").setMessage("Bản thử nghiệm Android native. Bài 25–44, Kết nối tri thức với cuộc sống. 40 câu nâng cao tùy chọn. Câu hỏi tự soạn theo chủ đề, chưa xác minh từng câu với vở bài tập gốc. Không cần Internet.").setPositiveButton("Đóng",null).show();
        }).setNegativeButton("Đóng",null).show();
    }
    private JSONObject backup() throws JSONException{
        JSONObject j=new JSONObject();j.put("schema",2);j.put("app","vn.toanlop3");j.put("lesson",lesson);j.put("screen",screen);j.put("sound",sound);j.put("advanced",advanced);
        JSONObject all=new JSONObject(),extra=new JSONObject();for(int n=25;n<=44;n++){all.put(""+n,data[n-25].json());extra.put(""+n,bonus[n-25].json());}j.put("lessons",all);j.put("bonus",extra);return j;
    }
    @Override protected void onActivityResult(int req,int result,Intent intent){
        super.onActivityResult(req,result,intent);if(result!=RESULT_OK||intent==null||intent.getData()==null)return;
        try{
            if(req==EXPORT){
                try(OutputStream out=getContentResolver().openOutputStream(intent.getData(),"wt")){if(out==null)throw new IOException();out.write(backup().toString(2).getBytes(StandardCharsets.UTF_8));}
                Toast.makeText(this,"Đã sao lưu",Toast.LENGTH_SHORT).show();
            }else if(req==IMPORT){
                ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                try(InputStream in=getContentResolver().openInputStream(intent.getData())){if(in==null)throw new IOException();byte[] buf=new byte[4096];int len;while((len=in.read(buf))!=-1){if(bytes.size()+len>131072)throw new IOException("Too large");bytes.write(buf,0,len);}}
                JSONObject j=new JSONObject(new String(bytes.toByteArray(),StandardCharsets.UTF_8));
                if((j.optInt("schema")!=1&&j.optInt("schema")!=2)||!"vn.toanlop3".equals(j.optString("app")))throw new IOException("Wrong format");
                JSONObject all=j.getJSONObject("lessons"),extra=j.optJSONObject("bonus");SaveState[] imported=new SaveState[20],importedBonus=new SaveState[20];
                for(int n=25;n<=44;n++){imported[n-25]=SaveState.read(all.getJSONObject(""+n),n);importedBonus[n-25]=SaveState.read(extra==null?null:extra.optJSONObject(""+n),n,true);}
                new AlertDialog.Builder(this).setTitle("Khôi phục tiến độ?").setMessage("Tiến độ hiện tại sẽ được thay bằng bản sao lưu.").setNegativeButton("Hủy",null).setPositiveButton("Khôi phục",(d,w)->{data=imported;bonus=importedBonus;advanced=j.optBoolean("advanced",false);lesson=SaveState.bound(j.optInt("lesson",25),25,44);sound=j.optBoolean("sound",true);screen="home";persist();render();Toast.makeText(this,"Đã khôi phục",Toast.LENGTH_SHORT).show();}).show();
            }
        }catch(Exception e){new AlertDialog.Builder(this).setTitle("Không thể xử lý tệp").setMessage("Hãy chọn bản sao lưu hợp lệ của Toán lớp 3. Tiến độ hiện tại không thay đổi.").setPositiveButton("Đóng",null).show();}
    }
    @Override public void onBackPressed(){if(!screen.equals("home"))navigate("home");else super.onBackPressed();}
}
