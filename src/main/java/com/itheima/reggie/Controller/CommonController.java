package com.itheima.reggie.Controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String sufferName = originalFilename.substring(originalFilename.lastIndexOf(".")); //截取尾戳
        //UUID生成name 防止名字一样覆盖
        String newName = UUID.randomUUID().toString() + sufferName;
        //防止文件目录不存在
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File((basePath+newName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(newName);
    }
    @GetMapping("/download")
    public  void download(String name, HttpServletResponse Response){
        try {
            //输入流读取对象
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            ServletOutputStream servletOutputStream =Response.getOutputStream();
            Response.setContentType("image/jpeg");
            int len ;
            byte[] bytes = new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
               servletOutputStream.write(bytes,0,len);
               servletOutputStream.flush();
            }
            servletOutputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }



}
