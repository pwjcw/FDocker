package com.pwjcw.servlet.docker;

import com.github.dockerjava.api.DockerClient;
import com.pwjcw.servlet.base.ModelBaseServlet;
import com.pwjcw.utils.DockerUtil;
import com.pwjcw.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DockerServlet", value = "/docker")
public class DockerServlet extends ModelBaseServlet {
    private static DockerClient dockerClient;
    DockerUtil dockerUtil=new DockerUtil();
    static {
        dockerClient=DockerUtil.getDockerClient();
    }

    /**
     * 查看所有的容器
     * @param request
     * @param response
     */
    public void getnumdocker(HttpServletRequest request,HttpServletResponse response){
        LinkedHashMap<String, Object> resMap = new LinkedHashMap<>();
        List<Map<String, String>> allContainer = null;
        try {
            allContainer = dockerUtil.getAllContainer(dockerClient);
            resMap.put("code",0);
            resMap.put("data", allContainer);
        } catch (Exception e) {
            resMap.put("code",1);
        }
        System.out.println(allContainer);
        JsonUtils.writeResult(response,resMap);
    }

    /**
     * 停止正在运行的容器
     * @param request
     * @param response
     * @throws IOException
     */
    public void stopcontainer(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject json = (JSONObject) JsonUtils.JsonToString(request);
        String containerId = json.getString("containerId");
        Map<String,String> map=new HashMap<>();
        try {
            dockerUtil.stopContainer(dockerClient,containerId);
            map.put("msg","1");
            JsonUtils.writeResult(response,map);
        } catch (Exception e) {
            map.put("msg","0");
            JsonUtils.writeResult(response,map);
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动容器
     * @param request
     * @param response
     * @throws IOException
     */
    public void startcontainer(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject json = (JSONObject) JsonUtils.JsonToString(request);
        String containerId = json.getString("containerId");
        Map<String,String> map=new HashMap<>();
        try {
            dockerUtil.startContainer(dockerClient,containerId);
            map.put("msg","1");
            JsonUtils.writeResult(response,map);
        } catch (Exception e) {
            dockerUtil.deleteImage(containerId);       //如果没有启动成功，则删除该容器
            map.put("msg","0");
            JsonUtils.writeResult(response,map);
            throw new RuntimeException(e);
        }

    }

    /**
     * 删除正在运行的容器
     * @param request
     * @param response
     * @throws IOException
     */
    public void deletecontainer(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject json = (JSONObject) JsonUtils.JsonToString(request);
        String containerId = json.getString("containerId");
        Map<String,String> map=new HashMap<>();
        try {
            dockerUtil.removeContainer(dockerClient,containerId);
            map.put("msg","1");
            JsonUtils.writeResult(response,map);
        } catch (Exception e) {
            map.put("msg","0");
            JsonUtils.writeResult(response,map);
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加容器
     * @param request
     * @param response
     * @throws IOException
     */
    public void addcontainer(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject json = (JSONObject) JsonUtils.JsonToString(request);
        System.out.println(json);
        String containerName = json.getString("containerName");
        String ImageTag = json.getString("ImageTag");
//        JSONArray portMappings = json.getJSONArray("portMappings");
//        System.out.println(portMappings);
        LinkedHashMap<Integer, Integer> port = new LinkedHashMap<>();
        System.out.println((json.length()-2)/2);
//        for (int i=0;i<portMappings.length();i++){
//            JSONObject jsonObject = portMappings.getJSONObject(i);
//            int containerPort = jsonObject.getInt("containerPort");
//            int hostPort = jsonObject.getInt("hostPort");
//            port.put(hostPort,containerPort);
//        }
        for (int i=0;i<(json.length()-2)/2;i++){
            int hostPort = json.getInt(String.format("hostPort[%d]", i));
            int containerPort = json.getInt(String.format("containerPort[%d]", i));
            port.put(hostPort,containerPort);
        }
        System.out.println(port);
        Map<String,String> map=new HashMap<>();
        try {
            String id = dockerUtil.createContainers(dockerClient, containerName, ImageTag, port).getId();
            dockerUtil.startContainer(dockerClient,id);
            map.put("msg","1");
            JsonUtils.writeResult(response,map);
        } catch (Exception e) {
            map.put("msg","0");
            JsonUtils.writeResult(response,map);
            throw new RuntimeException(e);
        }

    }

    /**
     * 调换到容器管理界面
     * @param request
     * @param response
     * @throws IOException
     */
    public void toShowcontainer(HttpServletRequest request,HttpServletResponse response) throws IOException {
        processTemplate("admin/docker/container",request,response);
    }

    /**
     * 跳转到添加容器的界面
     * @param request
     * @param response
     * @throws IOException
     */
    public void toaddcontainer(HttpServletRequest request,HttpServletResponse response) throws IOException {
        processTemplate("admin/docker/newcontainer",request,response);
    }

    /**
     * 跳转到数据可视化界面
     * @param request
     * @param response
     * @throws IOException
     */
    public void tovisualization(HttpServletRequest request,HttpServletResponse response) throws IOException {
        processTemplate("admin/visualization",request,response);
    }
}