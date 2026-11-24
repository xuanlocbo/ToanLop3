package vn.toanlop3;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;
import java.util.*;
import org.json.*;
/** No external test dependency: exercise every question layout and state on the Android runtime. */
public final class QaRunner extends Instrumentation{
 private int layouts=0, states=0;
 private Throwable failure;
 @Override public void onCreate(Bundle a){super.onCreate(a);start();}
 private static void require(boolean v,String message){if(!v)throw new AssertionError(message);}
 private static final MathAdventureView.Host HOST=new MathAdventureView.Host(){
  public void save(boolean sync){}public void next(){}public void back(){}public void guide(){}public void sound(int n){}public void practice(Runnable c){c.run();}
 };
 private void measure(View v,int w,int h,String id){
  v.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));v.layout(0,0,w,h);
  if(v instanceof MathAdventureView){MathAdventureView box=(MathAdventureView)v;List<Rect> clickable=new ArrayList<>();
   for(int i=0;i<box.getChildCount();i++){View ch=box.getChildAt(i);Rect r=new Rect(ch.getLeft(),ch.getTop(),ch.getRight(),ch.getBottom());require(r.width()>0&&r.height()>0&&r.left>=0&&r.top>=0&&r.right<=w&&r.bottom<=h,"Control outside display "+id+" "+r);if(ch.isClickable()){for(Rect old:clickable)require(!Rect.intersects(old,r),"Overlapping controls "+id+" "+old+" / "+r);clickable.add(r);}}
  }layouts++;
 }
 private void state(int l,boolean adv,int index)throws JSONException{
  JourneyState s=new JourneyState(l,adv);s.cursor=index;BookBank.Q q=s.current();s.player=.47f;s.hint=true;s.wrong=1;s.draft[0]="5";s.draft[1]="2";
  if(q.kind==BookBank.Kind.MULTI)s.selected.add(0);
  if(q.kind==BookBank.Kind.POUR){s.liquidA=q.a/2;s.liquidB=q.b;}
  if(q.kind==BookBank.Kind.DRAW)s.drawing="1,1;2,2;";
  JourneyState r=JourneyState.read(l,adv,s.json());
  require(r.current().id().equals(q.id())&&r.cursor==index&&r.hint&&r.wrong==1&&r.draft[0].equals("5")&&Math.abs(r.player-.47)<.001,"Restore "+q.id());
  if(q.kind==BookBank.Kind.MULTI)require(r.selected.equals(s.selected),"Multi restore");
  if(q.kind==BookBank.Kind.POUR)require(r.liquidA==s.liquidA&&r.liquidB==s.liquidB,"Water restore");
  if(q.kind==BookBank.Kind.DRAW)require(r.drawing.equals(s.drawing),"Drawing restore");
  s.correct();r=JourneyState.read(l,adv,s.json());require(r.solved&&r.done.contains(q.id()),"Solved "+q.id());states++;
 }
 private void migration()throws JSONException{
  JourneyState s=new JourneyState(43,false);for(int i=0;i<s.count();i++)s.done.add(s.get(i).id());s.cursor=s.count();
  JSONObject old=s.json();old.put("bankRevision",6);JourneyState r=JourneyState.read(43,false,old);
  require(!r.finished()&&!r.done.contains("43.1.109.2.1")&&!r.done.contains("43.1.110.4.1"),"Revised questions must be replayed");
 }
 @Override public void onStart(){Bundle result=new Bundle();Activity a=null;
  try{
   Intent in=new Intent(getTargetContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);a=startActivitySync(in);waitForIdleSync();final Activity host=a;
   for(int l=25;l<=44;l++){final int n=l;runOnMainSync(()->{try{
    for(boolean adv:new boolean[]{false,true}){
     JourneyState sample=new JourneyState(n,adv);
     for(int i=0;i<sample.count();i++){
      JourneyState s=new JourneyState(n,adv);s.cursor=i;state(n,adv,i);
      MathAdventureView view=new MathAdventureView(host,s,n,adv,false,HOST);
      measure(view,1280,720,s.current().id());measure(view,1920,1080,s.current().id());
      s.correct();MathAdventureView done=new MathAdventureView(host,s,n,adv,false,HOST);measure(done,1280,720,s.current().id()+" solved");
     }
    }
   }catch(Throwable t){failure=t;}});if(failure!=null)throw new RuntimeException("Lesson "+n,failure);}
   migration();require(states==533&&layouts==1599,"Unexpected sweep size "+states+"/"+layouts);
   result.putString("stream","\nQA_SWEEP_PASS "+states+" task states, "+layouts+" layouts across all 20 lessons and enrichment.\n");finish(Activity.RESULT_OK,result);
  }catch(Throwable t){result.putString("stream","\nQA_SWEEP_FAIL "+android.util.Log.getStackTraceString(t));finish(Activity.RESULT_CANCELED,result);}
 }
}
