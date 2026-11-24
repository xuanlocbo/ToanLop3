import vn.toanlop3.BookBank;
import java.util.*;
public final class BookBankCheck {
 public static void main(String[] args){
  int total=0,practice=0,bonus=0;Set<String> ids=new HashSet<>();
  int[] pages={64,66,69,71,74,76,78,80,81,82,85,88,90,94,98,100,102,106,109,113,116};
  for(int l=25;l<=44;l++){
   List<BookBank.Q> bank=BookBank.lesson(l);if(bank.isEmpty())throw new AssertionError("Empty lesson "+l);
   int lastPage=0,lastPeriod=0,lastExercise=0;
   for(BookBank.Q q:bank){
    if(q.lesson!=l||q.page<pages[l-25]||q.page>=pages[l-24]||!ids.add(q.id()))throw new AssertionError(q.id());
    if(q.page<lastPage||q.period<lastPeriod||(q.period==lastPeriod&&q.exercise<lastExercise))throw new AssertionError("Order "+q.id());
    lastPage=q.page;lastPeriod=q.period;lastExercise=q.exercise;
    if(q.prompt.isEmpty()||q.hint.isEmpty()||q.explain.isEmpty()||!q.check(q.answer)||q.check("INVALID"))throw new AssertionError(q.id());
    if(q.kind==BookBank.Kind.DIVISION&&!q.answer.equals(q.a/q.b+","+(q.a%q.b)))throw new AssertionError(q.id());
    if(q.kind==BookBank.Kind.MULTIPLY&&!q.answer.equals(""+q.a*q.b))throw new AssertionError(q.id());
    if((q.kind==BookBank.Kind.CHOICE||q.kind==BookBank.Kind.GEOMETRY)&&!Arrays.asList(q.options).contains(q.answer))throw new AssertionError(q.id());
    if(q.kind==BookBank.Kind.MULTI){Set<String> selected=new HashSet<>();for(String v:q.answer.split(",")){int i=Integer.parseInt(v);if(i<0||i>=q.options.length||!selected.add(v))throw new AssertionError(q.id());}}
    if(q.kind==BookBank.Kind.BALANCE&&!q.answer.equals(""+(q.a+q.b)))throw new AssertionError(q.id());
    if(q.kind==BookBank.Kind.PRACTICE)practice++;
    total++;
   }
   for(int i=0;i<2;i++){BookBank.Q q=BookBank.advanced(l,i);if(!q.check(q.answer)||q.prompt.isEmpty())throw new AssertionError("Advanced "+l);bonus++;}
  }
  if(total!=488||bonus!=40||practice!=5)throw new AssertionError("Unexpected bank size");
  if(!BookBank.get(25,0).answer.equals("5,0")||BookBank.get(25,0).page!=64)throw new AssertionError("VBT p64 first exercise");
  Map<String,String> sourceChecks=new HashMap<>();
  sourceChecks.put("35.1.85.2.1","500");sourceChecks.put("34.2.84.4.1","50");sourceChecks.put("35.1.86.4.1","100");
  sourceChecks.put("34.2.83.1.3","38");sourceChecks.put("35.2.86.2.1","38");sourceChecks.put("43.1.109.1.1","2");sourceChecks.put("43.1.109.1.7","3");sourceChecks.put("43.1.109.1.8","11");sourceChecks.put("44.2.115.3.3","600");
  for(int l=25;l<=44;l++)for(BookBank.Q q:BookBank.lesson(l))if(sourceChecks.containsKey(q.id())){if(!q.answer.equals(sourceChecks.remove(q.id())))throw new AssertionError("Source "+q.id());}
  if(!sourceChecks.isEmpty())throw new AssertionError("Missing corrected source tasks "+sourceChecks);
  System.out.println("Verified "+total+" book-indexed tasks ("+practice+" practical) and "+bonus+" optional advanced tasks.");
 }
}
