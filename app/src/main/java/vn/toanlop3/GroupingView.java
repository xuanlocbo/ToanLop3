package vn.toanlop3;
import android.content.Context;import android.graphics.*;import android.view.View;import java.util.Set;
final class GroupingView extends View {
 final int apples;final Set<Integer> selected;final Paint p=new Paint(3);
 GroupingView(Context c,int n,Set<Integer> s){super(c);apples=n;selected=s;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 void fill(int color){p.setShader(null);p.setColor(color);p.setStyle(Paint.Style.FILL);}
 void apple(Canvas c,float x,float y,float r){p.setShader(new LinearGradient(x-r,y-r,x+r,y+r,0xffff9984,0xffdb2f43,Shader.TileMode.CLAMP));c.drawOval(x-r,y-r,x+r,y+r,p);p.setShader(null);fill(0xff6cae4b);c.drawOval(x,y-r-3,x+r*.8f,y-r+r*.3f,p);fill(0xffffd1c4);c.drawCircle(x-r*.3f,y-r*.3f,r*.22f,p);}
 @Override protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();int plates=selected.isEmpty()?0:selected.iterator().next()+2;float unit=Math.min(w/24f,h/4.2f);
  if(plates==0){for(int i=0;i<apples;i++)apple(c,w*.5f+(i%10-4.5f)*unit*1.8f,h*.35f+(i/10)*unit*1.6f,unit*.60f);return;}
  int per=apples/plates,rem=apples%plates;float width=w/(plates+(rem>0?1:0));
  for(int j=0;j<plates;j++){float cx=width*(j+.5f);fill(0xffaf8ed8);c.drawOval(cx-width*.45f,h*.24f,cx+width*.45f,h*.95f,p);fill(0xfffff5f7);c.drawOval(cx-width*.41f,h*.26f,cx+width*.41f,h*.87f,p);float r=Math.min(h*.115f,width/12f);int columns=(per+1)/2;for(int k=0;k<per;k++)apple(c,cx+(k%columns-(columns-1)/2f)*r*2.1f,h*.42f+(k/columns)*r*2.1f,r);}
  for(int i=0;i<rem;i++)apple(c,w-width/2+i*unit*.5f,h*.6f,unit*.50f);
 }
}
