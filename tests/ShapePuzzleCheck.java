import vn.toanlop3.ShapePuzzle;
public class ShapePuzzleCheck{
 public static void main(String[] args){
  if(!ShapePuzzle.matches(0,"1,5;3,1;7,5;1,5;")||!ShapePuzzle.matches(0,"7,5;3,1;1,5;7,5;"))throw new AssertionError("triangle");
  if(!ShapePuzzle.matches(1,"1,1;5,1;5,5;1,5;1,1;"))throw new AssertionError("square");
  if(!ShapePuzzle.matches(2,"2,1;5,1;5,5;2,5;2,1;"))throw new AssertionError("rectangle");
  if(ShapePuzzle.matches(1,"1,1;5,5;5,1;1,5;1,1;")||ShapePuzzle.matches(2,"1,1;5,1;5,5;1,5;1,1;"))throw new AssertionError("invalid shape");
  if(ShapePuzzle.validDraft("9,0;")||ShapePuzzle.validDraft("1,7;"))throw new AssertionError("bad draft");
  System.out.println("Shape drawing verified: dimensions, closure, translation, traversal and invalid shapes.");
 }
}
