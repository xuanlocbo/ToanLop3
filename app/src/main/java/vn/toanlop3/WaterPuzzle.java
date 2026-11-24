package vn.toanlop3;
/** Exact integer millilitre model: no arbitrary filling by reading a target value. */
public final class WaterPuzzle {
 private WaterPuzzle(){}
 public static int[] apply(int capA,int capB,int a,int b,int action){
  if(capA<=0||capB<=0||a<0||a>capA||b<0||b>capB)throw new IllegalArgumentException("capacity");
  switch(action){
   case 0:a=capA;break;case 1:b=capB;break;case 2:a=0;break;case 3:b=0;break;
   case 4:{int v=Math.min(a,capB-b);a-=v;b+=v;break;}
   case 5:{int v=Math.min(b,capA-a);b-=v;a+=v;break;}
   default:throw new IllegalArgumentException("action");
  }return new int[]{a,b};
 }
 public static boolean solved(int a,int b,int target){return target>0&&(a==target||b==target);}
}
