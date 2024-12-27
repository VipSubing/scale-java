package com.example.scale.service;

import com.example.scale.entity.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import java.util.Base64;

/**
 * JSON文件处理服务
 * 提供JSON数据的远程获取和缓存功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JsonService {
    
    /** 远程JSON数据URL */
    private static final String REMOTE_JSON_URL = 
        "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/recommands-test.json";
    
    /** 所有测试数据URL */
    private static final String ALL_TESTS_URL = 
        "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/all-tests.json";

    private static final String QUESTIONS_URL = "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/questions/";
    
    /** RestTemplate用于发送HTTP请求 */
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    
    

    /**
     * 获取并缓存指定ID的问题数据
     */
    @Cacheable(value = "jsonCache", key = "#id")
    public Response getQuestionsData(String id) {
        String url = QUESTIONS_URL + id + ".json.gzip";
        try {
            byte[] zipData = restTemplate.getForObject(url, byte[].class);
            
            if (zipData == null) {
                return Response.error("获取问题数据失败: 远程数据为空");
            }
            // * 转base64字符串
            String base64Data = Base64.getEncoder().encodeToString(zipData);
            log.debug("zipData: {}", zipData);
            log.debug("base64Data: {}", base64Data);
            return Response.success(base64Data);
        } catch (Exception e) {
            log.error("获取问题数据失败 url: {}", url, e);
            return Response.error("获取问题数据失败");
        }
    }

    /**
     * 获取推荐JSON数据
     * 使用Spring Cache缓存结果 
     */
    @Cacheable(value = "recommendData")
    public Response getCachedJsonData() {
        log.info(">>> 缓存未命中，从远程获取推荐JSON数据");
        try {
            String data = restTemplate.getForObject(REMOTE_JSON_URL, String.class);
            log.info("<<< 远程推荐数据获取成功，将被缓存");
            Object jsonData = objectMapper.readValue(data != null ? data : "[]", Object.class);
            return Response.success(jsonData);
        } catch (Exception e) {
            log.error("远程推荐数据加载失败: {}", e.getMessage(), e);
            return Response.error("远程推荐数据加载失败");
        }
    }

    /**
     * 获取所有测试JSON数据
     * 使用Spring Cache缓存结果
     */
    @Cacheable(value = "allTestsData")
    public Response getAllTestsData() {
        log.info(">>> 缓存未命中，从远程获取所有测试数据");
        try {
            String data = restTemplate.getForObject(ALL_TESTS_URL, String.class);
            log.info("<<< 远程测试数据获取成功，将被缓存");
            Object jsonData = objectMapper.readValue(data != null ? data : "[]", Object.class);
            return Response.success(jsonData);
        } catch (Exception e) {
            log.error("远程测试数据加载失败: {}", e.getMessage(), e);
            return Response.error("远程测试数据加载失败");
        }
    }

    /**
     * 根据类型刷新缓存数据
     * 
     * @param type 缓存类型
     */
    public Response refreshCache(String type) {
        log.info(">>> 开始刷新缓存数据, type: {}", type);
        try {
            if (type == null) {
                // 刷新所有缓存
                clearAllCache();
                getCachedJsonData();
                getAllTestsData();
                log.info("<<< 所有缓存刷新成功");
                return Response.success("所有缓存刷新成功");
            }

            switch (type.toLowerCase()) {
                case "all":
                    clearAllTestCache();
                    getAllTestsData();
                    log.info("<<< 所有测试数据缓存刷新成功");
                    return Response.success("所有测试数��缓存刷新成功");
                
                case "pro":
                    clearRecommendCache();
                    getCachedJsonData();
                    log.info("<<< 推荐数据缓存刷新成功");
                    return Response.success("推荐数据缓存刷新成功");
                
                case "script":
                    clearScriptCache();
                    log.info("<<< 脚本缓存刷新成功");
                    return Response.success("脚本缓存刷新成功");
                
                case "questions":
                    clearQuestionsCache();
                    log.info("<<< 问题数据缓存刷新成功");
                    return Response.success("问题数据缓存刷新成功");
                
                default:
                    return Response.error("未知的缓存类型: " + type);
            }
        } catch (Exception e) {
            log.error("缓存刷新失败: {}", e.getMessage(), e);
            return Response.error("缓存刷新失败: " + e.getMessage());
        }
    }

    @CacheEvict(value = "allTestsData", allEntries = true)
    public void clearAllTestCache() {
        log.info("清除所有测试数据缓存");
    }

    @CacheEvict(value = "recommendData", allEntries = true)
    public void clearRecommendCache() {
        log.info("清除推荐数据缓存");
    }

    @CacheEvict(value = "scriptData", allEntries = true)
    public void clearScriptCache() {
        log.info("清除脚本缓存");
    }

    @CacheEvict(value = "jsonCache", allEntries = true)
    public void clearQuestionsCache() {
        log.info("清除问题数据缓存");
    }

    @CacheEvict(value = {"recommendData", "allTestsData", "scriptData", "jsonCache","testInfoCache"}, allEntries = true)
    public void clearAllCache() {
        log.info("清除所有缓存");
    }

    /**
     * 获取并缓存指定ID的测试信息
     * 
     * @param id 测试ID
     * @return 统一响应格式的测试信息
     */
    @Cacheable(value = "testInfoCache", key = "#id")
    public Response getTestInfo(String id) {
        log.info(">>> 缓存未命中，开始获取测试信息, id: {}", id);
        try {
            // 获取所有测试数据
            Response allTestsResponse = getAllTestsData();
            if (allTestsResponse.getCode() != 200) {
                return Response.error("getAllTestsData error");
            }
            
            // 从测试列表中查找指定ID的测试
            JsonNode allTests = objectMapper.valueToTree(allTestsResponse.getData());
            JsonNode targetTest = null;
            for (JsonNode test : allTests) {
                if (test.get("id").asText().equals(id)) {
                    targetTest = test;
                    break;
                }
            }
            
            if (targetTest == null) {
                return Response.error("未找到ID为" + id + "的测试");
            }

            // 获取问题数据
            Response questionsResponse = getQuestionsData(id);
            if (questionsResponse.getCode() != 200) {
                return Response.error("getQuestionsData error");
            }

            // 组装返回数据
            ObjectNode result = (ObjectNode)targetTest;
            result.set("items", objectMapper.valueToTree(questionsResponse.getData()));
            log.info(id, result);
            log.info("<<< 测试信息获取成功，将被缓存");
            return Response.success(result);
        } catch (Exception e) {
            log.error("获取测试信息失败", e);
            return Response.error("获取测试信息失败");
        }
    }

    
} 