package vn.toanlop3;
import org.json.*;
import java.util.*;
/** A checkpoint is indexed by a stable book exercise ID rather than an 8-bit score. */
final class JourneyState {
 int cursor=0, field=0, wrong=0;
 int labPlates=2;
 int liquidA=0,liquidB=0;
 String drawing="";
 String appleLayout="00000000000000000000";
 boolean solved=false, hint=false;
 float player=0;
 String[] draft={"",""};
 final TreeSet<Integer> selected=new TreeSet<>();
 final HashSet<String> done=new HashSet<>();
 final HashSet<String> missed=new HashSet<>();
 final boolean advanced;
 final int lesson;
 JourneyState(int lesson,boolean adv){this.lesson=lesson;advanced=adv;}
 int count(){return advanced?Curriculum.ADVANCED_COUNT:BookBank.count(lesson);}
 BookBank.Q get(int i){return advanced?BookBank.advanced(lesson,i):BookBank.get(lesson,i);}
 BookBank.Q current(){return get(Math.min(cursor,count()-1));}
 int completed(){int n=0;for(int i=0;i<count();i++)if(done.contains(get(i).id()))n++;return n;}
 boolean finished(){return cursor>=count();}
 String input(){if(current().kind==BookBank.Kind.MULTI){StringBuilder b=new StringBuilder();for(int x:selected){if(b.length()>0)b.append(",");b.append(x);}return b.toString();}return current().kind==BookBank.Kind.DIVISION?draft[0]+","+draft[1]:draft[0];}
 void correct(){if(!finished()){done.add(current().id());solved=true;}}
 void fail(){if(!finished()){wrong++;hint=true;missed.add(current().id());}}
 void next(){if(solved&&!finished()){cursor++;resetDraft();if(finished()&&completed()<count()){for(int i=0;i<count();i++)if(!done.contains(get(i).id())){cursor=i;break;}}}}
 void resetDraft(){draft[0]="";draft[1]="";selected.clear();field=wrong=0;solved=hint=false;player=0;labPlates=2;appleLayout="00000000000000000000";liquidA=liquidB=0;drawing="";}
 void replay(){cursor=0;resetDraft();}
 JSONObject json()throws JSONException{
  JSONObject j=new JSONObject();j.put("cursor",cursor);j.put("question",finished()?"END":current().id());j.put("field",field);j.put("wrong",wrong);j.put("solved",solved);j.put("hint",hint);j.put("player",player);j.put("labPlates",labPlates);j.put("liquidA",liquidA);j.put("liquidB",liquidB);j.put("bankRevision",7);j.put("drawing",drawing);j.put("appleLayout",appleLayout);j.put("draft",new JSONArray(Arrays.asList(draft)));j.put("selected",new JSONArray(selected));j.put("done",new JSONArray(done));j.put("missed",new JSONArray(missed));return j;
 }
 static int bound(int x,int a,int b){return Math.max(a,Math.min(b,x));}
 static JourneyState read(int l,boolean adv,JSONObject j){
  JourneyState s=new JourneyState(l,adv);if(j==null)return s;
  String id=j.optString("question","");int requested=bound(j.optInt("cursor",0),0,s.count());
  for(int i=0;i<s.count();i++)if(s.get(i).id().equals(id)){requested=i;break;}
  s.cursor="END".equals(id)?s.count():requested;
  JSONArray completed=j.optJSONArray("done"),errors=j.optJSONArray("missed");
  Set<String> known=new HashSet<>();for(int i=0;i<s.count();i++)known.add(s.get(i).id());
  if(completed!=null)for(int i=0;i<completed.length();i++){String v=completed.optString(i);if(known.contains(v))s.done.add(v);}
  if(errors!=null)for(int i=0;i<errors.length();i++){String v=errors.optString(i);if(known.contains(v))s.missed.add(v);}
  if(!adv&&j.optInt("bankRevision",5)<6){
   String[] changed={"35.1.85.2.1","35.2.86.2.2","34.2.84.4.1","35.1.86.4.1"};
   for(String old:changed)s.done.remove(old);
  }
  if(!adv&&j.optInt("bankRevision",5)<7){for(String old:new String[]{"43.1.109.2.1","43.1.110.4.1","44.1.113.3.2"})s.done.remove(old);}
  if(s.finished()&&s.completed()<s.count()){for(int i=0;i<s.count();i++)if(!s.done.contains(s.get(i).id())){s.cursor=i;return s;}}
  if(s.finished())return s;
  s.field=bound(j.optInt("field"),0,1);s.wrong=bound(j.optInt("wrong"),0,1000);s.hint=j.optBoolean("hint");s.solved=j.optBoolean("solved")&&s.done.contains(s.current().id());double pos=j.optDouble("player",0);s.player=(float)(Double.isFinite(pos)?Math.max(0,Math.min(1,pos)):0);
  JSONArray dr=j.optJSONArray("draft");if(dr!=null)for(int i=0;i<2;i++){String v=dr.optString(i);if(v.matches("[0-9]{0,4}"))s.draft[i]=v;}
  s.labPlates=bound(j.optInt("labPlates",2),2,4);String apples=j.optString("appleLayout","00000000000000000000");if(apples.matches("[0-4]{20}"))s.appleLayout=apples;
  if(s.current().kind==BookBank.Kind.POUR){s.liquidA=bound(j.optInt("liquidA"),0,s.current().a);s.liquidB=bound(j.optInt("liquidB"),0,s.current().b);}
  if(s.current().kind==BookBank.Kind.DRAW){String d=j.optString("drawing","");if(ShapePuzzle.validDraft(d))s.drawing=d;}
  JSONArray sel=j.optJSONArray("selected");if(sel!=null)for(int i=0;i<sel.length();i++){int v=sel.optInt(i,-1);if(v>=0&&v<s.current().options.length)s.selected.add(v);}
  return s;
 }
 /** Only the unchanged advanced bank can be migrated question-for-question. Old core bank is archived, not falsely mapped to book exercises. */
 static JourneyState migrateAdvanced(int l,JSONObject j){
  JourneyState s=new JourneyState(l,true);if(j==null)return s;
  s.cursor=bound(j.optInt("stage"),0,s.count());int earned=j.optInt("earned");
  for(int i=0;i<s.count();i++)if((earned&(1<<i))!=0)s.done.add(s.get(i).id());
  if(!s.finished()){s.solved=j.optBoolean("solved")&&s.done.contains(s.current().id());s.hint=j.optBoolean("hint");s.wrong=j.optInt("wrong");String d=j.optString("draft","");if(d.matches("[0-9]{0,4}"))s.draft[0]=d;s.player=j.optInt("position",0)/4f;}
  return s;
 }
}
