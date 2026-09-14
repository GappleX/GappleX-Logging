# GXL (GapplX-Logging)

一个面向 Java 8 的轻量日志门面。核心模块 `gxl-core` 只依赖 JDK，提供一套不绑定具体日志实现的 API；`gxl-loggers` 提供对 SLF4J、JUL、Log4j 等实现的适配。

## 特性

- **零运行时依赖**：`gxl-core` 只依赖 JDK，不引入任何日志框架。
- **默认安全**：未配置时使用 NOOP logger，不会因为少装一个依赖就抛异常或刷屏。
- **占位符风格**：支持 SLF4J 风格的 `{}` 占位符，最后一个 `Throwable` 参数自动作为异常处理。
- **懒加载 + 缓存**：`getLogger` 返回代理，第一次实际输出时才创建真实 logger；同名 logger 全局缓存。
- **可热切换**：`setLoggerCreator(..., force=true)` 支持在运行时切换底层实现，已有代理会自动重建。
- **状态可查**：`LoggerFactory.getStatus()` 返回 JSON 格式的当前状态，便于排查配置问题。

## 环境要求

- Java 8 及以上
- 构建：Maven 3.6+
- 开发环境使用 JDK 17 + GraalVM 验证，编译目标为 Java 8

## 模块结构

```
gxl
├── gxl-core                            日志门面核心 API（Logger / LoggerFactory / 格式化）
├── gxl-loggers                         各类日志实现的适配器聚合
│   ├── gxl-loggers-slf4j               适配 SLF4J
│   ├── gxl-loggers-jul                 适配 java.util.logging
│   ├── gxl-loggers-log4j               适配 Log4j
│   └── gxl-loggers-systemInterceptor   接管 System.out / System.err
└── logax                               独立实验模块
```

## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>io.github.gapplex</groupId>
    <artifactId>gxl-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 2. 安装一个 logger 实现

`gxl-core` 默认使用 NOOP。启动时安装一个真实实现：

```java
LoggerFactory.setLoggerCreator(SimpleLogger::new);
```

如果在没有安装任何实现的情况下调用了日志方法，`LoggerFactory` 会向 `System.err` 打印一次警告（可通过系统属性关闭，见下文），但不会抛异常。

### 3. 使用

```java
public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        log.info("application started");
        log.debug("user={}, id={}", "alice", 42);

        try {
            int x = 1 / 0;
        } catch (Exception e) {
            log.error("calculation failed: {}", "divide by zero", e);
        }
    }
}
```

## API 概览

### Logger

```java
void debug(String msg);
void info(String msg);
void warn(String msg);
void error(String msg);

void debug(String msg, Throwable t);
// info / warn / error 同理

void debug(String msg, Object... args);
// 占位符格式： {} 会被依次替换，最后一个 Throwable 作为异常
```

`Logger.NOOP_LOGGER` 是一个不做任何事的实现，可用作默认值。

### LoggerFactory

```java
// 获取 logger（返回代理，懒加载）
Logger getLogger(Class<?> clazz);
Logger getLogger(String name);

// 安装/替换实现
void setLoggerCreator(Function<String, Logger> creator);
void setLoggerCreator(Function<String, Logger> creator, boolean force);

// 恢复为 NOOP
void resetToNoop();

// 查询当前状态
String getStatus();              // JSON 字符串
Map<String, Object> getStatusMap();
```

安装时会用一个名为 `__gxl_validation__` 的测试名调用一次 `creator`，如果返回 `null` 或抛异常则拒绝安装，避免后续所有调用点集体踩坑。

### 格式化

`MessageFormatter.format(msg, args)` 返回 `FormattingTuple`，其中包含最终消息和可能的 `Throwable`。

- `{}` 占位符按顺序替换，参数不足时填 `"null"`；
- 最后一个参数如果是 `Throwable`，会被提取出来，不参与占位符替换；
- `\` 是转义字符，`\\` 得到 `\`，`\{` 得到 `{`；
- 消息中的 `{x}`（`{` 后面不是 `}`）会被原样保留。

```java
FormattingTuple t = MessageFormatter.format("a={}, b={}", "x", 1);
t.getMessage();    // "a=x, b=1"
t.getThrowable();  // null
```

## 系统属性

| 属性 | 默认值 | 说明 |
| --- | --- | --- |
| `gxl.level` | `INFO` | `SimpleLogger` 的最低输出级别。可选 `DEBUG` / `INFO` / `WARN` / `ERROR` |
| `gxl.suppress` | `false` | 设为 `true` 时关闭 `LoggerFactory` 的所有提示性输出 |
| `gxl.status.maxDisplay` | `10` | `getStatus()` 中最多列出的已缓存 logger 数量 |

## 构建

```bash
git clone https://github.com/GappleX/GappleX-Logging.git
cd GappleX-Logging
mvn clean install
```

## 设计说明

- **门面 + 代理 + 懒加载**：`LoggerFactory.getLogger()` 返回 `LoggerProxy`，底层 `Logger` 在第一次真正使用时才创建。这样启动阶段的大量 `getLogger` 不会触发任何实现类的初始化。
- **单点安装**：通过 `setLoggerCreator` 替换实现，而不是让每个 logger 自己去 ServiceLoader 查找。这样谁装的、装的是什么、什么时候装的都能在 `getStatus()` 里查清楚。
- **NOOP 优先**：不配置也有安全默认值，避免日志系统本身成为应用启动的失败点。

## 许可证

[Apache License 2.0](LICENSE)

---

如果你只是想在自己项目里用一个不绑实现的日志门面，`gxl-core` + 一个 `gxl-loggers-*` 适配器就够了。如果你在用 GraalVM 做 native image，`gxl-core` 本身没有反射和动态代理，是 native 友好的；具体适配器的行为请参考各子模块的说明。