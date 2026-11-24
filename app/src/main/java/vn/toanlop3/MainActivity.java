package vn.toanlop3;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** One native Android journey: the math answers open the game route. */
public final class MainActivity extends Activity {
 private static final int PINK=0xffd62f85, DARK=0xff432b62, MUTED=0xff795d77, WHITE=0xfffffdf7, CREAM=0xfffff4d9, GOLD=0xffffd45f, PALE=0xffffe5f2, PURPLE=0xff7954c8, BLUE=0xff3d8bca, GREEN=0xff38864c;
 private static final int EXPORT=81,IMPORT=82;
 private final JourneyState[] core=new JourneyState[20],bonus=new JourneyState[20];
 private SharedPreferences prefs;
 private int lesson=25;
 private boolean advanced=false,reviewing=false,sound=true;
 private JourneyState review;
 private String screen="map",legacyArchive="";
 private LinearLayout root;
 private GameAudio audio;
 private int dp(float v){return Math.round(v*getResources().getDisplayMetrics().density);}
 private int heightDp(){return (int)(getResources().getDisplayMetrics().heightPixels/getResources().getDisplayMetrics().density);}
 private int widthDp(){return (int)(getResources().getDisplayMetrics().widthPixels/getResources().getDisplayMetrics().density);}
 private JourneyState regular(){return (advanced?bonus:core)[lesson-25];}
 private JourneyState state(){return reviewing&&review!=null?review:regular();}
 private JSONObject parse(String s){try{return new JSONObject(s);}catch(Exception e){return null;}}
 @Override public void onCreate(Bundle b){
  super.onCreate(b);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);immersive();
  prefs=getSharedPreferences("toanlop3.journey.v3",MODE_PRIVATE);
  SharedPreferences old=getSharedPreferences("toanlop3.progress.v1",MODE_PRIVATE);
  boolean first=!prefs.getBoolean("initialized",false);
  lesson=JourneyState.bound(first?old.getInt("lesson",25):prefs.getInt("lesson",25),25,44);
  advanced=first?old.getBoolean("advanced",false):prefs.getBoolean("advanced",false);sound=first?old.getBoolean("sound",true):prefs.getBoolean("sound",true);
  screen=first?"map":prefs.getString("screen","map");if(!Arrays.asList("map","game","review","awards","settings").contains(screen))screen="map";
  legacyArchive=prefs.getString("legacy","");
  try{
   JSONObject archive=new JSONObject();JSONObject oldCore=new JSONObject(),oldBonus=new JSONObject();
   for(int i=0;i<20;i++){int n=25+i;
    core[i]=JourneyState.read(n,false,parse(prefs.getString("core"+n,"")));
    JSONObject prev=parse(old.getString("bonus"+n,""));
    bonus[i]=first?JourneyState.migrateAdvanced(n,prev):JourneyState.read(n,true,parse(prefs.getString("bonus"+n,"")));
    if(first){JSONObject x=parse(old.getString("lesson"+n,""));if(x!=null)oldCore.put(""+n,x);if(prev!=null)oldBonus.put(""+n,prev);}
   }
   if(first&&oldCore.length()>0){archive.put("lessons",oldCore);archive.put("bonus",oldBonus);archive.put("lesson",lesson);legacyArchive=archive.toString();}
   reviewing=!first&&prefs.getBoolean("reviewing",false)&&screen.equals("game");
   if(reviewing){review=JourneyState.read(lesson,advanced,parse(prefs.getString("reviewState","")));if(review.finished()){reviewing=false;screen="review";}}
  }catch(JSONException e){throw new IllegalStateException(e);}
  save(true);render();
 }
 private void immersive(){getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);}
 @Override public void onWindowFocusChanged(boolean f){super.onWindowFocusChanged(f);if(f)immersive();}
 @Override public void onConfigurationChanged(Configuration c){super.onConfigurationChanged(c);save(true);render();}
 @Override protected void onPause(){save(true);if(audio!=null)audio.pause();super.onPause();}
 @Override protected void onResume(){super.onResume();syncAudio();}
 @Override protected void onDestroy(){if(audio!=null){audio.close();audio=null;}super.onDestroy();}
 private void save(boolean sync){
  if(prefs==null)return;
  SharedPreferences.Editor e=prefs.edit().putBoolean("initialized",true).putInt("lesson",lesson).putBoolean("advanced",advanced).putBoolean("sound",sound).putString("screen",screen).putString("legacy",legacyArchive).putBoolean("reviewing",reviewing);
  try{for(int i=0;i<20;i++){e.putString("core"+(i+25),core[i].json().toString());e.putString("bonus"+(i+25),bonus[i].json().toString());}if(review!=null)e.putString("reviewState",review.json().toString());}
  catch(JSONException ex){throw new IllegalStateException(ex);}
  if(sync)e.commit();else e.apply();
 }
 private int countDone(){int n=0;for(JourneyState s:core)if(s.completed()==s.count())n++;return n;}
 private int stars(){int n=0;for(JourneyState s:core)n+=s.completed();for(JourneyState s:bonus)n+=s.completed();return n;}
 private int levelStars(int n){JourneyState s=core[n-25];int a=s.completed();return a==s.count()?3:a==0?0:a*2>=s.count()?2:1;}
 private void syncAudio(){try{if(audio==null)audio=new GameAudio(this);audio.setEnabled(sound);if(sound)audio.music(Curriculum.world(lesson));}catch(RuntimeException ignored){}}
 private void playSound(int effect){if(!sound)return;try{if(audio==null)audio=new GameAudio(this);audio.play(effect);}catch(RuntimeException ignored){}}
 private GradientDrawable background(int top,int bot,int radius,int stroke){
  GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{top,bot});d.setCornerRadius(dp(radius));if(stroke!=0)d.setStroke(dp(2),stroke);return d;
 }
 private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
 private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
 private TextView text(String s,float size,int color,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextColor(color);v.setTextSize(size);v.setTypeface(Typeface.create("sans-serif-medium",bold?Typeface.BOLD:Typeface.NORMAL));v.setIncludeFontPadding(true);return v;}
 private TextView center(String s,float size,int color,boolean bold){TextView v=text(s,size,color,bold);v.setGravity(Gravity.CENTER);return v;}
 private Button button(String s,int top,int bot,int fg,float size,Runnable action){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(fg);b.setTextSize(size);b.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));b.setMinHeight(dp(42));b.setMinimumHeight(dp(42));b.setMinWidth(0);b.setMinimumWidth(0);b.setPadding(dp(8),dp(3),dp(8),dp(3));b.setGravity(Gravity.CENTER);b.setBackground(new RippleDrawable(ColorStateList.valueOf(0x32ffffff),background(top,bot,14,0xaaffffff),null));b.setElevation(dp(2));b.setSoundEffectsEnabled(sound);b.setOnClickListener(v->action.run());return b;}
 private Button pink(String s,float size,Runnable a){return button(s,0xfffc77b6,PINK,Color.WHITE,size,a);}
 private Button pale(String s,float size,Runnable a){return button(s,0xfffff7ed,0xffffdfec,DARK,size,a);}
 private void add(LinearLayout l,View v,int h,int margin){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,h<0?h:dp(h));p.topMargin=dp(margin);l.addView(v,p);}
 private void weight(LinearLayout l,View v,int h,float w,int margin){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,h<0?h:dp(h),w);p.leftMargin=dp(margin);p.rightMargin=dp(margin);l.addView(v,p);}
 private void pad(View v,int n){v.setPadding(dp(n),dp(n),dp(n),dp(n));}
 private LinearLayout card(){LinearLayout l=column();l.setBackground(background(WHITE,CREAM,17,0xffefb95c));pad(l,10);return l;}
 private ScrollView scroll(LinearLayout child){ScrollView s=new ScrollView(this);s.setFillViewport(false);s.setClipToPadding(false);s.addView(child,new ScrollView.LayoutParams(-1,-2));return s;}
 private void bar(LinearLayout parent,int n,int total){ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(Math.max(1,total));p.setProgress(n);p.setProgressTintList(ColorStateList.valueOf(PINK));p.setProgressBackgroundTintList(ColorStateList.valueOf(0xffead9df));add(parent,p,9,4);}
 private void nav(String to){reviewing=false;screen=to;save(false);render();}
 private void start(int n,boolean adv){lesson=n;advanced=adv;reviewing=false;screen="game";save(true);render();}
 private void startReview(int n,boolean adv,int index){lesson=n;advanced=adv;reviewing=true;review=new JourneyState(n,adv);review.cursor=index;screen="game";save(false);render();}
 private void render(){
  syncAudio();
  if(screen.equals("map")){
   setContentView(new ArtMapView(this,core,bonus,lesson,sound,new ArtMapView.Listener(){
    public void select(int n){lesson=n;advanced=false;playSound(0);save(true);render();}
    public void open(boolean adv){playSound(0);start(lesson,adv);}
    public void navigate(String to){playSound(0);nav(to);}
    public void sound(){sound=!sound;save(true);render();}
   }));
   return;
  }
  if(screen.equals("game")&&!state().finished()){
   JourneyState current=state();BookBank.Q question=current.current();
   setContentView(new MathAdventureView(this,current,lesson,advanced,reviewing,new MathAdventureView.Host(){
    public void save(boolean sync){MainActivity.this.save(sync);}
    public void next(){
     if(!current.solved)return;
     if(reviewing){regular().missed.remove(question.id());nav("review");}
     else{current.next();MainActivity.this.save(true);if(current.finished())playSound(4);render();}
    }
    public void back(){nav(reviewing?"review":"map");}
    public void guide(){showGuide();}
    public void sound(int effect){playSound(effect);}
    public void practice(Runnable confirm){new AlertDialog.Builder(MainActivity.this).setTitle("Đã làm bài thực hành?").setMessage("Bé hoặc người lớn xác nhận đã thực hành theo hướng dẫn trong sách.").setNegativeButton("Chưa",null).setPositiveButton("Đã làm",(d,w)->confirm.run()).show();}
   }));
   return;
  }
  root=column();root.setBackgroundColor(0xff72cef3);pad(root,6);setContentView(root);
  if(screen.equals("game")){showLessonComplete();return;}
  LinearLayout header=row();header.setBackground(background(0xffffe6f2,0xfffff9ed,15,0xffffffff));pad(header,5);
  TextView title=text("Toán lớp 3",widthDp()<680?24:30,DARK,true);title.setShadowLayer(dp(1),0,dp(1),0xffffffff);header.addView(title,new LinearLayout.LayoutParams(0,-2,1.2f));
  LinearLayout who=row();ImageView face=new ImageView(this);face.setImageResource(R.drawable.mascot);face.setScaleType(ImageView.ScaleType.FIT_CENTER);who.addView(face,new LinearLayout.LayoutParams(dp(34),dp(42)));TextView profile=center("Mộc Nhiên",widthDp()<680?18:22,PINK,true);who.addView(profile,new LinearLayout.LayoutParams(0,-2,1));header.addView(who,new LinearLayout.LayoutParams(0,-2,1));
  TextView total=center("⭐ "+stars(),20,DARK,true);header.addView(total,new LinearLayout.LayoutParams(0,-2,.55f));
  Button soundBtn=pale(sound?"🔊":"🔇",21,()->{sound=!sound;save(false);render();});soundBtn.setContentDescription(sound?"Tắt âm thanh":"Bật âm thanh");header.addView(soundBtn,new LinearLayout.LayoutParams(dp(47),dp(42)));
  add(root,header,-2,0);
  LinearLayout body=row();body.setGravity(Gravity.TOP);LinearLayout menu=column();menu.setGravity(Gravity.CENTER);menu.setBackground(background(0xff2a88bd,0xff3964a9,18,0xffffffff));pad(menu,4);
  String[] names={"🏡 Hành trình","📖 Ôn tập","🏆 Thành tích","⚙ Cài đặt"},pages={"map","review","awards","settings"};
  for(int i=0;i<4;i++){final String to=pages[i];boolean sel=screen.equals(to);Button b=button(names[i],sel?0xffffe67a:0xffa784f4,sel?0xffefa72f:0xff7751c4,sel?DARK:Color.WHITE,widthDp()<680?14:17,()->nav(to));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,0,1);lp.topMargin=dp(3);lp.bottomMargin=dp(3);menu.addView(b,lp);}
  LinearLayout.LayoutParams ml=new LinearLayout.LayoutParams(dp(widthDp()<690?111:133),-1);ml.rightMargin=dp(5);body.addView(menu,ml);
  LinearLayout page=card();if(screen.equals("review"))reviewPage(page);else if(screen.equals("awards"))awards(page);else settings(page);ScrollView s=scroll(page);body.addView(s,new LinearLayout.LayoutParams(0,-1,1));
  LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=dp(5);root.addView(body,bp);
  LinearLayout footer=row();footer.setBackground(background(0xfffff6d8,0xffffe8bd,13,0xffffffff));pad(footer,3);
  TextView summary=center("Hành trình "+countDone()+"/20 bài",14,DARK,true);footer.addView(summary,new LinearLayout.LayoutParams(0,dp(26),1));
  ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(20);p.setProgress(countDone());p.setProgressTintList(ColorStateList.valueOf(0xff62bd3c));footer.addView(p,new LinearLayout.LayoutParams(0,dp(12),1));
  TextView saved=center("✓ Đã lưu",13,GREEN,true);footer.addView(saved,new LinearLayout.LayoutParams(0,dp(26),.55f));add(root,footer,-2,5);
 }
 private void showGuide(){new AlertDialog.Builder(this).setTitle(Curriculum.title(lesson)).setMessage(LessonGuide.RULE[lesson-25]+"\n\nVí dụ: "+LessonGuide.EXAMPLE[lesson-25]+"\n\nSGK trang "+LessonGuide.pages(lesson)).setPositiveButton("Đã hiểu",null).show();}
 private void showLessonComplete(){
  root.removeAllViews();root.setPadding(0,0,0,0);
  FrameLayout frame=new FrameLayout(this);
  int[] art={R.drawable.world_0,R.drawable.world_1,R.drawable.world_2,R.drawable.world_3};
  ImageView scenery=new ImageView(this);scenery.setImageResource(art[Curriculum.world(lesson)]);scenery.setScaleType(ImageView.ScaleType.CENTER_CROP);frame.addView(scenery,new FrameLayout.LayoutParams(-1,-1));
  LinearLayout body=row();body.setGravity(Gravity.CENTER);pad(body,14);
  LinearLayout celebration=column();celebration.setGravity(Gravity.CENTER);
  ImageView hero=new ImageView(this);hero.setImageResource(R.drawable.mascot);hero.setScaleType(ImageView.ScaleType.FIT_CENTER);celebration.addView(hero,new LinearLayout.LayoutParams(-1,0,1));
  ImageView chest=new ImageView(this);chest.setImageResource(R.drawable.treasure);chest.setScaleType(ImageView.ScaleType.FIT_CENTER);celebration.addView(chest,new LinearLayout.LayoutParams(-1,0,.5f));
  body.addView(celebration,new LinearLayout.LayoutParams(0,-1,.75f));
  LinearLayout pane=card();add(pane,center(advanced?"Hoàn thành nâng cao!":"Hoàn thành bài "+lesson+"!",heightDp()<400?25:30,PINK,true),-2,0);
  add(pane,center("★ "+state().completed()+"/"+state().count()+" nhiệm vụ",23,DARK,true),-2,3);
  if(!advanced)add(pane,pink("Thử thách nâng cao · 2 câu",20,()->start(lesson,true)),47,6);
  LinearLayout next=row();
  if(lesson<44)weight(next,pink("Bài "+(lesson+1)+"  ▶",20,()->start(lesson+1,false)),47,1,3);
  else weight(next,pink("Về hành trình",20,()->nav("map")),47,1,3);
  weight(next,pale("Chơi lại",18,()->{state().replay();save(true);render();}),47,1,3);add(pane,next,-2,6);
  if(lesson<44)add(pane,pale("Về hành trình",18,()->nav("map")),43,5);
  body.addView(scroll(pane),new LinearLayout.LayoutParams(0,-1,1.25f));frame.addView(body,new FrameLayout.LayoutParams(-1,-1));root.addView(frame,new LinearLayout.LayoutParams(-1,0,1));
 }
 private void reviewPage(LinearLayout page){
  add(page,text("Ôn lại câu chưa vững",25,PINK,true),-2,0);int total=0;
  for(int i=0;i<20;i++)for(int a=0;a<2;a++){JourneyState s=a==0?core[i]:bonus[i];for(int j=0;j<s.count();j++){BookBank.Q q=s.get(j);if(!s.missed.contains(q.id()))continue;total++;final int n=i+25,index=j;final boolean ad=a==1;add(page,pale("Bài "+n+(ad?" · Nâng cao":" · Tiết "+q.period)+" — "+q.prompt,17,()->startReview(n,ad,index)),-2,6);}}
  if(total==0)add(page,center("Chưa có câu nào cần ôn lại.\nTiếp tục hành trình nhé!",23,DARK,true),-2,12);
 }
 private void awards(LinearLayout page){add(page,center("🏆 Thành tích của Mộc Nhiên",26,PINK,true),-2,0);
  add(page,center("⭐ "+stars()+" nhiệm vụ đã hoàn thành",21,DARK,true),-2,5);
  for(int i=0;i<20;i++){int n=i+25;JourneyState s=core[i];add(page,text("Bài "+n+"  ·  "+Curriculum.title(n),18,DARK,true),-2,10);add(page,text(s.completed()+"/"+s.count()+" cơ bản   ·   "+bonus[i].completed()+"/2 nâng cao",15,MUTED,false),-2,2);bar(page,s.completed(),s.count());}
 }
 private void settings(LinearLayout page){add(page,text("Cài đặt",25,PINK,true),-2,0);add(page,pale(sound?"🔊 Tắt âm thanh":"🔇 Bật âm thanh",20,()->{sound=!sound;save(false);render();}),48,7);
  add(page,pale("Sao lưu tiến độ",20,()->{Intent in=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/json").putExtra(Intent.EXTRA_TITLE,"ToanLop3-tien-do.json");startActivityForResult(in,EXPORT);}),48,7);
  add(page,pale("Khôi phục tiến độ",20,()->{Intent in=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/json");startActivityForResult(in,IMPORT);}),48,7);
  add(page,text("Toán lớp 3 · 0.6.0\nSGK Toán 3 tập một, tr.72–121; VBT tập một, tr.64–115. Ngân hàng chuyển thể có chỉ mục trang/tiết. 2 câu nâng cao tự soạn mỗi bài. Các nhiệm vụ thực hành thật do người học xác nhận; câu đo trên hình được chấm tự động. Android native, chạy offline.",16,MUTED,false),-2,9);
  if(!legacyArchive.isEmpty())add(page,text("Tiến độ bản thử nghiệm cũ vẫn được lưu trong bản sao lưu; nội dung cơ bản đã thay sang ngân hàng theo sách nên không tự gán kết quả cũ cho câu mới.",14,MUTED,false),-2,8);
 }
 private JSONObject backup()throws JSONException{JSONObject j=new JSONObject(),a=new JSONObject(),b=new JSONObject();j.put("schema",3);j.put("app","vn.toanlop3");j.put("lesson",lesson);j.put("advanced",advanced);j.put("sound",sound);j.put("screen",screen);j.put("legacy",legacyArchive);for(int i=0;i<20;i++){a.put(""+(i+25),core[i].json());b.put(""+(i+25),bonus[i].json());}j.put("lessons",a);j.put("bonus",b);if(reviewing&&review!=null){j.put("reviewing",true);j.put("reviewState",review.json());}return j;}
 @Override protected void onActivityResult(int req,int result,Intent in){super.onActivityResult(req,result,in);if(result!=RESULT_OK||in==null||in.getData()==null)return;
  try{if(req==EXPORT){try(OutputStream out=getContentResolver().openOutputStream(in.getData(),"wt")){if(out==null)throw new IOException();out.write(backup().toString(2).getBytes(StandardCharsets.UTF_8));}Toast.makeText(this,"Đã sao lưu",Toast.LENGTH_SHORT).show();return;}
   if(req!=IMPORT)return;ByteArrayOutputStream buf=new ByteArrayOutputStream();try(InputStream stream=getContentResolver().openInputStream(in.getData())){if(stream==null)throw new IOException();byte[] bytes=new byte[4096];int len;while((len=stream.read(bytes))!=-1){if(buf.size()+len>2*1024*1024)throw new IOException("File too large");buf.write(bytes,0,len);}}
   JSONObject j=new JSONObject(new String(buf.toByteArray(),StandardCharsets.UTF_8));int schema=j.optInt("schema");if(schema<1||schema>3||!"vn.toanlop3".equals(j.optString("app")))throw new IOException("Invalid backup");
   JSONObject ca=j.getJSONObject("lessons"),ba=j.optJSONObject("bonus");JourneyState[] x=new JourneyState[20],y=new JourneyState[20];
   for(int i=0;i<20;i++){int n=i+25;JSONObject v=ca.getJSONObject(""+n),w=ba==null?null:ba.optJSONObject(""+n);x[i]=schema==3?JourneyState.read(n,false,v):new JourneyState(n,false);y[i]=schema==3?JourneyState.read(n,true,w):JourneyState.migrateAdvanced(n,w);}
   new AlertDialog.Builder(this).setTitle("Khôi phục tiến độ?").setMessage(schema==3?"Thay tiến độ hiện tại bằng bản sao lưu.":"Bản cũ có ngân hàng câu hỏi khác. Lưu dữ liệu cũ và khôi phục phần nâng cao tương ứng.").setNegativeButton("Hủy",null).setPositiveButton("Khôi phục",(d,w)->{System.arraycopy(x,0,core,0,20);System.arraycopy(y,0,bonus,0,20);lesson=JourneyState.bound(j.optInt("lesson",25),25,44);advanced=j.optBoolean("advanced");sound=j.optBoolean("sound",true);legacyArchive=schema==3?j.optString("legacy",""):j.toString();screen="map";reviewing=false;save(true);render();Toast.makeText(this,"Đã khôi phục",Toast.LENGTH_SHORT).show();}).show();
  }catch(Exception ex){new AlertDialog.Builder(this).setTitle("Không thể xử lý tệp").setMessage("Hãy chọn bản sao lưu hợp lệ. Tiến độ hiện tại chưa bị thay đổi.").setPositiveButton("Đóng",null).show();}
 }
 @Override public void onBackPressed(){if(!screen.equals("map"))nav(reviewing?"review":"map");else super.onBackPressed();}
}
