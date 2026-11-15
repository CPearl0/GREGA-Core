package io.github.cpearl0.grega.core.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

/** Keeps DyeColor initialization safe while Botania installs its data fixers before bootstrap. */
@Pseudo
@Mixin(targets = "dev.latvian.mods.kubejs.color.KubeColor", remap = false)
public interface KubeColorMixin {
    // KubeJSCodecs also initializes IntProvider.CODEC, which needs bootstrapped registries.
    // Keep this color codec independent, following the fix in KubeJS-Mods/KubeJS#1207.
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target =
            "Ldev/latvian/mods/kubejs/KubeJSCodecs;stringResolverCodec(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static <T> Codec<T> grega$bootstrapSafeColorCodec(
            Function<T, String> serialize, Function<String, T> resolve) {
        return Codec.STRING.flatXmap(name -> {
            T value = resolve.apply(name);
            return value == null
                    ? DataResult.error(() -> "Unknown element id: " + name)
                    : DataResult.success(value);
        }, value -> DataResult.success(serialize.apply(value)));
    }
}
