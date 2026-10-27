# Arcana

**Minecraft 26.2 · NeoForge 26.2.0.93 · Java 25**

> 本模组是 [KevinT742MinecraftMod](../README.md) 家族仓库的成员 **arcana**，
> 源码位于仓库的 `arcana/` 目录。

Arcana 把 Genesis 的"游离魔力"变成玩家用得上的东西。

它**不做自己的魔法**：每个地区有多少魔力、魔力是什么性格，都由 Genesis 决定。
Arcana 只给玩家操作它的办法——一座**奥术祭坛**，和一枚**奥术透镜**。

---

## 一、它能做什么

| | |
|---|---|
| **奥术祭坛** `arcane_altar` | 站在上面右键，祭坛按下面三件事之一行动 |
| **奥术透镜** `arcane_lens` | 右键，告诉你脚下的魔力浓度与强度 |
| **魔药瓶** `arcane_vial` | 用来装魔力的瓶子，装了多少存在物品自己身上 |

祭坛的三件事：

* **空瓶右键** → **抽取**：把这一片地的游离魔力收进瓶子里，地因此变贫瘠
* **满瓶右键** → **倒回**：把瓶里的魔力倒回脚下的地里，瓶子变空
* **拿着魔力材料右键** → **献祭**：材料被消耗，它的魔力变成脚下的魔力

> **这是 Arcana 存在的全部理由**：魔力的世界是**存下来的**，所以祭坛对一片地做过的事
> 一直算数。那片被抽干的地会一直贫瘠，直到有人再灌回去。

---

## 二、它怎么用上 Genesis

```groovy
dependencies {
    // ★ 这一行就是"家族"二字的全部含义
    implementation project(':base')
}
```

```java
// 读这一片地的魔力
Optional<MagicField> field = GenesisApi.magicField(serverLevel);

// 抽干它
RitualResult result = RitualRules.drain(field.concentrationAt(x, z));
field.influence(x, z, RitualRules.RADIUS, result.groundDelta());

// 祭坛的硬度、抗性、声音，全部由 Genesis 的模板推导
BLOCKS.registerBlock("arcane_altar", properties -> new ArcaneAltar(
        GenesisApi.blockProperties(ALTAR).apply(properties)));
```

Arcana **不碰 Genesis 的内部结构**：它只走 `genesis.mc.GenesisApi` 这一个门面。

---

## 三、工程结构

```
arcana/src/main/java/arcana/
├── ArcanaMod.java                   模组入口
├── api/                             契约（兄弟模组可用）
│   ├── RitualOutcome.java           仪式结果的五种可能
│   └── RitualResult.java            仪式的结果（纯数据）
├── core/ritual/
│   └── RitualRules.java             纯算法：浓度 + 材料 → 结果
└── mc/                              Minecraft 适配层
    ├── ArcanaAltar.java             奥术祭坛
    ├── ArcaneLens.java              奥术透镜
    ├── ArcaneVial.java              魔药瓶（装了多少存在物品上）
    ├── ArcanaBlocks.java            方块注册
    ├── ArcanaItems.java             物品注册
    └── ArcanaRituals.java           唯一与 Genesis 打交道的地方
```

**规则是纯的**：一个仪式只是"浓度 + 献祭了多少 → 结果"的函数，和游戏没有关系。
`core/ritual` 因此可以用**窄类路径**编译并自检——类路径里只有本模组的类，
没有游戏。这机械地证明了仪式规则不依赖 Minecraft。

---

## 四、自检覆盖

`./gradlew :arcana:selfTest`（或离线验证工具链）校验 25 条断言：

| 分组 | 覆盖内容 |
|---|---|
| **抽取** | 贫瘠的地面抽不出东西；抽出的量符合规则；地面按抽走的量变贫瘠；瓶子装不下超过容量 |
| **灌注** | 没有魔力的材料被拒绝；献祭的量就是地面得到的量；饱和的地面装不下更多；差一点的地面只吃得下剩下的 |
| **守恒** | 从一片地抽出来、倒进另一片地的魔力，中间不会变多 |

---

## 五、构建与运行

命令在**家族根目录**执行，用 `:arcana:` 指定成员：

```bash
./gradlew :arcana:build         # 构建
./gradlew :arcana:runClient     # 启动客户端
./gradlew :arcana:runServer     # 启动服务端
./gradlew :arcana:selfTest      # 运行自检
```

离线验证（够不到 `maven.neoforged.net` 时）：

```powershell
cd verification
pwsh -File fetch26.ps1         # 首次：下载 Minecraft 26.2
pwsh -File prep.ps1            # 解包并生成编译类路径
pwsh -File build-and-test.ps1  # 编译全族 + 跑全族自检
```

> 在成员目录里单独跑 Gradle 是构建不了的：成员依赖根 `gradle.properties` 的版本号
> 和根 `build.gradle` 的插件声明，所以**必须在装配好的母文件夹里构建**。

---

## 六、许可

MIT
