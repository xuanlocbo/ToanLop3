package vn.toanlop3;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import java.util.*;

/** A single playable scene: number blocks, answer platforms, measuring tools and the hero share
 * the same world. Native child controls remain accessible and usable on a small landscape display.
 * Correct answers bridge the obstacle; the character physically jumps and runs between targets. */
final class MathAdventureView extends ViewGroup {
 interface Host {void save(boolean sync);void next();void back();void guide();void sound(int effect);void practice(Runnable confirm);}
 private static final float W=1440,H=810;
 private static final int PINK=0xffdf3585,INK=0xff392953,WHITE=0xfffffff8,MUTED=0xff754b77,CREAM=0xffffedc8;
 private static final Bitmap[] worlds=new Bitmap[4];private static Bitmap pig,chest;
 private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
 private final JourneyState s;private final BookBank.Q q;private final Host host;
 private final int lesson,theme;private final boolean advanced,reviewing;
 private final ArrayList<RectF> layout=new ArrayList<>();private final ArrayList<Float> fontSizes=new ArrayList<>();
 private final ArrayList<Button> options=new ArrayList<>();private final TextView[] fields=new TextView[2];
 private final ArrayList<RectF> platforms=new ArrayList<>();
 private TextView feedback;private InstrumentView instrument;private AppleLabView grouping;
 private ValueAnimator collectMotion;private float gemX,gemY,gemT;private String gem="";
 private float scale=1,ox,oy,heroX,heroY,hop,bridge,celebration;private boolean faceRight=true;
 private ValueAnimator motion;private int lastTap=-1;
 private float boardBottom=245;
 MathAdventureView(Context c,JourneyState state,int lesson,boolean advanced,boolean reviewing,Host host){
  super(c);this.s=state;this.q=state.current();this.lesson=lesson;this.advanced=advanced;this.reviewing=reviewing;this.host=host;theme=Curriculum.world(lesson);
  if(pig==null)pig=BitmapFactory.decodeResource(getResources(),R.drawable.mascot);if(chest==null)chest=BitmapFactory.decodeResource(getResources(),R.drawable.treasure);
  int[] ids={R.drawable.world_0,R.drawable.world_1,R.drawable.world_2,R.drawable.world_3};if(worlds[theme]==null)worlds[theme]=BitmapFactory.decodeResource(getResources(),ids[theme]);
  setWillNotDraw(false);setBackgroundColor(0xff65c8ef);setClickable(true);
  heroX=s.solved?1240:110+Math.min(.85f,s.player)*1000;heroY=770;bridge=s.solved?1:0;build();
 }
 private void place(View v,RectF rect,float textSize){layout.add(rect);fontSizes.add(textSize);addView(v);}
 private RectF r(float x,float y,float w,float h){return new RectF(x,y,x+w,y+h);}
 private GradientDrawable bg(int top,int bottom,int stroke,float rad){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{top,bottom});d.setCornerRadius(rad);if(stroke!=0)d.setStroke(Math.max(1,Math.round(3*scale)),stroke);return d;}
 private TextView text(String value,int color,boolean bold){TextView t=new TextView(getContext());t.setText(value);t.setGravity(Gravity.CENTER);t.setTextColor(color);t.setTypeface(Typeface.create("sans-serif-medium",bold?Typeface.BOLD:Typeface.NORMAL));t.setIncludeFontPadding(false);t.setPadding(0,0,0,0);return t;}
 private Button button(String value,String desc,boolean primary,Runnable action){Button b=new Button(getContext());b.setText(value);b.setAllCaps(false);b.setTextColor(primary?Color.WHITE:INK);b.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));b.setGravity(Gravity.CENTER);b.setMinWidth(0);b.setMinHeight(0);b.setMinimumWidth(0);b.setMinimumHeight(0);b.setPadding(4,0,4,0);b.setIncludeFontPadding(false);b.setSoundEffectsEnabled(false);b.setContentDescription(desc);b.setOnClickListener(v->action.run());b.setTag(primary?"pink":"gold");return b;}
 private void style(Button b){String tag=String.valueOf(b.getTag());boolean pink=tag.equals("pink"),selected=tag.equals("selected"),blue=tag.equals("blue");int a=pink?0xffff80ba:selected?0xffffec91:blue?0xffdbf5ff:0xfffff6dc;int z=pink?PINK:selected?0xffe9a429:blue?0xff8acff4:0xffe6b66e;int outline=pink?0xffffd8ed:selected?0xffa87515:blue?0xff5c9dc6:0xffb7874f;b.setTextColor(pink?Color.WHITE:INK);b.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44ffffff),bg(a,z,outline,Math.max(6,20*scale)),null));b.setElevation(Math.max(1,4*scale));}
 private void label(String value,RectF box,float size,int color,boolean bold){place(text(value,color,bold),box,size);}
 private void action(String value,String desc,RectF box,float size,boolean primary,Runnable run){Button b=button(value,desc,primary,run);place(b,box,size);}
 private void header(){
  action("‹","Về hành trình",r(14,9,105,83),55,true,host::back);
  String title=(reviewing?"Ôn tập · ":advanced?"Nâng cao · ":"")+"Bài "+lesson+" · "+Curriculum.title(lesson);
  TextView t=text(title,INK,true);t.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);place(t,r(139,7,1005,47),title.length()>60?27:32);
  String detail=advanced?"Thử thách "+(s.cursor+1)+"/2":q.source();TextView meta=text(detail,MUTED,false);meta.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);place(meta,r(140,54,995,32),21);
  label("★ "+s.completed()+"/"+s.count(),r(1191,12,233,64),33,0xff95485e,true);
 }
 private void build(){
  removeAllViews();layout.clear();fontSizes.clear();options.clear();platforms.clear();instrument=null;grouping=null;feedback=null;Arrays.fill(fields,null);header();
  if(s.solved){solved();requestLayout();invalidate();return;}
  int len=q.prompt.length();boardBottom=len>145?290:len>82?265:225;
  float font=len>145?31:len>95?34:len>50?39:len>20?46:60;
  TextView prompt=text(q.prompt,INK,true);prompt.setMaxLines(4);place(prompt,r(55,110,1193,boardBottom-115),font);
  action("?","Gợi ý",r(1280,113,118,103),52,false,()->{s.hint=true;host.save(true);refreshFeedback();});
  if(q.kind==BookBank.Kind.PRACTICE){practice();}
  else if(q.kind==BookBank.Kind.CHOICE||q.kind==BookBank.Kind.GEOMETRY||q.kind==BookBank.Kind.MULTI){choices();}
  else numbers();
  feedback=text("",0xff862d68,true);feedback.setMaxLines(2);place(feedback,r(275,716,720,78),25);refreshFeedback();
  requestLayout();invalidate();
 }
 private void solved(){
  boardBottom=275;
  label(q.kind==BookBank.Kind.PRACTICE?"Đã ghi nhận thực hành!":"Chính xác!",r(78,112,1284,75),52,0xff287d45,true);
  TextView sol=text(q.explain,INK,true);sol.setMaxLines(4);place(sol,r(100,184,1240,95),q.explain.length()>95?29:38);
  label("Mộc Nhiên đã vượt qua thử thách!",r(218,305,1000,64),37,0xff663466,true);
  action(reviewing?"Hoàn tất ôn tập":"Tiếp tục hành trình  ▶","Tiếp tục nhiệm vụ",r(965,673,433,105),34,true,host::next);
 }
 private void practice(){
  TextView note=text("Cùng làm với vở bài tập hoặc dụng cụ thật. Không có đáp án tự động cho phần thực hành này.",INK,true);place(note,r(310,boardBottom+22,1010,190),36);
  action("Đã thực hành","Xác nhận thực hành",r(995,boardBottom+230,370,112),36,true,()->host.practice(()->submit(true,-1)));
  action("Cách làm","Hướng dẫn thực hành",r(350,boardBottom+230,320,112),34,false,()->{s.hint=true;host.save(true);refreshFeedback();});
 }
 private boolean gauge(){switch(q.kind){case RULER:case BALANCE:case SCALE:case CUP:case THERMO:case CUPS:case CLINICAL:return true;default:return false;}}
 private boolean multi(){return q.kind==BookBank.Kind.MULTI;}
 private void choices(){
  boolean apples=q.kind==BookBank.Kind.MULTI&&q.lesson==25&&q.page==64&&q.exercise==3;
  boolean diagram=q.kind==BookBank.Kind.GEOMETRY||apples;
  float left=apples?905:diagram?510:140,available=1380-left;
  if(q.kind==BookBank.Kind.GEOMETRY){instrument=new InstrumentView(getContext(),q);place(instrument,r(34,boardBottom+22,455,282),0);}
  if(apples){grouping=new AppleLabView(getContext(),s,()->host.save(true));place(grouping,r(20,boardBottom+12,855,380),0);}
  int n=q.options.length;boolean longText=Arrays.stream(q.options).anyMatch(x->x.length()>30);
  int cols=apples?1:longText?(diagram?1:2):Math.min(n,diagram?2:3);if(n==1)cols=1;
  int rows=(n+cols-1)/cols;
  float start=boardBottom+40,space=multi()?612-start:661-start;
  float gap=12,h=Math.min(143,(space-(rows-1)*gap)/Math.max(1,rows));h=Math.max(84,h);
  float w=(available-(cols-1)*14)/cols;
  for(int i=0;i<n;i++){final int idx=i;float x=left+(i%cols)*(w+14),y=start+(i/cols)*(h+gap);RectF rect=r(x,y,w,h);String opt=q.options[i];
   Button b=button(opt,(multi()?"Chọn ":"Đáp án ")+opt,false,()->{float target=rect.centerX();if(multi()){if(!s.selected.add(idx))s.selected.remove(idx);refreshOptions();if(grouping!=null)grouping.plates(idx+2);move(target,false,true);host.sound(0);host.save(true);}else{submit(q.check(opt),idx);}});b.setTag(s.selected.contains(i)?"selected":"gold");place(b,rect,opt.length()>48?27:opt.length()>23?32:40);options.add(b);platforms.add(rect);
  }
  if(multi()){label("Chọn tất cả đáp án đúng",r(left,boardBottom+4,available,36),24,0xff55365d,true);action("Kiểm tra","Kiểm tra đáp án",r(1006,634,375,76),32,true,()->{if(!s.selected.isEmpty())submit(q.check(s.input()),-1);else message("Chọn ít nhất một đáp án nhé!");});}
 }
 private void refreshOptions(){for(int i=0;i<options.size();i++){Button b=options.get(i);b.setTag(s.selected.contains(i)?"selected":"gold");b.setText((s.selected.contains(i)?"✓ ":"")+q.options[i]);style(b);}}
 private void numbers(){
  boolean div=q.kind==BookBank.Kind.DIVISION;
  boolean measure=gauge();if(!div)s.field=0;
  float top=boardBottom+16;
  // Left area: actual math tool / written calculation and the answer slot(s).
  if(measure){instrument=new InstrumentView(getContext(),q);place(instrument,r(20,top,515,220),0);if(instrument.canMeasure()){
    instrument.setClickable(true);instrument.setContentDescription("Dụng cụ đo tương tác");instrument.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    instrument.setOnTouchListener((v,e)->{if(e.getAction()==MotionEvent.ACTION_DOWN)return true;if(e.getAction()==MotionEvent.ACTION_UP){Integer value=instrument.valueAt(e.getX(),e.getY());if(value!=null&&value>=0){s.draft[0]=Integer.toString(value);instrument.performClick();refreshFields();move(300,false,true);host.sound(0);host.save(true);}return true;}return true;});
   }
  }else if(div){
   // Long-division model kept visible, instead of a generic equation alone.
   TextView board=text(q.a+"  │  "+q.b+"\n       ───",INK,true);board.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);place(board,r(44,top+1,490,117),50);
  }else if(q.kind==BookBank.Kind.MULTIPLY){TextView board=text(String.format(Locale.ROOT,"%4d\n×%3d\n────",q.a,q.b),INK,true);board.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);place(board,r(52,top+2,485,160),42);}
  else label("Thu thập khối số",r(45,top+8,490,73),35,0xff974169,true);
  float fy=measure?top+232:div?top+132:q.kind==BookBank.Kind.MULTIPLY?top+169:top+100;
  int count=div?2:1;float sw=div?235:480;
  for(int i=0;i<count;i++){final int idx=i;float x=46+i*250;label(div?(i==0?"Thương":"Số dư"):"Đáp án",r(x,fy-7,sw,37),28,MUTED,true);
   TextView f=text(s.draft[i].isEmpty()?"?":s.draft[i],PINK,true);f.setContentDescription(div?(i==0?"Ô thương":"Ô số dư"):"Ô đáp án");f.setClickable(true);f.setFocusable(true);f.setOnClickListener(v->{s.field=idx;refreshFields();host.save(true);});fields[i]=f;place(f,r(x,fy+36,sw,94),55);
  }
  float keyTop=boardBottom+43,keyW=153,keyH=103,gap=14;
  // Touchable collectible blocks. Their positions are game-world coordinates, not an Android keyboard.
  label(div?"Chạm khối số để điền thương và số dư":"Chạm khối số để điền đáp án",r(548,boardBottom+4,850,38),25,0xff5b365c,true);
  for(int i=0;i<10;i++){String digit=Integer.toString((i+1)%10);RectF rect=r(550+(i%5)*(keyW+gap),keyTop+(i/5)*(keyH+17),keyW,keyH);
   Button key=button(digit,"Chữ số "+digit,false,()->{int f=div?s.field:0;String old=s.draft[f];if(old.length()<4)s.draft[f]=old.equals("0")?digit:old+digit;refreshFields();collect(rect.centerX(),rect.centerY(),digit,div&&s.field==1?413:168,fy+82);move(rect.centerX(),false,true);host.sound(0);host.save(true);});place(key,rect,54);platforms.add(rect);
  }
  float actions=keyTop+2*(keyH+17)+6;
  action("⌫","Xóa chữ số",r(550,actions,180,93),39,false,()->{int f=div?s.field:0;String d=s.draft[f];if(!d.isEmpty())s.draft[f]=d.substring(0,d.length()-1);refreshFields();host.sound(0);host.save(true);});
  if(div)action("Đổi ô","Đổi ô",r(745,actions,202,93),30,false,()->{s.field=1-s.field;refreshFields();host.save(true);});
  action("Kiểm tra","Kiểm tra đáp án",r(div?963:750,actions,div?421:634,93),37,true,()->{if(s.draft[0].isEmpty()||(div&&s.draft[1].isEmpty()))message(div?"Điền thương và số dư, kể cả số dư 0.":"Chọn thẻ số trước nhé!");else submit(q.check(s.input()),-1);});
  if(!advanced&&!reviewing&&s.cursor==0)action("Cách làm","Cách làm · SGK tr. "+LessonGuide.pages(lesson),r(32,686,224,93),28,false,host::guide);
  refreshFields();
 }
 private void collect(float x,float y,String value,float tx,float ty){
  if(collectMotion!=null)collectMotion.cancel();gem=value;gemT=0;collectMotion=ValueAnimator.ofFloat(0,1);collectMotion.setDuration(430);collectMotion.addUpdateListener(a->{float t=(float)a.getAnimatedValue();gemT=t;gemX=x+(tx-x)*t;gemY=y+(ty-y)*t-(float)Math.sin(Math.PI*t)*115;invalidate();});collectMotion.start();
 }
 @Override protected void dispatchDraw(Canvas c){super.dispatchDraw(c);if(!gem.isEmpty()&&gemT<1){c.save();c.translate(ox,oy);c.scale(scale,scale);float rr=37*(1-gemT*.25f);gradient(c,r(gemX-rr,gemY-rr,2*rr,2*rr),18,0xfffff7a4,0xffffb23f,0xffb7792d,4);p.setStyle(Paint.Style.FILL);p.setColor(INK);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));p.setTextSize(47);p.setTextAlign(Paint.Align.CENTER);c.drawText(gem,gemX,gemY+15,p);c.restore();}}
 private void refreshFields(){for(int i=0;i<fields.length;i++)if(fields[i]!=null){fields[i].setText(s.draft[i].isEmpty()?"?":s.draft[i]);fields[i].setBackground(bg(0xfffffdf7,0xffffe6f4,s.field==i?PINK:0xffc495b5,Math.max(6,22*scale)));}}
 private void refreshFeedback(){if(feedback==null)return;String f=s.hint?q.hint:s.wrong>0?"Thử lại nhé! Không mất sao.":"";feedback.setText(f);feedback.setBackground(f.isEmpty()?null:bg(0xfffff9dc,0xffffe6a2,0xffedb86c,Math.max(6,16*scale)));}
 private void message(String msg){if(feedback!=null){feedback.setText(msg);feedback.setBackground(bg(0xfffff8da,0xffffe2ad,0xffe5ae65,Math.max(6,16*scale)));}}
 private void submit(boolean ok,int chosen){
  if(s.solved)return;lastTap=chosen;
  if(ok){s.correct();s.player=1;host.save(true);host.sound(1);float before=heroX;build();heroX=before;bridge=0;move(1240,true,true);}
  else{s.fail();host.save(true);host.sound(2);if(chosen>=0&&chosen<platforms.size()){float target=platforms.get(chosen).centerX();move(target,false,true);}message("Thử lại nhé! "+q.hint);invalidate();}
 }
 /** Horizontal traversal and a parabolic jump, rendered frame by frame. The unsolved gate
  * prevents moving to the chest, independent of math input, and is saved in the checkpoint. */
 private void move(float target,boolean success,boolean jump){
  if(motion!=null)motion.cancel();float from=heroX;target=Math.max(110,Math.min(success?1265:1115,target));final float dest=target;faceRight=dest>=from;
  if(!success){s.player=Math.max(0,Math.min(.85f,(dest-110)/1000));host.save(false);}
  motion=ValueAnimator.ofFloat(0,1);motion.setDuration(success?1050:Math.max(230,Math.min(550,(long)(Math.abs(dest-from)*.57f))));motion.setInterpolator(new DecelerateInterpolator(1.1f));
  motion.addUpdateListener(a->{float t=(float)a.getAnimatedValue();heroX=from+(dest-from)*t;hop=jump?(float)Math.sin(Math.PI*t)*(success?150:75):0;if(success){bridge=Math.min(1,t*1.8f);celebration=Math.max(0,1-t*.7f);}invalidate();});motion.start();
 }
 @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN)return true;if(e.getAction()==MotionEvent.ACTION_UP){float x=(e.getX()-ox)/scale,y=(e.getY()-oy)/scale;if(y>boardBottom&&!s.solved){move(x,false,true);host.sound(3);}performClick();return true;}return true;}
 @Override public boolean performClick(){super.performClick();return true;}
 @Override protected void onMeasure(int ws,int hs){int w=MeasureSpec.getSize(ws),h=MeasureSpec.getSize(hs);setMeasuredDimension(w,h);scale=Math.min(w/W,h/H);ox=(w-W*scale)/2;oy=(h-H*scale)/2;
  for(int i=0;i<layout.size();i++){View child=getChildAt(i);RectF rect=layout.get(i);float font=fontSizes.get(i);if(child instanceof TextView&&font>0)((TextView)child).setTextSize(TypedValue.COMPLEX_UNIT_PX,Math.max(10,font*scale));if(child instanceof Button)style((Button)child);child.measure(MeasureSpec.makeMeasureSpec(Math.max(1,Math.round(rect.width()*scale)),MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(Math.max(1,Math.round(rect.height()*scale)),MeasureSpec.EXACTLY));}refreshFields();}
 @Override protected void onLayout(boolean changed,int l,int t,int r,int b){for(int i=0;i<layout.size();i++){RectF z=layout.get(i);View v=getChildAt(i);int x=Math.round(ox+z.left*scale),y=Math.round(oy+z.top*scale);v.layout(x,y,x+v.getMeasuredWidth(),y+v.getMeasuredHeight());}}
 private void color(int col){p.setShader(null);p.setColor(col);p.setStyle(Paint.Style.FILL);}
 private void gradient(Canvas c,RectF r,float rad,int a,int b,int outline,float stroke){p.setStyle(Paint.Style.FILL);p.setAlpha(255);p.setShader(new LinearGradient(r.left,r.top,r.left,r.bottom,a,b,Shader.TileMode.CLAMP));c.drawRoundRect(r,rad,rad,p);p.setShader(null);if(outline!=0){p.setStyle(Paint.Style.STROKE);p.setColor(outline);p.setStrokeWidth(stroke);c.drawRoundRect(r,rad,rad,p);p.setStyle(Paint.Style.FILL);}}
 private void art(Canvas c,Bitmap b,RectF r){if(b==null)return;p.setShader(null);p.setStyle(Paint.Style.FILL);p.setAlpha(255);p.setColor(Color.WHITE);c.drawBitmap(b,null,r,p);}
 private void star(Canvas c,float x,float y,float size){Path z=new Path();for(int i=0;i<10;i++){double a=i*Math.PI/5-Math.PI/2;float rr=i%2==0?size:size*.46f;float xx=x+(float)Math.cos(a)*rr,yy=y+(float)Math.sin(a)*rr;if(i==0)z.moveTo(xx,yy);else z.lineTo(xx,yy);}z.close();p.setAlpha(255);p.setShader(new LinearGradient(x,y-size,x,y+size,0xfffff7b0,0xffffaf18,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);c.drawPath(z,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(0xffb57716);c.drawPath(z,p);p.setStyle(Paint.Style.FILL);}
 private void worldPath(Canvas c){
  // Draw the traversable foreground and a visible unfinished gap. Each math answer changes
  // the actual route, not a decorative progress meter.
  if(theme==0){
   // Existing original river scene has wooden bridge halves around the central gap.
   int pieces=Math.round(7*bridge);for(int i=0;i<pieces;i++){float x=628+i*36;gradient(c,r(x,662,40,32),5,0xffffcf7b,0xffa65c32,0xff804d2b,2);}
  }else if(theme==2){
   for(int i=0;i<5;i++){float x=205+i*245;boolean lit=s.solved||i<2;gradient(c,r(x,674,160,48),18,lit?0xffc7ef94:0xffded2eb,lit?0xff74ad55:0xffa99abb,0xff557c83,3);}
  }else{
   for(int i=0;i<9;i++){float x=55+i*155;gradient(c,r(x,753,132,32),11,0xffffe5a1,0xffd19749,0xffb47e3b,2);}
  }
  if(!s.solved){gradient(c,r(1260,621,91,125),13,0xffd4b28d,0xffa77959,0xff73583c,4);gradient(c,r(1286,666,40,50),7,0xffffdf6a,0xffdba120,0xff9c711e,3);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(8);p.setColor(0xffc18d28);c.drawArc(1290,644,1323,682,180,180,false,p);p.setStyle(Paint.Style.FILL);}
  else {art(c,chest,r(1250,507,150,148));star(c,1320,488,38);}
 }
 @Override protected void onDraw(Canvas c){super.onDraw(c);c.save();c.translate(ox,oy);c.scale(scale,scale);art(c,worlds[theme],r(0,0,W,H));
  // Header and question sign share the same illustrated scene and leave the terrain visible.
  gradient(c,r(7,4,1426,92),34,0xffffe7f2,0xfffff7e9,0xfffaefff,4);gradient(c,r(1190,15,234,64),22,0xffffeb9f,0xffffd476,0xffdb9b41,3);
  float frac=s.completed()/(float)Math.max(1,s.count());gradient(c,r(140,91,1145,9),4,0xffead2e6,0xffd1a0bf,0,0);if(frac>0)gradient(c,r(141,91,1143*frac,9),4,0xfff58dc2,PINK,0,0);
  gradient(c,r(23,103,1395,boardBottom-95),30,0xfffffcf0,0xffffe9f4,0xffe463a3,5);
  if(!s.solved){
   if(gauge()||q.kind==BookBank.Kind.DIVISION||q.kind==BookBank.Kind.MULTIPLY||q.kind==BookBank.Kind.NUMBER){gradient(c,r(24,boardBottom+14,513,Math.min(394,H-boardBottom-150)),26,0xf9fff8e9,0xf2ffe5f1,0xfff3a2c6,4);}
   // platforms receive visual bases and shadows; interactive blocks are native child views.
   for(RectF z:platforms){gradient(c,r(z.left+4,z.bottom-14,z.width()-8,27),10,0xffc48b55,0xff98683b,0xff784d35,2);}
  }
  worldPath(c);
  float h=s.solved?250:205,w=pig==null?140:h*pig.getWidth()/pig.getHeight();float foot=heroY-hop;
  color(0x480b4528);c.drawOval(heroX-w*.46f,heroY-8,heroX+w*.46f,heroY+13,p);
  c.save();if(!faceRight){c.scale(-1,1,heroX,0);}art(c,pig,r(heroX-w/2,foot-h,w,h));c.restore();
  // Correct-answer burst and a visible treasure. Drawing bitmap always resets paint alpha.
  if(s.solved&&celebration>0){for(int i=0;i<8;i++){double a=i*Math.PI/4;float rr=(1-celebration)*160+35;star(c,heroX+(float)Math.cos(a)*rr,foot-138+(float)Math.sin(a)*rr,13+celebration*7);}}
  c.restore();}
 @Override protected void onDetachedFromWindow(){if(motion!=null)motion.cancel();if(collectMotion!=null)collectMotion.cancel();super.onDetachedFromWindow();}
}
