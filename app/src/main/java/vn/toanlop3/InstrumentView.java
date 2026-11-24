package vn.toanlop3;
import android.content.Context;import android.graphics.*;import android.view.View;
/** Redrawn textbook-style instruments, with uniform scale so geometric angles remain correct. */
final class InstrumentView extends View {
 final BookBank.Q q; final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);Integer marker=null;
 InstrumentView(Context c,BookBank.Q q){super(c);this.q=q;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 void f(String x){p.setStyle(Paint.Style.FILL);p.setShader(null);p.setColor(Color.parseColor(x));}
 void line(Canvas c,float a,float b,float x,float y,float w,String col){f(col);p.setStrokeWidth(w);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(a,b,x,y,p);}
 void text(Canvas c,String s,float x,float y,float size){f("#443653");p.setTextAlign(Paint.Align.CENTER);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));c.drawText(s,x,y,p);}
 void rect(Canvas c,float a,float b,float x,float y,float r,String col){f(col);c.drawRoundRect(a,b,x,y,r,r,p);}
 void circ(Canvas c,float a,float b,float r,String col){f(col);c.drawCircle(a,b,r,p);}
 float scale(){return Math.min(getWidth()/340f,getHeight()/175f);}
 boolean canMeasure(){return q.kind==BookBank.Kind.RULER||q.kind==BookBank.Kind.THERMO||q.kind==BookBank.Kind.CUP||q.kind==BookBank.Kind.SCALE||q.kind==BookBank.Kind.CLINICAL;}
 Integer valueAt(float px,float py){
  float s=scale();if(s<=0)return null;float x=(px-(getWidth()-340*s)/2)/s,y=(py-(getHeight()-175*s)/2)/s;Integer v=null;
  switch(q.kind){
   case RULER:if(x>=17&&x<=323&&y>=35&&y<=145)v=Math.max(0,Math.min(50,Math.round((x-20)/6)));break;
   case THERMO:if(x>=145&&x<=216&&y>=12&&y<=147)v=Math.round((145-y)/(130f/80f))-30;break;
   case CUP:if(x>=105&&x<=226&&y>=23&&y<=155)v=Math.max(0,Math.min(q.b,Math.round((150-y)/24.8f)*(q.b/5)));break;
   case SCALE:if(Math.hypot(x-170,y-87)>18&&Math.hypot(x-170,y-87)<78){double a=Math.atan2(y-87,x-170)+Math.PI/2;if(a<0)a+=2*Math.PI;v=(int)Math.round(a/(2*Math.PI)*20)*50;}break;
   case CLINICAL:if(x>=36&&x<=304&&y>=48&&y<=126)v=Math.max(35,Math.min(42,Math.round(35+(x-40)/37f)));break;
   default:break;
  }if(v!=null){marker=v;invalidate();}return v;
 }
 @Override public boolean performClick(){super.performClick();return true;}
 void cup(Canvas c,float x,float y,float w,float h,int value,int max,int tick){
  rect(c,x,y,x+w,y+h,8,"#DDF4FD");float top=y+8,bottom=y+h-7;rect(c,x+5,bottom-(value/(float)max)*(bottom-top),x+w-6,bottom,3,"#57C9F5");
  for(int v=0;v<=max;v+=tick){float yy=bottom-v/(float)max*(bottom-top);line(c,x+7,yy,x+15,yy,1.3f,"#496C7A");if(v==max||v==max/2||v==tick)text(c,""+v,x+w*.52f,yy+4,11);}
  text(c,"ml",x+w*.82f,y+14,10);p.setStyle(Paint.Style.STROKE);p.setColor(Color.parseColor("#527C8F"));p.setStrokeWidth(1.8f);c.drawRoundRect(x,y,x+w,y+h,8,8,p);p.setStyle(Paint.Style.FILL);
 }
 @Override protected void onDraw(Canvas c){
  super.onDraw(c);if(getWidth()==0||getHeight()==0)return;float s=scale();c.save();c.translate((getWidth()-340*s)/2,(getHeight()-175*s)/2);c.scale(s,s);
  rect(c,0,0,340,175,14,"#FFF8E9");
  switch(q.kind){
   case RULER:
    rect(c,10,66,330,142,9,"#FFD985");
    for(int i=0;i<=50;i++){float x=20+6*i;line(c,x,68,x,i%10==0?100:i%5==0?92:83,i%10==0?2:1,"#714928");if(i%10==0)text(c,""+(i/10),x,122,15);}
    line(c,20,47,20+q.a*6,47,7,"#E45095");circ(c,20,47,4,"#983961");circ(c,20+q.a*6,47,4,"#983961");text(c,"cm",310,137,12);
    if(marker!=null)line(c,20+marker*6,53,20+marker*6,140,2.5f,"#3A8FCC");break;
   case BALANCE:
    line(c,65,86,275,86,8,"#6E8BAA");line(c,170,86,170,145,12,"#86AECE");rect(c,111,141,229,153,5,"#75A1BF");
    for(int x:new int[]{65,275}){line(c,x,86,x-43,55,2,"#91ADC5");line(c,x,86,x+43,55,2,"#91ADC5");rect(c,x-45,55,x+45,64,4,"#A4D9E7");}
    rect(c,27,22,64,53,4,"#C6D6E0");rect(c,67,17,105,53,4,"#B6C6D6");text(c,q.a+" g",45,45,12);text(c,q.b+" g",86,42,12);rect(c,248,13,302,54,8,"#FF9EC8");text(c,"?",275,42,24);break;
   case CUP:
    rect(c,104,16,226,159,10,"#DDF4FD");rect(c,111,150-(q.a/(float)q.b)*124,219,152,3,"#57C9F5");
    for(int i=0;i<=5;i++){float y=150-i*24.8f;line(c,113,y,134,y,1.7f,"#496C7A");if(i>0)text(c,""+(i*q.b/5),153,y+5,12);}text(c,"ml",201,31,13);
    if(marker!=null)line(c,109,150-(marker/(float)q.b)*124,221,150-(marker/(float)q.b)*124,2.5f,"#E45095");break;
   case CUPS:
    for(int j=0;j<3;j++)cup(c,18+j*106,21,90,133,q.a,q.b,20);break;
   case SCALE:
    rect(c,100,14,240,162,20,"#8DD9DB");circ(c,170,87,66,"#FFFFFF");
    for(int i=0;i<20;i++){double a=i*Math.PI/10-Math.PI/2;float x=(float)Math.cos(a),y=(float)Math.sin(a);line(c,170+x*54,87+y*54,170+x*(i%5==0?45:49),87+y*(i%5==0?45:49),2,"#4A6574");}
    text(c,"0 / 1 kg",170,48,11);text(c,"250",207,91,12);text(c,"500",170,131,12);text(c,"750",133,91,12);double ang=q.a/1000.0*Math.PI*2-Math.PI/2;line(c,170,87,170+(float)Math.cos(ang)*50,87+(float)Math.sin(ang)*50,4,"#E34D78");circ(c,170,87,5,"#813E64");
    if(marker!=null){double m=marker/1000.0*Math.PI*2-Math.PI/2;circ(c,170+(float)Math.cos(m)*57,87+(float)Math.sin(m)*57,4,"#3787CB");}break;
   case THERMO:
    rect(c,126,6,214,168,16,"#E5F5FF");rect(c,150,15,166,147,8,"#FFFFFF");float low=145,step=130/80f;
    for(int i=-30;i<=50;i++){float y=low-(i+30)*step;line(c,169,y,i%10==0?184:176,y,1.2f,"#627B91");if(i%10==0)text(c,""+i,195,y+4,10);}
    rect(c,153,low-(q.a+30)*step,163,151,2,"#EF467A");circ(c,158,151,10,"#EF467A");text(c,"°C",235,23,15);
    if(marker!=null)line(c,146,low-(marker+30)*step,206,low-(marker+30)*step,2.5f,"#3787CB");break;
   case CLINICAL:
    rect(c,15,57,325,119,28,"#DDF4FD");rect(c,25,79,313,96,9,"#FFFFFF");
    line(c,32,87,40+(q.a-35)*37,87,5,"#2898CC");for(int i=0;i<=70;i++){float x=40+i*3.7f;line(c,x,91,x,i%10==0?108:i%5==0?104:98,i%10==0?1.5f:1,"#477993");if(i%10==0)text(c,""+(35+i/10),x,74,12);}
    text(c,"°C",313,74,12);if(marker!=null)line(c,40+(marker-35)*37,59,40+(marker-35)*37,117,2.5f,"#E45095");break;
   case GEOMETRY:
    // VBT p.109: AB and BC are diagonals of square cells, ED and MN horizontal.
    for(int i=0;i<=12;i++)line(c,74+16*i,7,74+16*i,168,.65f,"#D1DEEA");for(int i=0;i<=10;i++)line(c,74,7+i*16,266,7+i*16,.65f,"#D1DEEA");
    float[] xs={106,170,234,218,122,106,234},ys={87,23,87,119,119,151,151};String[] names={"A","B","C","D","E","M","N"};int[][] edges={{0,1},{1,2},{2,3},{3,6},{6,5},{5,4},{4,0},{0,2},{4,3}};
    for(int[] z:edges)line(c,xs[z[0]],ys[z[0]],xs[z[1]],ys[z[1]],2.5f,"#344C69");
    line(c,170,23,170,151,2.5f,"#344C69");for(int i=0;i<xs.length;i++){circ(c,xs[i],ys[i],2.8f,"#344C69");text(c,names[i],xs[i]+(i==1?0:i==2||i==3||i==6?11:-11),ys[i]+(i==1?-7:4),12);}
    circ(c,170,87,3,"#344C69");circ(c,170,119,3,"#344C69");circ(c,170,151,3,"#344C69");text(c,"K",161,81,12);text(c,"I",161,114,12);text(c,"H",170,166,12);break;
   default:break;
  }c.restore();
 }
}
