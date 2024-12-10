package com.example.scale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 应用程序入口
 */
@Slf4j
@EnableAsync
@SpringBootApplication
public class ScaleApplication implements CommandLineRunner {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(ScaleApplication.class, args);
    }

    @Override
    public void run(String... args) throws UnknownHostException {
        // 获取当前激活的环境
        String[] activeProfiles = environment.getActiveProfiles();
        String currentEnv = activeProfiles.length > 0 ? Arrays.toString(activeProfiles) : "default";
        
        // 获取应用端口和地址
        String port = environment.getProperty("server.port", "8080");
        String address = environment.getProperty("server.address", "0.0.0.0");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        
        // 输出启动信息
        log.info("----------------------------------------");
        log.info("应用启动成功!");
        log.info("当前环境: {}", currentEnv);
        log.info("绑定地址: {}", address);
        log.info("服务地址: http://{}:{}{}", address.equals("0.0.0.0") ? InetAddress.getLocalHost().getHostAddress() : address, port, contextPath);
        log.info("日志路径: {}", environment.getProperty("logging.file.name"));
        log.info("----------------------------------------");
    }
} 