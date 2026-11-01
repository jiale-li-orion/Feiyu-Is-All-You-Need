# FatFish · 设计 Skill 全量筛选

- 9 个包含相关 Agent Skill 的入口，其中部分还包含子 Skill 和大型参考知识库。
- 4 个主要是组件库或运行时代码：WeUI、QMUI Android、open-claude-ui、dsh-live2d-pet。适合查组件行为和实现机制，不应该当作全局设计指令注入。

对于组件与布局设计，优先考虑首屏、会话、事项、设置和 Dashboard 的空间组织，信息密度、交互组件及 PC/手机适配。
另外，苹果风的材质、复杂弹簧动效、Live2D 等内容会同时评估，作为后续阶段的候选知识。

## 一、按设计问题分工

根据 Skill 的主文件、其直接引用的工作流和关键参考实现，得到如下。

### 1. [Apple HIG Skill](https://github.com/ykswang/apple-hig-skill) — 组件选择的规范参考

已读 SKILL.md，以及 Layout、Tab Bars、Sidebars、Split Views、Lists、Scroll Views、Sheets、Settings 等具体条目。

它适合回答：为什么此处应使用列表、详情、分栏或临时操作层？手机和 PC 应如何适配？

使用方法：每次只查当前组件问题对应的 HIG 页面。不要全量注入，也不要把 iOS 专属行为强加给 Android。

### 2. [Tencent / WeUI](https://github.com/Tencent/weui) — 移动端信息密度与列表实现参考

阅读了 Cell 的实际 Less 实现、Tabbar 和 Dialog 示例。

它的 Cell 通过统一容器、内部细分隔线、弹性正文区、右侧辅助信息和按压反馈组织内容，会话目录与设置列表等等适合参考。

使用方法：参考行的构成、文字截断、对齐、分组和交互反馈。不要直接复制整套微信皮肤或旧版 jQuery 示例。

### 3. [open-claude-ui-design](https://github.com/maxritter/open-claude-design/tree/main/skills/open-claude-ui-design) — 具体页面的设计流程

除了 Review Skill，同仓库还有 UI Design Skill，以及 layout-patterns.md、discovery-and-direction.md。

它要求先检查产品约束与既有组件，再确定布局；不允许为了让页面显得完整而虚构字段、功能、统计或操作。

使用方法：作为一次具体页面设计任务的执行流程。FatFish 的 IA 和交互语义仍是上游权威，仓库提供的通用页面模板只能作为候选。

### 4. [open-claude-ui](https://github.com/Caho1/open-claude-ui) — 聊天组件专项参考

重点读了 ChatComposer.tsx、useClaudeScrollPinning.ts、ScrollFadeContainer.tsx 和 ConfirmationDialog.tsx。

最有价值的是输入法合成保护、输入区域伸缩、流式内容的底部跟随，以及用户主动滚动后释放自动跟随。

使用方法：按功能检查实现机制，不能直接将其组件当作生产级 FatFish 组件复制。

HIG 负责组件选择的理由，WeUI 提供成熟的列表结构，UI Design Skill 负责设计流程，open-claude-ui 提供聊天专项工程参考。

### 其余仓库怎么用

