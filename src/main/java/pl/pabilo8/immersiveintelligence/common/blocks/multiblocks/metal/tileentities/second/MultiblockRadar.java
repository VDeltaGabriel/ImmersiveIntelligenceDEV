package pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.second;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.api.utils.IAdvancedMultiblock;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.blocks.MultiblockStuctureBase;
import pl.pabilo8.immersiveintelligence.common.blocks.types.IIBlockTypes_MetalMultiblock1;

import javax.annotation.Nullable;

@IAdvancedMultiblock
public class MultiblockRadar extends MultiblockStuctureBase<TileEntityRadar>
{
	public static MultiblockRadar instance = new MultiblockRadar();

	public MultiblockRadar()
	{
		super(new ResourceLocation(ImmersiveIntelligence.MODID, "multiblocks/radar"));
		offset = new Vec3i(4,0, 0);
	}

	@Override
	protected void addBlockEvent(World world, BlockPos pos)
	{
		world.addBlockEvent(pos, IIContent.blockMetalMultiblock1, 255, 0);
	}

	@Override
	@Nullable
	protected TileEntityRadar placeTile(World world, BlockPos pos)
	{
		world.setBlockState(pos, IIContent.blockMetalMultiblock1.getStateFromMeta(IIBlockTypes_MetalMultiblock1.RADAR.getMeta()));
		return (TileEntityRadar)world.getTileEntity(pos);
	}

	@Override
	protected TileEntityRadar getMBInstance()
	{
		return new TileEntityRadar();
	}
}
