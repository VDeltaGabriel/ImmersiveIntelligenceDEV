package pl.pabilo8.immersiveintelligence.common.items.tools;

import blusunrize.immersiveengineering.api.tool.ITool;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.api.rotary.CapabilityRotaryEnergy;
import pl.pabilo8.immersiveintelligence.api.rotary.IRotaryEnergy;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.items.ItemIIBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author Pabilo8
 * @since 2019-05-30
 */
public class ItemIITachometer extends ItemIIBase implements ITool
{
	public ItemIITachometer()
	{
		super("tachometer", 1);
		canRepair = false;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag)
	{

	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if(tileEntity!=null&&tileEntity.hasCapability(CapabilityRotaryEnergy.ROTARY_ENERGY, facing.getOpposite()))
			{
				IRotaryEnergy energy = tileEntity.getCapability(CapabilityRotaryEnergy.ROTARY_ENERGY, facing.getOpposite());

				if(energy==null)
					return EnumActionResult.PASS;

				float int_torque = energy.getTorque();
				float ext_torque = energy.getOutputRotationSpeed();
				float int_speed = energy.getRotationSpeed();
				float ext_speed = energy.getOutputRotationSpeed();
				if(int_torque!=ext_torque&&int_speed!=ext_speed)
					ChatUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(CommonProxy.INFO_KEY+"tachometer_message.advanced", int_speed, int_torque, ext_speed, ext_torque));
				else
					ChatUtils.sendServerNoSpamMessages(player, new TextComponentTranslation(CommonProxy.INFO_KEY+"tachometer_message.basic", int_speed, int_torque));

				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Nonnull
	@Override
	public Set<String> getToolClasses(ItemStack stack)
	{
		return ImmutableSet.of(CommonProxy.TOOL_TACHOMETER);
	}

	@Override
	public boolean isTool(ItemStack item)
	{
		return true;
	}
}