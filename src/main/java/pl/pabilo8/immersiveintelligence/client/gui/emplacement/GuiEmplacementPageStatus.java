package pl.pabilo8.immersiveintelligence.client.gui.emplacement;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.elements.GuiReactiveList;
import blusunrize.immersiveengineering.common.util.network.MessageTileSync;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.api.utils.MachineUpgrade;
import pl.pabilo8.immersiveintelligence.client.gui.elements.buttons.GuiButtonSwitch;
import pl.pabilo8.immersiveintelligence.client.gui.elements.buttons.GuiSliderII;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.IIGuiList;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.second.TileEntityEmplacement;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.second.TileEntityEmplacement.EmplacementWeapon.MachineUpgradeEmplacementWeapon;

import java.io.IOException;

/**
 * @author Pabilo8
 * @since 16.07.2021
 */
public class GuiEmplacementPageStatus extends GuiEmplacement
{
	GuiButtonSwitch switchRSControl, switchDataControl, switchSendTarget;
	GuiSliderII sliderRepair;

	public GuiEmplacementPageStatus(InventoryPlayer inventoryPlayer, TileEntityEmplacement tile)
	{
		super(inventoryPlayer, tile, IIGuiList.GUI_EMPLACEMENT_STATUS);
		title = I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.status");
	}

	@Override
	public void initGui()
	{
		super.initGui();

		addLabel(8, 24, 96, 0, 0xffffff, tile.currentWeapon!=null?(
				I18n.format("machineupgrade.immersiveintelligence."+tile.currentWeapon.getName())):
				I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.weapon_none")).setCentered();
		addLabel(8, 86, Utils.COLOR_H1, I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.upgrades"));
		//
		GuiReactiveList upgradeList = new GuiReactiveList(this, buttonList.size(), guiLeft+11, guiTop+86+6+1, 124, 54,
				tile.getUpgrades().stream().filter(upgrade -> !(upgrade instanceof MachineUpgradeEmplacementWeapon)).map(MachineUpgrade::getName).toArray(String[]::new))
				//"Heavy Barrel","Ballistic Circuitry","High-Quality Bearings")
				.setTranslationFunc(s -> I18n.format("machineupgrade.immersiveintelligence."+s))
				.setFormatting(0.75f, true);
		addButton(upgradeList);

		addLabel(112, 22, 93, 0, Utils.COLOR_H1,
				I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.settings")).setCentered();

		switchRSControl = addSwitch(112, 28, 80, Utils.COLOR_H1, 0x4c7bb1, 0xffb515, tile.redstoneControl,
				I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.redstone_control"));
		switchDataControl = addSwitch(112,
				28+switchRSControl.getTextHeight(fontRenderer),
				80, Utils.COLOR_H1, 0x4c7bb1, 0xffb515, tile.dataControl,
				I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.data_control"));
		switchSendTarget = addSwitch(112,
				28+switchRSControl.getTextHeight(fontRenderer)+switchDataControl.getTextHeight(fontRenderer),
				80, Utils.COLOR_H1, 0x4c7bb1, 0xffb515, tile.sendAttackSignal,
				I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.send_attack_signal"));

		sliderRepair = addSlider(116,
				28+switchRSControl.getTextHeight(fontRenderer)+switchDataControl.getTextHeight(fontRenderer)+switchSendTarget.getTextHeight(fontRenderer)
						+fontRenderer.getWordWrappedHeight(I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.auto_repair_threshold"),70),
				80, Utils.COLOR_H1, tile.autoRepairAmount,
				I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.emplacement.auto_repair_threshold"));

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
		if(button instanceof GuiButtonSwitch)
		{
			syncDataToServer();
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		syncDataToServer();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(partialTicks, mx, my);
		bindTexture();
		drawTexturedModalRect(guiLeft+8+96, guiTop+16, 205, 9, 6, 130);

		draw3DRotato(guiLeft+8, guiTop+16, partialTicks);
		ClientUtils.drawColouredRect(guiLeft+8, guiTop+86+4, 96, 54, 0x4f000000);

	}

	private void draw3DRotato(int x, int y, float partialTicks)
	{
		//ClientUtils.drawGradientRect(x, y, x+86, y+76, 0xff000000,0xff000000);
		ClientUtils.drawColouredRect(x, y, 96, 76-12, 0xff000000);
		GlStateManager.pushMatrix();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.translate(x+47, y+65-12, 0);
		GlStateManager.rotate(-15, 1, 0, 0);
		GlStateManager.scale(-22, -22, -1);
		GlStateManager.rotate(360*(((mc.world.getTotalWorldTime()%120)+partialTicks)/120f), 0, 1, 0);
		tile.renderWithUpgrades(tile.getUpgrades().toArray(new MachineUpgrade[0]));
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	@Override
	protected void syncDataToServer()
	{
		super.syncDataToServer();
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("redstoneControl", switchRSControl.state);
		nbt.setBoolean("dataControl", switchDataControl.state);
		nbt.setBoolean("sendAttackSignal", switchSendTarget.state);
		nbt.setFloat("autoRepairAmount", (float)sliderRepair.sliderValue);

		ImmersiveEngineering.packetHandler.sendToServer(new MessageTileSync(this.tile, nbt));
	}
}
