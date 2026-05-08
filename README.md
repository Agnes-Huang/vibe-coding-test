# 高校选课管理系统（SpringBoot 3.x）

本项目用于完成“高校选课管理系统 - 学生选课基础处理工具”综合题，包含：
- A：Java 基础处理（去重、排序、格式化输出）
- B：SQL 统计题（GROUP BY / HAVING）
- C：SpringBoot 3.x 功能升级 + 简单页面
- D：数据架构、并发、索引设计分析
- E：AI 工具与提示词说明

## 1. 运行方式

### 环境要求
- JDK 17+
- Maven 3.9+

### 启动
```bash
mvn spring-boot:run
```

访问页面：`http://localhost:8080/`

## 2. 项目结构

- `src/main/java/com/example/enrollment/core/BasicEnrollmentProcessor.java`  
  第一题基础处理工具（可独立运行）。
- `src/main/java/com/example/enrollment/controller/EnrollmentController.java`  
  接口层：导入、查询、检索。
- `src/main/java/com/example/enrollment/service/EnrollmentService.java`  
  业务层：CSV解析、去重、排序、分类、检索。
- `src/main/java/com/example/enrollment/entity/EnrollRecord.java`  
  实体层：选课记录模型。
- `src/main/resources/templates/index.html`  
  页面：CSV 文本框导入 + 检索 + 数据展示。
- `src/main/resources/static/css/style.css`  
  页面样式。
- `src/main/resources/static/js/app.js`  
  前端交互逻辑。
- `docs/sql-answers.md`  
  SQL 题答案。
- `docs/design-analysis.md`  
  数据模型/ER图/并发/索引设计分析。

## 3. 功能说明（对应题目要求）

### A 部分：去重、排序、输出
- 去重规则：`studentId + courseId` 完全一致视为重复。
- 排序规则：先按 `studentId` 升序，再按 `courseId` 升序。
- 输出格式：`学生ID：XXX，课程ID：XXX，课程名称：XXX`。

### C 部分：SpringBoot 升级
- CSV 批量导入：支持文本框粘贴多行记录。
- 选课分类：公共课 / 专业课 / 选修课（支持手动标注或自动识别）。
- 选课检索：支持学生ID、课程ID、课程名称、课程类型四类关键词。
- 无匹配提示：返回“无匹配选课记录”。
- 前后端联动：导入后后端处理并回显到页面。

## 4. SQL 答案

见 `docs/sql-answers.md`。

## 5. 分析设计

见 `docs/design-analysis.md`。

## 6. AI 工具说明（作业要求）

### 使用的 AI 编程工具
- ChatGPT（GPT-5/Codex 类模型）

### 给 AI 的完整提示词（可复用）

```text
请基于 SpringBoot 3.x 生成一个“高校选课管理系统 - 学生选课基础处理工具”示例项目，要求如下：
1) 严格采用分层架构：Controller -> Service -> Entity，禁止把业务逻辑写在 Controller。
2) 后端核心能力：
   - 接收学生选课记录列表并处理：按 studentId+courseId 去重；
   - 处理后按 studentId 升序，再按 courseId 升序；
   - 增加选课分类：课程类型包括公共课、专业课、选修课，支持手动标注或按课程名称自动识别；
   - 增加选课检索：支持按学生ID、课程ID、课程名称、课程类型四种关键词检索；
   - 检索无结果时返回“无匹配选课记录”。
3) 页面要求（简单即可）：
   - 仅一个页面；
   - 提供一个文本框用于输入多行 CSV（每行 studentId,courseId,courseName,courseType）并批量导入；
   - 导入后展示处理结果；
   - 页面可显示后端样例数据并支持检索。
4) 前后端衔接：
   - 页面提交 CSV 到 SpringBoot 后端；
   - 后端完成去重、排序、分类后，将结果回显到页面。
5) 性能要求：
   - 1000 条以上数据的检索/排序响应 <= 1 秒；
   - 支持单次 >= 500 条的批量导入。
6) 请输出完整代码：pom.xml、Controller、Service、Entity、HTML/CSS/JS。
```

### 代码归因说明

#### AI 生成
- SpringBoot 项目骨架（`pom.xml`、启动类）。
- 分层代码初版（Entity、Service、Controller）。
- 前端页面初版（HTML/CSS/JS）。

#### 人工修改优化（本次实现）
- 增加 `BasicEnrollmentProcessor`，满足第一题“独立Java处理工具”要求。
- 调整 CSV 解析，自动忽略空行/异常行，提升鲁棒性。
- 兼容“标签格式录入”（如 `学生ID：S001244，课程ID：C000161，课程名称：数学`）。
- 增加分类自动识别逻辑（课程名关键词推断课程类型）。
- 增加检索无结果提示“无匹配选课记录”。
- 增加导入成功弹窗，并在弹窗中展示导入统计信息（更易读的用户提示语）。
- 增加课程ID冲突校验：同一 `courseId` 对应不同课程名/类型时自动跳过冲突行。
- 调整系统初始样例数据为题目示例（`Java程序设计`、`计算机网络`）以贴合作业场景。
- 增加处理耗时回显，便于性能验收。
- 增加单元测试覆盖去重排序和 1000+ 记录检索时延。

## 7. 性能说明

当前实现采用内存结构：
- 去重：`HashSet / LinkedHashSet`，平均 O(n)
- 排序：`Comparator` + TimSort，O(n log n)
- 检索：线性扫描，1000~几千条下通常远小于 1 秒

如需更大规模，可升级为数据库索引查询或搜索引擎方案。
