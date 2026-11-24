package vn.toanlop3;
import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import java.util.Arrays;

/** Interactive division model for the actual 20-apple VBT exercise. A child can drag an apple
 * or tap an apple and then a plate; plate contents are persisted in the journey checkpoint. */
final class AppleLabView extends View {
 private final JourneyState state;private final Runnable save;private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
 private final int count=20;private final int[] bins=new int[count];private int held=-1;private float px,py,scale=1,ox,oy;private boolean dragging;
 private static final float W=850,H=390;
 AppleLabView(Context c,JourneyState s,Runnable save){super(c);state=s;this.save=save;setClickable(true);setContentDescription("Chia 20 quả táo vào đĩa: chạm hoặc kéo từng quả");setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);load();}
 private void load(){if(state.labPlates<2||state.labPlates>4)state.labPlates=2;Arrays.fill(bins,0);if(state.appleLayout!=null&&state.appleLayout.matches("[0-4]{20}"))for(int i=0;i<count;i++){int n=state.appleLayout.charAt(i)-'0';bins[i]=n<=state.labPlates?n:0;}}
 void plates(int n){if(n>=2&&n<=4&&state.labPlates!=n){state.labPlates=n;Arrays.fill(bins,0);held=-1;persist();invalidate();}}
 private void persist(){StringBuilder b=new StringBuilder();for(int n:bins)b.append(n);state.appleLayout=b.toString();save.run();}
 private void fill(int c){p.setShader(null);p.setColor(c);p.setStyle(Paint.Style.FILL);}
 private void label(Canvas c,String s,float x,float y,float size,int col){fill(col);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));p.setTextSize(size);p.setTextAlign(Paint.Align.CENTER);c.drawText(s,x,y,p);}
 private void apple(Canvas c,float x,float y,float r){p.setStyle(Paint.Style.FILL);p.setShader(new RadialGradient(x-r*.35f,y-r*.4f,r*1.65f,new int[]{0xffffc9ae,0xfff96761,0xffce293e},new float[]{0,.48f,1},Shader.TileMode.CLAMP));c.drawOval(x-r,y-r,x+r,y+r,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setColor(0xffa8363e);p.setStrokeWidth(1.7f);c.drawOval(x-r,y-r,x+r,y+r,p);fill(0xff467c43);p.setStrokeWidth(3);c.drawLine(x,y-r+2,x+2,y-r-9,p);c.drawOval(x,y-r-10,x+r*.9f,y-r+1,p);fill(0xffffe8ce);c.drawOval(x-r*.52f,y-r*.6f,x-r*.18f,y-r*.2f,p);}
 private float center(int plate){return 64+(plate-.5f)*(W-128)/state.labPlates;}
 private float width(){return (W-128)/state.labPlates;}
 @Override protected void onDraw(Canvas c){super.onDraw(c);scale=Math.min(getWidth()/W,getHeight()/H);if(scale<=0)return;ox=(getWidth()-W*scale)/2;oy=(getHeight()-H*scale)/2;c.save();c.translate(ox,oy);c.scale(scale,scale);
  fill(0xfffff6e5);c.drawRoundRect(0,0,W,H,26,26,p);p.setStyle(Paint.Style.STROKE);p.setColor(0xffeb9dbb);p.setStrokeWidth(4);c.drawRoundRect(1,1,W-1,H-1,26,26,p);p.setStyle(Paint.Style.FILL);
  int remaining=0;int[] used=new int[state.labPlates+1];for(int v:bins){if(v==0)remaining++;else used[v]++;}
  label(c,"Còn "+remaining+" quả · Chia vào "+state.labPlates+" đĩa",W/2,35,28,0xff714063);
  for(int i=0;i<count;i++){if(bins[i]!=0||i==held)continue;float x=89+(i%10)*74.7f,y=82+(i/10)*56;apple(c,x,y,23);}
  for(int b=1;b<=state.labPlates;b++){
   float x=center(b),ww=width();p.setShader(new LinearGradient(x,200,x,345,0xfffff8ed,0xffcfb1e9,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);c.drawOval(x-ww*.45f,194,x+ww*.45f,342,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setColor(0xff8e71bf);p.setStrokeWidth(5);c.drawOval(x-ww*.45f,194,x+ww*.45f,342,p);p.setStyle(Paint.Style.FILL);
   fill(0xfffffbf4);c.drawOval(x-ww*.36f,211,x+ww*.36f,322,p);int j=0,cols=Math.max(2,(int)(ww/41));float rr=Math.min(18,ww/9);
   for(int i=0;i<count;i++)if(bins[i]==b&&i!=held){int row=j/cols,col=j%cols;float xx=x+(col-(cols-1)/2f)*rr*2.02f,yy=234+row*rr*1.82f;apple(c,xx,yy,rr);j++;}
   label(c,""+used[b],x,370,31,0xff66417b);
  }
  if(held>=0){apple(c,px,py,28);p.setStyle(Paint.Style.STROKE);p.setColor(0xffffbb20);p.setStrokeWidth(4);c.drawCircle(px,py,33,p);p.setStyle(Paint.Style.FILL);}
  c.restore();}
 private int sourceAt(float x,float y){if(y<50||y>170)return -1;int col=Math.round((x-89)/74.7f),row=Math.round((y-82)/56);if(col<0||col>9||row<0||row>1)return -1;int idx=row*10+col;return bins[idx]==0?idx:-1;}
 private int plateAt(float x,float y){if(y<188||y>352)return 0;int n=(int)Math.floor((x-64)/width())+1;return n>=1&&n<=state.labPlates?n:0;}
 @Override public boolean onTouchEvent(MotionEvent e){if(scale<=0)return false;float x=(e.getX()-ox)/scale,y=(e.getY()-oy)/scale;switch(e.getActionMasked()){
  case MotionEvent.ACTION_DOWN:{int src=sourceAt(x,y);if(src>=0){held=src;px=x;py=y;dragging=true;invalidate();return true;}int b=plateAt(x,y);if(b>0&&held>=0){bins[held]=b;held=-1;persist();invalidate();performClick();return true;}if(b>0){for(int i=count-1;i>=0;i--)if(bins[i]==b){bins[i]=0;persist();break;}invalidate();return true;}return true;}
  case MotionEvent.ACTION_MOVE:if(held>=0&&dragging){px=x;py=y;invalidate();}return true;
  case MotionEvent.ACTION_UP:{if(held>=0){int b=plateAt(x,y);if(b>0){bins[held]=b;held=-1;persist();}else{px=x;py=y;}}dragging=false;invalidate();performClick();return true;}
  case MotionEvent.ACTION_CANCEL:dragging=false;held=-1;invalidate();return true;
  default:return true;
 }}
 @Override public boolean performClick(){super.performClick();return true;}
}
