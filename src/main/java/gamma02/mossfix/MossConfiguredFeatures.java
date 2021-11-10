package gamma02.mossfix;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import static net.minecraft.data.worldgen.Features.MOSS_VEGETATION;

public class MossConfiguredFeatures
{
    public static ConfiguredFeature<VegetationPatchConfiguration, ?> MOSS_PATCH_CONFIGURED = Mossfix.MOSS_PATCH_FEATURE.get().configured(new VegetationPatchConfiguration(
        BlockTags.MOSS_REPLACEABLE.getName(),
        new SimpleStateProvider(Blocks.MOSS_BLOCK.defaultBlockState()), () -> {
    return MOSS_VEGETATION;
    }, CaveSurface.FLOOR, ConstantInt.of(1), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F));

    public static void RegisterConfiguredFeatures() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation("mossfix", "moss_configured_feature"), MOSS_PATCH_CONFIGURED);
    }
}
