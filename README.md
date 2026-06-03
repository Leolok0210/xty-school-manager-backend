# # 校务管理系统

本项目是一个基于 Java 的单体架构校务管理系统。以下是项目的详细介绍和使用说明。

## 功能模块

<br/>

## 技术栈

### 后端框架

- **编程语言**：Java 8
- **框架**：Spring Boot 2.7为核心框架，其他框架的版本号需要兼容核心框架的版本号
- **持久层**：MyBatisPlus
- **权限认证**：Sa-Token
- **接口文档**：Knif4j
- **数据库连接池**：druid
- **分页插件**：pagehelper
- **excel工具类**：easyexcel
- **JSON解析器**：fastjson
- **java工具包**：lombok,hutool

### 组件

- **缓存**：redis 6.0
- **数据库**：MySQL 8.0

### 开发工具

- **IDE**：IntelliJ IDEA
- **版本控制**：Git
- **构建工具**：Maven 3.x
  - **groupId**:com.xiaotiyun
  - **artifactId**:xty-school-manager
  - **version**:1.0.0-SNAPSHOT
  - **modelVersion**:4.0.0
  - **仓库配置**：
    ```
    <repositories>
        <repository>
            <id>sw-campus-component</id>
            <name>sw-campus-component</name>
            <url>http://nexus.iydsj.com/repository/sw-campus-component/</url>
        </repository>
        <repository>
            <id>sw-campus-release</id>
            <name>sw-campus-release</name>
            <url>http://nexus.iydsj.com/repository/sw-campus-release/</url>
        </repository>
    </repositories>
    
    <distributionManagement>
        <snapshotRepository>
            <id>sw-campus-component</id>
            <name>sw-campus-component</name>
            <url>http://nexus.iydsj.com/repository/sw-campus-component/</url>
        </snapshotRepository>
        <repository>
            <id>sw-campus-release</id>
            <name>sw-campus-release</name>
            <url>http://nexus.iydsj.com/repository/sw-campus-release/</url>
        </repository>
    </distributionManagement>
    ```
  - **打包**：项目打包的时候，只需要把对应环境的yml配置文件打包进jar包里，不是对应环境的配置文件不需要放进jar包内
  - **编译配置**：编译时，资源文件只需要加载当前环境的yml配置文件
    - **包类型**：jar包
    - **包名**：同artifactId

## 项目结构

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.xiaotiyun.school.manager
│   │   │       ├── controller  # 控制器层
│   │   │       │   └── auth    # 认证控制器
│   │   │       │   └── basic    # 基础管理控制器
│   │   │       ├── service     # 服务层
│   │   │       │   └── impl    # 服务层实现类
│   │   │       ├── dao         # 数据访问层
│   │   │       │   └── impl    # 数据访问层实现类
│   │   │       └── model      # 实体类
│   │   │       │    └── entity      # 数据库实体类，类型需要以Entity结尾
│   │   │       │    └── req      # 接口接收参数实体类，类名需要以ReqModel结尾
│   │   │       │    └── res      # 接口返回参数实体类，类名需要以ResModel结尾
│   │   │       └── filter      # 拦截器
│   │   │       └── handler      # 
│   │   │       │    └── GlobalExceptionHandler.java      # 全局异常捕获
│   │   │       └── basic      # 基础服务包
│   │   │       │   └── util    # 工具类
│   │   │       │   └── common    # 公用基础类
│   │   │       │   └── exception    # 异常类
│   │   │       ├── MainApplication.java  # 启动类
│   │   ├── resources
│   │   │   └── mapper # 数据库操作xml
│   │   │   └── application.yml # 配置文件
│   └── test                    # 测试代码
├── pom.xml                     # Maven 配置文件
└── README.md                   # 项目说明文件
└── doc                   # 项目文件
│   └── sql      # 存放sql脚本
```

<br/>

## 快速开始

### 环境要求

1. 安装 JDK 8
2. 安装 Maven 3.x
3. 安装 MySQL 8.0 并创建数据库

### 配置

1. 克隆项目：
   ```sh
   git@221.229.103.166:server/xty-school-manager.git
   ```

### 启动项目

1. 进入项目目录并构建：
   ```sh
   mvn clean install
   ```
2. 启动项目：
   
3. 在浏览器中访问：
   ```
   http://localhost:8080
   ```
4. 项目打包
   1. 打包开发环境
      ```
      mvn clean package -Pdev
      ```
   2. 打包测试环境
      ```
      mvn clean package -Ptest
      ```
   3. 打包预发环境
      ```
      mvn clean package -Ppre
      ```
   4. 打包生产环境
      ```
      mvn clean package -Pprod
      ```

## TODO

- 提供 RESTful API 以支持移动端开发。
