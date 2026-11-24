package vn.toanlop3;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import java.util.Random;
/** Original native vector artwork inspired by the approved colorful landscape mockup. */
final class WorldView extends View {
 final Paint p=new Paint(3); boolean map,open; int world; float pos,hop,phase; ValueAnimator anim;
 WorldView(Context c,boolean map,int world,float pos,boolean open){super(c);this.map=map;this.world=world;this.pos=pos;this.open=open;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 int co(String s){return Color.parseColor(s);}
 void fill(String s){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(co(s));}
 void line(Canvas c,float x,float y,float a,float b,String s,float w){fill(s);p.setStrokeWidth(w);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,a,b,p);}
 void oval(Canvas c,float x,float y,float a,float b,String col){fill(col);c.drawOval(x,y,a,b,p);}
 void circle(Canvas c,float x,float y,float r,String s){fill(s);c.drawCircle(x,y,r,p);}
 void box(Canvas c,float x,float y,float a,float b,float r,String col){fill(col);c.drawRoundRect(x,y,a,b,r,r,p);}
 void grad(Canvas c,RectF r,float radius,String top,String bottom){p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(r.left,r.top,r.left,r.bottom,co(top),co(bottom),Shader.TileMode.CLAMP));c.drawRoundRect(r,radius,radius,p);p.setShader(null);}
 void poly(Canvas c,String col,float... a){Path z=new Path();z.moveTo(a[0],a[1]);for(int i=2;i<a.length;i+=2)z.lineTo(a[i],a[i+1]);z.close();fill(col);c.drawPath(z,p);}
 void outlinedOval(Canvas c,RectF r,String top,String bot,String stroke,float width){
  p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(r.left,r.top,r.right,r.bottom,co(top),co(bot),Shader.TileMode.CLAMP));c.drawOval(r,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(width);p.setColor(co(stroke));c.drawOval(r,p);p.setStyle(Paint.Style.FILL);
 }
 void star(Canvas c,float x,float y,float r){Path z=new Path();for(int i=0;i<10;i++){double a=Math.PI*i/5-Math.PI/2;float rr=(i%2==0?r:r*.46f);float xx=x+(float)Math.cos(a)*rr,yy=y+(float)Math.sin(a)*rr;if(i==0)z.moveTo(xx,yy);else z.lineTo(xx,yy);}z.close();fill("#FFC938");c.drawPath(z,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(co("#D87820"));c.drawPath(z,p);p.setStyle(Paint.Style.FILL);}
 void cloud(Canvas c,float x,float y,float s){oval(c,x-s*1.5f,y-s*.24f,x+s*1.5f,y+s*.55f,"#E0F6FF");circle(c,x-s*.6f,y,s*.6f,"#FFFFFF");circle(c,x,y-s*.3f,s*.79f,"#FFFFFF");circle(c,x+s*.7f,y,s*.6f,"#FFFFFF");}
 void tree(Canvas c,float x,float y,float s,boolean pink){c.save();c.translate(x,y);c.scale(s,s);box(c,-12,-85,11,0,8,"#A5673B");line(c,-4,-42,-28,-75,"#B77E45",9);line(c,5,-43,28,-70,"#B77E45",9);
  String a=pink?"#F398D0":"#58C341",b=pink?"#FFB6DA":"#9BE45B",d=pink?"#D867AA":"#318C36";
  circle(c,-36,-85,35,d);circle(c,34,-86,34,d);circle(c,0,-114,44,a);circle(c,-35,-97,34,a);circle(c,32,-101,32,a);circle(c,0,-122,30,b);circle(c,-35,-106,22,b);circle(c,27,-110,21,b);c.restore();}
 void flower(Canvas c,float x,float y,String col,float s){line(c,x,y,x,y+15*s,"#3BA343",2*s);for(int i=0;i<5;i++){double a=i*Math.PI*2/5;circle(c,x+(float)Math.cos(a)*5*s,y+(float)Math.sin(a)*5*s,3.7f*s,col);}circle(c,x,y,3.4f*s,"#FFDB59");}
 void bush(Canvas c,float x,float y,float s){circle(c,x-s*.55f,y,s*.65f,"#44AE46");circle(c,x,y-s*.45f,s*.85f,"#58CA49");circle(c,x+s*.65f,y,s*.7f,"#70D856");circle(c,x-3,y-s*.7f,s*.36f,"#A9EA76");}
 void castle(Canvas c,float x,float y,float s){c.save();c.translate(x,y);c.scale(s,s);box(c,-73,-101,73,0,6,"#F9CFDA");box(c,-85,-129,-52,0,4,"#F5B2CB");box(c,52,-129,85,0,4,"#F5B2CB");poly(c,"#DA477E",-92,-129,-68,-171,-45,-129);poly(c,"#DA477E",45,-129,68,-171,92,-129);box(c,-33,-149,33,0,6,"#FFE8E9");poly(c,"#E65791",-41,-149,0,-204,41,-149);box(c,-15,-53,15,0,13,"#A86266");for(int k=-1;k<=1;k++){box(c,k*67-8,-99,k*67+8,-75,8,"#F6B64D");box(c,k*67-4,-95,k*67+4,-79,3,"#A06368");}line(c,0,-205,0,-229,"#AD4C79",4);poly(c,"#FF6BAA",0,-228,36,-222,0,-212);c.restore();}
 void pig(Canvas c,float x,float y,float s,float hop){
  c.save();c.translate(x,y-hop);c.scale(s,s);
  oval(c,-57,-7,52,9,"#4A934D");
  // Legs and shiny shoes.
  outlinedOval(c,new RectF(-27,-36,-6,-7),"#FFD9E2","#F4A5BF","#9A4B66",3);
  outlinedOval(c,new RectF(8,-36,28,-7),"#FFD9E2","#F4A5BF","#9A4B66",3);
  grad(c,new RectF(-38,-15,-4,3),9,"#FFF7FB","#F0609E");grad(c,new RectF(5,-15,42,3),9,"#FFF7FB","#F0609E");
  // Backpack and dress.
  grad(c,new RectF(-57,-101,-20,-43),13,"#FFCF63","#E68E38");
  box(c,-52,-93,-23,-54,9,"#F27EA9");star(c,-38,-72,11);
  Path skirt=new Path();skirt.moveTo(-24,-77);skirt.lineTo(24,-77);skirt.lineTo(39,-28);skirt.quadTo(0,-13,-40,-28);skirt.close();
  p.setShader(new LinearGradient(0,-77,0,-25,co("#F69ACB"),co("#DC4086"),Shader.TileMode.CLAMP));c.drawPath(skirt,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setColor(co("#9D3A6C"));p.setStrokeWidth(4);c.drawPath(skirt,p);p.setStyle(Paint.Style.FILL);
  for(int i=-1;i<=1;i++)line(c,i*14,-57,i*23,-28,"#FFB9D8",3);
  outlinedOval(c,new RectF(-28,-104,28,-62),"#FFFFFF","#FFE2EC","#A45E77",3);
  outlinedOval(c,new RectF(-40,-98,-18,-58),"#FFDBE4","#F7A6C1","#A45E77",3);
  outlinedOval(c,new RectF(18,-99,56,-70),"#FFDBE4","#F7A6C1","#A45E77",3);
  grad(c,new RectF(-23,-93,23,-61),7,"#FA79B7","#DF3D87");box(c,-12,-78,12,-66,5,"#FFB9D9");
  // White outline, pink ears and face.
  outlinedOval(c,new RectF(-48,-175,-9,-128),"#FFD4DF","#F598BB","#A5486B",4);
  outlinedOval(c,new RectF(8,-181,48,-133),"#FFD4DF","#F598BB","#A5486B",4);
  oval(c,-38,-164,-19,-137,"#EF8CB2");oval(c,18,-171,38,-143,"#EF8CB2");
  outlinedOval(c,new RectF(-57,-163,65,-76),"#FFF0F3","#F9B2CC","#98435F",4);
  outlinedOval(c,new RectF(24,-142,79,-99),"#FFD8E4","#F6A3C0","#AA4C68",3.8f);
  oval(c,41,-130,50,-116,"#C65077");oval(c,59,-128,68,-115,"#C65077");
  oval(c,-43,-119,-13,-97,"#F698B7");oval(c,13,-117,32,-101,"#F698B7");
  oval(c,-21,-141,-6,-119,"#38252F");oval(c,12,-144,26,-122,"#38252F");
  circle(c,-15,-135,3.8f,"#FFFFFF");circle(c,17,-138,3.8f,"#FFFFFF");
  line(c,-20,-138,-25,-143,"#38252F",2.5f);line(c,23,-140,29,-145,"#38252F",2.5f);
  Path smile=new Path();smile.moveTo(-11,-106);smile.quadTo(9,-98,25,-109);smile.quadTo(22,-83,6,-84);smile.quadTo(-6,-86,-11,-106);fill("#7E2745");c.drawPath(smile,p);oval(c,0,-96,18,-85,"#F7698D");
  // Bow on the left ear.
  c.save();c.translate(-31,-157);c.rotate(-24);outlinedOval(c,new RectF(-40,-20,0,14),"#FF98CC","#E93D8D","#983D66",3);outlinedOval(c,new RectF(0,-23,34,17),"#FF98CC","#E93D8D","#983D66",3);outlinedOval(c,new RectF(-9,-10,9,11),"#FFB4D8","#E64B91","#983D66",3);c.restore();
  c.restore();
 }
 void moveTo(float target,boolean jump){if(anim!=null)anim.cancel();float from=pos;anim=ValueAnimator.ofFloat(0,1);anim.setDuration(jump?520:480);anim.setInterpolator(new DecelerateInterpolator());anim.addUpdateListener(a->{float t=(float)a.getAnimatedValue();pos=from+(target-from)*t;hop=jump?(float)Math.sin(t*Math.PI)*75:0;phase=t;invalidate();});anim.start();}
 @Override protected void onDetachedFromWindow(){if(anim!=null)anim.cancel();super.onDetachedFromWindow();}
 @Override protected void onDraw(Canvas c){
  super.onDraw(c);if(getWidth()==0||getHeight()==0)return;c.save();c.scale(getWidth()/960f,getHeight()/600f);
  String sky=world==2?"#B9BEFC":world==3?"#FFD6EC":"#49BDF5";
  grad(c,new RectF(0,0,960,600),0,sky,"#DFF8FF");
  circle(c,825,75,38,"#FFF09A");cloud(c,145,66,33);cloud(c,460,43,30);cloud(c,717,112,26);
  poly(c,"#91D3D7",0,245,130,92,250,220,400,68,580,215,710,105,960,245,960,600,0,600);
  poly(c,"#D7F4EF",345,120,400,68,454,122,420,112,402,125,386,113);
  oval(c,-200,160,490,580,"#7ACB72");oval(c,390,155,1200,580,"#85D37B");
  if(map){
    grad(c,new RectF(0,155,960,600),0,"#32C8F0","#087EBF");
    for(int i=0;i<22;i++){float x=(i*83)%960,y=180+(i*43)%410;line(c,x,y,x+35,y,"#8EE8FF",4);}
    // layered terraces and a serpentine path directly under the five rows of clickable nodes
    for(int row=0;row<5;row++){
      float y=75+row*112;
      grad(c,new RectF(45,y,915,y+96),32,"#839C80","#4A6970");
      grad(c,new RectF(35,y-11,925,y+62),35,"#AAEA56","#5FBF39");
      box(c,80,y+14,880,y+47,16,"#E8BD71");box(c,80,y+10,880,y+39,15,"#FFE3A3");
      if(row<4){float x=row%2==0?105:855;box(c,x-20,y+38,x+20,y+123,14,"#C28C50");for(int i=0;i<5;i++)line(c,x-18,y+48+i*13,x+18,y+48+i*13,"#F7D68B",5);}
    }
    castle(c,780,89,.34f);tree(c,55,106,.42f,true);tree(c,930,228,.47f,false);tree(c,48,340,.5f,false);
    Random r=new Random(440);for(int i=0;i<42;i++){int row=r.nextInt(5);float x=43+r.nextInt(865),y=123+112*row+r.nextInt(16);flower(c,x,y,i%3==0?"#FF9FD5":"#FFF496",.65f);}
    for(int i=0;i<9;i++)bush(c,30+i*110,593,25);pig(c,64,585,.52f,0);
  }else{
    tree(c,114,356,1.15f,true);tree(c,270,346,.8f,false);tree(c,828,347,.78f,false);castle(c,846,344,.78f);
    grad(c,new RectF(0,357,550,600),0,"#7F9A60","#A77750");grad(c,new RectF(750,357,960,600),0,"#7F9A60","#A77750");
    grad(c,new RectF(0,350,550,388),11,"#B0EE62","#50A83C");grad(c,new RectF(750,350,960,388),11,"#B0EE62","#50A83C");
    grad(c,new RectF(550,365,750,600),0,"#69E6FF","#168BCF");
    for(int i=0;i<7;i++){float y=405+i*26;line(c,560+(i%2)*25,y,716+(i%2)*20,y,"#C4F9FF",6);}
    for(int i=0;i<9;i++){float x=25+i*54;flower(c,x,355,i%2==0?"#FF86C8":"#FFF18D",.85f);}
    if(open){box(c,535,340,766,374,10,"#AF733F");for(int i=0;i<9;i++)line(c,542+i*26,341,542+i*26,374,"#EBC284",5);line(c,540,330,760,330,"#C4894D",9);star(c,654,280,27);}
    else{box(c,548,221,581,354,8,"#CFA36C");box(c,720,221,753,354,8,"#CFA36C");grad(c,new RectF(579,239,720,350),12,"#EBC18D","#AF773E");for(int i=0;i<5;i++)line(c,599+i*24,242,599+i*24,346,"#9F6C3E",3);box(c,626,276,674,322,10,"#F6C94C");p.setStyle(Paint.Style.STROKE);p.setColor(co("#E5A437"));p.setStrokeWidth(8);c.drawArc(634,253,666,294,180,180,false,p);p.setStyle(Paint.Style.FILL);circle(c,650,294,7,"#966323");}
    for(int i=0;i<4;i++)if(pos<i*.2f+.08f)star(c,125+i*92,305-(i%2)*22,19);
    pig(c,75+pos*(open?715:430),353,1.20f,hop);
    for(int i=0;i<5;i++)bush(c,40+i*110,590,30);
  }c.restore();
 }
}
