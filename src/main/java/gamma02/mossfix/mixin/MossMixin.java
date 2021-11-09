package gamma02.mossfix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.Features;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MossBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(MossBlock.class)
public class MossMixin
{
    /**
     * @reason no other way
     * @author gamma_02
     */
    @Overwrite
    public boolean isValidBonemealTarget(BlockGetter p_153797_, BlockPos p_153798_, BlockState p_153799_, boolean p_153800_) {
        return true;
    }


    /**
     * @author gamma_02
     * @reason no other way
     */
    @Overwrite
    public void performBonemeal(ServerLevel p_153792_, Random p_153793_, BlockPos p_153794_, BlockState p_153795_) {
        Feature.VEGETATION_PATCH.place(new FeaturePlaceContext<>(p_153792_, p_153792_.getChunkSource().getGenerator(), p_153793_, p_153794_.above(), Features.MOSS_PATCH_BONEMEAL.config()));
    }
}
