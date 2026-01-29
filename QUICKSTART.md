# 🎯 快速开始指南

## 立即运行

1. **编译并运行**
   ```bash
   ./gradlew clean assembleDebug
   ./gradlew installDebug
   ```

2. **或在 Android Studio 中**
   - 点击 Run 按钮 ▶️
   - 选择设备/模拟器

---

## 🆕 新功能快速预览

### 1. 单元列表界面
- **空状态**: 首次启动会看到欢迎页面
- **添加单元**: 点击右下角 ➕ 按钮
- **批量输入**: 支持 `U1:20 U2:18 U3:25` 格式
- **查看进度**: 每个卡片显示掌握进度条和熟练度分布

### 2. 题目网格界面
- **点击题目**: 弹出对/错选择对话框
- **长按题目**: 查看详细统计(正确率、次数)
- **颜色变化**: 标记后颜色会平滑过渡
- **触觉反馈**: 点击时有震动反馈

### 3. 统计界面
- **活跃度热力图**: 最近35天的练习密度
- **总体统计**: 总练习次数和正确率
- **最近日期**: 显示最后一次练习时间

---

## 🎨 主题切换

- **跟随系统**: 自动适配系统深色/浅色模式
- **动态颜色** (Android 12+): 自动匹配系统壁纸颜色

---

## 💡 使用技巧

### 批量创建单元
支持多种格式:
```
U1: 20 U2: 18 U3: 25
```
或
```
Unit1: 10
Unit2: 20
Unit3: 15
```

### 查看题目详情
**长按任意题目**即可看到:
- 当前熟练度等级
- 正确/错误次数
- 正确率百分比

### 删除单元
在单元卡片上点击 🗑️ 图标，会弹出确认对话框

---

## 🏗️ 架构概览 (开发者)

```
UI Layer (Presentation)
    ↓ Actions
ViewModel Layer
    ↓ Business Logic
UseCase Layer
    ↓ Data Operations  
Repository Layer
    ↓ Database Access
Data Layer (Room)
```

### 核心文件
- `StudyApp.kt` - Application 入口
- `AppContainer.kt` - 依赖注入
- `presentation/*/` - UI 和 ViewModel
- `domain/` - 业务逻辑

---

## 🐛 故障排除

### 编译错误
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### 数据问题
数据库位置: `/data/data/com.edward.todov2/databases/study_database`

### 清除数据
在设置中删除所有单元，或清除应用数据

---

## 📱 系统要求

- **最低 SDK**: Android 7.0 (API 24)
- **目标 SDK**: Android 14 (API 36)
- **推荐**: Android 12+ (支持动态颜色)

---

## 🎯 下一步改进建议

### 功能增强
- [ ] 题目标签/分类
- [ ] 导出/导入数据
- [ ] 题目笔记
- [ ] 搜索功能
- [ ] 统计图表优化

### 技术改进
- [ ] 使用 Hilt 替代手动 DI
- [ ] 添加单元测试
- [ ] 添加 UI 测试
- [ ] 数据库迁移策略
- [ ] 性能监控

### UI/UX 优化
- [ ] 添加启动页
- [ ] 引导页教程
- [ ] 更多动画
- [ ] 主题选择器
- [ ] 字体大小调节

---

## 📞 支持

遇到问题? 检查:
1. Android Studio 日志 (Logcat)
2. 编译错误信息
3. MODERNIZATION_REPORT.md 文档

---

**享受使用! 🎉**
