package vn.toanlop3;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Arrays;

/** Versioned local checkpoint. Earned stars are independent of a replay. */
final class SaveState {
    int stage=0, position=0, earned=0, wrong=0;
    boolean solved=false, hint=false;
    String draft="";
    int[] groups=new int[4];
    static int bound(int v,int min,int max){return Math.max(min,Math.min(max,v));}
    int stars(){return Integer.bitCount(earned&255);}
    void next(){
        if(!solved||stage>=Curriculum.COUNT)return;
        stage++;
        position=0;solved=false;hint=false;wrong=0;draft="";Arrays.fill(groups,0);
    }
    void replay(){
        stage=0;position=0;solved=false;hint=false;wrong=0;draft="";Arrays.fill(groups,0);
    }
    void correct(){
        if(stage>=Curriculum.COUNT)return;
        solved=true;earned|=1<<stage;
    }
    JSONObject json() throws JSONException{
        JSONObject j=new JSONObject();
        j.put("stage",stage);j.put("position",position);j.put("earned",earned);
        j.put("wrong",wrong);j.put("solved",solved);j.put("hint",hint);j.put("draft",draft);
        JSONArray a=new JSONArray();for(int v:groups)a.put(v);j.put("groups",a);
        return j;
    }
    static SaveState read(JSONObject j,int lesson){
        SaveState s=new SaveState();
        if(j==null)return s;
        s.stage=bound(j.optInt("stage",0),0,Curriculum.COUNT);
        s.position=bound(j.optInt("position",0),0,4);
        s.earned=j.optInt("earned",0)&255;
        s.wrong=bound(j.optInt("wrong",0),0,1000);
        s.solved=j.optBoolean("solved",false)&&s.stage<Curriculum.COUNT&&s.position==4;
        s.hint=j.optBoolean("hint",false);
        String d=j.optString("draft","");
        s.draft=d.matches("[0-9]{0,4}")?d:"";
        if(s.stage<Curriculum.COUNT){
            Curriculum.Q q=Curriculum.question(lesson,s.stage);
            JSONArray a=j.optJSONArray("groups");int left=q.a;
            if(a!=null&&q.kind==Curriculum.SHARE)
                for(int i=0;i<q.b;i++){s.groups[i]=bound(a.optInt(i,0),0,left);left-=s.groups[i];}
        }else{s.position=0;s.hint=false;s.draft="";}
        return s;
    }
}
