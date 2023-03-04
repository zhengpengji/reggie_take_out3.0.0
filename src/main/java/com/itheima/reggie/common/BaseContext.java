package com.itheima.reggie.common;
//用于ThreadLocal ，用于保存一次线程内用户的ID
public class BaseContext {
    private static   ThreadLocal<Long> threadLocal =new ThreadLocal<>();
    public static void setCurrentID(Long id){
        threadLocal.set(id);
    };
    public  static Long getCurrentID(){
        return threadLocal.get();
    }

}
