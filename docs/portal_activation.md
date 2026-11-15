# 开启传送门之魔符 / Glyph of Open Portal

物品与魔符 ID：`grega_core:glyph_open_portal`。在抄写台制作并学习后，组合「接触 → 开启传送门」。

| 传送门 | 施法位置 | 保留的原生条件 |
| --- | --- | --- |
| 下界 | 完整黑曜石门框内侧 | 原版门框与主世界/下界限制 |
| 暮色森林 | 水池或水池底部 | 水池、底部、植物边框、大小、维度、权限、进度与安全目的地检查 |
| 天境 | 门框内侧 | 原生可用门框、维度与配置检查 |
| 植物魔法精灵传送门 | 精灵传送门核心 | 原生门体、自然水晶、魔力池、开启与维持魔力消耗；保留精灵交易功能 |

普通点火、丢钻石、放水和森林法杖不再直接开启对应传送门。只限制玩家开启入口的路径，各模组自动生成目的地和返回门的逻辑不变。

The glyph invokes each mod's native activation logic with a scoped spell-activation flag. Nether ignition, Twilight's catalyst activation, Aether's water activation and Botania's wand activation require that flag. Native structure, configuration, progression and mana checks remain in effect. Generated destination/return portals and Elven trading retain their original behavior.

Integrations use narrow mixin bridges to the installed Minecraft 1.21.1 mod APIs, without requiring local third-party JARs on the compile classpath. Review these method signatures when updating the corresponding mods.
