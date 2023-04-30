package com.Hao.reggie.controller;

import com.Hao.reggie.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class UpDownloadController {
    @Value("${reggie.path}")
    private String path;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file)throws IOException {

        String originalFilename=file.getOriginalFilename();
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename= UUID.randomUUID().toString()+suffix;

        //创建目录对象
        File dir=new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }


        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会自动删除
        file.transferTo(new File(path+filename));
        return  Result.success(filename);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     * @return
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException{
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream=new FileInputStream(new File(path+name));
        //输出流，通过输出流将文件协会浏览器，在浏览器展示图片。
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");

        int len=0;
        byte[] bytes=new byte[1024];
        while((len=fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();
    }
}
