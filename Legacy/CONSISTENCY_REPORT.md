# JAR 反编译与一致性测试报告

## 导出内容

| 原始 JAR | 导出模块 | POM 来源 | 重新构建产物 |
| --- | --- | --- | --- |
| `Legacy/LuoFM-1.0-SNAPSHOT.jar` | `LuoFM/` | JAR 内 `META-INF/maven/cn.luotiany1/LuoFM/pom.xml` | `LuoFM/target/LuoFM-1.0-SNAPSHOT.jar` |
| `Legacy/LuoDeath-1.0-SNAPSHOT.jar` | `LuoDeath/` | JAR 内 `META-INF/maven/cn.luotiany1/LuoDeath/pom.xml` | `LuoDeath/target/LuoDeath-1.0-SNAPSHOT.jar` |

Java 源码由 CFR 0.152 从各 JAR 的 `.class` 文件反编译。`plugin.yml` 和 `config.yml` 按原始内容导出到每个模块的 `src/main/resources`。每个模块也保留一份 `original-*.jar`，便于后续本地比对。

## 构建结果

两个模块均执行 `mvn -B clean package` 成功，编译目标均为 Java 8（class major version 52）。

## 一致性检查

| 检查项 | LuoFM | LuoDeath |
| --- | ---: | ---: |
| 原始 / 重建类数量 | 11 / 11 | 1 / 1 |
| 缺失或新增类 | 0 / 0 | 0 / 0 |
| `plugin.yml` SHA-256 一致 | 是 | 是 |
| `config.yml` SHA-256 一致 | 是 | 是 |
| 所有类的 public API（`javap -public -s`）一致 | 是 | 是 |

LuoFM 的两个类在“含 private 合成 lambda 方法名”的完整 `javap -p -s` 文本中存在差异：`LFMInventoryListener` 与 `SelectCommandHandler`。这是重新编译时编译器对私有 lambda 辅助方法重新编号所致，公共 API、类集合、资源和字节码目标版本均一致；它不表示可调用插件接口发生变化。反编译再编译无法合理要求逐字节 JAR 哈希一致，因为常量池、调试信息、构建时间与该类合成名称可变。
