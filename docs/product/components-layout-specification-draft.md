# FatFish · 组件布局设计 · 笔记稿

**Components & Layout Specification**

上游：Product Behavior、Information Architecture、Interaction Semantics。

> 自然语言描述空间系统只能起到约束实现，无法充分表达真实比例、视觉层级与对齐关系、组件混排空间效果、多端形态变化、组件各状态的视觉差异。
> 组件布局设计由可编辑设计稿+组件规范+少量设计说明组成
> 结构设计和组件设计，不需要提前做成高保真视觉稿。先把空间和组件关系画对，再进入 Typography、Color、Material、Motion。
> 另外，优先看github上的开源小众技术子集，一般质量会反而比闭源的好很多。

目前优先考虑的方案：[OpenPencil](https://github.com/open-pencil/open-pencil?utm_source=chatgpt.com) + [React Cosmos](https://github.com/react-cosmos/react-cosmos?utm_source=chatgpt.com)。前者负责可编辑的空间设计，后者负责真实组件的状态与交互验证。两者分别覆盖我们当前最缺的两种能力。

---

当前：区分三种设计工具的工作方式：

1. Design-first：在画布上设计，再转换成代码。适合探索布局比例、视觉关系和设计系统。
2. Code-first：直接通过真实组件与代码构建可视界面。适合检查输入法、滚动、状态变化和响应式表现。
3. Hybrid：先在可编辑画布中收敛空间方案，再用真实组件原型验证。适合 FatFish 当前已经有完整 IA、但还缺乏视觉表达的阶段。

这里真正需要选择的是设计过程中的 Source of Truth。画布和代码各有优势；若两者长期独立修改，就会产生 Design–Code Drift（设计与实现偏离）。

## 1. 开源工具候选

以下把候选按实际用途、源码开放情况和维护状态进行了筛选。

| 工具                                                                                  | 最适合做什么            | 优势                                                             | 成本与局限                                            |
| ----------------------------------------------------------------------------------- | ----------------- | -------------------------------------------------------------- | ------------------------------------------------ |
| [OpenPencil](https://github.com/open-pencil/open-pencil?utm_source=chatgpt.com)     | AI 辅助画布设计         | MIT；提供 CLI、MCP、组件变体、Auto Layout、设计检查和代码导出；支持读取 `.fig` / `.pen` | 2026 年的新项目，功能迭代快，复杂文档兼容性需要验证                     |
| [Penpot](https://github.com/penpot/penpot?utm_source=chatgpt.com)                   | 完整设计系统与高保真页面      | MPL-2.0；成熟的组件、Tokens、CSS Grid/Flex、原型和 MCP；支持自托管               | 平台规模较大，设计资产管理和前端开发构成两套工作流                        |
| [React Cosmos](https://github.com/react-cosmos/react-cosmos?utm_source=chatgpt.com) | 真实 React 组件原型     | MIT；针对组件状态建立隔离实验环境，直接复用实际组件                                    | 自由空间探索主要通过代码修改完成，前期需要组件骨架                        |
| [Ladle](https://github.com/tajo/ladle?utm_source=chatgpt.com)                       | 轻量 React 组件 Story | MIT；基于 Vite，适合快速呈现多种组件变体                                       | 更偏组件展示和测试，复杂跨组件交互仍需独立页面                          |
| [Histoire](https://github.com/histoire-dev/histoire?utm_source=chatgpt.com)         | Vue 等生态的组件实验台     | MIT；可视化组件状态、交互与文档，适合基于 Vite 的组件开发                              | 依赖所选框架生态，主要解决组件层                                 |
| [Onlook](https://github.com/onlook-dev/onlook?utm_source=chatgpt.com)               | 在运行中的 UI 上修改源码    | Apache-2.0；视觉编辑直接映射到真实 DOM 和代码                                 | 开源仓库主要面向 Next.js + Tailwind；新托管产品处于 Early Access |
| [Excalidraw](https://github.com/excalidraw/excalidraw?utm_source=chatgpt.com)       | 低保真布局与交互草图        | MIT；表达速度快、文件可版本化、容易批注和比较                                       | 对组件实例、响应式约束及精细视觉的表达较弱                            |

以上依据 GitHub 项目文档和 2026 年 11 月 3 日查询的维护信息。OpenPencil、React Cosmos、Histoire 与 Penpot 均有近期提交；Ladle 最近一次提交在 2026 年 6 月。

还有两个需要单独区分的候选：

[pen.dev（原 pencil.dev）](https://www.pen.dev/?utm_source=chatgpt.com) 是 `.pen` 工作流代表。它提供 Agent 可读写的 JSON 设计格式、MCP 和 IDE 画布集成，设计文件能够跟随 Git 管理。当前可以验证的是其开放文件格式与外部接口；编辑器完整开源程度与 OpenPencil存在区别。

[Puck](https://github.com/puckeditor/puck?utm_source=chatgpt.com) 是 MIT 许可的 React 可视化编辑器，主要通过拖放真实 React 组件来组装页面。它尤其适合可配置页面和 CMS；FatFish 的会话滚动、输入法及消息状态则更适合在组件实验台中验证。

## 2. 可尝试组合

方案 A · OpenPencil + React Cosmos

OpenPencil 负责布局、组件外观和可编辑设计稿；React Cosmos 负责组件状态、输入、滚动与交互验证。

优势：完全开源、Agent 可编程、人工 Review 方便，设计文件与真实组件各有归属。代价：需要建立画布到代码的映射，OpenPencil 的兼容性仍处于演进期。

方案 B · React Cosmos 单工具

直接用 React 实现主会话原型，在 Cosmos 中切换不同状态与视口。

优势：最快检验真实交互，原型代码可以直接保留为产品组件。代价：早期空间探索依赖代码修改，容易围绕第一版结构持续微调。

方案 C · Penpot + React Cosmos

Penpot 作为长期设计系统和组件库，Cosmos 承担真实交互验证。

优势：稳定、成熟、适合未来扩大团队和系统性管理 Tokens。代价：设计资产管理更重，早期单人探索的操作成本较高。 （暂时不考虑）

方案 D · Onlook

在真实 Next.js + Tailwind 页面上进行视觉编辑，直接修改代码。

优势：设计与代码共享同一实体。代价：框架限制明显，开源版本和新托管产品需要分别评估。

另外，[tldraw](https://github.com/tldraw/tldraw?utm_source=chatgpt.com) 适合构建更自由的画布，但其当前 SDK 使用专门的 tldraw License，生产部署有许可要求。若希望采用宽松开源许可的白板组件，Excalidraw 更直接。

## 3. OpenPencil 特别介绍

OpenPencil 有几个对 Agent 协作特别有价值的能力，值得单独提出。

它允许 Agent 通过 MCP 修改设计节点，也提供独立的 CLI 查询节点树、检查布局和提取 Tokens。其内置的 `analyze spacing`、`analyze typography`、`lint` 等操作，使我们能够在画布外做确定性检查。它还支持把设计导出为 JSX、Storybook stories 等格式。

这些能力带来一种新的协作方式：

Agent负责生成和调整设计结构，人类可以直接在画布里修改，再把最终接受的结构归约成可复用组件，这是目前纯 Markdown 做不到的工作方式。

值得注意的是，OpenPencil 原生工作流偏向 `.fig`，可以打开与转换支持的 `.pen` 文件。`.pen` 兼容程度应根据实际文档验证，尤其要检查组件实例、复杂布局和导出后的结构保真。

它的 MIT 源码、CLI、MCP 与设计分析能力都已经在仓库中明确提供；另一方面，其 README 也主动标记为 Active Development，意味着我们应通过真实页面测试它的稳定性。


在 OpenPencil 中绘制手机和桌面主会话。用同一组具有代表性的真实内容，观察导航、正文、输入和详情空间的关系。

产物：两个主要视口的实际设计画布。

第二轮：组件变体和状态

从已选定页面中提取 `ConversationRow`、`MessageBody`、`Composer`、`ApprovalSurface` 等组件。每种组件至少呈现常态及真正改变空间的状态，例如长消息、附件、多行输入和授权操作。

产物：可编辑组件库与状态设计稿。

第三轮：可运行组件原型

在 React Cosmos 中用真实组件验证滚动锚定、输入法、详情展开、导航切换和异常反馈，再根据实际运行效果回改画布设计。

产物：可交互原型，以及少量明确记录的 Design Decisions。

组件布局和视觉语言之间存在双向依赖。 排版、间距、组件边界和表面处理会直接改变空间关系，因此第四阶段就需要采用初步的视觉规范，直到布局具有可判断的真实效果；第五阶段再将这些选择统一归约为完整的视觉系统和 Motion Tokens。

这样，`components-layout-specification.md` 应该逐渐成为设计索引与决策记录，引用实际画布、组件原型和验收结果。它保留不可从图片中看出来的重要约束；大部分空间信息则由设计稿本身表达。
