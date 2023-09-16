package com.pwjcw.utils;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

public class MyBatisUtil {

    private static SqlSessionFactory sessionFactory;

    static {
        // 加载 MyBatis 配置文件并构建 SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = MyBatisUtil.class.getClassLoader().getResourceAsStream(resource);
        sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public static SqlSession getSession() {
        return sessionFactory.openSession(true);
    }

    /**
     * 查询单个对象
     * @param mapper Mapper 接口
     * @param methodName 查询方法名
     * @param args 查询参数
     * @param <T> 查询结果类型
     * @return 查询结果
     */
    public static <T> T selectOne(Class mapper, String methodName, Object... args) {
        try (SqlSession session = getSession()) {
            Object object = session.getMapper(mapper).getClass();
            return session.selectOne(mapper.getName() + "." + methodName, args);
        }catch (Exception e){
                throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 查询多个对象
     * @param mapper Mapper 接口
     * @param methodName 查询方法名
     * @param args 查询参数
     * @param <T> 查询结果类型
     * @return 查询结果列表
     */
    public static <T> List<T> selectList(Class mapper, String methodName, Object... args) {
        try (SqlSession session = getSession()) {
            return session.selectList(mapper.getName() + "." + methodName, args);
        }
    }

/**
 * 插入对象
 * @param obj 要插入的对象
 * @param <T> 对象类型
 * @return 插入结果
 */
public static <T> int insert(T obj) {
    try (SqlSession session = getSession()) {
        return session.insert(obj.getClass().getName() + ".insert", obj);
    }
}

    /**
     * 更新对象
     * @param obj 要更新的对象
     * @param <T> 对象类型
     * @return 更新结果
     */
    public static <T> int update(T obj) {
        try (SqlSession session = getSession()) {
            return session.update(obj.getClass().getName() + ".update", obj);
        }
    }

    /**
     * 删除对象
     * @param obj 要删除的对象
     * @param <T> 对象类型
     * @return 删除结果
     */
    public static <T> int delete(T obj) {
        try (SqlSession session = getSession()) {
            return session.delete(obj.getClass().getName() + ".delete", obj);
        }
    }

    /**
     * 自定义 SQL 查询
     * @param sqlBuilder SQL 语句生成器
     * @return 查询结果列表
     */
    public static List<Object> customSelect(SQL sqlBuilder) {
        try (SqlSession session = getSession()) {
            return session.selectList(sqlBuilder.toString());
        }
    }

    /**
     * 自定义 SQL 更新
     * @param sqlBuilder SQL 语句生成器
     * @return 更新结果
     */
    public static int customUpdate(SQL sqlBuilder) {
        try (SqlSession session = getSession()) {
            return session.update(sqlBuilder.toString());
        }
    }
    public static void clear(SqlSession session){
        session.commit();
        session.close();
    }
}