# Scale API 文档

## 评分计算接口

### 接口信息
- 请求方法：POST
- 请求路径：/scale/compute
- Content-Type: application/json

### 请求参数
| 字段 | 类型 | 必填 | 描述 |
|------|------|------|------|
| id | String | 是 | 评分脚本ID |
| items | Array | 是 | 问题答案列表 |

### 响应参数
| 字段 | 类型 | 描述 |
|------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Object | 响应数据 |

### 示例

#### 请求示例 