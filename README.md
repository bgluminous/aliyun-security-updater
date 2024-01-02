# aliyun-security-updater

一个用于定时更新阿里云 RDS/ECS/DNS 安全组IP的服务

## 配置文件

如果使用Docker创建已经打包好的镜像则应该把此文件映射到容器中的 /settings.yml

如果是直接运行项目则可以通过 **ConfigInstance.java** 文件来修改加载的配置文件和路径

```yaml
# Aliyun A/K 
accessKeyId: XXXXXX
accessKeySecret: XXXXXXX
# 执行时间
cron: 0 0 0,9 * * ?
# 获取IP的API (需要返回Json会读取data部分作为IP)
ipApi: https://api.kiiiv.com/v1/openapi/other/ip
# 更新DNS(可配置多条) endpoint(接入点) domainName(目标域名) rr(主机记录)
dns:
  - endpoint: alidns.cn-beijing.aliyuncs.com
    domainName: demo.com
    rr: www
  - endpoint: alidns.cn-beijing.aliyuncs.com
    domainName: xx.com
    rr: www1
# 更新ECS安全组(可配置多条) endpoint(接入点) regionId(区域) securityGroupId(安全组ID) description(访问规则描述)
ecs:
  - endpoint: ecs.cn-beijing.aliyuncs.com
    regionId: cn-beijing
    securityGroupId: sg-XXXXXXXXX
    description: demo
# 更新RDS(可配置多条) endpoint(接入点) dbInstanceId(RDS实例ID) ipArrayName(白名单分组名称)
rds:
  - endpoint: rds.cn-beijing.aliyuncs.com
    dbInstanceId: rm-XXXXXXXXX
    ipArrayName: ip_connect_demo
```

## Docker Compose 启动Demo

```yaml
version: "3.7"

services:
  service0:
    image: docker.kiiiv.com/luminous/aliyun-security-updater:latest
    pull_policy: always
    container_name: aliyun-security-updater
    restart: on-failure
    network_mode: host
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /home/settings.yml:/settings.yml
      - /data/logs/aliyun-security-updater:/logs
```

## 更新日志

### 1.1.0
- 新增 SingletonStorage 用于存储IP缓存信息（节省查询API调用额度）
- 文档 修改部分日志提示（变得更加浪费存储空间了）