| 资源 | 最有用的具体内容 | 使用阶段与限制 |
|---|---|---|
| [QMUI Android](https://github.com/Tencent/QMUI_Android) | QMUIGroupListView 的设置分组、TopBar、BottomSheet、键盘处理 | 条件使用。原生 Android 开发适合；WebView 前端不能直接复用原生控件。 |
| [Semi Design](https://github.com/DouyinFE/semi-design) | 官方 semi-ui-skills、组件文档查询、Form、Table、Modal 等 | PC Dashboard 优先。适合复杂表单和表格；若不是 React 技术栈，先借鉴行为与结构。 |
| [Emil Kowalski Skills](https://github.com/emilkowalski/skills) | apple-design、mobile-native、pick-ui-library | mobile-native 当前有用；弹簧、惯性、手势主要进入第五阶段。 |
| [apple-design-skill](https://github.com/naplesblue/apple-design-skill) | patterns.md、components.md、统一表面、细分隔线、Token、检查清单 | 视觉阶段。可以借鉴页面组织与克制风格；不采用其固定配色、Hero、CTA 模板。 |
| [Apple Design Reviewer](https://github.com/dickwu/apple-design-skill) | HIG 审查、cross-platform.md、代码审查、平台差异 | 设计完成后独立审查。与 HIG 主知识库高度重叠，通常不同时作为生成指令。 |
| [Apple Skills 合集](https://github.com/rshankras/claude-code-apple-skills) | ui-prototyping、ux-writing、typography、game-feel 等 | 原型探索和后续视觉阶段。多数是 SwiftUI 专属实现，不能整体注入 Web 项目。 |
| [DeerFlow frontend-design](https://github.com/bytedance/deer-flow/blob/main/skills/public/frontend-design/SKILL.md) | 有方向感的视觉设计、避免模板化、完整界面验证 | 第五阶段辅助。它带有自身文件命名和品牌署名要求，不能直接作为 FatFish 全局指令。 |
| [open-claude-ui-review](https://github.com/maxritter/open-claude-design/tree/main/skills/open-claude-ui-review) | 视觉、可访问性、交互状态与运行时验收 | 组件实现之后的审查者。Review 模式只报告问题，不自行修改。 |
| [dsh-live2d-pet](https://github.com/br0ny4/dsh-live2d-pet) | 角色素材注册、形变、桌宠位置与事件联动 | 第六阶段。当前默认角色渲染主要是 WebGL 网格形变，不能等同于完整 Cubism Live2D。 |
| [dsh-whale-girl-live2d](https://github.com/Andersen216/dsh-whale-girl-live2d) | 真实 Live2D、表情动作、常驻道具、事件驱动和状态复位 | 第六阶段。其中 SKILL.md 是桌宠操作说明，负责如何驱动已有组件，不负责设计应用布局。 |

## 二、精读后发现的实际冲突

1. **页面模板会偷偷替产品增加功能。**

   naplesblue/apple-design-skill 的 Home 配方默认包含 Hero、统计、分段视图、卡片、彩色 CTA。maxritter 的通用 Application Shell 配方也包含顶栏搜索和一套 Dashboard 结构。

   这些都是合理的模板，但 FatFish 的上游设计已经排除了独立搜索中心、统计式首页和通用 AI Dashboard。

   所以组件设计时只能吸收“统一列表”“阅读宽度”“响应式折叠”等局部机制。不能让模板里的默认组件变成新增产品需求。

2. **漂亮的参考代码可能缺少真正的交互保障。**

   open-claude-ui 的 ChatComposer.tsx 在调用发送回调后立即清空输入内容，没有等待服务器受理确认。对 FatFish 而言，这个行为会破坏消息提交结果不确定时的草稿保留。

   ConfirmationDialog.tsx 的注释声称实现了焦点限制，但代码只处理 Escape，没有完整的焦点约束与恢复。

   它们是有价值的参考，却还需要我们自己的组件验收。不能把“生产级复刻”的仓库描述当成测试结果。

3. **同一设计问题可能被多个 Skill 给出不同答案。**

   rshankras/ui-prototyping 建议一次探索 6–10 个差异较大的布局；maxritter/open-claude-ui-design 默认生成一个完整方向，仅在确实需要比较时生成多个。

   对于 FatFish，页面信息架构已经相对明确，因此大部分页面用一个主方案推进更节省 Review 成本。只有首屏这样的关键开放问题，才值得做两三个差异真正存在的布局方案，而非机械生成十张。

4. **设计审查也不能变成新的全局设计权威。**

   open-claude-ui-review 的处理方式比较好：区分可验证的标准问题、实际产品质量问题和主观建议，并要求操作截图和失败路径来支持结论。

   因此，用户已经接受的 FatFish 风格不应该因为某个通用 Skill 偏好其他圆角、字体或配色，就被判定为错误。

## 三、FatFish 第四阶段的具体使用路由

这一阶段我建议按页面逐一解决组件与布局，不把所有 Skill 同时交给 Codex。

| 设计对象 | 主参考 | 需要解决的具体问题 |
|---|---|---|
| 大肥鱼首屏与主会话 | Apple HIG Layout + open-claude-ui 聊天专项 | 会话内容、输入区、主要导航怎样分配空间；首屏如何不被次要信息占据 |
| 肥鱼会话目录 | WeUI Cell + HIG Lists | 头像、姓名、最近原文、时间和未读的稳定对齐；长名称与小屏处理 |
| 事项列表与详情 | HIG Lists / Split Views + UI Design Skill | 待我处理、正在推进如何扫描；详情怎样保持连续性；PC 是否需要并列阅读 |
| 个人设置 | WeUI Cell + HIG Settings | 设置分组、标签与控件的邻近关系，次级详情的进入方式 |
| 开发者 Dashboard | Semi Design + HIG Split Views | 任务列表、运行信息、管理控件与详情的分栏；避免无意义 KPI |
| 移动端实际适配 | Emil mobile-native | 安全区、输入法、键盘遮挡、动态视口、触控与滚动问题 |

这里有一个重要的工程选择：基础组件系统尽量保持统一。

WeUI、QMUI、Semi Design 和 open-claude-ui 可以同时提供参考，但不意味着我们要同时安装四套组件库。否则其样式重置、交互状态、焦点管理、主题变量和响应式策略会互相冲突。

特别是 QMUI 的 QMUIGroupListView 实际继承 LinearLayout，适合分组设置，并非用来承载数千条聊天消息的高性能虚拟列表。需要长列表时应选择当前技术栈已有的虚拟化方案，不能因为组件叫 List 就直接复用。

### 关于首屏的独立判断

IA 特意为大肥鱼首屏保留了设计自由度。这种情况下，直接拿 HIG 的聊天布局或 naplesblue 的首页模板套上去，都会过早锁定空间表达。

第四阶段应该围绕同一个已确定的交互目标，比较有限的几个空间组织方案：怎样让主会话始终可用、历史阅读与输入不互相抢占、其他肥鱼和事项保持可达，以及未来角色层能够融入其中。

角色可以预留合理的空间约束，但暂不决定立绘尺寸和动画。那是第六阶段需要通过实际动态内容验证的事情。

## 四、可以复用的 Skill 筛选方法

以后每次筛 Skill 都应该遵循同一份极短的合同，而不是重新阅读并注入整个设计合集。

```
当前阶段：
当前页面 / 组件：
上游约束：引用 FatFish 已确认的设计文档

要解决的具体问题：
候选参考：1 个主知识源，必要时 1 个实现源
需要阅读的文件：明确列出路径

采用：
哪些规则、机制或组件结构真正解决了当前问题？

拒绝：
哪些模板、默认功能、平台假设或实现要求与 FatFish 冲突？

验收：
通过什么实际交互、截图、设备状态确认结果？
```

筛选过程依次回答：该资源属于规范、设计流程、组件实现、视觉参考还是审查工具；它解决当前哪个真实缺口；它依赖什么技术栈；它是否会改写上游设计；最后是否有可以验证的产物。

一个设计问题设一个主要知识来源，再通过独立审查验证，而非通过堆积 Skill 获得共识。

这会让 Skill 真正成为可复用的知识工具：需要列表规范就读列表规范，需要输入框实现就查输入框，需要动效时再读取物理与手势知识。任务结束后，把被接受的具体决策归约进 FatFish 自己的设计文档，外部 Skill 不成为常驻约束。

## 五、对组件与布局文档的 Ownership 建议

接下来这份新文档可以明确写为：

**FatFish Components & Layout Specification**：继承已确定的信息架构与交互语义，决定各页面需要哪些组件、组件如何组合、信息与操作如何分配空间，以及不同屏幕尺寸和输入方式下如何适配。产物是页面结构、组件职责、布局规则与交互状态的空间映射。

它应该能够直接指导 Codex 实现页面，同时把 Typography、品牌色、玻璃材质、弹簧动效和肥鱼人格留给后续阶段。

我建议当前正式保留 Apple HIG、WeUI、open-claude-ui 聊天专项、open-claude-ui-design 作为第四阶段的主要知识来源；Semi Design 和 Emil mobile-native 按技术栈与具体问题调用；open-claude-ui-review 作为实施后的独立审查。其他资料已经确认有价值，但现在加载只会增加上下文竞争。

这套筛选之后，我们就能以大肥鱼首屏为第一个对象，开始真正的组件与布局设计，而不必再重新选一遍工具。
