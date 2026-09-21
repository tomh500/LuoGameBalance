# LuoGameBalance

面向 Spigot 26.2 的 Minecraft 生存平衡插件。它以自定义铁砧和锻造台工作站提供附魔处理、物品升级和死亡经验返还，并将经验成本统一为原始经验点。

## 功能

- 自定义铁砧：合并附魔、提取整件物品的附魔、拆分附魔书、修复工具。
- 自定义锻造台：木质→石质→铁质→金质→钻石的阶梯升级。
- 死亡经验返还：按可配置比例保存并在重生后返还经验。
- 中英双语：默认 `zh_CN`，可切换到 `en_US`。
- 创造模式不会校验或扣除经验。
- GUI 末位按钮可让下一次右键同类工作站打开原版界面一次。

## 安装

1. 使用 Java 25 和 Spigot 26.2 或兼容服务端。
2. 将 `target/luo-game-balance-1.0.0.jar` 放入服务器的 `plugins/` 目录。
3. 启动服务器后修改 `plugins/LuoGameBalance/config.yml`，随后执行管理员命令重载。

## 使用方式

普通玩家右键真实的铁砧或锻造台即可打开插件 GUI；游戏功能不能通过命令绕过工作站。

管理员命令：

```text
/luogamebalance reload
/luogamebalance status
/lgb reload
```

权限节点：`luogamebalance.admin.*`，默认授予 OP。

## 配置与实验玩法

`config.yml` 包含完整中文注释。稳定玩法默认启用；提取、拆分、合并、修复、升级和死亡经验的实验规则默认关闭。

启用 `experimental.upgrade.enabled` 后，每次阶梯升级还要求玩家背包持有 `experimental.upgrade.template` 指定的锻造模板。模板仅是可重复使用的通行证，不会被消耗。

## 构建

```text
mvn -B verify
```

构建产物位于 `target/`。Spigot API 为 `provided` 依赖，不会被打入插件 JAR。

## 开发约定

- 使用 Java 25 和 Spigot API 26.2。
- 物品与经验操作必须先完成全部前置校验，再提交变更。
- 禁止临时赋予 OP 或重载其他插件。
