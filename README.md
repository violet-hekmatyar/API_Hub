# 项目介绍

**<font style="color:rgb(31, 35, 40);">😀</font>**<font style="color:rgb(31, 35, 40);">
作为用户您可以通过注册登录账户，获取接口调用权限，并根据自己的需求浏览和选择适合的接口。您可以在线进行接口调试，快速验证接口的功能和效果。</font>

**<font style="color:rgb(31, 35, 40);">💻</font>**<font style="color:rgb(31, 35, 40);"> 作为开发者 平台提供了客户端SDK:
api-SD</font>K<font style="color:rgb(31, 35, 40);">， 通过</font>开发者凭证<font style="color:rgb(31, 35, 40);">
即可将轻松集成接口到您的项目中，实现更高效的开发和调用。</font>

**<font style="color:rgb(31, 35, 40);">🤝</font>**<font style="color:rgb(31, 35, 40);"> 您可以将自己的接口接入到API-hub
接口开放平台平台上，并发布给其他用户使用。 您可以管理您的各个接口，以便更好地分析和优化接口性能。</font>

**<font style="color:rgb(31, 35, 40);">🏁</font>**<font style="color:rgb(31, 35, 40);"> 无论您是用户还是开发者，API-hub
接口开放平台都致力于提供稳定、安全、高效的接口调用服务，帮助您实现更快速、便捷的开发和调用体验。</font>

**<font style="color:rgb(31, 35, 40);">👌</font>**<font style="color:rgb(31, 35, 40);">  平台还将提供了开发者在线文档<font style="color:rgb(31, 35, 40);">
和技术支持，帮助您快速接入和发布接口，敬请期待！</font>

