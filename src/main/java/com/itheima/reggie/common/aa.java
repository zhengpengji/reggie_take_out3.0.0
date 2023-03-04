package com.itheima.reggie.common;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

public class aa {
    public static void main(String[] args) {
        int[][] arr  ={{1,1,0},{1,2,0},{1,3,0},{2,1,0},{2,2,0},{2,3,0},{3,1,0},{3,2,0},{3,3,0}};
        Random random= new Random();
        int count = 0;
        int min = 100;
   int [ ] a ={0,6,2,5,7};
        for (int i = 0; i < a.length; i++) {
            extracted(arr,a[i]);
            for (int k = 0; k < arr.length; k++) {
                System.out.print(Arrays.toString(arr[k]));
            }
            System.out.println();
        }
        for (int k = 0; k < arr.length; k++) {
               System.out.print(Arrays.toString(arr[k]));
           }
//        for (int I = 0; I < 10000; I++) {
//            int r = random.nextInt(0, 9);
//            extracted(arr, r);
//            count++;
//            System.out.println();
//            System.out.println(r);
//            for (int k = 0; k < arr.length; k++) {
//                System.out.print(Arrays.toString(arr[k]));
//            }
//            if (arr[0][2]==0){
//                boolean a = true;
//                for (int L = 1; L < arr.length; L++) {
//                    if (arr[L][2]==1){
//                        a=false;
//                    }
//                }
//                if (a){
//                    System.out.println("**********************************************");
//                    for (int i1 = 0; i1 < arr.length; i1++) {
//                          arr[i1][2]=0;
//                     }
//                    System.out.println( "count"+count);
//                    min=Math.min(min,count);
//                    System.out.println("min"+min);
//                    count=0;
//                }
            }
//            if (arr[0][2]==1){
//                for (int i = 1; i < arr.length; i++) {
//                    if (arr[i][2]==1) {count++; break;}
//                    if (arr[i][2]==0&&i==arr.length-1){
//                        min= Math.min(min,count);
//                        count=0;
//                        System.out.println(min);
//                        for (int i1 = 0; i1 < arr.length; i1++) {
//                            arr[i1][2]=0;
//                      }
//                    }
////                  if(i==arr.length-1){min= Math.min(min,count);
////                        System.out.println(min);
////                        count=0;
////
////                    }
//                }
//           }
  //      }

  //  }

    private static void extracted(int[][] arr, int r) {
        int[] ints = arr[r];
        int one = ints[0];
        int two = ints[1];
        for (int i = 0; i < 9; i++) {
               if (arr[i][0]==one){
                   if (arr[i][2]==1){

                       arr[i][2]=0;
                   }else arr[i][2]=1;
                   continue;
               }
               if (arr[i][1]==two){
                if (arr[i][2]==1){ 
                    arr[i][2]=0;
                }else arr[i][2]=1;
               }
        }
    }
}
