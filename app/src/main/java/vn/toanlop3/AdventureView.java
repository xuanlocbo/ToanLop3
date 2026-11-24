package vn.toanlop3;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/** Lightweight native vector-art platform scene. No WebView or external assets. */
final class AdventureView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private int world,stage,position,count;
    private boolean opened,home;
    private float playerX,hop;
    private ValueAnimator animation;
    private static final float[] STEPS={65,145,230,365,485};
    AdventureView(Context c,int w,int stage,int position,boolean opened,boolean home){this(c,w,stage,position,opened,home,Curriculum.COUNT);}
    AdventureView(Context c,int w,int stage,int position,boolean opened,boolean home,int count){
        super(c);this.count=count;world=w;this.stage=stage;this.position=position;this.opened=opened;this.home=home;
        playerX=STEPS[Math.max(0,Math.min(4,position))];
        setContentDescription(home?"Mộc Nhiên và thỏ nhỏ trên đường tới lâu đài":"Mộc Nhiên đang phiêu lưu");
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
    }
    private int color(String s){return Color.parseColor(s);}
    private void fill(String s){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color(s));}
    private void line(Canvas c,float x,float y,float x2,float y2,String col,float width){
        fill(col);p.setStrokeWidth(width);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,x2,y2,p);
    }
    private void circle(Canvas c,float x,float y,float r,String col){fill(col);c.drawCircle(x,y,r,p);}
    private void oval(Canvas c,float l,float t,float r,float b,String col){fill(col);c.drawOval(l,t,r,b,p);}
    private void rect(Canvas c,float l,float t,float r,float b,float rad,String col){fill(col);c.drawRoundRect(l,t,r,b,rad,rad,p);}
    private void path(Canvas c,String col,float... coords){
        Path q=new Path();q.moveTo(coords[0],coords[1]);for(int i=2;i<coords.length;i+=2)q.lineTo(coords[i],coords[i+1]);q.close();fill(col);c.drawPath(q,p);
    }
    private void star(Canvas c,float x,float y,float size,String col){
        Path q=new Path();for(int i=0;i<10;i++){double a=Math.PI*i/5-Math.PI/2;float r=(i%2==0?size:size*.46f);float xx=x+(float)Math.cos(a)*r,yy=y+(float)Math.sin(a)*r;if(i==0)q.moveTo(xx,yy);else q.lineTo(xx,yy);}q.close();fill(col);c.drawPath(q,p);
    }
    private void text(Canvas c,String s,float x,float y,float size,String col){
        fill(col);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));p.setTextSize(size);c.drawText(s,x,y,p);
    }
    private void cloud(Canvas c,float x,float y,float s){
        oval(c,x-s*1.3f,y-s*.25f,x+s*1.4f,y+s*.53f,"#FFFFFF");circle(c,x-s*.65f,y,s*.56f,"#FFFFFF");circle(c,x,y-s*.35f,s*.74f,"#FFFFFF");circle(c,x+s*.7f,y,s*.55f,"#FFFFFF");
    }
    private void tree(Canvas c,float x,float y,float scale,String foliage){
        c.save();c.translate(x,y);c.scale(scale,scale);
        rect(c,-5,-56,6,0,4,"#B78471");
        circle(c,-20,-63,22,foliage);circle(c,20,-63,22,foliage);circle(c,0,-86,27,foliage);
        circle(c,-9,-83,4,"#FCE1EF");circle(c,19,-61,4,"#FCE1EF");c.restore();
    }
    private void flower(Canvas c,float x,float y,String petals){
        line(c,x,y,x,y+12,"#65A978",2.5f);
        for(int i=0;i<5;i++){double a=i*Math.PI*2/5;circle(c,x+(float)Math.cos(a)*4.2f,y+(float)Math.sin(a)*4.2f,3.2f,petals);}
        circle(c,x,y,2.7f,"#FFDE82");
    }
    private void castle(Canvas c,float x,float y,float s){
        c.save();c.translate(x,y);c.scale(s,s);
        rect(c,-53,-78,53,0,4,"#F7C3DE");rect(c,-67,-99,-40,0,4,"#ED9FC8");rect(c,40,-99,67,0,4,"#ED9FC8");
        path(c,"#CA82BB",-75,-99,-54,-128,-32,-99);
        path(c,"#CA82BB",32,-99,54,-128,75,-99);
        rect(c,-23,-114,23,0,4,"#FFE3ED");path(c,"#D68BC6",-29,-114,0,-148,29,-114);
        rect(c,-9,-35,9,0,8,"#AE6B9D");rect(c,-9,-95,9,-72,7,"#AE6B9D");
        rect(c,-59,-76,-48,-58,4,"#FFF5AF");rect(c,48,-76,59,-58,4,"#FFF5AF");
        line(c,0,-148,0,-165,"#BC6B99",3);path(c,"#F8D15F",0,-164,24,-158,0,-151);
        c.restore();
    }
    private void girl(Canvas c,float x,float feet,float scale){
        c.save();c.translate(x,feet);c.scale(scale,scale);
        oval(c,-31,-5,31,5,"#ADBB9E");
        rect(c,-20,-22,-8,-3,5,"#FFD7BD");rect(c,8,-22,20,-3,5,"#FFD7BD");
        rect(c,-24,-7,-7,1,4,"#C64C86");rect(c,7,-7,24,1,4,"#C64C86");
        oval(c,-29,-91,29,-30,"#794D4F");
        oval(c,-30,-48,-14,-23,"#FFD7BD");oval(c,14,-48,30,-23,"#FFD7BD");
        path(c,"#E96FA9",-15,-53,15,-53,27,-20,-27,-20);
        path(c,"#F5A0C9",-13,-52,13,-52,19,-33,-19,-33);
        circle(c,0,-73,27,"#FFDBC4");
        path(c,"#794D4F",-26,-78,-22,-93,-9,-101,8,-101,25,-90,27,-72,13,-83,2,-75,-9,-84,-22,-74);
        circle(c,-9,-70,3.4f,"#4C3440");circle(c,9,-70,3.4f,"#4C3440");
        circle(c,-14,-62,4.2f,"#F3A4A7");circle(c,14,-62,4.2f,"#F3A4A7");
        fill("#A95770");p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.5f);c.drawArc(-6,-65,6,-54,15,150,false,p);p.setStyle(Paint.Style.FILL);
        path(c,"#D84D91",17,-96,33,-105,33,-87,17,-93);path(c,"#D84D91",17,-96,4,-105,4,-87,17,-93);circle(c,18,-96,4,"#F6A7CD");
        c.restore();
    }
    private void bunny(Canvas c,float x,float y){
        oval(c,x-13,y-36,x-3,y-12,"#FFFDF7");oval(c,x+3,y-36,x+13,y-12,"#FFFDF7");
        oval(c,x-10,y-31,x-6,y-15,"#F3B8CF");oval(c,x+6,y-31,x+10,y-15,"#F3B8CF");
        oval(c,x-18,y-21,x+18,y+1,"#FFFDF7");
        circle(c,x-6,y-11,2.2f,"#543B49");circle(c,x+6,y-11,2.2f,"#543B49");
        circle(c,x,y-6,2.4f,"#D77FA5");circle(c,x-11,y-6,2.5f,"#F6B7CE");circle(c,x+11,y-6,2.5f,"#F6B7CE");
    }
    void animateFrom(int old,boolean jump){
        if(animation!=null)animation.cancel();
        float start=STEPS[Math.max(0,Math.min(4,old))],end=STEPS[position];
        animation=ValueAnimator.ofFloat(0,1);animation.setDuration(jump?420:260);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addUpdateListener(v->{float t=(float)v.getAnimatedValue();playerX=start+(end-start)*t;hop=jump?(float)Math.sin(t*Math.PI)*58:0;invalidate();});
        animation.start();
    }
    @Override protected void onDetachedFromWindow(){if(animation!=null)animation.cancel();super.onDetachedFromWindow();}
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);float w=getWidth(),h=getHeight();if(w<=0||h<=0)return;
        c.save();c.scale(w/640f,h/270f);
        Path clip=new Path();clip.addRoundRect(new RectF(0,0,640,270),20,20,Path.Direction.CW);c.clipPath(clip);
        String sky=new String[]{"#C5EEFF","#D2F4FA","#DCD9FC","#F8D6EC"}[world];
        String hill=new String[]{"#B7DFBB","#C5E4D5","#D6C6F2","#E4BBDE"}[world];
        String ground=new String[]{"#9DCE9C","#9AD6BA","#C1B6E8","#D7B0D8"}[world];
        p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(0,0,0,270,color(sky),color("#FFF1F6"),Shader.TileMode.CLAMP));c.drawRect(0,0,640,270,p);p.setShader(null);
        circle(c,555,35,23,"#FFF2A8");cloud(c,90,38,24);cloud(c,310,26,20);cloud(c,495,68,18);
        oval(c,-95,82,235,260,hill);oval(c,146,85,442,265,hill);oval(c,394,82,735,265,hill);
        tree(c,42,198,.9f,"#99CDA7");tree(c,180,193,.73f,"#A9D7AB");tree(c,421,197,.7f,"#B5DDB1");
        castle(c,580,195,home?.95f:.63f);
        rect(c,0,193,640,270,0,ground);
        rect(c,0,207,640,270,0,"#F2D9B8");rect(c,0,193,640,207,4,"#80B987");
        for(int i=0;i<9;i++)flower(c,18+69*i,231+(i%2)*17,i%2==0?"#F6B4D7":"#FFF3A9");
        if(home){
            path(c,"#FFF4DF",0,270,120,270,535,202,600,202,578,190,501,190);
            for(int i=0;i<4;i++){circle(c,205+i*91,245-i*12,13,"#FFF9F0");star(c,205+i*91,245-i*12,9,"#E5AA4E");}
            girl(c,111,218,1.13f);bunny(c,160,217);
        }else{
            // Low obstacle can be cleared only with Jump.
            if(position<=2){rect(c,272,172,322,205,12,"#B69BAA");oval(c,279,173,307,188,"#DCC8D3");}
            // Gate/bridge at the end of each checkpoint; math unlocks passage.
            rect(c,505,195,557,270,0,"#8AD4F1");
            for(int y=214;y<270;y+=16)line(c,513,y,548,y,"#D2F1FC",3);
            if(opened){rect(c,490,187,572,201,5,"#AC795A");for(int x=496;x<572;x+=12)line(c,x,188,x,199,"#E7BE96",2);star(c,530,133,22,"#FFDA72");}
            else{rect(c,502,115,548,193,7,"#BA8F6B");rect(c,495,108,555,129,7,"#DAB28B");circle(c,525,155,19,"#FFF4D9");text(c,"?",525,164,28,"#965B7A");}
            for(int i=0;i<3;i++)if(position<=i){circle(c,145+i*91,149,12,"#FFF2B8");star(c,145+i*91,149,8,"#E9AC43");}
            girl(c,playerX,193-hop,.84f);bunny(c,playerX-36,192-hop*.6f);
            if(stage<count){rect(c,14,12,122,41,14,"#FFF9F2");text(c,(stage+1)+"/"+count,68,33,17,"#79465D");}
        }
        c.restore();
    }
}
