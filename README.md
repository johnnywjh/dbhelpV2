### 代码生成工具V2 
- github: https://github.com/johnnywjh/dbhelpV2.git
- gitee:  https://gitee.com/sesamekim/dbhelpV2.git

#### 主要功能.
> 所有数据前端都会本地缓存
- ========= 已有功能
- 1 选择数据库 查询表结构
- 1.1 表结构界面 => 选择字段生成sql查询语句
- 1.2 表结构界面 => 选择字段生成文档
- 1.3 表结构界面 => 查看DDL语句
- 1.4 表结构界面 => 选择字段,查询表数据 `2.10`
- 2 对比数据库 => 用于对比不同数据库表是否缺失
- 3 搜索字段 => 输入字段名称,查询有哪些表里有这个字段
- 5 根据模板 => 预览代码
- 6 根据模板 => 下载生成的代码
- 7 表详情里面支持数据预览  `2.7 新增`
- 8 增加模板引擎由freemark => Beetl `2.14 新增`
```
模板说明 : 运行的时候需要指定模板, 支持`本地目录`和`git项目`
```

```
2.x 重构了前端,vue3
前端代码在 /html 中
cd html
npm install
npm run dev
npm run build => src/main/resources/public 里是编译好的文件
```

### docker运行方式
```shell
# 目录映射按需, 可以直接更换git的配置
docker run -p 8071:8071 --restart always  --name dbhelp \
-v ~/ars/dbhelp:/root/ars/dbhelp \
-e code.data.git-url=https://gitee.com/resources1/themeList.git \
-d johnnywjh/dbhelp:v.2.35

或者 (hub.docker推送有限制了)
registry.cn-shanghai.aliyuncs.com/johnnywjh/dbhelp:v.2.35
registry.cn-shanghai.aliyuncs.com/johnnywjh/dbhelp:v.2.35.arm

# 也可以自己编译镜像
# mvn clean package docker:build
# docker build -t johnnywjh/dbhelp:v.2.35 .
```
- [Dockerfile](./src/main/docker/Dockerfile)
- [docker-compose.yml](build/compose/docker-compose.yml)
- [docker tag list](https://hub.docker.com/repository/docker/johnnywjh/dbhelp)

### 默认配置
```yaml
code:
  data:
    base-path: ${user.home}/ars/dbhelp
    theme-dic-name: themeList
    git-enable: false
    git-url: https://gitee.com/resources1/themeList.git
    git-user:
    git-pwd:
    branch:
```

### 模板位置

- 模板的位置会存放在 ~/themeList
- 项目每次启动的时候会删除这个文件, 从新从git上pull

### 模板编写

- [Beetl3官方文档](https://www.kancloud.cn/xiandafu/beetl3_guide/2138944)

- 模板命名格式 Controller.java.txt
- 生成的文件   ClassName**Controller.java**
-  生成后的文件目录保持和模板的目录结构一样。

