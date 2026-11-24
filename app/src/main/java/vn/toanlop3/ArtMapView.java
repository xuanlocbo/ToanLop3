package vn.toanlop3;

import android.content.Context;
import android.graphics.*;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.*;
import java.util.ArrayList;

/** Native live map on the original full-resolution approved illustrated skin. */
final class ArtMapView extends ViewGroup {
 interface Listener {void select(int n);void open(boolean advanced);void navigate(String screen);void sound();}
 private static final int W=1648,H=928;
 private static final int[][][] NODES={
  {{566,213,63,50},{717,213,64,50},{869,213,64,50},{1020,213,64,50}},
  {{516,326,66,53},{685,326,67,53},{852,326,66,53},{1018,326,67,53}},
  {{452,436,68,55},{628,436,69,55},{806,436,69,55},{984,436,68,55}},
  {{501,552,75,57},{674,552,75,57},{848,552,75,57},{1025,552,76,57}},
  {{516,698,80,62},{698,698,81,62},{875,698,81,62},{1045,703,91,71}}};
 private static Bitmap background;
 private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
 private final TextPaint tp=new TextPaint(Paint.ANTI_ALIAS_FLAG);
 private final ArrayList<RectF> slots=new ArrayList<>();
 private final JourneyState[] core,bonus;
 private final int lesson;private final boolean sound;private final Listener listener;
 private final int starCount,completed;
 private float scale=1,ox,oy;
 ArtMapView(Context c,JourneyState[] core,JourneyState[] bonus,int lesson,boolean sound,Listener listener){
  super(c);this.core=core;this.bonus=bonus;this.lesson=lesson;this.sound=sound;this.listener=listener;
  setWillNotDraw(false);setBackgroundColor(0xff36baf3);
  if(background==null)background=BitmapFactory.decodeResource(getResources(),R.drawable.map_skin);
  int st=0,done=0;for(int i=0;i<20;i++){st+=core[i].completed()+bonus[i].completed();if(core[i].completed()==core[i].count())done++;}starCount=st;completed=done;
  hit(new RectF(9,5,444,132),"Toán lớp 3",null);hit(new RectF(481,8,811,102),"Mộc Nhiên",null);
  hit(new RectF(8,203,270,300),"Hành trình",()->listener.navigate("map"));
  hit(new RectF(8,305,270,390),"Ôn tập",()->listener.navigate("review"));
  hit(new RectF(8,399,270,488),"Thành tích",()->listener.navigate("awards"));
  hit(new RectF(8,494,270,583),"Cài đặt",()->listener.navigate("settings"));
  hit(new RectF(1510,12,1627,104),sound?"Tắt âm thanh":"Bật âm thanh",listener::sound);
  for(int r=0;r<5;r++)for(int k=0;k<4;k++){
   int n=number(r,k);int[] z=NODES[r][k];JourneyState s=core[n-25];
   hit(new RectF(z[0]-z[2],z[1]-z[3],z[0]+z[2],z[1]+z[3]),"Bài "+n+", "+s.completed()+" trên "+s.count()+" nhiệm vụ",()->listener.select(n));
  }
  hit(new RectF(1225,438,1630,518),"Học và chơi bài "+lesson,()->listener.open(false));
  if(core[lesson-25].completed()==core[lesson-25].count())hit(new RectF(1250,639,1610,706),"Thử thách nâng cao",()->listener.open(true));
 }
 private int number(int r,int col){return r%2==0?44-r*4-col:41-r*4+col;}
 private void hit(RectF r,String name,Runnable run){
  View v=new View(getContext());v.setContentDescription(name);v.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);v.setFocusable(true);if(run!=null){v.setClickable(true);v.setOnClickListener(x->run.run());}slots.add(r);addView(v);
 }
 @Override protected void onMeasure(int ws,int hs){int w=MeasureSpec.getSize(ws),h=MeasureSpec.getSize(hs);setMeasuredDimension(w,h);scale=Math.min(w/(float)W,h/(float)H);ox=(w-W*scale)/2;oy=(h-H*scale)/2;for(int i=0;i<slots.size();i++){RectF r=slots.get(i);getChildAt(i).measure(MeasureSpec.makeMeasureSpec(Math.max(1,Math.round(r.width()*scale)),MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(Math.max(1,Math.round(r.height()*scale)),MeasureSpec.EXACTLY));}}
 @Override protected void onLayout(boolean changed,int l,int t,int r,int b){for(int i=0;i<slots.size();i++){RectF z=slots.get(i);View v=getChildAt(i);int x=Math.round(ox+z.left*scale),y=Math.round(oy+z.top*scale);v.layout(x,y,x+v.getMeasuredWidth(),y+v.getMeasuredHeight());}}
 private void fill(int col){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(col);p.setAlpha(255);}
 private void gradient(Canvas c,RectF r,float radius,int top,int bottom,int outline,float sw){p.setAlpha(255);p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(r.left,r.top,r.left,r.bottom,top,bottom,Shader.TileMode.CLAMP));c.drawRoundRect(r,radius,radius,p);p.setShader(null);if(outline!=0){p.setStyle(Paint.Style.STROKE);p.setColor(outline);p.setStrokeWidth(sw);c.drawRoundRect(r,radius,radius,p);p.setStyle(Paint.Style.FILL);}}
 private void label(Canvas c,String s,float x,float y,float sz,int color,boolean outline){p.setShader(null);p.setAlpha(255);p.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));p.setTextAlign(Paint.Align.CENTER);p.setTextSize(sz);if(outline){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(sz*.105f);p.setColor(0xff693068);p.setStrokeJoin(Paint.Join.ROUND);c.drawText(s,x,y,p);}p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawText(s,x,y,p);}
 private void paragraph(Canvas c,String s,RectF r,float size,int col){tp.setColor(col);tp.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));float ss=size;StaticLayout lay;do{tp.setTextSize(ss);lay=StaticLayout.Builder.obtain(s,0,s.length(),tp,(int)r.width()).setAlignment(Layout.Alignment.ALIGN_CENTER).setIncludePad(false).setLineSpacing(0,1f).build();ss-=1;}while(lay.getHeight()>r.height()&&ss>17);c.save();c.translate(r.left,r.top+(r.height()-lay.getHeight())/2);lay.draw(c);c.restore();}
 private void star(Canvas c,float x,float y,float size,boolean earned){Path path=new Path();for(int i=0;i<10;i++){double a=i*Math.PI/5-Math.PI/2;float rr=i%2==0?size:size*.47f;float xx=x+(float)Math.cos(a)*rr,yy=y+(float)Math.sin(a)*rr;if(i==0)path.moveTo(xx,yy);else path.lineTo(xx,yy);}path.close();p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(x,y-size,x,y+size,earned?0xfffff6a1:0xfff6eafa,earned?0xffffa400:0xffa7a0be,Shader.TileMode.CLAMP));c.drawPath(path,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeJoin(Paint.Join.ROUND);p.setStrokeWidth(3);p.setColor(earned?0xffb36810:0xff7b6e92);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);}
 private void node(Canvas c,int n,int[] z){
  int x=z[0],y=z[1],rx=z[2],ry=z[3];JourneyState s=core[n-25];int q=s.completed(),score=q==0?0:q==s.count()?3:q*2>=s.count()?2:1;boolean sel=n==lesson;
  c.save();c.translate(x,y);c.scale(rx/72f,ry/56f);
  fill(0x66000000);p.setAlpha(85);c.drawOval(-72,-24,72,56,p);p.setAlpha(255);
  int top=sel?0xffffc8e4:score==3?0xffc7fc93:score>0?0xffdcf8ac:0xfff9faff;
  int bottom=sel?0xfff363a6:score==3?0xff52c437:score>0?0xff9cda44:0xffbcc7e0;
  int base=sel?0xffa8226e:score>0?0xffa96825:0xff657088;
  gradient(c,new RectF(-72,-12,72,53),28,base,0xff766246,0xfff9f2e5,3.4f);
  p.setShader(new LinearGradient(0,-52,0,33,top,bottom,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);c.drawOval(-73,-53,73,36,p);p.setShader(null);
  p.setColor(sel?0xffffef8e:0xfffcffff);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(sel?5.2f:3.3f);c.drawOval(-70,-51,70,34,p);p.setStyle(Paint.Style.FILL);
  fill(0xffFFFFFF);p.setAlpha(106);c.drawOval(-55,-43,54,-18,p);p.setAlpha(255);
  label(c,"Bài "+n,0,-11,27,0xff302450,false);
  for(int i=0;i<3;i++)star(c,-30+i*30,26,14,i<score);
  if(sel){fill(0xffd41b70);c.drawRoundRect(53,-59,58,-15,2,2,p);Path f=new Path();f.moveTo(58,-59);f.quadTo(82,-50,69,-33);f.lineTo(58,-40);f.close();c.drawPath(f,p);}
  c.restore();
 }
 @Override protected void onDraw(Canvas c){super.onDraw(c);c.save();c.translate(ox,oy);c.scale(scale,scale);p.setAlpha(255);p.setShader(null);p.setStyle(Paint.Style.FILL);if(background!=null)c.drawBitmap(background,null,new RectF(0,0,W,H),p);
  gradient(c,new RectF(869,16,1128,88),35,0xffa044a4,0xff662281,0xfffef1ff,5);star(c,910,52,28,true);label(c,Integer.toString(starCount),1024,67,38,0xfffff8b2,true);
  gradient(c,new RectF(1515,10,1618,105),45,0xffed86d8,0xff9b37b6,0xfffaf4ff,5);label(c,sound?"♫":"×",1567,76,52,Color.WHITE,true);
  for(int r=0;r<5;r++)for(int k=0;k<4;k++)node(c,number(r,k),NODES[r][k]);
  JourneyState s=core[lesson-25];label(c,"Bài "+lesson,1430,185,48,Color.WHITE,true);paragraph(c,Curriculum.title(lesson),new RectF(1244,224,1618,305),33,0xff242567);
  label(c,s.finished()?"Chơi lại":s.cursor>0||s.solved?"Học tiếp":"Học & chơi",1452,491,37,Color.WHITE,true);
  paragraph(c,s.completed()+" / "+s.count()+" nhiệm vụ",new RectF(1260,539,1604,586),31,0xff29265e);
  gradient(c,new RectF(1263,594,1605,617),12,0xffc6c3d3,0xffa9a3b8,0xffa299ba,2);if(s.completed()>0)gradient(c,new RectF(1264,595,1264+340*s.completed()/(float)s.count(),616),11,0xffa6ef5f,0xff4aac29,0xff70b82b,2);
  String detail=s.finished()?"Thử thách nâng cao  "+bonus[lesson-25].completed()+"/2":("Tiết "+s.current().period+" · Bài tập "+s.current().exercise);
  if(s.completed()==s.count())gradient(c,new RectF(1252,636,1612,706),25,0xffffe77d,0xfff2bd3c,0xffd68e27,3);
  paragraph(c,detail,new RectF(1260,643,1608,701),27,0xff392760);
  paragraph(c,"Tiến độ học tập: "+completed+" / 20 bài",new RectF(529,829,1088,864),27,0xff2c2963);
  gradient(c,new RectF(529,871,985,891),10,0xffc3b9ca,0xffa29ab0,0xff978ba2,2);if(completed>0)gradient(c,new RectF(530,872,530+454*completed/20f,890),9,0xffb2ee69,0xff4dbb30,0xff62b332,2);label(c,"Đã lưu",1041,890,20,0xff318044,false);
  c.restore();}
}