展示地址（可能会过期）：[http://calmkin.top/](http://calmkin.top/)

# 技术选型

○ Spring Boot 2.7.0：作为项目的基础框架，提供快速开发、简化配置等特性。

○ Spring MVC：用于构建RESTful API，实现前后端分离。

○ MySQL数据库：作为关系型数据库，存储结构化数据。

○ Redis数据库：作为内存数据库，用于缓存数据，提高系统性能。

○ Spring Cloud：提供分布式系统解决方案，包括RPC（远程过程调用）、服务注册与发现等。

○ Nacos：作为服务注册与配置中心，管理微服务实例和动态配置。

○ Spring Cloud Gateway：作为微服务网关，实现路由转发、访问控制等功能。

○ Spring Cloud OpenFeign：简化远程服务调用，实现服务间的通信。

○ Sentinel：提供熔断降级、限流控制等服务保护功能，确保系统稳定性。

○ API签名认证（Http调用）：对API请求进行签名验证，确保数据安全性。

○ Swagger + Knife4j：自动生成API文档，提供在线接口测试功能。

○ Spring Boot Starter：用于开发客户端SDK，简化集成过程。

○ Apache Commons Lang3：提供常用的工具类，简化代码编写。

○ MyBatis-Plus及MyBatis X：自动生成数据访问层代码，提高开发效率。

○ justauth：实现第三方登录功能，支持多种社交账号登录。

○ spring-boot-starter-mail：提供邮件推送功能，用于发送验证码等。

○ Hutool、Apache Common Utils、Gson等：提供丰富的工具类库，满足各种开发需求。

# 项目流程图

![](https://cdn.nlark.com/yuque/0/2023/png/22678511/1701876846917-76c39e67-efe9-4518-a252-656d73eee03b.png)

# 功能概览

| **<font style="color:rgb(31, 35, 40);">功能</font>**           | <font style="color:rgb(31, 35, 40);">游客</font> | **<font style="color:rgb(31, 35, 40);">普通用户</font>** | **<font style="color:rgb(31, 35, 40);">管理员</font>** |
|--------------------------------------------------------------|------------------------------------------------|------------------------------------------------------|-----------------------------------------------------|
| API-SDK<font style="color:rgb(31, 35, 40);">使用</font>        | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| 开发者API在线文档                                                   | <font style="color:rgb(31, 35, 40);">✅</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">切换主题、深色、暗色</font>       | <font style="color:rgb(31, 35, 40);">✅</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">在线调试接口</font>           | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">每日签到得积分</font>          | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">接口大厅搜索接口、浏览接口</font>    | <font style="color:rgb(31, 35, 40);">✅</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">邮箱验证码登录注册</font>        | <font style="color:rgb(31, 35, 40);">✅</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">余额充值</font>             | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">支付成功邮箱通知(需要绑定邮箱)</font> | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">更新头像</font>             | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">绑定、换绑、解绑邮箱</font>       | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">取消订单、删除订单</font>        | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">✅</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">商品管理、上线、下架</font>       | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">❌</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">用户管理、封号解封等</font>       | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">❌</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |
| <font style="color:rgb(31, 35, 40);">接口管理、接口发布审核、下架</font>   | <font style="color:rgb(31, 35, 40);">❌</font>  | <font style="color:rgb(31, 35, 40);">❌</font>        | <font style="color:rgb(31, 35, 40);">✅</font>       |

# 项目亮点

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">● </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">模块化设计</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：项目后端采用模块化设计，划分为公共模块、客户端SDK、Gateway网关、用户中心、接口中心、记录中心、优惠券中心等7个子项目，通过Maven进行多模块依赖管理和打包，提高了项目的可维护性和可扩展性。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
●</font><font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);"> </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">服务注册与发现</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：基于Nacos实现服务注册与发现及业务动态配置切换，使得服务能够灵活应对变化，提高了系统的稳定性和灵活性。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">● </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">客户端SDK简化开发</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">：为解决开发者调用成本过高的问题，平台基于SpringBoot
Starter开发了客户端SDK，只需少量代码即可调用接口，并统一异常返回类，极大地提升了开发体验和效率。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
●</font><font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);"> </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">API网关增强安全性</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">：选用Spring Cloud
Gateway作为API网关，实现了路由转发、访问控制、流量染色等功能，并集中处理签名校验、请求参数校验等业务逻辑，既提高了安全性，又便于系统开发维护。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
●</font><font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);"> </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">公共模块减少重复代码</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：通过抽象模型层和业务层代码为公共模块，并使用OpenFeign实现子系统间的高性能接口调用，大幅减少了重复代码，提高了代码的可复用性和开发效率。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">● </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">异步处理提升响应速度</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：为减轻API调用模块的即时负载，提升服务响应速度，平台使用RabbitMQ记录用户调用信息，以异步方式处理队列信息并进行相应的数据库更新操作，保证了数据的一致性和完整性。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">● </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">自主研发接口提升可用性</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：自主开发了OJ沙箱接口、图片搜索接口等，累计调用次数众多，接口调用可用性高达99.99%，充分证明了平台的技术实力和接口的稳定性。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
●</font><font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);"> </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">熔断降级与限流控制保障稳定性</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：通过开启Feign的Sentinel功能，将Sentinel的熔断降级和限流控制应用于OpenFeign的远程调用中，有效保障了系统的稳定性。当接口异常调用的比例超出阈值时，会自动熔断该接口并降级处理，恢复正常后再放行请求，同时触发限流或熔断后的请求会返回默认数据或友好提示。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
●</font><font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);"> </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">全局过滤器简化校验流程</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：自定义GlobalFilter，包括AKGlobalFilter和LoginGlobalFilter，对于需要拦截的路由进行统一的AccessKey和SecretKey校验或者LoginToken校验，简化了代码，实现了业务解耦。</font>

<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">● </font>*
*<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">API文档自动生成与在线调用</font>
**<font style="color:rgb(5, 7, 59);background-color:rgb(253, 253, 254);">
：使用swagger和Knife4j等API管理产品实现了API文档自动生成和在线调用功能，方便开发者快速了解和测试接口，提高了开发效率。</font>

# <font style="color:rgb(31, 35, 40);">SDK使用</font>

## 引用

下载相关源码，并在项目中引用

```xml
<!--SDK-->
<dependency>
  <groupId>com.apihub</groupId>
  <artifactId>api-SDK</artifactId>
  <version>0.0.1</version>
</dependency>
```

## 配置

yaml

```yaml
apihub:
  client:
    access-key: 你的accessKey
    secret-key: 你的secretKey
    api-token: 你的自营接口Token(非必须)
```

## 根据interfaceId请求

### 参数

+ accessKey
+ secretKey
+ interfaceId
+ body

### 方法

+ interfaceIdByGet ---使用get方法请求
+ interfaceIdByPost ---使用post方法请求

直接配置也可以

```java
String accessKey = "你的 accessKey";
String secretKey = "你的 secretKey";
ApiHubIdClient apiHubIdClient = new ApiHubIdClient(accessKey, secretKey);

```

