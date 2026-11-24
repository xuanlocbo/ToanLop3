package vn.toanlop3;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.content.res.ColorStateList;
import android.view.*;
import android.widget.*;

/** Genuine approved-art backdrop with 20 independently clickable, live native checkpoints.
 * The image contains no lesson number or saved progress. */
final class JourneyMapView extends ViewGroup {
    interface Listener { void select(int lesson); }
    private static Bitmap backdrop, mascot;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final JourneyState[] states;
    private final int current;
    private final Listener listener;
    private static final int[][] X={{294,448,604,755},{244,413,583,754},{181,360,543,728},{230,407,583,760},{245,419,600,777}};
    private static final int[] Y={78,190,303,423,569};
    private static final int[] RX={61,65,68,72,77}, RY={48,49,52,54,57};
    static synchronized Bitmap art(Context c){
        if(backdrop==null)backdrop=BitmapFactory.decodeResource(c.getResources(),R.drawable.journey_art);
        return backdrop;
    }
    static synchronized Bitmap mascot(Context c){
        if(mascot==null)mascot=BitmapFactory.decodeResource(c.getResources(),R.drawable.moc_nhien);
        return mascot;
    }
    JourneyMapView(Context c,JourneyState[] states,int current,Listener l){
        super(c);this.states=states;this.current=current;this.listener=l;
        art(c);mascot(c);setWillNotDraw(false);setClipChildren(true);
        setBackgroundColor(0xff33bde2);
        for(int row=0;row<5;row++)for(int col=0;col<4;col++){
            int n=row%2==0?44-row*4-col:41-row*4+col;
            addView(new Node(c,n,states[n-25],n==current,()->listener.select(n)));
        }
    }
    private int dp(float v){return Math.round(v*getResources().getDisplayMetrics().density);}
    @Override protected void onMeasure(int w,int h){
        int width=MeasureSpec.getSize(w),height=MeasureSpec.getSize(h);setMeasuredDimension(width,height);
        for(int i=0;i<getChildCount();i++){
            int row=i/4;
            int nw=Math.max(1,Math.round(width*RX[row]*2/932f)),nh=Math.max(1,Math.round(height*RY[row]*2/664f));
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(nw,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(nh,MeasureSpec.EXACTLY));
        }
    }
    @Override protected void onLayout(boolean changed,int l,int t,int r,int b){
        float w=r-l,h=b-t;
        for(int i=0;i<getChildCount();i++){
            View v=getChildAt(i);int row=i/4,col=i%4;
            int x=Math.round(w*X[row][col]/932f)-v.getMeasuredWidth()/2,y=Math.round(h*Y[row]/664f)-v.getMeasuredHeight()/2;
            v.layout(x,y,x+v.getMeasuredWidth(),y+v.getMeasuredHeight());
        }
    }
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        if(backdrop!=null)c.drawBitmap(backdrop,null,new RectF(0,0,getWidth(),getHeight()),paint);
        if(mascot!=null){
            float h=getHeight()*.30f,w=h*mascot.getWidth()/mascot.getHeight();
            c.drawBitmap(mascot,null,new RectF(getWidth()*.007f,getHeight()-h-3,w+getWidth()*.007f,getHeight()-3),paint);
        }
    }
    private final class Node extends View {
        final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);final int lesson,score;final boolean chosen;
        Node(Context c,int n,JourneyState s,boolean selected,Runnable onClick){
            super(c);lesson=n;chosen=selected;
            int done=s.completed();score=done==0?0:done==s.count()?3:done*2>=s.count()?2:1;
            setClickable(true);setFocusable(true);
            setContentDescription("Bài "+n+", "+done+" trên "+s.count()+" nhiệm vụ");
            setOnClickListener(v->onClick.run());
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
        private void ellipse(Canvas c,float l,float t,float r,float b,int col){p.setShader(null);p.setColor(col);p.setStyle(Paint.Style.FILL);c.drawOval(l,t,r,b,p);}
        private void star(Canvas c,float x,float y,float size,boolean earned){
            Path path=new Path();for(int i=0;i<10;i++){double a=Math.PI*i/5-Math.PI/2;float r=i%2==0?size:size*.46f;float xx=x+(float)Math.cos(a)*r,yy=y+(float)Math.sin(a)*r;if(i==0)path.moveTo(xx,yy);else path.lineTo(xx,yy);}path.close();
            p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(earned?0xffffda46:0xffc5c7d3);c.drawPath(path,p);p.setColor(earned?0xffc48116:0xff888ba4);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.7f);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);
        }
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);float w=getWidth(),h=getHeight();if(w<2||h<2)return;
            c.save();c.scale(w/144f,h/100f);
            ellipse(c,3,16,141,99,0x440f2d31);
            int dark=chosen?0xffb82976:score==3?0xff6a7e21:score>0?0xff688a36:0xff777c93;
            int top=chosen?0xffffd0e8:score==3?0xffbdfb75:score>0?0xffe8f9b8:0xfff8faff;
            int bottom=chosen?0xfff071ad:score==3?0xff6dc739:score>0?0xffaddd59:0xffc3c9df;
            ellipse(c,2,9,142,94,dark);
            p.setShader(new LinearGradient(0,2,0,76,top,bottom,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);c.drawOval(2,0,142,79,p);p.setShader(null);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(chosen?5:2.5f);p.setColor(chosen?0xffffe88b:0xfffaffff);c.drawOval(3,2,141,78,p);p.setStyle(Paint.Style.FILL);
            ellipse(c,15,7,129,28,0x66ffffff);
            p.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));p.setTextAlign(Paint.Align.CENTER);p.setTextSize(29);p.setColor(0xff30264f);c.drawText("Bài "+lesson,72,38,p);
            for(int i=0;i<3;i++)star(c,44+i*28,65,13,i<score);
            if(chosen){p.setColor(0xffe12c85);c.drawRoundRect(126,0,130,26,2,2,p);Path flag=new Path();flag.moveTo(130,0);flag.lineTo(144,6);flag.lineTo(130,13);flag.close();c.drawPath(flag,p);}
            c.restore();
        }
        @Override public void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo info){
            super.onInitializeAccessibilityNodeInfo(info);info.setClassName(Button.class.getName());info.setSelected(chosen);
        }
    }
}
