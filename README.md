### 代码生成工具V2 
```
第二个版本: 重构了前端,用了 antd vue3
前端代码在 /html 中
cd html
npm install
npm run dev
npm run build
```

### docker运行方式
```shell
# 目录映射按需, 可以直接更换git的配置
docker run -p 8071:8071 --restart always  --name dbhelp \
-v ~/ars/dbhelp:/root/ars/dbhelp \
-v ~/themeList:/root/themeList \
-e code.data.git-url=https://gitee.com/resources1/themeList.git \
-d johnnywjh/dbhelp:v.2.0

# 也可以自己编译镜像 在dbhelp-web 目录下执行
mvn clean package docker:build
```
- [Dockerfile](./src/main/docker/Dockerfile)
- [docker-compose.yml](build/compose/docker-compose.yml)
- [docker tag list](https://hub.docker.com/repository/docker/johnnywjh/dbhelp)

### 默认配置
```text
code.data.base-path=${user.home}
code.data.theme-dic-name=themeList
code.data.git-enable=false
code.data.git-url=https://gitee.com/resources1/themeList.git
code.data.git-user=
code.data.git-pwd=
```

### 模板位置

- 模板的位置会存放在 ~/themeList
- 项目每次启动的时候会删除这个文件, 从新从git上pull
- 模板一级目录名规则  必须以 theme开头
```java
if (name.startsWith("theme")) {
  list.add(name);
}
```

### 模板编写

- 模板采用 freemarker
- 模板命名格式 **Controller.java**.flt
- 生成的文件   ClassName**Controller.java**
-  生成后的文件目录保持和模板的目录结构一样