### 引用服务

```java
import com.apihub.sdk.client.ApiHubIdClient;
//自定义错误
import com.apihub.sdk.exception.ApiException;

import javax.annotation.Resource;

@Resource
private ApiHubIdClient apiHubIdClient;
```

### 示例代码

示例：1号和2号接口-----都是土味情话接口，1号只允许get，2号允许get和post

```java
@Test
void SDKGetTest() {
    try {
        System.out.println(apiHubIdClient.interfaceIdByGet(1L, "format=text"));
    } catch (ApiException e) {
        System.out.println(e.getMessage());
    }
}

@Test
void SDKPostTest() {
    try {
        System.out.println(apiHubIdClient.interfaceIdByPost(2L, "format=text"));
    } catch (ApiException e) {
        System.out.println(e.getMessage());
    }
}
```

### 响应

```json
# 请求成功
{"code":0,"data":"我觉得你接近我就是在害我，害得我好喜欢你呀","message":"ok"}

# 成功发送，但返回错误提示
{"code":40000,"data":null,"message":"请求方法错误"}

# 发送失败，返回错误提示
accessKey与secretKey不匹配
```

## 复杂接口

### 参数

apiToken ----购买时段卡后获得token

PictureQueryRequest

+ searchText -----搜索关键词
+ current ----当前页号
+ pageSize ----页面大小

### 引用服务

```java
import com.apihub.sdk.client.ApiHubSelfApiClient;
import com.apihub.sdk.exception.ApiException;

import javax.annotation.Resource;

@Resource
private ApiHubSelfApiClient apiHubSelfApiClient;
```

### 示例代码

```java
@Test
    void OJSandBox() {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setCode("import java.io.*;\nimport java.util.*;\n\npublic class Main\n{\n    public static void main(String args[]) throws Exception\n    {\n       Scanner cin=new Scanner(System.in);\n        int a=cin.nextInt(),b=cin.nextInt();\n        a=a+b;System.out.println(a);\n    }\n}");
        executeCodeRequest.setLanguage("java");
        List<String> list = new ArrayList<>();
        list.add("1 2");
        list.add("1 3");
        list.add("2 3");
        executeCodeRequest.setInputList(list);
        try {
            System.out.println(apiHubSelfApiClient.OJSandBox(executeCodeRequest));
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }
```

```java
@Test
void PictureApi() {
    PictureQueryRequest pictureQueryRequest = new PictureQueryRequest();
    pictureQueryRequest.setSearchText("原神");
    pictureQueryRequest.setPageSize(15);
    pictureQueryRequest.setCurrent(4);
    try {
        System.out.println(apiHubSelfApiClient.PictureApi(pictureQueryRequest));
    } catch (ApiException e) {
        System.out.println(e.getMessage());
    }
}
```

### 响应

```json
{
    "outputList": [
        "3",
        "4",
        "5"
    ],
    "message": null,
    "status": 1,
    "judgeInfo": {
        "message": null,
        "memory": null,
        "time": "0"
    }
}
```

