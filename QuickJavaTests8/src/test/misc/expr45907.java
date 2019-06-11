package test.misc;

import java.io.PrintStream;

public class expr45907 {
  static double meth(){
      return 3.2;
  }
  public static void main(String args[]) {
      double Arr1[] = {2.};
      System.exit(run(System.out, Arr1)); 
  } 
 
  public static int run(PrintStream out, double Arr1[]) { 
      double var1,var2;

      var1=Arr1[0];
      if ( var1 == 1.0d ) {
          out.println(var1);
          return 104;/* STATUS_FAILED */
      }
      var2 = 1332.0d;
      if ( (var2/=var1) != 666.0d ) {
          return 105;/* STATUS_FAILED */
      }
      return 0;/* STATUS_PASSED */
  }
}

