package com.example.scale.service;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * 评分计算服务
 * 提供评分计算和脚本缓存功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComputeService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    /** 远程脚本URL基础地址 */
    private static final String SCRIPT_URL = 
        "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/scriptes";
    
    /** JavaScript引擎例 */
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

    /**
     * 处理评分请求
     */
    @Async
    public CompletableFuture<Response> processItems(ComputeRequest request) {
        log.info("收到请求，ID: {}", request.getId());
    
        try {
            String scriptContent = getScript(request.getId());
            // 获取 base64 编码的数据
            String base64Data = request.getBuffer();
            
            // 解码 base64 数据
            byte[] buffer = Base64.getDecoder().decode(base64Data);
            
            // * 解压
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            String itemsJson = new String(gzipInputStream.readAllBytes());
            gzipInputStream.close();
            byteArrayInputStream.close();
            // log.debug("itemsJson: {}", itemsJson);

            Object result = null;
            synchronized (scriptEngine) {
                scriptEngine.eval(scriptContent);
                Invocable invocable = (Invocable) scriptEngine;
                result = invocable.invokeFunction("scoreCalculator", itemsJson);
                log.info("计算结果: {}", result);
            }
            if (result == null) {
                return CompletableFuture.completedFuture(Response.error("计算结果为空"));
            }
             // 返回html
             String resultString = result.toString();
             // 将 resultString 转换为 JSON 对象
             ObjectNode resultJson = objectMapper.readValue(resultString, ObjectNode.class);

            return CompletableFuture.completedFuture(Response.success(resultJson));
        } catch (Exception e) {
            log.error("处理脚本出错，ID: {}, 错误: {}", request.getId(), e.getMessage());
            return CompletableFuture.completedFuture(Response.error("脚本处理失败: " + e.getMessage()));
        }
    }
   
    /**
     * 获取脚本内容
     * 使用Spring Cache缓存结果
     */
    @Cacheable(value = "scriptData", key = "#scriptId")
    private String getScript(String scriptId) {
        
        log.info(">>> 缓存未命中，从远程获取脚本: {}", scriptId);
        try {
            //scriptId 为 000000005，加载本地脚本
            if (scriptId.equals("000000005")) {
                String script = new String(Files.readAllBytes(Paths.get("src/main/resources/000000005.js")));
                log.info("<<< 本地脚本获取成功，将被缓存");
                return script;
            }
            //scriptId 为 000000004，加载本地脚本
            if (scriptId.equals("000000004")) {
                String script = new String(Files.readAllBytes(Paths.get("src/main/resources/000000004.js")));
                log.info("<<< 本地脚本获取成功，将被缓存");
                return script;
            }
            //scriptId 为 000000003，加载本地脚本
            if (scriptId.equals("000000003")) {
                String script = new String(Files.readAllBytes(Paths.get("src/main/resources/000000003.js")));
                log.info("<<< 本地脚本获取成功，将被缓存");
                return script;
            }
            //scriptId 为 000000002，加载本地脚本
            if (scriptId.equals("000000002")) {
                String script = new String(Files.readAllBytes(Paths.get("src/main/resources/000000002.js")));
                log.info("<<< 本地脚本获取成功，将被缓存");
                return script;
            }
            //scriptId 为 000000001，加载本地脚本
            if (scriptId.equals("000000001")) {
                String script = new String(Files.readAllBytes(Paths.get("src/main/resources/000000001.js")));
                log.info("<<< 本地脚本获取成功，将被缓存");
                return script;
            }
            String script = restTemplate.getForObject(
                SCRIPT_URL + "/" + scriptId + ".js", 
                String.class
            );
            log.info("<<< 远程脚本获取成功，将被缓存");
            return script != null ? script : "";
        } catch (Exception e) {
            log.error("远程脚本获取失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取脚本失败: " + e.getMessage());
        }
    }
} 