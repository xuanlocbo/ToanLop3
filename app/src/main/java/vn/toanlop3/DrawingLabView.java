package vn.toanlop3;
import android.content.Context;import android.graphics.*;import android.view.*;import java.util.*;
final class DrawingLabView extends View{
 private final BookBank.Q q;private final JourneyState s;private final Runnable changed;private final Paint p=new Paint(3);
 private static final float W=1010,H=410,STEP=49,X=526,Y=85;
 private float scale,ox,oy;
 DrawingLabView(Context c,BookBank.Q q,JourneyState s,Runnable changed){super(c);this.q=q;this.s=s;this.changed=changed;setClickable(true);setContentDescription("Lưới vẽ hình: chạm từng đỉnh rồi chạm lại đỉnh đầu");}
 void reset(){s.drawing="";changed.run();invalidate();}
 void undo(){List<int[]> a=ShapePuzzle.parse(s.drawing);if(a.isEmpty())return;a.remove(a.size()-1);StringBuilder b=new StringBuilder();for(int[] v:a)b.append(v[0]).append(',').append(v[1]).append(';');s.drawing=b.toString();changed.run();invalidate();}
 void color(int c){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(c);}
 void text(Canvas c,String t,float x,float y,int size){color(0xff58385f);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));p.setTextAlign(Paint.Align.CENTER);p.setTextSize(size);c.drawText(t,x,y,p);}
 void grid(Canvas c,float x,float y,int nx,int ny,float step){color(0xffe4d9e9);p.setStrokeWidth(1.4f);for(int i=0;i<=nx;i++)c.drawLine(x+i*step,y,x+i*step,y+ny*step,p);for(int i=0;i<=ny;i++)c.drawLine(x,y+i*step,x+nx*step,y+i*step,p);}
 void shape(Canvas c,List<int[]> a,float x,float y,float step,boolean dots){if(a.isEmpty())return;Path path=new Path();for(int i=0;i<a.size();i++){int[] v=a.get(i);float px=x+v[0]*step,py=y+v[1]*step;if(i==0)path.moveTo(px,py);else path.lineTo(px,py);}color(0xffbd3681);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(6);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);if(dots)for(int[] v:a)c.drawCircle(x+v[0]*step,y+v[1]*step,7,p);}
 @Override protected void onDraw(Canvas c){super.onDraw(c);scale=Math.min(getWidth()/W,getHeight()/H);if(scale<=0)return;ox=(getWidth()-W*scale)/2;oy=(getHeight()-H*scale)/2;c.save();c.translate(ox,oy);c.scale(scale,scale);color(0xfffff8ea);c.drawRoundRect(0,0,W,H,25,25,p);text(c,"Hình mẫu",216,40,30);text(c,"Bé vẽ ở đây",722,40,30);
  grid(c,34,85,8,6,43);grid(c,X,Y,8,6,STEP);
  int[][] target=ShapePuzzle.pattern(q.a);List<int[]> a=new ArrayList<>();for(int[] v:target)a.add(v);a.add(target[0]);shape(c,a,77,128,43,false);
  shape(c,ShapePuzzle.parse(s.drawing),X,Y,STEP,true);text(c,"Chạm lại đỉnh đầu để khép hình",724,398,22);c.restore();}
 @Override public boolean onTouchEvent(MotionEvent e){if(e.getActionMasked()==MotionEvent.ACTION_DOWN)return true;if(e.getActionMasked()==MotionEvent.ACTION_UP){if(scale<=0)return false;float x=(e.getX()-ox)/scale,y=(e.getY()-oy)/scale;int a=Math.round((x-X)/STEP),b=Math.round((y-Y)/STEP);if(a>=0&&a<=8&&b>=0&&b<=6&&Math.hypot(x-X-a*STEP,y-Y-b*STEP)<=STEP*.55f){List<int[]> v=ShapePuzzle.parse(s.drawing);int max=ShapePuzzle.pattern(q.a).length+1;if(v.size()<max&&(v.isEmpty()||v.get(v.size()-1)[0]!=a||v.get(v.size()-1)[1]!=b)){s.drawing+=a+","+b+";";changed.run();invalidate();}}performClick();return true;}return true;}
 @Override public boolean performClick(){super.performClick();return true;}
}
