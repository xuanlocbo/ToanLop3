package vn.toanlop3;
import android.content.Context;import android.graphics.*;import android.view.View;
/** Independently drawn diagrams of the workbook page 110 and 113 tasks. */
final class DiagramView extends View{
 private final BookBank.Q q;private final Paint p=new Paint(3);
 DiagramView(Context c,BookBank.Q q){super(c);this.q=q;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 static boolean supports(BookBank.Q q){return q.lesson==43&&q.page==110&&q.exercise>=3&&q.exercise<=5||q.lesson==44&&q.page==113&&q.exercise==3;}
 void fill(int col){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(col);}
 void line(Canvas c,float x,float y,float a,float b){fill(0xff3c4564);p.setStrokeWidth(3);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,a,b,p);}
 void text(Canvas c,String t,float x,float y){fill(0xff3c365b);p.setTextSize(20);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));p.setTextAlign(Paint.Align.CENTER);c.drawText(t,x,y,p);}
 void circle(Canvas c,float x,float y,float r){fill(0xff3c4564);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawCircle(x,y,r,p);p.setStyle(Paint.Style.FILL);}
 void face(Canvas c,int col,float... pts){Path z=new Path();z.moveTo(pts[0],pts[1]);for(int i=2;i<pts.length;i+=2)z.lineTo(pts[i],pts[i+1]);z.close();fill(col);c.drawPath(z,p);p.setStyle(Paint.Style.STROKE);p.setColor(0xff4a8299);p.setStrokeWidth(1.6f);c.drawPath(z,p);p.setStyle(Paint.Style.FILL);}
 void cube(Canvas c,float x,float y,float u){float dx=u*.43f,dy=u*.48f;face(c,0xff91d5f0,x,y,x+u,y,x+u,y+u,x,y+u);face(c,0xff5fb2d8,x+u,y,x+u+dx,y-dy,x+u+dx,y+u-dy,x+u,y+u);face(c,0xffb7e7f8,x,y,x+dx,y-dy,x+u+dx,y-dy,x+u,y);}
 void cylinder(Canvas c,float x,float y,float w,float h){fill(0xff73c5e2);c.drawRect(x-w/2,y,x+w/2,y+h,p);c.drawOval(x-w/2,y+h-13,x+w/2,y+h+13,p);fill(0xffa6e6f6);c.drawOval(x-w/2,y-13,x+w/2,y+13,p);fill(0xff3987ac);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.7f);c.drawOval(x-w/2,y-13,x+w/2,y+13,p);c.drawArc(x-w/2,y+h-13,x+w/2,y+h+13,0,180,false,p);p.setStyle(Paint.Style.FILL);}
 @Override protected void onDraw(Canvas c){super.onDraw(c);float s=Math.min(getWidth()/460f,getHeight()/300f);if(s<=0)return;c.save();c.translate((getWidth()-460*s)/2,(getHeight()-300*s)/2);c.scale(s,s);fill(0xfffff9ea);c.drawRoundRect(0,0,460,300,20,20,p);
 if(q.lesson==44){float x=230,y=150,r=122;circle(c,x,y,r);float a=106,b=(float)Math.sqrt(r*r-a*a);float[] xs={x-a,x+a,x+a,x-a},ys={y-b,y-b,y+b,y+b};for(int i=0;i<4;i++){line(c,xs[i],ys[i],xs[(i+1)%4],ys[(i+1)%4]);text(c,"ABCD".substring(i,i+1),xs[i]+(i==0||i==3?-13:13),ys[i]+(i<2?-9:19));}line(c,xs[0],ys[0],xs[2],ys[2]);line(c,xs[1],ys[1],xs[3],ys[3]);fill(0xff3c4564);c.drawCircle(x,y,4,p);text(c,"O",x,y-12);}
 else if(q.exercise==3){float x=230,y=150,r=117;circle(c,x,y,r);double[] ang={Math.PI,0,-Math.PI/3,2*Math.PI/3,Math.PI/3};float[] xs=new float[5],ys=new float[5];for(int i=0;i<5;i++){xs[i]=x+(float)Math.cos(ang[i])*r;ys[i]=y+(float)Math.sin(ang[i])*r;line(c,x,y,xs[i],ys[i]);text(c,"ABCDE".substring(i,i+1),x+(float)Math.cos(ang[i])*(r+19),y+(float)Math.sin(ang[i])*(r+19)+6);}line(c,xs[0],ys[0],xs[2],ys[2]);fill(0xff3c4564);c.drawCircle(x,y,4,p);text(c,"O",x-13,y-10);}
 else if(q.exercise==4){float u=48;for(int depth=3;depth>=0;depth--){for(int col=0;col<4;col++)cube(c,77+col*u+depth*u*.43f,227-depth*u*.48f,u);if(depth==3){cube(c,77+depth*u*.43f,227-depth*u*.48f-u,u);cube(c,77+3*u+depth*u*.43f,227-depth*u*.48f-u,u);}}
 cylinder(c,230,149,92,70);cylinder(c,230,65,44,84);p.setShader(new RadialGradient(221,43,31,new int[]{0xffd2f6ff,0xff2999c9},null,Shader.TileMode.CLAMP));c.drawCircle(230,52,24,p);p.setShader(null);cube(c,77,179,u);cube(c,221,179,u);}
 else{float u=68;for(int d=1;d>=0;d--)for(int y=0;y<2;y++)for(int x=0;x<4;x++)cube(c,56+x*u+d*u*.43f,179-y*u-d*u*.48f,u);}
 c.restore();}
}
