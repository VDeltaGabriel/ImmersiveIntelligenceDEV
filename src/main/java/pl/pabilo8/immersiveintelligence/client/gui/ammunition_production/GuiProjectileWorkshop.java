package pl.pabilo8.immersiveintelligence.client.gui.ammunition_production;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.network.MessageTileSync;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import pl.pabilo8.immersiveintelligence.Config.IIConfig.Machines.ProjectileWorkshop;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry.EnumCoreTypes;
import pl.pabilo8.immersiveintelligence.api.bullets.IBullet;
import pl.pabilo8.immersiveintelligence.api.bullets.IBulletComponent;
import pl.pabilo8.immersiveintelligence.client.gui.elements.buttons.GuiButtonDropdownList;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.second.TileEntityProjectileWorkshop;
import pl.pabilo8.immersiveintelligence.common.gui.ContainerProjectileWorkshop;
import pl.pabilo8.immersiveintelligence.common.network.IIPacketHandler;
import pl.pabilo8.immersiveintelligence.common.network.MessageBooleanAnimatedPartsSync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Pabilo8
 * @since 10-07-2019
 */
public class GuiProjectileWorkshop extends GuiAmmunitionBase<TileEntityProjectileWorkshop>
{
	GuiButtonDropdownList typeList = null, bulletList = null;
	ItemStack exampleStack = ItemStack.EMPTY;
	int coreIconID = 0;
	private GuiTextField valueEdit;

	public GuiProjectileWorkshop(InventoryPlayer inventoryPlayer, TileEntityProjectileWorkshop tile)
	{
		super(inventoryPlayer, tile, ContainerProjectileWorkshop::new);
		IIPacketHandler.INSTANCE.sendToServer(
				new MessageBooleanAnimatedPartsSync(true, blusunrize.immersiveengineering.common.util.Utils.RAND.nextInt(2),
						tile.getPos()));
	}

