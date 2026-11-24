package vn.toanlop3;
import java.util.*;
/** The workbook asks for a translated copy of the same orientation and dimensions. */
public final class ShapePuzzle{
 private ShapePuzzle(){}
 public static int[][] pattern(int shape){switch(shape){case 0:return new int[][]{{0,4},{2,0},{6,4}};case 1:return new int[][]{{0,0},{4,0},{4,4},{0,4}};case 2:return new int[][]{{0,0},{3,0},{3,4},{0,4}};default:throw new IllegalArgumentException();}}
 public static boolean validDraft(String d){return d!=null&&d.matches("([0-8],[0-6];){0,5}");}
 public static List<int[]> parse(String d){List<int[]> a=new ArrayList<>();if(!validDraft(d))return a;for(String v:d.split(";")){if(v.isEmpty())continue;a.add(new int[]{v.charAt(0)-'0',v.charAt(2)-'0'});}return a;}
 public static boolean matches(int shape,String drawing){List<int[]> p=parse(drawing);int[][] target=pattern(shape);int n=target.length;if(p.size()!=n+1||!Arrays.equals(p.get(0),p.get(n)))return false;
  int[][] actual=p.subList(0,n).toArray(new int[n][]);int ax=9,ay=7;for(int[] v:actual){ax=Math.min(ax,v[0]);ay=Math.min(ay,v[1]);}
  for(int shift=0;shift<n;shift++)for(int dir:new int[]{1,-1}){boolean ok=true;for(int i=0;i<n;i++){int[] t=target[Math.floorMod(shift+dir*i,n)];if(actual[i][0]-ax!=t[0]||actual[i][1]-ay!=t[1]){ok=false;break;}}if(ok)return true;}return false;
 }
}
