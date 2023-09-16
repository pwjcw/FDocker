package com.pwjcw.utils;

import java.text.SimpleDateFormat;
import java.util.*;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.LoadImageCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.InvocationBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;


public class DockerUtil {
    private static DockerClient dockerClient;

    //获取docker连接
    static {
        // 进行安全认证
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                // 服务器ip
                .withDockerHost("tcp://106.12.141.38:2375")
                .withDockerTlsVerify(true)
                // 压缩包解压的路径
                .withDockerCertPath(DockerUtil.class.getClassLoader().getResource("docker").getPath().substring(1))
                // API版本 可通过在服务器 docker version 命令查看
                .withApiVersion("1.41")
                .build();
        // docker命令执行工厂
        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(60000)
                .withConnectTimeout(60000)
                .withMaxTotalConnections(100)
                .withMaxPerRouteConnections(100);
        DockerClient d = DockerClientBuilder.getInstance(config).withDockerCmdExecFactory(dockerCmdExecFactory).build();
        dockerClient = d;
    }

    public static DockerClient getDockerClient() {
        return dockerClient;
    }

    /**
     * 创建docker容器,传入的端口map参数为{主机端口:容器端口}
     * @param client
     * @param containerName
     * @param imageName
     * @param portMappings
     * @return
     */

    public CreateContainerResponse createContainers(DockerClient client, String containerName, String imageName, Map<Integer, Integer> portMappings) {
        HostConfig hostConfig = new HostConfig();
        Ports portBindings = new Ports();

        for (Map.Entry<Integer, Integer> entry : portMappings.entrySet()) {   //遍历端口数组对象
            Integer withoutPort = entry.getKey();
            Integer interiorPort = entry.getValue();

            ExposedPort exposedPort = ExposedPort.tcp(interiorPort);
            Ports.Binding binding = Ports.Binding.bindPort(withoutPort);

            portBindings.bind(exposedPort, binding);
        }

        hostConfig.withPortBindings(portBindings);

        List<ExposedPort> exposedPorts = new ArrayList<>(portBindings.getBindings().keySet());

        CreateContainerResponse container = client.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withExposedPorts(exposedPorts)
                .exec();

        return container;
    }


    //启动容器
    public void startContainer(DockerClient client, String containerId) {
        client.startContainerCmd(containerId).exec();
    }

    //停止容器
    public void stopContainer(DockerClient client, String containerId) {
        client.stopContainerCmd(containerId).exec();
    }

    //删除容器
    public void removeContainer(DockerClient client, String containerId) {
        client.removeContainerCmd(containerId).exec();
    }

    //获取所有的容器及其详细信息
    public List<Map<String,String>> getAllContainer(DockerClient dockerClient) {
        Long time = System.currentTimeMillis();  //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置时间格式
        List<Map<String,String>> AllList=new ArrayList<>();
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        for (Container c :
                containers) {
            List<LinkedHashMap<String, Integer>> portmap=new ArrayList<>();
            for (int i=0;i<c.getPorts().length/2;i++){        //因为可能有多个端口的存在，所以需要提取出来，减小冗余
                LinkedHashMap<String,Integer> tmp=new LinkedHashMap<>();
                tmp.put("privatePort",c.getPorts()[i].getPrivatePort());
                tmp.put("publicPort",c.getPorts()[i].getPublicPort());
                portmap.add(tmp);
            }
            System.out.println(portmap);
            Map<String, String> Mcontainers = new LinkedHashMap<>();
            Mcontainers.put("ContainerId", c.getId().substring(0, 6));
            Mcontainers.put("ContainName", c.getNames()[0].substring(1));
            Mcontainers.put("Image", c.getImage());
            Mcontainers.put("Status", c.getStatus());
            Mcontainers.put("port", portmap.toString());
            Mcontainers.put("Created", format.format(c.getCreated() * 1000L));  //获取创建时间,并将时间戳转化为时间
            AllList.add(Mcontainers);
        }
        return AllList;
    }

    //读取本地镜像
    public LoadImageCmd loadImage(DockerClient client, String filePath) {
        LoadImageCmd loadImageCmd = null;
        try {
            loadImageCmd = client.loadImageCmd(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return loadImageCmd;
    }

    //拉取远程镜像
    public boolean pullImage(DockerClient client, String imageName) {
        if (!imageName.contains(":")){
            imageName+=":latest";    //如果没有指定版本号，默认拉取最新的镜像,没有此操作会拉取多个可用镜像
        }
        System.out.println(imageName);
        boolean isSuccess = false;
        try {
            isSuccess = client.pullImageCmd(imageName)
                    .start()
                    .awaitCompletion(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            return isSuccess;
        }
    }

    //获取所有的镜像
    public List<Map<String,String>> getImageList(DockerClient dockerClient) {
        List<Image> images = dockerClient.listImagesCmd().exec();
        List<Map<String,String>> imagesList=new ArrayList<>();
        Long time = System.currentTimeMillis();  //获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置时间格式
        for (Image image : images) {
            Map<String,String> tmp=new LinkedHashMap<>();
            tmp.put("ImageTag",image.getRepoTags()[0]);
            tmp.put("ImageId",image.getId().substring(7,15));
            tmp.put("Created", format.format(image.getCreated()*1000L));
            tmp.put("Size", String.valueOf(image.getSize()/1024/1024)+'M');
            imagesList.add(tmp);
        }
        return imagesList;
    }

    //删除镜像
    public boolean deleteImage(String imageId){
        try {
            dockerClient.removeImageCmd(imageId).withForce(true).exec();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    //获取docker的cpu利用率,网络带宽，内存占用等数据
    public Statistics getstats(String dockerId){
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        this.dockerClient.statsCmd(dockerId).exec(callback);
        Statistics res = callback.awaitResult();
        return res;
    }
}
