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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 应用程序入口
 */
@Slf4j
@EnableAsync
@SpringBootApplication
@EnableScheduling
@EnableCaching
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
        
        // 获取服务器IP地址
        String serverIp = "";
        try {
            Process process = new ProcessBuilder("curl", "-s", "ifconfig.me")
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                serverIp = reader.readLine();
            }
        } catch (IOException e) {
            serverIp = "<your-server-ip>";
            log.warn("无法获取服务器IP地址: {}", e.getMessage());
        }
       
        // 输出启动信息
        log.info("----------------------------------------");
        log.info("应用启动成功!");
        log.info("当前环境: {}", currentEnv);
        log.info("绑定地址: {}", address);
        if (currentEnv.equals("[dev]")) {
            String localIp = InetAddress.getLocalHost().getHostAddress();
            log.info("服务地址: http://{}:{}{}", address.equals("0.0.0.0") ? localIp : address, port, contextPath);
            // 局域网地址
            // log.info("局域网地址: http://{}:{}{}", address.equals("0.0.0.0") ? serverIp : address, port, contextPath);
        } else {
            // 外网地址
            log.info("服务地址: http://{}:{}{}", address.equals("0.0.0.0") ? serverIp : address, port, contextPath);
        }
        log.info("日志路径: {}", environment.getProperty("logging.file.name"));
        log.info("----------------------------------------");
    }
} 