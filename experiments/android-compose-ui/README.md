# FEIYU · Android UI Spike

独立验证样机（非产品端），从已确认的 FEIYU v0.4 信息结构进入 Compose 原生控件实验。

- Stage 1 · Layout: 三个一级入口（消息、事项、笔记），头像设置入口；联系人 → 极简会话 → 返回，保留已确认的内容与层级。
- Stage 2 · Components: LazyColumn 会话列表、消息项、BasicTextField Composer，独立的笔记阅读和事项 Mock。
- Stage 3 · Feel: 本地模拟流式回复、输入法布局、滚动、返回键。真实手机手感需在手机设备上进一步验证。

所有内容与自动回复均为 Mock，本工程不发送网络请求或读取 FEIYU 后台数据。测试设备为 P30S / Android 12 / 1920×1080 横屏平板。主界面内容最大宽度为 390dp，平板仅用于小宽度布局与基础交互检查。

第一轮编译与 ADB 实测结果见 [RESULTS.md](./RESULTS.md)。

Build:

    ./gradlew :app:assembleDebug

APK:

    app/build/outputs/apk/debug/app-debug.apk

第一轮只检验一条纵向交互链：**联系人 → 聊天 → 输入 → 模拟流式回复 → 上滑阅读 → 返回**。其他页面保留信息布局 Mock。
