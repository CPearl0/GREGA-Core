# 启动兼容补丁 / Bootstrap compatibility

Botania `457-20261114.172517-32` 在 Minecraft 注册表启动前安装数据迁移规则时读取 `DyeColor`。KubeJS `2101.7.1-build.181` 为 `DyeColor` 实现了 `KubeColor`，它的颜色编解码器间接初始化 `IntProvider.CODEC`，导致 `Not bootstrapped` 崩溃。

`KubeColorMixin` 只替换颜色编解码器的创建调用，保持字符串解析与错误处理行为，避免在此时初始化其他依赖注册表的编解码器。保留 Botania 数据迁移和当前模组版本。

Botania initializes `DyeColor` while installing data fixers before Minecraft bootstrap. KubeJS build 181's `KubeColor` codec indirectly initializes `IntProvider.CODEC`, which accesses uninitialized registries. The mixin constructs the same color codec independently without changing data migration or color serialization.

修复原理参考 [KubeJS 上游 #1207](https://github.com/KubeJS-Mods/KubeJS/pull/1207)（[commit ba1f722587e5](https://github.com/KubeJS-Mods/KubeJS/commit/ba1f722587e5)）。当前补丁针对整合包锁定的 KubeJS build 181；以后升级到包含上游修复的版本时，应一并移除本补丁。

Only compilation and JAR replacement are performed under the maintainer's current verification instructions. Runtime acceptance is performed by the maintainer.