	@Override
	public void initGui()
	{
		super.initGui();
		if(!tile.fillerUpgrade)
		{
			addLabel(guiLeft+122, guiTop+5+5, Utils.COLOR_H1, "Core:");
			addLabel(guiLeft+122, guiTop+5+32-10+5, Utils.COLOR_H1, "Type:");

			String[] cores = Arrays.stream(tile.producedBullet.getAllowedCoreTypes()).map(EnumCoreTypes::getName).toArray(String[]::new);
			typeList = new GuiButtonDropdownList(buttonList.size(), guiLeft+122, guiTop+20+32-8-7, 72, 12, 3, cores);
			typeList.setTranslationFunc(s -> I18n.format(CommonProxy.DESCRIPTION_KEY+"bullet_core_type."+s));
			typeList.selectedEntry = Arrays.asList(cores).indexOf(tile.coreType.getName());
			addButton(typeList);

			String[] names = BulletRegistry.INSTANCE.registeredBulletItems.values().stream().map(IBullet::getName).toArray(String[]::new);
			bulletList = new GuiButtonDropdownList(buttonList.size(), guiLeft+122, guiTop+20-6, 72, 12, 6, names);
			bulletList.setTranslationFunc(s -> I18n.format("item.immersiveintelligence."+s+".core.name"));

			bulletList.selectedEntry = Arrays.asList(names).indexOf(tile.producedBullet.getName());
			addButton(bulletList);

			IBullet bullet = BulletRegistry.INSTANCE.registeredBulletItems.get(bulletList.getEntry(bulletList.selectedEntry));
			exampleStack = bullet==null?ItemStack.EMPTY:
					bullet.getBulletCore("core_brass", typeList.getEntry(typeList.selectedEntry));
			coreIconID = tile.coreType.ordinal();
		}
		else
		{
			valueEdit = new GuiTextField(buttonList.size(), fontRenderer, guiLeft+122, guiTop+20+32-8-7+11, 72, 12);
			this.valueEdit.setFocused(false);
			this.valueEdit.setText(String.valueOf(tile.fillAmount));
			this.valueEdit.updateCursorCounter();

			addLabel(guiLeft+122, guiTop+5+5+22+11, Utils.COLOR_H1, "Insert Amount:");
		}

		addLabel(guiLeft+1, guiTop+8, 118, 0, Utils.COLOR_H1, I18n.format("tile.immersiveintelligence.metal_multiblock1.projectile_workshop.name")).setCentered();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(!tile.fillerUpgrade||!this.valueEdit.textboxKeyTyped(typedChar, keyCode))
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(!tile.fillerUpgrade||!this.valueEdit.mouseClicked(mouseX, mouseY, mouseButton))
			super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
		if(!tile.fillerUpgrade)
		{
			if(button==typeList)
			{
				sendList("core_type", typeList.getEntry(typeList.selectedEntry));
			}
			else if(button==bulletList)
			{
				sendList("produced_bullet", bulletList.getEntry(bulletList.selectedEntry));

				IBullet bullet = BulletRegistry.INSTANCE.registeredBulletItems.get(bulletList.getEntry(bulletList.selectedEntry));
				String selectedType = typeList.getEntry(typeList.selectedEntry);

				int id = typeList.id;
				buttonList.remove(typeList);

				//reset
				String[] cores = Arrays.stream(bullet.getAllowedCoreTypes()).map(EnumCoreTypes::getName).toArray(String[]::new);
				typeList = new GuiButtonDropdownList(id, guiLeft+122, guiTop+20+32-8-7, 72, 12, 3, cores);
				typeList.setTranslationFunc(s -> I18n.format(CommonProxy.DESCRIPTION_KEY+"bullet_core_type."+s));
				typeList.selectedEntry = Math.max(Arrays.asList(cores).indexOf(selectedType), 0);
				this.buttonList.add(id, typeList);

				sendList("core_type", typeList.getEntry(typeList.selectedEntry));

			}

			coreIconID = EnumCoreTypes.v(typeList.getEntry(typeList.selectedEntry)).ordinal();

			IBullet bullet = BulletRegistry.INSTANCE.registeredBulletItems.get(bulletList.getEntry(bulletList.selectedEntry));
			exampleStack = bullet==null?ItemStack.EMPTY:
					bullet.getBulletCore("core_brass", typeList.getEntry(typeList.selectedEntry));
		}
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		Utils.bindTexture(TEXTURE);
		if(tile.fillerUpgrade)
		{
			drawTexturedModalRect(guiLeft+6, guiTop+9+32, 224, 0, 20, 23); //in core
			drawTexturedModalRect(guiLeft+6+44-8, guiTop+15, 220, 0, 24, 23); //in component
			drawTexturedModalRect(guiLeft+6+64+21, guiTop+9+32, 224, 23, 20, 23); //out
			drawTexturedModalRect(guiLeft+6+44+22-4, guiTop+15+3, 62, 230, 49, 20); //tank back

			drawTexturedModalRect(guiLeft+6+22, guiTop+9+30, 123, 176, 62, 34); //progress back

			if(!tile.effect.isEmpty())
			{
				int max = (int)(ProjectileWorkshop.fillingTime+ProjectileWorkshop.fillingTime*0.3*tile.producedBullet.getCaliber());
				drawTexturedModalRect(guiLeft+6+22, guiTop+9+30, 0, 176, (int)(62*(1f-(tile.productionProgress/(float)max))), 34); //progress top
			}

			GlStateManager.enableBlend();
			ClientUtils.handleGuiTank(tile.tanksFiller[0], guiLeft+6+44+22-4, guiTop+15+3, 49, 20, 62, 210, 49, 20, mx, my, TEXTURE.toString(), null);
			GlStateManager.disableBlend();

			IBulletComponent component = tile.componentInside.getComponent();
			if(component!=null)
			{
				int cc = 0xff000000+tile.componentInside.getColour();
				Utils.drawGradientBar(guiLeft+6+44-6, guiTop+20, 2, 16, cc, cc, tile.componentInside.getAmountPercentage());


				Utils.drawStringCentered(fontRenderer, tile.componentInside.getTranslatedName(), guiLeft+122, guiTop+5, 71, 0, Utils.COLOR_H1);
				fontRenderer.drawString(TextFormatting.ITALIC+I18n.format(CommonProxy.DESCRIPTION_KEY+"bullet_type."+component.getRole().getName())+TextFormatting.RESET,
						guiLeft+122, guiTop+5+11, Utils.COLOR_H2);
			}

			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemIntoGUI(tile.effect, guiLeft+6+64+21+2, guiTop+9+32+4);
			RenderHelper.disableStandardItemLighting();

			valueEdit.drawTextBox();
		}
		else
		{
			drawTexturedModalRect(guiLeft+6, guiTop+29+6, 224, 0, 20, 23); //in
			drawTexturedModalRect(guiLeft+6+64+21, guiTop+29+6, 224, 23, 20, 23); //out

			drawTexturedModalRect(guiLeft+6+22, guiTop+10+6, 123, 210, 62, 41); //progress back

			if(!tile.effect.isEmpty())
			{
				int max = (int)(ProjectileWorkshop.fillingTime+ProjectileWorkshop.productionTime*0.3*tile.producedBullet.getCaliber());
				drawTexturedModalRect(guiLeft+6+22, guiTop+10+6, 0, 210, (int)(62*(1f-(tile.productionProgress/(float)max))), 41); //progress top
			}

			Utils.bindTexture(TEXTURE_ICONS);
			drawTexturedModalRect(guiLeft+122+16, guiTop+5+48, 16*coreIconID, 30, 16, 16); //fuse icon

			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemIntoGUI(tile.effect, guiLeft+6+64+21+2, guiTop+29+6+4);
			itemRender.renderItemIntoGUI(exampleStack, guiLeft+122, guiTop+5+48);
			RenderHelper.disableStandardItemLighting();
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		if(tile.fillerUpgrade)
		{
			try
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("fill_amount", Integer.parseInt(valueEdit.getText()));
				ImmersiveEngineering.packetHandler.sendToServer(new MessageTileSync(tile, nbt));
			}
			catch(NumberFormatException ignored)
			{

			}
		}

		IIPacketHandler.INSTANCE.sendToServer(new MessageBooleanAnimatedPartsSync(false, 0, tile.getPos()));
		IIPacketHandler.INSTANCE.sendToServer(new MessageBooleanAnimatedPartsSync(false, 1, tile.getPos()));
	}

	@Override
	public void drawScreen(int mx, int my, float partial)
	{
		super.drawScreen(mx, my, partial);
	}

	@Override
	ArrayList<String> drawTooltip(int mx, int my, ArrayList<String> tooltip)
	{
		if(!tile.effect.isEmpty())
		{
			if(tile.fillerUpgrade?
					isPointInRegion(6+64+21+2, 9+32+4, 16, 16, mx, my):
					isPointInRegion(6+64+21+2, 29+6+4, 16, 16, mx, my))
				tooltip.addAll(tile.effect.getTooltip(ClientUtils.mc().player, mc.gameSettings.advancedItemTooltips?TooltipFlags.ADVANCED: TooltipFlags.NORMAL));
		}
		if(tile.fillerUpgrade)
			ClientUtils.handleGuiTank(tile.tanksFiller[0], guiLeft+6+44+22-4, guiTop+15+3, 49, 20, 62, 210, 49, 20, mx, my, TEXTURE.toString(), tooltip);
		return super.drawTooltip(mx, my, tooltip);
	}
}
