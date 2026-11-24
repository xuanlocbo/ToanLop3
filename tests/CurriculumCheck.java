import vn.toanlop3.Curriculum;
import java.util.*;
public class CurriculumCheck {
    public static void main(String[] args){
        int total=0;
        if(Curriculum.TITLES.length!=20||Curriculum.PAGES.length!=21)throw new AssertionError();
        for(int n=25;n<=44;n++)for(int i=0;i<Curriculum.COUNT;i++){
            Curriculum.Q q=Curriculum.question(n,i);
            if(q.text.isEmpty()||q.hint.isEmpty()||q.explain.isEmpty()||!q.check(q.answer)||q.check("INVALID"))throw new AssertionError(n+":"+i);
            if(q.kind==Curriculum.CHOICE&&(new HashSet<>(Arrays.asList(q.opts)).size()!=3||!Arrays.asList(q.opts).contains(q.answer)))throw new AssertionError("choices");
            if(q.kind==Curriculum.SHARE){
                int[] g=new int[q.b];Arrays.fill(g,q.a/q.b);
                if(!q.shareOK(g)||q.a%q.b>=q.b)throw new AssertionError("share");
                g[0]++;if(q.shareOK(g))throw new AssertionError("invalid share");
            }
            if(q.kind==Curriculum.RULER&&(q.a<=0||q.a>50||!q.answer.equals(""+q.a)))throw new AssertionError("ruler");
            if(q.kind==Curriculum.THERMO&&(q.a<0||q.a>50||!q.answer.equals(""+q.a)))throw new AssertionError("thermo");
            if(!Arrays.equals(q.opts,Curriculum.question(n,i).opts))throw new AssertionError("not deterministic");
            total++;
        }
        System.out.println("Checked "+total+" questions");
    }
}
