package vn.toanlop3;
import android.content.Context;import android.graphics.*;import android.view.View;
final class InstrumentView extends View {
 final BookBank.Q q; final Paint p=new Paint(3);
 InstrumentView(Context c,BookBank.Q q){super(c);this.q=q;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 void f(String x){p.setStyle(Paint.Style.FILL);p.setColor(Color.parseColor(x));}
 void line(Canvas c,float a,float b,float x,float y,float w,String col){f(col);p.setStrokeWidth(w);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(a,b,x,y,p);}
 void text(Canvas c,String s,float x,float y,float size){f("#443653");p.setTextAlign(Paint.Align.CENTER);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));c.drawText(s,x,y,p);}
 void rect(Canvas c,float a,float b,float x,float y,float r,String col){f(col);c.drawRoundRect(a,b,x,y,r,r,p);}
 void circ(Canvas c,float a,float b,float r,String col){f(col);c.drawCircle(a,b,r,p);}
 @Override protected void onDraw(Canvas c){if(getWidth()==0||getHeight()==0)return;c.save();c.scale(getWidth()/340f,getHeight()/175f);
  rect(c,0,0,340,175,14,"#FFF8E9");
  switch(q.kind){
  case RULER:
   rect(c,10,66,330,142,9,"#FFD985");for(int i=0;i<=50;i++){float x=20+6*i;line(c,x,68,x,i%10==0?100:i%5==0?92:83,i%10==0?2:1,"#714928");if(i%10==0)text(c,""+(i/10),x,122,15);}line(c,20,47,20+q.a*6,47,7,"#E45095");circ(c,20,47,4,"#983961");circ(c,20+q.a*6,47,4,"#983961");text(c,"cm",310,137,12);break;
  case BALANCE:
   line(c,65,86,275,86,8,"#6E8BAA");line(c,170,86,170,145,12,"#86AECE");rect(c,111,141,229,153,5,"#75A1BF");
   for(int x:new int[]{65,275}){line(c,x,86,x-43,55,2,"#91ADC5");line(c,x,86,x+43,55,2,"#91ADC5");rect(c,x-45,55,x+45,64,4,"#A4D9E7");}
   rect(c,27,22,64,53,4,"#C6D6E0");rect(c,67,17,105,53,4,"#B6C6D6");text(c,q.a+" g",45,45,12);text(c,q.b+" g",86,42,12);rect(c,248,13,302,54,8,"#FF9EC8");text(c,"?",275,42,24);break;
  case CUP:
   rect(c,104,16,226,159,10,"#DDF4FD");rect(c,111,150-(q.a/(float)q.b)*124,219,152,3,"#57C9F5");for(int i=0;i<=5;i++){float y=150-i*24.8f;line(c,113,y,134,y,1.7f,"#496C7A");if(i>0)text(c,""+(i*q.b/5),153,y+5,12);}text(c,"ml",201,31,13);break;
  case SCALE:
   rect(c,100,14,240,162,20,"#8DD9DB");circ(c,170,87,66,"#FFFFFF");
   for(int i=0;i<20;i++){double a=i*Math.PI/10-Math.PI/2;float x=(float)Math.cos(a),y=(float)Math.sin(a);line(c,170+x*54,87+y*54,170+x*(i%5==0?45:49),87+y*(i%5==0?45:49),2,"#4A6574");}
   text(c,"1 kg",170,48,12);text(c,"250",207,91,12);text(c,"500",170,131,12);text(c,"750",133,91,12);double a=q.a/1000.0*Math.PI*2-Math.PI/2;line(c,170,87,170+(float)Math.cos(a)*50,87+(float)Math.sin(a)*50,4,"#E34D78");circ(c,170,87,5,"#813E64");break;
  case THERMO:
   rect(c,126,6,214,168,16,"#E5F5FF");rect(c,150,15,166,147,8,"#FFFFFF");
   float low=145,high=15,step=(low-high)/80f;for(int i=-30;i<=50;i++){float y=low-(i+30)*step;line(c,169,y,i%10==0?184:176,y,1.2f,"#627B91");if(i%10==0)text(c,""+i,195,y+4,10);}
   rect(c,153,low-(q.a+30)*step,163,151,4,"#EF467A");circ(c,158,151,10,"#EF467A");text(c,"°C",235,23,15);break;
  case GEOMETRY:
   for(int i=0;i<=12;i++)line(c,50+20*i,5,50+20*i,170,.6f,"#D1DEEA");for(int i=0;i<=8;i++)line(c,50,5+i*20,290,5+i*20,.6f,"#D1DEEA");
   float[] x={90,170,250,230,110,90,250},y={85,5,85,125,125,165,165};String[] name={"A","B","C","D","E","M","N"};int[][] edges={{0,1},{1,2},{2,3},{3,6},{6,5},{5,4},{4,0},{0,2},{4,3},{1,6},{1,5}}; // the middle vertical and source outline
   for(int[] z:edges){if(z[0]==1&&(z[1]==6||z[1]==5))continue;line(c,x[z[0]],y[z[0]],x[z[1]],y[z[1]],2.5f,"#344C69");}
   line(c,170,5,170,165,2.5f,"#344C69");for(int i=0;i<x.length;i++){circ(c,x[i],y[i],3,"#C85192");text(c,name[i],x[i]+(i==1?13:-12),y[i]+10,12);}text(c,"K",159,81,12);text(c,"I",159,121,12);text(c,"H",159,163,12);break;
  default:break;
  }c.restore();
 }
}
