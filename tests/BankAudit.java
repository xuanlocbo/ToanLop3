import vn.toanlop3.BookBank;
import java.util.*;
import java.util.regex.*;
/** Recalculate direct numeric expressions independently of the stored answer and verify that
 * every task is reachable by the game's available response controls. */
public final class BankAudit{
 static final Pattern DIRECT=Pattern.compile("^([0-9\\s+×:−()\\-]+)\\s*=\\s*\\?(?:\\s+\\S+)?$");
 static class Expr{
  String s;int i;Expr(String s){this.s=s.replaceAll("\\s+","");}
  int read(){int n=sum();if(i!=s.length())throw new IllegalArgumentException(s);return n;}
  int atom(){if(i<s.length()&&s.charAt(i)=='('){i++;int n=sum();if(i>=s.length()||s.charAt(i++)!=')')throw new IllegalArgumentException(s);return n;}int a=i;while(i<s.length()&&Character.isDigit(s.charAt(i)))i++;return Integer.parseInt(s.substring(a,i));}
  int prod(){int n=atom();while(i<s.length()&&(s.charAt(i)=='×'||s.charAt(i)==':')){char c=s.charAt(i++);int m=atom();if(c=='×')n*=m;else{if(m==0||n%m!=0)throw new IllegalArgumentException(s);n/=m;}}return n;}
  int sum(){int n=prod();while(i<s.length()&&(s.charAt(i)=='+'||s.charAt(i)=='−'||s.charAt(i)=='-')){char c=s.charAt(i++);int m=prod();n=c=='+'?n+m:n-m;}return n;}
 }
 static int expressions=0, total=0;
 static void audit(BookBank.Q q){
  total++;
  switch(q.kind){
   case NUMBER:case MULTIPLY:case RULER:case SCALE:case BALANCE:case CUP:case THERMO:case CUPS:case CLINICAL:case POUR:
    if(!q.answer.matches("[0-9]{1,4}"))throw new AssertionError("Cannot enter "+q.id()+" "+q.answer);break;
   case CHOICE:case GEOMETRY:case MULTI:
    if(q.options.length==0||q.options.length>9||new HashSet<>(Arrays.asList(q.options)).size()!=q.options.length)throw new AssertionError("Options "+q.id());break;
   case DIVISION:
    if(q.b<=0||!q.answer.matches("[0-9]{1,4},[0-9]{1,4}"))throw new AssertionError(q.id());break;
   case DRAW:case PRACTICE:
    if(!q.answer.equals("done"))throw new AssertionError(q.id());break;
  }
  if(q.kind==BookBank.Kind.NUMBER){Matcher m=DIRECT.matcher(q.prompt);if(m.matches()){int v=new Expr(m.group(1)).read();if(v!=Integer.parseInt(q.answer))throw new AssertionError("Arithmetic "+q.id()+" "+q.prompt+" expected "+v+" actual "+q.answer);expressions++;}}
 }
 public static void main(String[] args){for(int l=25;l<=44;l++){for(BookBank.Q q:BookBank.lesson(l))audit(q);for(int i=0;i<2;i++)audit(BookBank.advanced(l,i));}if(total!=533)throw new AssertionError(total);System.out.println("Bank audit: "+total+" accessible tasks; "+expressions+" independent direct expression calculations.");}
}
