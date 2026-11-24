import vn.toanlop3.WaterPuzzle;
import java.util.Arrays;
public final class WaterPuzzleCheck{
 static void check(int ca,int cb,int target,int...actions){int[] v={0,0};for(int i:actions)v=WaterPuzzle.apply(ca,cb,v[0],v[1],i);if(!WaterPuzzle.solved(v[0],v[1],target))throw new AssertionError(Arrays.toString(v));}
 public static void main(String[] args){
  check(250,100,50,0,4,3,4);
  check(250,400,100,0,4,0,4);
  for(int a=0;a<=250;a+=50)for(int b=0;b<=400;b+=50)for(int op=0;op<6;op++){int[] v=WaterPuzzle.apply(250,400,a,b,op);if(v[0]<0||v[0]>250||v[1]<0||v[1]>400)throw new AssertionError();if(op>=4&&v[0]+v[1]!=a+b)throw new AssertionError("conservation");}
  System.out.println("Water puzzle solutions and conservation verified.");
 }
}
