# FEIYU Android UI Spike · 第一轮实测

测试日：2026-11-04（设备本地时区）。

## 范围

- Stage 1（布局）：从 FEIYU v0.4 冻结设计重建「消息联系人 → 大肥鱼会话」以及事项、笔记的独立入口。
- Stage 2（组件）：原生 Compose LazyColumn、Message、BasicTextField、头像、导航。
- Stage 3（初步交互）：输入法联动、本地 Mock 流式回复、返回。全部 Mock，无网络业务。

## 环境

P30S / Android 12（API 31）/ 1920×1080 / 240dpi。当前为横屏平板，主界面在中间展示宽度受限的手机内容列；**正式 Android 手机的物理屏幕、触控、系统手势和 IME 未验证**。

Kotlin 2.3.10 / Android Gradle Plugin 8.13.2 / Gradle 8.13 / Jetpack Compose UI 1.8.3 / Material3 1.3.2。Gradle 编译成功并在 P30S 成功安装独立包 `ai.feiyu.uispike`。

## 已验证

| 对象 | 结果 | 图片 |
|---|---|---|
| 480dp 列表和会话 | App 启动与布局正常，联系人点击进入会话 | `evidence/tablet-contacts-preview.jpg`、`evidence/tablet-chat-preview.jpg` |
| 输入法 | PinyinIME 真实弹出，Composer 位于键盘上方，消息可见区域收缩 | `evidence/tablet-ime-preview.jpg` |
| 输入与回复 | 输入 `helloFEIYU`，点击发送，回复逐步生成，键盘收回 | `evidence/tablet-sent-preview.jpg` |
| 系统返回 | Android 返回键从会话回到联系人 | `evidence/tablet-return-preview.jpg` |
| 390dp 约束布局 | 联系人、消息换行与 Composer 排布正常，无可见横向溢出 | `evidence/tablet-390-crop.jpg`、`evidence/tablet-390-chat-crop.jpg` |
| 运行稳定性 | 期间未见 `ai.feiyu.uispike` 进程崩溃 | ADB 活动与 logcat 检查 |

## 尚待验证

- 手机实机 360/390/430dp，不同屏幕纵横比、状态栏和手势导航区域。
- 中文 IME 的拼音组合、选区、候选词以及多行编辑；本轮 ADB 通过英语注入测试，仅能证实基础 IME 联动。
- 连续长历史的滚动锚定、流式内容更新时的触摸中断、滚动手感与帧耗时。当前原型只有短历史，尚未具备这些测试条件。
- Predictive Back 的手势进度和 Android 13+ 行为；P30S 为 Android 12，仅验证返回键。
- 与 ChatGPT / Claude 的真实动效、触觉反馈和动画物理对照。

## 下一步依据

先独立收敛三个组件的行为验收：`ConversationRow` 的点击与触控目标、`MessageViewport` 的历史滚动与可读性、`Composer` 的 IME 与内容增长。实机手机得到后再开展手感比较。事项与笔记仍仅承担信息布局 Mock。
