package vn.toanlop3;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/** Animated in-game journey meter, not a separate game mode. A math answer is the action. */
final class AdventureTrail extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Bitmap art,sprite;
    private final int theme;
    private float step,hop,flash;
    private boolean won;
    private ValueAnimator motion;
    AdventureTrail(Context c,int theme,float progress,boolean solved){
        super(c);this.theme=theme;art=JourneyMapView.art(c);sprite=JourneyMapView.mascot(c);step=Math.max(0,Math.min(1,progress));won=solved;
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
    }
    void advance(float to,boolean success){
        if(motion!=null)motion.cancel();float from=step;won=success;
        motion=ValueAnimator.ofFloat(0,1);motion.setDuration(success?680:230);motion.setInterpolator(new DecelerateInterpolator());
        motion.addUpdateListener(a->{float t=(float)a.getAnimatedValue();step=from+(to-from)*t;hop=(float)Math.sin(Math.PI*t)*(success?0.24f:.14f);flash=success?1-t:0;invalidate();});motion.start();
    }
    private void box(Canvas c,RectF r,float rad,int a,int b){
        p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(r.left,r.top,r.left,r.bottom,a,b,Shader.TileMode.CLAMP));c.drawRoundRect(r,rad,rad,p);p.setShader(null);
    }
    private void star(Canvas c,float x,float y,float r,int col){
        Path z=new Path();for(int i=0;i<10;i++){double a=i*Math.PI/5-Math.PI/2;float s=i%2==0?r:r*.46f;float xx=x+(float)Math.cos(a)*s,yy=y+(float)Math.sin(a)*s;if(i==0)z.moveTo(xx,yy);else z.lineTo(xx,yy);}z.close();p.setColor(col);p.setStyle(Paint.Style.FILL);c.drawPath(z,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);p.setColor(0xffbf7920);c.drawPath(z,p);p.setStyle(Paint.Style.FILL);
    }
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);float w=getWidth(),h=getHeight();if(w<1||h<1)return;
        c.save();Path clip=new Path();clip.addRoundRect(new RectF(0,0,w,h),12,12,Path.Direction.CW);c.clipPath(clip);
        // Stretch a clean scenic strip from the approved art, not the erased checkpoint region.
        if(art!=null){int aw=art.getWidth(),ah=art.getHeight();Rect src=new Rect(0,0,aw,Math.max(1,(int)(ah*.095f)));c.drawBitmap(art,src,new RectF(0,0,w,h*.48f),p);}
        int[] sky={0xffb9e99a,0xff90e2fa,0xffc3b8ff,0xffedb9d8};
        box(c,new RectF(0,h*.32f,w,h),0,sky[theme],0xff5da845);
        p.setColor(0xfff7dc92);c.drawRoundRect(0,h*.66f,w,h,15,15,p);
        float start=h*.58f,end=w-h*.58f;
        // Each tile reflects real progress on the current task. No time pressure or extra movement.
        for(int i=0;i<7;i++){
            float x=start+(end-start)*i/6f,y=h*.73f;
            float a=w*.042f;
            box(c,new RectF(x-a,y-h*.11f,x+a,y+h*.115f),h*.09f,i<=step*6+.001?0xffffdf68:0xffdce5ef,i<=step*6+.001?0xffeba02d:0xff8ca8b9);
        }
        float characterH=h*.80f,characterW=sprite==null?h*.50f:characterH*sprite.getWidth()/sprite.getHeight();
        float x=start+(end-start)*step,foot=h*.84f-hop*h;
        p.setColor(0x400b5332);c.drawOval(x-characterW*.43f,h*.83f,x+characterW*.43f,h*.94f,p);
        if(sprite!=null)c.drawBitmap(sprite,null,new RectF(x-characterW/2,foot-characterH,x+characterW/2,foot),p);
        if(won){
            star(c,w-h*.24f,h*.25f,h*.15f,0xffffdc53);
            if(flash>0)for(int i=0;i<5;i++){double a=i*2*Math.PI/5;float r=(1-flash)*h*.42f;star(c,x+(float)Math.cos(a)*r,h*.22f+(float)Math.sin(a)*r,h*.055f,0xffffe885);}
        }else star(c,w-h*.24f,h*.25f,h*.13f,0xfff9e5a0);
        c.restore();
    }
    @Override protected void onDetachedFromWindow(){if(motion!=null)motion.cancel();super.onDetachedFromWindow();}
}