```json
{
  "code": 0,
  "data": {
    "records": [
      {
        "title": "原神-公开的收藏夹-站酷 (ZCOOL)",
        "url": "https://img.zcool.cn/community/0108065fa534b611013ee04d474fbb.jpg@520w_390h_1c_1e_2o_100sh.jpg"
      },
      {
        "title": "官服原神下载最新版本-原神官方正版下载v4.1.01805476018121248_18054760_18121248 安卓手机版-2265手游网",
        "url": "http://pic.2265.com/upload/2019-9/2019930161214096090.jpg"
      },
      {
        "title": "原神官方下载_原神攻略大全_特玩网原神官方合作网站",
        "url": "https://img.te5.com/uploads/allimg/201203/181-2012031140370-L.jpg"
      },
      {
        "title": "原神琉璃袋位置汇总 原神琉璃袋地图分布一览_18183原神专区",
        "url": "https://img.18183.com/uploads/idqr/2225686.png?1618569692"
      },
      {
        "title": "「原神」游戏评价与建议",
        "url": "https://imgheybox.max-c.com/bbs/2022/02/06/4b9b793ca0f91b2bb65b077ca6d7c1ac/thumb.jpeg"
      },
      {
        "title": "原神多久一个大版本？ - 原神综合 | TapTap 原神社区",
        "url": "https://img2.tapimg.com/bbcode/images/7b26a82ceb5be5b33fac1c6298d64ed4.jpg?imageView2/2/w/800/h/9999/q/80/format/jpg/interlace/1/ignore-error/1"
      },
      {
        "title": "原神 - 玩家社区 | TapTap 论坛",
        "url": "https://img2.tapimg.com/bbcode/images/fd16e39095c2df57746cc2928d57aa15.jpg?imageView2/2/w/984/h/9999/q/80/format/jpg/interlace/1/ignore-error/1"
      },
      {
        "title": "原神更新速度跟不上玩家需求，米哈游何以破局？_腾讯新闻",
        "url": "https://inews.gtimg.com/newsapp_bt/0/13364373844/1000"
      },
      {
        "title": "原神【神里綾華】 壁紙 | tsundora.com",
        "url": "https://tsundora.com/image/2021/01/genshin_627.jpg"
      },
      {
        "title": "原神图片_百度百科",
        "url": "https://bkimg.cdn.bcebos.com/pic/00e93901213fb80e7bec9f9dac9f382eb9389b503e60?x-bce-process=image/watermark,image_d2F0ZXIvYmFpa2UxODA=,g_7,xp_5,yp_5/format,f_auto"
      },
      {
        "title": "【原神】精选原画丨2K动漫壁纸合集 - 哔哩哔哩",
        "url": "https://i0.hdslb.com/bfs/article/6c99c84afde9020bf881cb454b74165a14f86f75.jpg"
      },
      {
        "title": "【原神】不人気スクロースちゃん・・本当は魅力にあふれてるよなぁ？",
        "url": "https://genshin.gamestlike.com/wp-content/uploads/2020/12/20201205_230000_0d8de682_000.jpg"
      },
      {
        "title": "【原神】いいから主人公の性別を変更させろー！ - 原神攻略まとめ テイワット速報",
        "url": "https://teyvatsokuho.com/wp-content/uploads/2020/09/syujinko.png"
      },
      {
        "title": "原神r18版本-原神r18mod下载v4.1.0-k73游戏之家",
        "url": "http://pic.k73.com/up/soft/2020/1110/113230_43090761.png"
      },
      {
        "title": "【原神】中国では原神のリアルイベント開催！グッズいいなぁ",
        "url": "https://genshin-matome.net/wp-content/uploads/2021/01/YAfd880.jpg"
      }
    ],
    "total": "0",
    "size": "15",
    "current": "4",
    "orders": [],
    "optimizeCountSql": true,
    "searchCount": true,
    "countId": null,
    "maxLimit": null,
    "pages": "0"
  },
  "message": "ok"
}
```

# 其他源码地址

<font style="color:rgb(51, 51, 51);">apihub前端</font>

[<font style="color:rgb(51, 51, 51);">https://gitee.com/violet_hekmatyar/api-hub-fronted</font>](https://gitee.com/violet_hekmatyar/api-hub-fronted)

<font style="color:rgb(51, 51, 51);">技术选型：vue3+vite+TypeScript+vue-router+element-plus+pinia</font>

<font style="color:rgb(51, 51, 51);">oj沙箱</font>

[<font style="color:rgb(51, 51, 51);">https://gitee.com/violet_hekmatyar/oj-backend-sandbox</font>](https://gitee.com/violet_hekmatyar/oj-backend-sandbox)

# 前端页面截图

![](https://cdn.nlark.com/yuque/0/2025/png/22678511/1736080301512-571f979f-cc93-4f36-8e9d-ff06d82a11e2.png)![](https://cdn.nlark.com/yuque/0/2025/png/22678511/1736080299950-37edf365-6614-4a54-b85d-97617bee566e.png)![](https://cdn.nlark.com/yuque/0/2025/png/22678511/1736080299404-37308b0d-9373-4e32-940b-fab21164a71c.png)![](https://cdn.nlark.com/yuque/0/2025/png/22678511/1736080299652-6ae899f4-815e-4ec0-8b43-1f923405261a.png)![](https://cdn.nlark.com/yuque/0/2025/png/22678511/1736080300505-1ea50665-c1f1-437b-b84e-f38ed04f7985.png)![](https://cdn.nlark.com/yuque/0/2025/png/22678511/1736080301065-199fc93c-9baa-417d-8d91-85f6dfb3175d.png)

