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
 private AdventureTrail trail;
 private GameAudio audio;
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
 private void playSound(boolean ok){if(!sound)return;try{if(audio==null)audio=new GameAudio();audio.play(ok?1:2);}catch(RuntimeException ignored){}}
 private void tapSound(){if(!sound)return;try{if(audio==null)audio=new GameAudio();audio.play(0);}catch(RuntimeException ignored){}}
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
  root=column();root.setBackgroundColor(0xff72cef3);pad(root,6);setContentView(root);
  if(screen.equals("game")){game();return;}
  LinearLayout header=row();header.setBackground(background(0xffffe6f2,0xfffff9ed,15,0xffffffff));pad(header,5);
  TextView title=text("Toán lớp 3",widthDp()<680?24:30,DARK,true);title.setShadowLayer(dp(1),0,dp(1),0xffffffff);header.addView(title,new LinearLayout.LayoutParams(0,-2,1.2f));
  LinearLayout who=row();ImageView face=new ImageView(this);face.setImageBitmap(JourneyMapView.mascot(this));face.setScaleType(ImageView.ScaleType.FIT_CENTER);who.addView(face,new LinearLayout.LayoutParams(dp(34),dp(42)));TextView profile=center("Mộc Nhiên",widthDp()<680?18:22,PINK,true);who.addView(profile,new LinearLayout.LayoutParams(0,-2,1));header.addView(who,new LinearLayout.LayoutParams(0,-2,1));
  TextView total=center("⭐ "+stars(),20,DARK,true);header.addView(total,new LinearLayout.LayoutParams(0,-2,.55f));
  Button soundBtn=pale(sound?"🔊":"🔇",21,()->{sound=!sound;save(false);render();});soundBtn.setContentDescription(sound?"Tắt âm thanh":"Bật âm thanh");header.addView(soundBtn,new LinearLayout.LayoutParams(dp(47),dp(42)));
  add(root,header,-2,0);
  LinearLayout body=row();body.setGravity(Gravity.TOP);LinearLayout menu=column();menu.setGravity(Gravity.CENTER);menu.setBackground(background(0xff2a88bd,0xff3964a9,18,0xffffffff));pad(menu,4);
  String[] names={"🏡 Hành trình","📖 Ôn tập","🏆 Thành tích","⚙ Cài đặt"},pages={"map","review","awards","settings"};
  for(int i=0;i<4;i++){final String to=pages[i];boolean sel=screen.equals(to);Button b=button(names[i],sel?0xffffe67a:0xffa784f4,sel?0xffefa72f:0xff7751c4,sel?DARK:Color.WHITE,widthDp()<680?14:17,()->nav(to));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,0,1);lp.topMargin=dp(3);lp.bottomMargin=dp(3);menu.addView(b,lp);}
  LinearLayout.LayoutParams ml=new LinearLayout.LayoutParams(dp(widthDp()<690?111:133),-1);ml.rightMargin=dp(5);body.addView(menu,ml);
  if(screen.equals("map"))map(body);else{LinearLayout page=card();if(screen.equals("review"))reviewPage(page);else if(screen.equals("awards"))awards(page);else settings(page);ScrollView s=scroll(page);body.addView(s,new LinearLayout.LayoutParams(0,-1,1));}
  LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=dp(5);root.addView(body,bp);
  LinearLayout footer=row();footer.setBackground(background(0xfffff6d8,0xffffe8bd,13,0xffffffff));pad(footer,3);
  TextView summary=center("Hành trình "+countDone()+"/20 bài",14,DARK,true);footer.addView(summary,new LinearLayout.LayoutParams(0,dp(26),1));
  ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(20);p.setProgress(countDone());p.setProgressTintList(ColorStateList.valueOf(0xff62bd3c));footer.addView(p,new LinearLayout.LayoutParams(0,dp(12),1));
  TextView saved=center("✓ Đã lưu",13,GREEN,true);footer.addView(saved,new LinearLayout.LayoutParams(0,dp(26),.55f));add(root,footer,-2,5);
 }
 private void map(LinearLayout body){
  JourneyMapView map=new JourneyMapView(this,core,lesson,n->{lesson=n;advanced=false;save(false);render();});
  body.addView(map,new LinearLayout.LayoutParams(0,-1,1.9f));
  LinearLayout panel=card();JourneyState s=core[lesson-25];
  TextView label=center("Bài "+lesson,27,Color.WHITE,true);label.setBackground(background(0xfffb8abf,0xffd93487,13,0xffffd8eb));add(panel,label,40,0);
  add(panel,center(Curriculum.title(lesson),widthDp()<690?19:22,DARK,true),-2,4);
  Button go=pink(s.finished()?"Xem lại bài":s.cursor>0||s.solved?"▶ Học tiếp":"▶ Học & chơi",21,()->start(lesson,false));
  go.setContentDescription("Học và chơi bài "+lesson);add(panel,go,50,9);
  add(panel,center(s.completed()+"/"+s.count()+" nhiệm vụ",16,MUTED,true),-2,5);bar(panel,s.completed(),s.count());
  if(!s.finished()){BookBank.Q q=s.current();add(panel,center("Tiết "+q.period+" · Bài tập "+q.exercise,15,MUTED,false),-2,5);}
  if(s.finished()||s.completed()==s.count())add(panel,pale("⭐ Nâng cao "+bonus[lesson-25].completed()+"/2",18,()->start(lesson,true)),44,5);
  else add(panel,center("⭐ 2 câu nâng cao cuối bài",14,MUTED,false),-2,7);
  ScrollView right=scroll(panel);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,-1,1.12f);rp.leftMargin=dp(5);body.addView(right,rp);
 }
 private void game(){
  JourneyState s=state();
  LinearLayout header=row();header.setBackground(background(0xffffe1f0,WHITE,15,Color.WHITE));pad(header,4);
  Button back=pale("‹",25,()->nav(reviewing?"review":"map"));back.setContentDescription("Về hành trình");header.addView(back,new LinearLayout.LayoutParams(dp(48),dp(44)));
  LinearLayout labels=column();String prefix=reviewing?"Ôn tập · ":advanced?"Nâng cao · ":"";
  add(labels,text(prefix+"Bài "+lesson+" · "+Curriculum.title(lesson),widthDp()<690?17:21,DARK,true),-2,0);
  if(!s.finished()){BookBank.Q q=s.current();add(labels,text(advanced?"Thử thách "+(s.cursor+1)+"/2":q.source(),13,MUTED,false),-2,0);}
  LinearLayout.LayoutParams lab=new LinearLayout.LayoutParams(0,-2,1);lab.leftMargin=dp(7);header.addView(labels,lab);
  TextView cnt=center("⭐ "+s.completed()+"/"+s.count(),18,PINK,true);header.addView(cnt,new LinearLayout.LayoutParams(dp(90),-2));add(root,header,-2,0);bar(root,s.completed(),s.count());
  if(s.finished()){showLessonComplete();return;}
  BookBank.Q q=s.current();
  LinearLayout stage=column();stage.setBackground(background(0xffdef7ff,0xffb7eaa1,18,Color.WHITE));pad(stage,6);
  LinearLayout task=card();pad(task,8);
  if(s.solved){
   add(task,center(q.kind==BookBank.Kind.PRACTICE?"Đã ghi nhận thực hành!":"⭐ Chính xác!",30,GREEN,true),-2,0);
   add(task,center(q.explain,24,DARK,true),-2,8);
   Button next=pink(reviewing?"Hoàn tất ôn tập":"Tiếp tục hành trình ▶",24,()->{if(reviewing){regular().missed.remove(q.id());nav("review");}else{s.next();save(true);render();}});
   next.setContentDescription("Tiếp tục nhiệm vụ");add(task,next,56,12);
  }else{
   LinearLayout questionRow=row();
   float size=q.prompt.length()>125?21:q.prompt.length()>70?23:q.prompt.length()>35?26:32;
   TextView question=text(q.prompt,size,DARK,true);question.setGravity(Gravity.CENTER);questionRow.addView(question,new LinearLayout.LayoutParams(0,-2,1));
   Button help=pale("💡",22,()->{s.hint=true;save(false);render();});help.setContentDescription("Gợi ý");questionRow.addView(help,new LinearLayout.LayoutParams(dp(48),dp(44)));add(task,questionRow,-2,0);
   if(q.kind==BookBank.Kind.MULTI&&q.lesson==25&&q.exercise==3&&q.page==64)add(task,new GroupingView(this,20,s.selected),76,4);
   if(q.kind==BookBank.Kind.CHOICE||q.kind==BookBank.Kind.GEOMETRY)choiceTiles(task,q);
   else if(q.kind==BookBank.Kind.MULTI)multi(task,q);
   else if(q.kind==BookBank.Kind.PRACTICE){
    add(task,text(q.hint,19,MUTED,false),-2,5);
    add(task,pink("Đã thực hành",22,()->new AlertDialog.Builder(this).setTitle("Đã làm bài thực hành?").setMessage("Xác nhận đã thực hiện theo hướng dẫn trong sách.").setNegativeButton("Chưa",null).setPositiveButton("Đã làm",(d,w)->submit(true)).show()),52,8);
   }else keypad(task,q);
   if(s.wrong>0)add(task,text("Thử lại nhé! Không mất sao.",19,PINK,true),-2,5);
   if(s.hint)add(task,text(q.hint,19,DARK,false),-2,5);
   if(!advanced&&!reviewing&&s.cursor==0)add(task,pale("Cách làm · SGK tr. "+LessonGuide.pages(lesson),16,()->showGuide()),44,5);
  }
  ScrollView sc=scroll(task);stage.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
  trail=new AdventureTrail(this,Curriculum.world(lesson),animationFrom>=0?animationFrom:s.player,s.solved);
  add(stage,trail,Math.max(66,Math.min(98,heightDp()/4)),5);
  if(animationFrom>=0){trail.advance(s.player,s.solved);animationFrom=-1;}
  LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=dp(5);root.addView(stage,bp);
 }
 private Button stone(String label,boolean selected,float size,Runnable action){
  Button b=button(label,selected?0xffffd46a:0xfffff8df,selected?0xfff19c29:0xffd8c6a3,DARK,size,action);
  b.setElevation(dp(3));return b;
 }
 private void choiceTiles(LinearLayout l,BookBank.Q q){
  if(q.kind==BookBank.Kind.GEOMETRY)add(l,new InstrumentView(this,q),Math.max(96,Math.min(130,heightDp()/3)),4);
  add(l,center("Chạm ô đáp án để Mộc Nhiên tiến lên",16,MUTED,true),-2,4);
  int cols=q.options.length<=3&&Arrays.stream(q.options).allMatch(x->x.length()<25)?q.options.length:2;
  if(q.options.length>0&&Arrays.stream(q.options).anyMatch(x->x.length()>40))cols=1;
  for(int i=0;i<q.options.length;i+=cols){LinearLayout line=row();for(int j=i;j<Math.min(i+cols,q.options.length);j++){String opt=q.options[j];Button b=stone(opt,false,opt.length()>32?19:25,()->submit(q.check(opt)));b.setContentDescription("Đáp án "+opt);weight(line,b,-2,1,3);}add(l,line,-2,6);}
 }
 private void digitMotion(){
  JourneyState s=state();float target=Math.min(.86f,(s.draft[0].length()+s.draft[1].length())*.15f+s.selected.size()*.12f);
  s.player=target;if(trail!=null)trail.advance(target,false);tapSound();save(false);
 }
 private void showGuide(){new AlertDialog.Builder(this).setTitle(Curriculum.title(lesson)).setMessage(LessonGuide.RULE[lesson-25]+"\n\nVí dụ: "+LessonGuide.EXAMPLE[lesson-25]+"\n\nSGK trang "+LessonGuide.pages(lesson)).setPositiveButton("Đã hiểu",null).show();}
 private void multi(LinearLayout l,BookBank.Q q){
  JourneyState s=state();add(l,center("Chọn tất cả ô đúng",19,MUTED,true),-2,2);
  int cols=Arrays.stream(q.options).anyMatch(x->x.length()>32)?1:q.options.length<=3?q.options.length:3;
  for(int i=0;i<q.options.length;i+=cols){LinearLayout line=row();for(int j=i;j<Math.min(i+cols,q.options.length);j++){
   final int idx=j;boolean selected=s.selected.contains(idx);Button b=stone((selected?"✓ ":"")+q.options[idx],selected,q.options[idx].length()>22?18:23,()->{
    animationFrom=s.player;if(!s.selected.add(idx))s.selected.remove(idx);s.player=Math.min(.82f,s.selected.size()*.13f);tapSound();save(false);render();
   });b.setContentDescription("Chọn "+q.options[idx]);weight(line,b,-2,1,3);
  }add(l,line,-2,4);}
  Button check=pink("Kiểm tra",22,()->{if(!s.selected.isEmpty())submit(q.check(s.input()));else Toast.makeText(this,"Chọn ít nhất một ô nhé!",Toast.LENGTH_SHORT).show();});
  check.setContentDescription("Kiểm tra đáp án");add(l,check,48,6);
 }
 private void keypad(LinearLayout l,BookBank.Q q){
  JourneyState s=state();boolean division=q.kind==BookBank.Kind.DIVISION;
  if(!division)s.field=0;
  LinearLayout group=row();group.setGravity(Gravity.TOP);LinearLayout left=column(),right=column();
  LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.rightMargin=dp(7);group.addView(left,lp);
  group.addView(right,new LinearLayout.LayoutParams(0,-2,1.15f));
  InstrumentView instrument=null;
  switch(q.kind){
   case RULER:case BALANCE:case SCALE:case CUP:case THERMO:case CUPS:case CLINICAL:
    instrument=new InstrumentView(this,q);add(left,instrument,Math.max(92,Math.min(140,heightDp()/3)),3);break;
   case DIVISION:{
    TextView board=center(q.a+"   │  "+q.b+"\n       ───",30,DARK,true);board.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);board.setBackground(background(WHITE,PALE,12,0xffffccdf));add(left,board,-2,4);break;}
   case MULTIPLY:{
    TextView board=center(String.format(java.util.Locale.ROOT,"%4d\n×%3d\n────",q.a,q.b),28,DARK,true);board.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);board.setBackground(background(WHITE,PALE,12,0xffffccdf));add(left,board,-2,4);break;}
   default:add(left,center("Thu thập thẻ số",19,PINK,true),-2,4);
  }
  TextView[] fields=new TextView[division?2:1];LinearLayout answers=row();
  for(int i=0;i<fields.length;i++){final int idx=i;LinearLayout cell=column();String label=division?(i==0?"Thương":"Số dư"):"Đáp án";
   add(cell,center(label,17,MUTED,true),-2,0);fields[i]=center(s.draft[i].isEmpty()?"?":s.draft[i],33,PINK,true);fields[i].setBackground(background(WHITE,PALE,12,s.field==i?PINK:0xffe3c9d9));fields[i].setContentDescription(division?(i==0?"Ô thương":"Ô số dư"):"Ô đáp án");fields[i].setClickable(true);fields[i].setOnClickListener(v->{s.field=idx;save(false);for(int j=0;j<fields.length;j++)fields[j].setBackground(background(WHITE,PALE,12,s.field==j?PINK:0xffe3c9d9));});add(cell,fields[i],51,2);weight(answers,cell,-2,1,2);
  }add(left,answers,-2,4);
  Runnable update=()->{for(int i=0;i<fields.length;i++){fields[i].setText(s.draft[i].isEmpty()?"?":s.draft[i]);fields[i].setBackground(background(WHITE,PALE,12,s.field==i?PINK:0xffe3c9d9));}digitMotion();};
  if(instrument!=null&&instrument.canMeasure()){
   final InstrumentView gauge=instrument;add(left,center("Chạm vào vạch để đọc số đo",14,MUTED,false),-2,2);
   gauge.setOnTouchListener((v,event)->{if(event.getAction()==MotionEvent.ACTION_DOWN)return true;if(event.getAction()==MotionEvent.ACTION_UP){Integer val=gauge.valueAt(event.getX(),event.getY());if(val!=null&&val>=0){s.draft[0]=Integer.toString(val);gauge.performClick();update.run();}return true;}return true;});
   gauge.setClickable(true);gauge.setContentDescription("Dụng cụ đo tương tác");gauge.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
  }
  for(int r=0;r<2;r++){LinearLayout keys=row();for(int j=0;j<5;j++){final String digit=String.valueOf(r*5+j+1==10?0:r*5+j+1);Button key=stone(digit,false,26,()->{int field=division?s.field:0;String old=s.draft[field];if(old.length()<4)s.draft[field]=old.equals("0")?digit:old+digit;update.run();});key.setContentDescription("Chữ số "+digit);weight(keys,key,48,1,2);}add(right,keys,-2,4);}
  LinearLayout buttons=row();Button del=pale("⌫",22,()->{int field=division?s.field:0;String v=s.draft[field];if(!v.isEmpty())s.draft[field]=v.substring(0,v.length()-1);update.run();});del.setContentDescription("Xóa chữ số");weight(buttons,del,48,1,2);
  if(division)weight(buttons,pale("Đổi ô",17,()->{s.field=1-s.field;update.run();}),48,1.1f,2);
  Button check=pink("Kiểm tra",20,()->{if(!s.draft[0].isEmpty()&&(!division||!s.draft[1].isEmpty()))submit(q.check(s.input()));else Toast.makeText(this,division?"Điền thương và số dư (0 nếu chia hết).":"Chọn thẻ số trước nhé!",Toast.LENGTH_SHORT).show();});check.setContentDescription("Kiểm tra đáp án");weight(buttons,check,48,1.6f,2);add(right,buttons,-2,5);add(l,group,-2,4);
 }
 private void submit(boolean ok){JourneyState s=state();if(s.finished()||s.solved)return;
  if(ok){animationFrom=s.player;s.correct();s.player=1;}else{animationFrom=s.player;s.fail();}playSound(ok);save(true);render();
 }
 private void showLessonComplete(){
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
  add(page,text("Toán lớp 3 · 0.4.0\nSGK Toán 3 tập một, tr.72–121; VBT tập một, tr.64–115. Ngân hàng chuyển thể có chỉ mục trang/tiết. 2 câu nâng cao tự soạn mỗi bài. Các nhiệm vụ thực hành thật do người học xác nhận; câu đo trên hình được chấm tự động. Android native, chạy offline.",16,MUTED,false),-2,9);
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
