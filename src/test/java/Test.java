import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Statistics;
import com.pwjcw.entity.User;
import com.pwjcw.mapper.UserMapper;
import com.pwjcw.utils.DockerUtil;
import com.pwjcw.utils.MyBatisUtil;
import com.pwjcw.utils.PropertiesUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Test {
    public DockerUtil dockerUtil = new DockerUtil();
    DockerClient dockerClient = DockerUtil.getDockerClient();

    @org.junit.Test
    public void getAllContainer() {
        List<Map<String, String>> allContainer = dockerUtil.getAllContainer(dockerClient);
        System.out.println(allContainer);
    }

    @org.junit.Test
    public void getAllImage() {
        List<Map<String, String>> imageList = dockerUtil.getImageList(dockerClient);
        System.out.println(imageList);
    }

    @org.junit.Test
    public void pullImage() {
        boolean b = dockerUtil.pullImage(dockerClient, "nginx");
        System.out.println(b);
    }

    @org.junit.Test
    public void getstat() throws InterruptedException {
        Statistics fb78 = dockerUtil.getstats("fb78");
        System.out.println(fb78.getRead());
    }

    @org.junit.Test
    public void getUserConfig() throws IOException {
        Properties properties = new Properties();
        try {
            URL resource = this.getClass().getClassLoader().getResource("user.config");
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("user.config");
            properties.load(resourceAsStream);

            String username = properties.getProperty("username");
            String password = properties.getProperty("passwd");
            properties.setProperty("username","a6666666");

            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            OutputStream outputStream = new FileOutputStream(String.valueOf(resource.toURI()).substring(6));
            System.out.println(this.getClass().getClassLoader().getResource("user.config").getPath());
            properties.store(outputStream, "保存配置文件");
            resourceAsStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    @org.junit.Test
    public void setconfig() throws IOException {
        Properties properties = new Properties();
        FileOutputStream fileOutputStream = new FileOutputStream(this.getClass().getClassLoader().getResource("user.config").getPath().substring(1));
        properties.setProperty("jdbc.driver", "com.mysql.jdbc.Driver");
        properties.setProperty("jdbc.url","jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8" );
        properties.setProperty("jdbc.username", "root");
        properties.setProperty("jdbc.password", "123456");
        properties.store(fileOutputStream,"1111");
    }
    @org.junit.Test
    public void testpropertiesUtil() throws FileNotFoundException {
        PropertiesUtil propertiesUtil=new PropertiesUtil();
        propertiesUtil.WriteConfigWiteJdbc(6,"127.0.0.1","5555","admin","root");
    }
    @org.junit.Test
    public void testtmp(){
        System.out.println(String.format("jdbc:mysql://%s:%s/FDocker", "5555", "3333"));
    }

    @org.junit.Test
    public void testLogin(){
        UserMapper mapper = MyBatisUtil.getSession().getMapper(UserMapper.class);
        User admin = mapper.Login("admin");
        System.out.println(admin);
    }
}