package gamma02.mossfix.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;


public class MossPatchGenerationFeature extends Feature<VegetationPatchConfiguration>
{
    public MossPatchGenerationFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }


    @Override
    public boolean place(FeaturePlaceContext<VegetationPatchConfiguration> context) {
        WorldGenLevel structureWorldAccess = context.level();
        VegetationPatchConfiguration vegetationPatchFeatureConfig = context.config();
        Random random = context.random();
        BlockPos blockPos = context.origin();
        Predicate<BlockState> predicate = getReplaceablePredicate(vegetationPatchFeatureConfig);
        int i = vegetationPatchFeatureConfig.xzRadius.sample(random) + 1;
        int j = vegetationPatchFeatureConfig.xzRadius.sample(random) + 1;
        Set<BlockPos> set = this.placeGroundAndGetPositions(structureWorldAccess, vegetationPatchFeatureConfig, random, blockPos, predicate, i, j);
        this.generateVegetation(context, structureWorldAccess, vegetationPatchFeatureConfig, random, set, i, j);
        return !set.isEmpty();
    }

    protected Set<BlockPos> placeGroundAndGetPositions(WorldGenLevel world, VegetationPatchConfiguration config, Random random, BlockPos pos, Predicate<BlockState> replaceable, int radiusX, int radiusZ) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockPos.MutableBlockPos mutable2 = mutable.mutable();
        Direction direction = config.surface.getDirection(); System.out.print(direction);
        Direction direction2 = direction.getOpposite(); System.out.println(direction2);
        Set<BlockPos> set = new HashSet();

        for(int i = -radiusX; i <= radiusX; ++i) {
            boolean bl = i == -radiusX || i == radiusX;

            for(int j = -radiusZ; j <= radiusZ; ++j) {
                boolean bl2 = j == -radiusZ || j == radiusZ;
                boolean bl3 = bl || bl2;
                boolean bl4 = bl && bl2;
                boolean bl5 = bl3 && !bl4;
                if (!bl4 && (!bl5 || config.extraEdgeColumnChance != 0.0F && !(random.nextFloat() > config.extraEdgeColumnChance))) {
                    mutable.setWithOffset(pos, i, 0, j);

                    //                    int k;
                    //                    for(k = 0; world.testBlockState(mutable, AbstractBlockState::isAir) && k < config.verticalRange; ++k) {
                    //                        mutable.move(direction);
                    //                    }
                    //
                    //                    for(k = 0; world.testBlockState(mutable, (blockStatex) -> {
                    //                        return !blockStatex.isAir();
                    //                    }) && k < config.verticalRange; ++k) {
                    //                        mutable.move(direction2);
                    //                    }
                    //REMEMBER: k < config.verticalRange!!!!
                    int k;
                    for(k=0; world.isStateAtPosition(mutable, MossPatchGenerationFeature::airSubstitute) && k < config.verticalRange; k++){//
                        mutable.move(direction);
                    }
                    for(k=0; world.isStateAtPosition(mutable, blockState -> !airSubstitute(blockState)) && k < config.verticalRange; k++){
                        mutable.move(direction2);
                    }


                    mutable2.setWithOffset(mutable, config.surface.getDirection());
                    BlockState blockState = world.getBlockState(mutable2);
                    BlockState firstState = world.getBlockState(mutable);
                    if (firstState.is(BlockTags.FLOWERS) || firstState.is(Blocks.GRASS) || firstState.is(Blocks.TALL_GRASS) || world.getBlockState(mutable).isAir() || firstState.is(Blocks.BIG_DRIPLEAF) || firstState.is(Blocks.BIG_DRIPLEAF_STEM) || firstState.is(Blocks.SMALL_DRIPLEAF)) {
                        int l = config.depth.sample(random) + (config.extraBottomBlockChance > 0.0F && random.nextFloat() < config.extraBottomBlockChance ? 1 : 0);
                        BlockPos blockPos = mutable2.mutable();
                        boolean bl6 = this.placeGround(world, config, replaceable, random, mutable2, l);
                        if (bl6) {
                            set.add(blockPos);
                        }
                    }
                }
            }
        }

        return set;
    }

    protected void generateVegetation(FeaturePlaceContext<VegetationPatchConfiguration> context, WorldGenLevel world, VegetationPatchConfiguration config, Random random, Set<BlockPos> positions, int radiusX, int radiusZ) {
        Iterator var8 = positions.iterator();

        while(var8.hasNext()) {
            BlockPos blockPos = (BlockPos)var8.next();
            if (config.vegetationChance > 0.0F && random.nextFloat() < config.vegetationChance) {
                this.generateVegetationFeature(world, config, context.chunkGenerator(), random, blockPos);
            }
        }

    }

    protected boolean generateVegetationFeature(WorldGenLevel world, VegetationPatchConfiguration config, ChunkGenerator generator, Random random, BlockPos pos) {
        BlockState toReplace = world.getBlockState(pos);
        if(toReplace.is(BlockTags.FLOWERS) || toReplace.is(Blocks.GRASS) || toReplace.is(Blocks.TALL_GRASS) || toReplace.is(Blocks.BIG_DRIPLEAF) || toReplace.is(Blocks.BIG_DRIPLEAF_STEM) || toReplace.is(Blocks.SMALL_DRIPLEAF)) {
            return false;
        }else {
            return config.vegetationFeature.get().place(world, generator, random, pos.relative(config.surface.getDirection().getOpposite()));
        }
    }

    protected boolean placeGround(WorldGenLevel world, VegetationPatchConfiguration config, Predicate<BlockState> replaceable, Random random, BlockPos.MutableBlockPos pos, int depth) {
        for(int i = 0; i < depth; ++i) {
            BlockState blockState = config.groundState.getState(random, pos);
            BlockState blockState2 = world.getBlockState(pos);
            if (!blockState.is(blockState2.getBlock())) {
                if (!replaceable.test(blockState2)) {
                    return i != 0;
                }

                world.setBlock(pos, blockState, 2);
                pos.move(config.surface.getDirection());
            }
        }

        return true;
    }

    private static Predicate<BlockState> getReplaceablePredicate(VegetationPatchConfiguration config) {
        Tag<Block> tag = BlockTags.getAllTags().getTag(config.replaceable);
        return tag == null ? (state) -> {
            return true;
        } : (state) -> {
            return state.is(tag);
        };
    }

    public static boolean airSubstitute(BlockState state){
        if(state.is(BlockTags.FLOWERS) || state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS) || state.isAir() || state.is(Blocks.BIG_DRIPLEAF) || state.is(Blocks.BIG_DRIPLEAF_STEM) || state.is(Blocks.SMALL_DRIPLEAF)){
            return true;
        }else {
            return false;
        }
    }
}
