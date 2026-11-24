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
 private WorldView world;
 private ToneGenerator tone;
 private float animationFrom=-1;
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
 @Override protected void onPause(){save(true);super.onPause();}
 @Override protected void onDestroy(){if(tone!=null){tone.release();tone=null;}super.onDestroy();}
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
 private void playSound(boolean ok){if(!sound)return;try{if(tone==null)tone=new ToneGenerator(AudioManager.STREAM_MUSIC,45);tone.startTone(ok?ToneGenerator.TONE_PROP_ACK:ToneGenerator.TONE_PROP_BEEP,ok?140:70);}catch(RuntimeException ignored){}}
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
 private void start(int n,boolean adv){lesson=n;advanced=adv;reviewing=false;screen="game";save(false);render();}
 private void startReview(int n,boolean adv,int index){lesson=n;advanced=adv;reviewing=true;review=new JourneyState(n,adv);review.cursor=index;screen="game";save(false);render();}
 private void render(){
  root=column();root.setBackgroundColor(0xffe4f7fc);pad(root,6);setContentView(root);
  if(screen.equals("game")){game();return;}
  LinearLayout header=row();header.setBackground(background(0xffffe6f2,0xfffff9ed,15,0xffffffff));pad(header,5);
  TextView title=text("📚 Toán lớp 3",widthDp()<680?22:28,DARK,true);header.addView(title,new LinearLayout.LayoutParams(0,-2,1.2f));
  TextView profile=center("🐷 Mộc Nhiên",widthDp()<680?17:23,PINK,true);header.addView(profile,new LinearLayout.LayoutParams(0,-2,1));
  TextView total=center("⭐ "+stars(),20,DARK,true);header.addView(total,new LinearLayout.LayoutParams(0,-2,.55f));
  Button soundBtn=pale(sound?"🔊":"🔇",21,()->{sound=!sound;save(false);render();});soundBtn.setContentDescription(sound?"Tắt âm thanh":"Bật âm thanh");header.addView(soundBtn,new LinearLayout.LayoutParams(dp(47),dp(42)));
  add(root,header,-2,0);
  LinearLayout body=row();body.setGravity(Gravity.TOP);LinearLayout menu=column();menu.setGravity(Gravity.CENTER);menu.setBackground(background(0xff2a88bd,0xff3964a9,18,0xffffffff));pad(menu,4);
  String[] names={"🏡 Hành trình","📖 Ôn tập","🏆 Thành tích","⚙ Cài đặt"},pages={"map","review","awards","settings"};
  for(int i=0;i<4;i++){final String to=pages[i];boolean sel=screen.equals(to);Button b=button(names[i],sel?0xffffe67a:0xffa784f4,sel?0xffefa72f:0xff7751c4,sel?DARK:Color.WHITE,widthDp()<680?14:17,()->nav(to));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,0,1);lp.topMargin=dp(3);lp.bottomMargin=dp(3);menu.addView(b,lp);}
  LinearLayout.LayoutParams ml=new LinearLayout.LayoutParams(dp(widthDp()<690?105:137),-1);ml.rightMargin=dp(5);body.addView(menu,ml);
  if(screen.equals("map"))map(body);else{LinearLayout page=card();if(screen.equals("review"))reviewPage(page);else if(screen.equals("awards"))awards(page);else settings(page);ScrollView s=scroll(page);body.addView(s,new LinearLayout.LayoutParams(0,-1,1));}
  LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=dp(5);root.addView(body,bp);
  LinearLayout footer=row();footer.setBackground(background(0xfffff6d8,0xffffe8bd,13,0xffffffff));pad(footer,3);
  TextView summary=center("Hành trình "+countDone()+"/20 bài",14,DARK,true);footer.addView(summary,new LinearLayout.LayoutParams(0,dp(26),1));
  ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(20);p.setProgress(countDone());p.setProgressTintList(ColorStateList.valueOf(0xff62bd3c));footer.addView(p,new LinearLayout.LayoutParams(0,dp(12),1));
  TextView saved=center("✓ Đã lưu",13,GREEN,true);footer.addView(saved,new LinearLayout.LayoutParams(0,dp(26),.55f));add(root,footer,-2,5);
 }
 private void map(LinearLayout body){
  FrameLayout frame=new FrameLayout(this);frame.setBackground(background(0xff41b7ec,0xff90e9bc,17,0xffffffff));frame.setClipToOutline(true);
  frame.addView(new WorldView(this,true,Curriculum.world(lesson),0,false),new FrameLayout.LayoutParams(-1,-1));
  LinearLayout grid=column();grid.setPadding(dp(12),dp(10),dp(10),dp(8));frame.addView(grid,new FrameLayout.LayoutParams(-1,-1));
  for(int row=0;row<5;row++){
   LinearLayout line=row();line.setGravity(Gravity.CENTER);
   for(int col=0;col<4;col++){
    int n= row%2==0?44-row*4-col:41-row*4+col;
    // Row 0: 44..41; row 1:37..40; row 2:36..33; row 3:29..32; row 4:28..25.
    final int choice=n;int score=levelStars(n);boolean active=n==lesson;
    int top=active?0xffffc9e5:score>0?0xffb5ef78:0xfff1f3ff,bot=active?0xffe85b9f:score>0?0xff64be38:0xffb2b8cf;
    String label=n+"\n"+(score==3?"★★★":score==2?"★★":score==1?"★":"·");
    Button b=button(label,top,bot,DARK,widthDp()<700?15:18,()->{lesson=choice;advanced=false;save(false);render();});b.setContentDescription("Bài "+n+", "+core[n-25].completed()+"/"+core[n-25].count()+" nhiệm vụ");
    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-1,1);lp.setMargins(dp(4),dp(3),dp(4),dp(3));line.addView(b,lp);
   }
   grid.addView(line,new LinearLayout.LayoutParams(-1,0,1));
  }
  body.addView(frame,new LinearLayout.LayoutParams(0,-1,1.8f));
  LinearLayout panel=card();JourneyState s=core[lesson-25];
  add(panel,center("Bài "+lesson,26,PINK,true),-2,0);
  add(panel,center(Curriculum.title(lesson),widthDp()<690?17:20,DARK,true),-2,4);
  add(panel,center(s.completed()+"/"+s.count()+" nhiệm vụ",16,MUTED,true),-2,7);bar(panel,s.completed(),s.count());
  if(!s.finished()){BookBank.Q q=s.current();add(panel,text("Tiết "+q.period+" · Bài tập "+q.exercise,14,MUTED,false),-2,5);}
  add(panel,pink(s.finished()?"Xem lại bài":"▶ Học & chơi",20,()->start(lesson,false)),48,8);
  if(s.finished()||s.completed()==s.count()){
   add(panel,pale("⭐ Nâng cao "+bonus[lesson-25].completed()+"/2",17,()->start(lesson,true)),44,6);
  }else add(panel,center("⭐ 2 câu nâng cao cuối bài",13,MUTED,false),-2,5);
  if(advanced&&bonus[lesson-25].cursor>0&&!bonus[lesson-25].finished())add(panel,pale("Tiếp tục nâng cao",15,()->start(lesson,true)),42,5);
  ScrollView right=scroll(panel);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,-1,1);rp.leftMargin=dp(5);body.addView(right,rp);
 }
 private void game(){
  JourneyState s=state();
  LinearLayout header=row();header.setBackground(background(0xffffe1f0,WHITE,15,Color.WHITE));pad(header,4);
  header.addView(pale("‹",25,()->nav(reviewing?"review":"map")),new LinearLayout.LayoutParams(dp(48),dp(40)));
  LinearLayout labels=column();String prefix=reviewing?"Ôn tập · ":advanced?"Nâng cao · ":"";
  add(labels,text(prefix+"Bài "+lesson+" · "+Curriculum.title(lesson),widthDp()<690?16:20,DARK,true),-2,0);
  if(!s.finished()){BookBank.Q q=s.current();add(labels,text(advanced?"Thử thách tự soạn "+(s.cursor+1)+"/2":q.source(),13,MUTED,false),-2,0);}
  LinearLayout.LayoutParams lab=new LinearLayout.LayoutParams(0,-2,1);lab.leftMargin=dp(7);header.addView(labels,lab);
  TextView cnt=center("⭐ "+s.completed()+"/"+s.count(),18,PINK,true);header.addView(cnt,new LinearLayout.LayoutParams(dp(96),-2));add(root,header,-2,0);bar(root,s.completed(),s.count());
  if(s.finished()){finish();return;}
  BookBank.Q q=s.current();
  LinearLayout body=row();body.setGravity(Gravity.TOP);
  LinearLayout left=column();left.setBackground(background(0xff4dbff3,0xffb3ed8c,18,Color.WHITE));pad(left,3);
  world=new WorldView(this,false,Curriculum.world(lesson),animationFrom>=0?animationFrom:s.player,s.solved);
  left.addView(world,new LinearLayout.LayoutParams(-1,0,1));
  if(animationFrom>=0){world.moveTo(s.player,true);animationFrom=-1;}
  TextView mission=center(s.solved?"Cầu đã mở!":q.kind==BookBank.Kind.PRACTICE?"Thực hành cùng Mộc Nhiên":advanced?"Thử thách kho báu":"Giải toán để mở đường",17,DARK,true);mission.setBackground(background(WHITE,CREAM,12,Color.WHITE));add(left,mission,38,4);
  if(!s.solved){LinearLayout controls=row();weight(controls,pale("◀",23,()->move(-.16f,false)),42,1,2);weight(controls,pink("Nhảy ↑",17,()->move(.12f,true)),42,1.25f,2);weight(controls,pale("▶",23,()->move(.16f,false)),42,1,2);add(left,controls,-2,3);}
  LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(0,-1,1);l.rightMargin=dp(5);body.addView(left,l);
  LinearLayout task=card();ScrollView scr=scroll(task);body.addView(scr,new LinearLayout.LayoutParams(0,-1,1.16f));
  LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=dp(5);root.addView(body,bp);
  if(s.solved){
   add(task,center(q.kind==BookBank.Kind.PRACTICE?"Đã ghi nhận thực hành!":"⭐ Chính xác!",27,GREEN,true),-2,0);
   add(task,center(q.explain,22,DARK,true),-2,8);
   add(task,pink(reviewing?"Hoàn tất ôn tập":"Qua cầu  ▶",22,()->{if(reviewing){regular().missed.remove(q.id());nav("review");}else{s.next();save(false);render();}}),48,12);return;
  }
  float size=q.prompt.length()>125?20:q.prompt.length()>70?23:28;
  add(task,center(q.prompt,size,DARK,true),-2,0);
  switch(q.kind){
   case RULER:case BALANCE:case SCALE:case CUP:case THERMO:case GEOMETRY:
    add(task,new InstrumentView(this,q),q.kind==BookBank.Kind.GEOMETRY?136:116,5);break;
   case DIVISION:divisionBoard(task,q);break;
   case MULTIPLY:multiplyBoard(task,q);break;
   default:break;
  }
  if(q.kind==BookBank.Kind.CHOICE||q.kind==BookBank.Kind.GEOMETRY){for(String opt:q.options)add(task,pale(opt,opt.length()>35?17:23,()->submit(q.check(opt))),-2,5);}
  else if(q.kind==BookBank.Kind.MULTI)multi(task,q);
  else if(q.kind==BookBank.Kind.PRACTICE){add(task,text("Thực hiện bằng vở hoặc dụng cụ thật. Không chấm số đo bằng kích thước màn hình.",17,MUTED,false),-2,5);add(task,pink("Đã thực hành",21,()->new AlertDialog.Builder(this).setTitle("Đã làm bài thực hành?").setMessage("Bé hoặc người lớn xác nhận đã thực hành theo hướng dẫn.").setNegativeButton("Chưa",null).setPositiveButton("Đã làm",(d,w)->submit(true)).show()),48,8);}
  else keypad(task,q);
  if(s.wrong>0)add(task,text("Thử lại nhé! Bé có thể xem gợi ý.",16,PINK,true),-2,5);
  if(s.hint){add(task,text(q.hint,19,DARK,false),-2,5);if(q.kind==BookBank.Kind.DIVISION)add(task,text("Từng lượt: chia, nhân, trừ, hạ. Kiểm tra bằng số chia × thương + dư.",16,MUTED,false),-2,4);}
  else add(task,pale("💡 Gợi ý",17,()->{s.hint=true;save(false);render();}),42,6);
  if(!advanced&&!reviewing&&s.cursor==0)add(task,pale("Cách làm · SGK tr. "+LessonGuide.pages(lesson),15,()->showGuide()),42,5);
 }
 private void move(float d,boolean jump){JourneyState s=state();if(s.solved)return;s.player=Math.max(0,Math.min(.98f,s.player+d));save(false);world.moveTo(s.player,jump);}
 private void showGuide(){new AlertDialog.Builder(this).setTitle(Curriculum.title(lesson)).setMessage(LessonGuide.RULE[lesson-25]+"\n\nVí dụ: "+LessonGuide.EXAMPLE[lesson-25]+"\n\nSGK trang "+LessonGuide.pages(lesson)).setPositiveButton("Đã hiểu",null).show();}
 private void divisionBoard(LinearLayout l,BookBank.Q q){
  LinearLayout board=row();board.setBackground(background(0xfffff8ed,0xffffe3f0,14,0xffffc6dd));pad(board,5);
  TextView left=center(String.valueOf(q.a)+"   │",33,DARK,true);left.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);weight(board,left,-2,1,1);
  LinearLayout right=column();TextView divisor=center(String.valueOf(q.b),28,DARK,true);divisor.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);add(right,divisor,-2,0);
  View line=new View(this);line.setBackgroundColor(DARK);add(right,line,2,0);
  add(right,center(state().draft[0].isEmpty()?"?":state().draft[0],24,PINK,true),-2,0);weight(board,right,-2,1,1);add(l,board,-2,4);
 }
 private void multiplyBoard(LinearLayout l,BookBank.Q q){TextView board=center(String.format(java.util.Locale.ROOT,"%4d\n×%3d\n────",q.a,q.b),27,DARK,true);board.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);board.setBackground(background(WHITE,PALE,12,0xffffccdf));add(l,board,-2,4);}
 private void multi(LinearLayout l,BookBank.Q q){JourneyState s=state();add(l,text("Chọn tất cả đáp án đúng.",15,MUTED,true),-2,3);
  int cols=q.options.length>4?2:1;
  for(int i=0;i<q.options.length;i+=cols){LinearLayout line=row();for(int j=i;j<Math.min(i+cols,q.options.length);j++){final int idx=j;boolean sel=s.selected.contains(idx);Button b=button((sel?"✓ ":"")+q.options[idx],sel?0xffffc2e1:WHITE,sel?0xffdc79b2:PALE,DARK,q.options[idx].length()>20?16:20,()->{if(!s.selected.add(idx))s.selected.remove(idx);save(false);render();});weight(line,b,-2,1,2);}add(l,line,-2,4);}
  add(l,pink("Kiểm tra",21,()->{if(!s.selected.isEmpty())submit(q.check(s.input()));}),46,6);
 }
 private void keypad(LinearLayout l,BookBank.Q q){
  JourneyState s=state();boolean division=q.kind==BookBank.Kind.DIVISION;
  TextView[] fields=new TextView[division?2:1];LinearLayout answers=row();
  for(int i=0;i<fields.length;i++){final int idx=i;LinearLayout cell=column();String label=division?(i==0?"Thương":"Số dư"):"Đáp án";
   add(cell,center(label,15,MUTED,true),-2,0);fields[i]=center(s.draft[i].isEmpty()?"?":s.draft[i],30,PINK,true);fields[i].setBackground(background(WHITE,PALE,12,s.field==i?PINK:0xffe3c9d9));fields[i].setOnClickListener(v->{s.field=idx;save(false);for(int j=0;j<fields.length;j++)fields[j].setBackground(background(WHITE,PALE,12,s.field==j?PINK:0xffe3c9d9));});add(cell,fields[i],43,2);weight(answers,cell,-2,1,3);
  }add(l,answers,-2,4);
  Runnable update=()->{for(int i=0;i<fields.length;i++){fields[i].setText(s.draft[i].isEmpty()?"?":s.draft[i]);fields[i].setBackground(background(WHITE,PALE,12,s.field==i?PINK:0xffe3c9d9));}save(false);};
  for(int r=0;r<2;r++){LinearLayout keys=row();for(int j=0;j<5;j++){final String digit=String.valueOf(r*5+j+1==10?0:r*5+j+1);weight(keys,pale(digit,23,()->{int f=division?s.field:0;String old=s.draft[f];if(old.length()<4)s.draft[f]=old.equals("0")?digit:old+digit;update.run();}),43,1,2);}add(l,keys,-2,4);}
  LinearLayout buttons=row();weight(buttons,pale("⌫ Xóa",16,()->{int f=division?s.field:0;String v=s.draft[f];if(!v.isEmpty())s.draft[f]=v.substring(0,v.length()-1);update.run();}),44,1,2);
  if(division)weight(buttons,pale("Đổi ô",16,()->{s.field=1-s.field;update.run();}),44,1,2);
  weight(buttons,pink("Kiểm tra",17,()->{if(!s.draft[0].isEmpty()&&(!division||!s.draft[1].isEmpty()))submit(q.check(s.input()));else Toast.makeText(this,division?"Điền thương và số dư (0 nếu chia hết).":"Nhập đáp án trước nhé!",Toast.LENGTH_SHORT).show();}),44,1.45f,2);add(l,buttons,-2,5);
 }
 private void submit(boolean ok){JourneyState s=state();if(s.finished()||s.solved)return;
  if(ok){animationFrom=s.player;s.correct();s.player=1;}else{s.fail();}playSound(ok);save(false);render();
 }
 private void finish(){
  LinearLayout pane=card();add(pane,center(advanced?"🏆 Hoàn thành nâng cao!":"🎉 Hoàn thành bài "+lesson+"!",30,PINK,true),-2,0);
  add(pane,center("⭐ "+state().completed()+"/"+state().count()+" nhiệm vụ",24,DARK,true),-2,6);
  if(!advanced)add(pane,pink("⭐ Thử thách nâng cao · 2 câu",22,()->start(lesson,true)),50,9);
  if(lesson<44)add(pane,pale("Bài "+(lesson+1)+"  ▶",21,()->start(lesson+1,false)),48,7);
  add(pane,pale("Về hành trình",20,()->nav("map")),45,7);add(pane,pale("Làm lại bài",18,()->{state().replay();save(false);render();}),44,6);
  root.addView(scroll(pane),new LinearLayout.LayoutParams(-1,0,1));
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
  add(page,text("Toán lớp 3 · 0.3.0\nSGK Toán 3 tập một, tr.72–121; VBT tập một, tr.64–115. Ngân hàng chuyển thể có chỉ mục trang/tiết. 2 câu nâng cao tự soạn mỗi bài. Các nhiệm vụ thực hành thật do người học xác nhận. Android native, chạy offline.",16,MUTED,false),-2,9);
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
