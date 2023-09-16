package com.pwjcw.servlet.docker;

import com.github.dockerjava.api.DockerClient;
import com.pwjcw.servlet.base.ModelBaseServlet;
import com.pwjcw.utils.DockerUtil;
import com.pwjcw.utils.JsonUtils;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ImageServlet", value = "/images")
public class ImageServlet extends ModelBaseServlet {
    private DockerUtil dockerUtil = new DockerUtil();
    private DockerClient dockerClient = DockerUtil.getDockerClient();

    /**
     * 获取所有的镜像
     *
     * @param request
     * @param response
     */

    public void getAllimage(HttpServletRequest request, HttpServletResponse response) {
        List<Map<String, String>> imageList = dockerUtil.getImageList(dockerClient);
        LinkedHashMap<String, Object> resMap = new LinkedHashMap<>();
        resMap.put("code", 0);
        resMap.put("data", imageList);
        System.out.println(imageList);
        JsonUtils.writeResult(response, resMap);
    }

    /**
     * 调换到查看镜像的界面
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void toimagepage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processTemplate("admin/images/images", request, response);
    }

    /**
     * 拉取镜像
     * @param request
     * @param response
     * @throws IOException
     */
    public void pullImage(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject json = (JSONObject) JsonUtils.JsonToString(request);
        String ImageName = json.getString("ImageName");
        dockerUtil.pullImage(dockerClient,ImageName);
        Map<String,String> map=new HashMap<>();
        map.put("msg","test");
        JsonUtils.writeResult(response,map);
    }

    /**
     * 删除镜像
     * @param request
     * @param response
     * @throws IOException
     */
    public void deleteImage(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject json = (JSONObject) JsonUtils.JsonToString(request);
        String imageId = json.getString("imageId");
        Map<String,String> map=new HashMap<>();
        boolean b = dockerUtil.deleteImage(imageId);
        if (b){
            map.put("msg","1");
            JsonUtils.writeResult(response,map);
        }else {
            map.put("msg","0");
            JsonUtils.writeResult(response,map);
        }
    }
}