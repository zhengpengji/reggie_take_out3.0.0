package com.itheima.reggie.filter;
//用户是否已经登入

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpResponse;

import static org.apache.ibatis.io.SerialFilterChecker.check;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器
   public  static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse =(HttpServletResponse) servletResponse;
        String requestURI = httpServletRequest.getRequestURI();
        log.info("接到请求：{}",requestURI);
        String[] urls={"/employee/login","/employee/logout","/backend/**","/front/**","/common/**","/user/sendMsg","/user/login"};
        //看请求路径是否符合urls，不符合就返回false
        boolean check=check(urls,requestURI);

        if (check){//如果不需要处理就放行
            log.info("本次请求是开放界面："+requestURI);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //用户未登入则拦截
        if(httpServletRequest.getSession().getAttribute("employee")!=null){
            Long empId = (Long) httpServletRequest.getSession().getAttribute("employee");
            log.info("用户已登入，用户ID为："+empId);
            BaseContext.setCurrentID(empId);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        if(httpServletRequest.getSession().getAttribute("user")!=null){
            Long Id = (Long) httpServletRequest.getSession().getAttribute("user");
            log.info("用户已登入，用户ID为："+Id);
            BaseContext.setCurrentID(Id);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        log.info("用户未登入拦截到请求：{}",requestURI);
        //json返回R.
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
           if (PATH_MATCHER.match(url,requestURI)){
               return true;
           }
        }
        return false;
    }
}
