package vn.toanlop3;
import android.content.Context;
import android.graphics.*;
import android.view.View;

final class MeasureView extends View {
    private final Curriculum.Q q;private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    MeasureView(Context c,Curriculum.Q q){super(c);this.q=q;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    private void color(String s){p.setColor(Color.parseColor(s));p.setStyle(Paint.Style.FILL);}
    private void line(Canvas c,float x,float y,float x2,float y2,float size,String col){color(col);p.setStrokeWidth(size);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,x2,y2,p);}
    private void text(Canvas c,String s,float x,float y,float sz){color("#503B52");p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));p.setTextSize(sz);p.setTextAlign(Paint.Align.CENTER);c.drawText(s,x,y,p);}
    @Override protected void onDraw(Canvas c){
        if(getWidth()==0||getHeight()==0)return;
        c.save();
        if(q.kind==Curriculum.RULER){
            c.scale(getWidth()/340f,getHeight()/104f);
            color("#FFF2BC");c.drawRoundRect(8,32,332,100,9,9,p);
            float start=20,step=6;
            line(c,start,25,start+q.a*step,25,7,"#E64F98");
            for(int i=0;i<=50;i++){float x=start+i*step;line(c,x,33,x,i%10==0?61:i%5==0?54:46,i%10==0?1.8f:1f,"#77604B");if(i%10==0)text(c,""+(i/10),x,82,14);}
            text(c,"cm",310,96,11);
        }else{
            c.scale(getWidth()/260f,getHeight()/190f);
            float top=14,bottom=166,step=(bottom-top)/50;
            color("#FFE9F2");c.drawRoundRect(60,4,150,188,12,12,p);
            color("#FFFFFF");c.drawRoundRect(91,top-4,109,bottom+5,9,9,p);
            color("#DB5478");c.drawRoundRect(94,bottom-q.a*step,106,bottom+8,5,5,p);c.drawCircle(100,bottom+7,10,p);
            for(int i=0;i<=50;i++){float y=bottom-i*step;line(c,112,y,i%10==0?126:i%5==0?121:117,y,1,"#62455A");if(i%10==0)text(c,""+i,141,y+4,12);}
            text(c,"°C",180,24,16);
        }
        c.restore();
    }
}
