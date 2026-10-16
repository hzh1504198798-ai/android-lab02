# 实验二：Android 网络访问与 Git 个人仓库

## 实验目标
* 掌握 Android 基础网络权限 (`INTERNET`) 的配置方法。
* 理解 Android 9+ 系统对明文 HTTP 流量的限制，并学会通过网络安全配置文件 (`network_security_config.xml`) 进行按域名放行。
* 熟练掌握 Android Studio 的 Git 版本控制，完成代码的本地提交与远程仓库推送。

## 运行方式
1. 使用 Android Studio 打开本项目[cite: 1]。
2. 在设备管理器（Device Manager）中启动 Android 模拟器（推荐 API 30 及以上）[cite: 1, 4]。
3. 点击顶部工具栏的 **Run** 按钮将 App 运行至模拟器中[cite: 1]。
4. 在 App 界面中分别点击 “HTTPS 请求” 和 “HTTP 请求” 按钮，观察并对比网络请求结果[cite: 1]。

## 接口地址说明
* **HTTPS 测试接口**：`https://httpbin.org/get?room=A203` (用于验证加密流量及参数变化)[cite: 1]
* **HTTP 测试接口**：`http://httpbin.org/get?room=A203` (用于验证明文安全策略拦截与放行)[cite: 1]

> **说明**：HTTP 仅用于教学对比测试，实际生产环境中所有网络服务应优先采用更安全的 HTTPS。