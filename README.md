# 📚 Problem Tracker - 题号标记器

一个面向刷题用户的 **熟练度追踪工具**。不保存题面、不做解析、不安排学习计划，只做一件事：**把你对每道题「当前熟不熟」用颜色记录下来**。

---

## ✨ 这个 App 是什么

这是一个极简的「题号标记器」（Problem Tracker）。

你创建一个题集（例如《高数1000题》），输入每个 Unit 的题数，App 会自动生成完整题号网格（U1-1、U1-2…）。之后你每做完一题，只需要在网格里点一下并选择「对/错」，题块就会变色：

| 颜色 | 含义 |
|------|------|
| ⬜ 灰色 | 从未做过 |
| 🟩 绿色 | 当前已掌握 |
| 🟥 红色（由浅到深） | 当前不稳，错得越多颜色越深 |

### 🔄 可逆的熟练度系统

关键在于它**不是一次性「错题本」**。每次你重新做一遍题，都可以再次标记：

- 做错 → 红色加深一档
- 做对 → 红色变浅一档（浅红做对会回到绿色）
- 绿色如果再错，会回到浅红

因此颜色本质上是一个**可逆的熟练度状态**，你扫一眼就知道：哪些题最不稳、哪些单元整体更薄弱。

---

## 🎯 我为什么要写这个 App

我刷题时遇到的痛点很明确：**记录成本太高、工具太复杂、而且经常偏离「做题本身」**。

### 痛点分析

1. **传统错题本/笔记系统**需要大量整理：抄题、截图、归档、写原因，时间很快被「管理」吃掉。

2. **记忆曲线/时间调度类工具**（比如背单词那套）对「刷题」不总是适配：刷题的节奏往往是按章节、按作业、按考试目标走，并不想被算法安排未做题。

3. 但我又确实需要一个东西能让我随时回答：**「我现在到底哪里最不熟？」** 并且能在反复练习后体现「变稳」的过程，而不是只留一个永久红点。

### 解决方案

所以我做了这个 App：

> 用 **题号网格 + 颜色深浅** 把复杂的学习状态压缩成最小动作（点一下）。

它让记录回到「刷题节奏」里：**你只负责做题和打标，App 负责把结构保持完整、把状态可视化**，让我能更快地发现薄弱点并进行针对性训练。

---

## 📱 功能特性

- **一键创建题集**：输入题集名称 + 每单元题数，自动生成完整题号结构
- **题号网格视图**：可视化展示所有题目的熟练度状态
- **快速标记**：点击题号即可标记对/错，自动更新熟练度颜色
- **多题集管理**：支持创建多个题集，随时切换
- **学习统计**：日历热力图展示练习活跃度
- **长按查看详情**：查看每道题的正确率和练习历史

---

## 🛠️ 技术栈

本项目采用现代 Android 开发技术栈：

### 架构 & 设计模式
- **MVVM 架构**：清晰的数据流向，UI 与业务逻辑分离
- **Clean Architecture**：分层设计（data / domain / presentation）
- **Repository Pattern**：统一的数据访问层

### UI 框架
- **Jetpack Compose**：声明式 UI 框架，构建现代化界面
- **Material Design 3**：遵循最新 Material 设计规范
- **Navigation Compose**：单 Activity 多 Compose 导航

### 数据层
- **Room Database**：本地数据持久化
- **Kotlin Flow**：响应式数据流
- **KSP**：Kotlin Symbol Processing，用于 Room 注解处理

### 其他
- **Kotlin Coroutines**：异步编程
- **StateFlow**：状态管理
- **ViewModel**：生命周期感知的数据持有者

---

## 📂 项目结构

```
app/src/main/java/com/edward/todov2/
├── data/                    # 数据层
│   ├── AppDatabase.kt       # Room 数据库定义
│   └── Models.kt            # 数据实体 & DAO
├── di/                      # 依赖注入
│   └── AppContainer.kt      # 简单的服务定位器
├── domain/                  # 领域层
│   ├── repository/          # Repository 接口
│   └── usecase/             # 业务用例
├── presentation/            # 表现层
│   ├── common/              # 通用 UI 组件
│   ├── problemgrid/         # 题号网格页面
│   ├── settings/            # 设置页面
│   ├── setup/               # 创建题集页面
│   ├── statistics/          # 统计页面
│   └── unitlist/            # 主页 & 单元列表
├── ui/                      # UI 主题 & 设计系统
│   ├── AppDesignSystem.kt   # 统一设计组件
│   ├── ProficiencyPalette.kt # 熟练度颜色定义
│   └── theme/               # Material 主题
├── MainActivity.kt          # 主 Activity
├── StudyApp.kt              # Application 类
└── StudyViewModel.kt        # 全局 ViewModel
```

---

## 🚀 快速开始

### 创建题集

1. 点击主页「新建」按钮
2. 输入题集名称（如：高数1000题）
3. 输入每个单元的题数，格式如下：
   ```
   U1: 32
   U2: 18
   U3: 25
   ```
4. 点击「创建」，App 会自动生成所有题号

### 标记题目

1. 在主页选择要刷的单元
2. 点击任意题号
3. 选择「✓ 正确」或「✗ 错误」
4. 题号颜色会自动更新

### 查看统计

- 底部导航栏切换到「统计」页面
- 查看日历热力图了解练习活跃度
- 长按任意题号查看详细正确率

---

## 📄 License

MIT License

---

## 🔒 Privacy & Offline Policy

- **No Tracking**: This app does not collect any user data.
- **No Ads**: This app contains no advertisements.
- **Works Offline**: All data is stored locally on your device. No internet connection is required.

---

## 👨‍💻 Author

Edward

---

> 🎯 **核心理念**：把「刷题记录」这件事的成本降到最低，让你专注于做题本身。
