package vn.toanlop3;
import android.content.Context;import android.graphics.*;import android.view.View;
final class WaterLabView extends View{
 final BookBank.Q q;final JourneyState s;final Paint p=new Paint(3);
 WaterLabView(Context c,BookBank.Q q,JourneyState s){super(c);this.q=q;this.s=s;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 void color(int c){p.setShader(null);p.setColor(c);p.setStyle(Paint.Style.FILL);}
 void label(Canvas c,String str,float x,float y,float size,int col){color(col);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));c.drawText(str,x,y,p);}
 void cup(Canvas c,float x,float y,float w,float h,String name,int capacity,int amount){
  color(0xffd9f5ff);c.drawRoundRect(x,y,x+w,y+h,17,17,p);
  float fill=amount/(float)capacity*h;
  p.setShader(new LinearGradient(x,y+h-fill,x+w,y+h,0xffa5eeff,0xff29a9dc,Shader.TileMode.CLAMP));
  c.save();Path shape=new Path();shape.addRoundRect(new RectF(x,y,x+w,y+h),17,17,Path.Direction.CW);c.clipPath(shape);c.drawRect(x,y+h-fill,x+w,y+h,p);c.restore();p.setShader(null);
  color(0xff407f9b);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(5);c.drawRoundRect(x,y,x+w,y+h,17,17,p);c.drawArc(x+w-4,y+25,x+w+58,y+142,-90,180,false,p);p.setStyle(Paint.Style.FILL);
  for(int i=0;i<=5;i++){float yy=y+h-i*h/5;p.setStrokeWidth(3);c.drawLine(x+9,yy,x+32,yy,p);}
  label(c,name+" · "+capacity+" ml",x+w/2,y-19,30,0xff433659);label(c,amount+" ml",x+w/2,y+h+43,36,0xff205e86);
 }
 @Override protected void onDraw(Canvas c){super.onDraw(c);float scale=Math.min(getWidth()/585f,getHeight()/390f);if(scale<=0)return;c.save();c.translate((getWidth()-585*scale)/2,(getHeight()-390*scale)/2);c.scale(scale,scale);
  color(0xfffff7e7);c.drawRoundRect(0,0,585,390,24,24,p);
  float max=Math.max(q.a,q.b);float ha=245*q.a/max,hb=245*q.b/max;
  cup(c,62,285-ha,164,ha,"A",q.a,s.liquidA);cup(c,337,285-hb,164,hb,"B",q.b,s.liquidB);
  label(c,"Mỗi lần rót: đến khi ca đầy hoặc ca kia hết.",292,368,23,0xff634b66);c.restore();
 }
}
