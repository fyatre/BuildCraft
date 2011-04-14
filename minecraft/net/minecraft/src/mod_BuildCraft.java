package net.minecraft.src;

import java.util.LinkedList;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.src.buildcraft.BlockMachine;
import net.minecraft.src.buildcraft.BlockPipe;
import net.minecraft.src.buildcraft.EntityPassiveItem;
import net.minecraft.src.buildcraft.ITickListener;
import net.minecraft.src.buildcraft.ItemWoodGear;
import net.minecraft.src.buildcraft.TileMachine;
import net.minecraft.src.buildcraft.TilePipe;

public class mod_BuildCraft extends BaseMod {	

	private static mod_BuildCraft instance;

	public final ItemWoodGear woodGearItem;
	
	public final BlockMachine machineBlock;
	public final BlockPipe pipeBlock;
	
	private class TickContainer {
		ITickListener listener;
		int pace;
	}
	
	public LinkedList <TickContainer> tickListeners = new LinkedList <TickContainer> ();
	
	public LinkedList<TickContainer> tickListenersScheduledForAddition = new LinkedList<TickContainer>(); 
	
	public mod_BuildCraft () {
		
		instance = this;
		
		woodGearItem = (ItemWoodGear) (new ItemWoodGear(4001)).setIconIndex(
				ModLoader.addOverride("/gui/items.png",
						"/buildcraft_gui/wood_gear.png")).setItemName(
				"woodGearItem");
		
		machineBlock = new BlockMachine (120, 1);
		pipeBlock = new BlockPipe (121, 1);
		
		ModLoader.AddName(woodGearItem, "Wood Gear");
		ModLoader.RegisterBlock(machineBlock);
		ModLoader.RegisterBlock(pipeBlock);

		CraftingManager craftingmanager = CraftingManager.getInstance();
		
		craftingmanager.addRecipe(new ItemStack(machineBlock), new Object[] {
				"##", Character.valueOf('#'), Block.dirt });
		
		craftingmanager.addRecipe(new ItemStack(pipeBlock), new Object[] {
			"#", "#", Character.valueOf('#'), Block.dirt });
		
		ModLoader.SetInGameHook(this, true, false);		
		
		ModLoader.RegisterTileEntity(TileMachine.class, "Machine");
		ModLoader.RegisterTileEntity(TilePipe.class, "Pipe");		
	}
		
	public static mod_BuildCraft getInstance () {
		return instance;
	}
	
	
	@Override
	public String Version() {
		return "0.1";
	}
	    
    long lastTick = 0;
    
    public void registerTicksListener (ITickListener listener, int pace) {
    	//  TODO: move registers on tiles and use the date to select when to
    	//  make the call.
    	TickContainer container = new TickContainer();
    	container.listener = listener;
    	container.pace = pace;    	    	
    	tickListenersScheduledForAddition.add(container);
    }
    
    public void OnTickInGame(Minecraft minecraft)
    {    	
    	tickListeners.addAll(tickListenersScheduledForAddition);
    	tickListenersScheduledForAddition.clear ();
    	
    	if (minecraft.theWorld.getWorldTime() != lastTick) {    		    		
    		lastTick = minecraft.theWorld.getWorldTime();
    		
    		for (TickContainer container : tickListeners) {
    			if (lastTick % container.pace == 0) {
    				container.listener.tick(minecraft);	
    			}				
			}    		
    	}
    	
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void AddRenderer(Map map) {
    	map.put (EntityPassiveItem.class, new RenderItem());
    }
    
    private boolean isPipeConnected (int id) {
    	return id == pipeBlock.blockID || id == machineBlock.blockID;
    }
    
    public boolean RenderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l)
    {
    	if (block.getRenderType() == pipeBlock.modelID) {    		
    		block.setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 0.7F, 0.7F);
    		renderblocks.renderStandardBlock(block, i, j, k);
    		
    		if (isPipeConnected (iblockaccess.getBlockId(i - 1, j, k))) {
    			block.setBlockBounds(0.0F, 0.3F, 0.3F, 0.3F, 0.7F, 0.7F);
        		renderblocks.renderStandardBlock(block, i, j, k);
    		}
    		
    		if (isPipeConnected (iblockaccess.getBlockId(i + 1, j, k))) {
    			block.setBlockBounds(0.7F, 0.3F, 0.3F, 1.0F, 0.7F, 0.7F);
        		renderblocks.renderStandardBlock(block, i, j, k);
    		}
    		
    		if (isPipeConnected (iblockaccess.getBlockId(i, j - 1, k))) {
    			block.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.3F, 0.7F);
        		renderblocks.renderStandardBlock(block, i, j, k);
    		}
    		
    		if (isPipeConnected (iblockaccess.getBlockId(i, j + 1, k))) {
    			block.setBlockBounds(0.3F, 0.7F, 0.3F, 0.7F, 1.0F, 0.7F);
        		renderblocks.renderStandardBlock(block, i, j, k);
    		}
    		
    		if (isPipeConnected (iblockaccess.getBlockId(i, j, k - 1))) {
    			block.setBlockBounds(0.3F, 0.3F, 0.0F, 0.7F, 0.7F, 0.3F);
        		renderblocks.renderStandardBlock(block, i, j, k);
    		}
    		
    		if (isPipeConnected (iblockaccess.getBlockId(i, j, k + 1))) {
    			block.setBlockBounds(0.3F, 0.3F, 0.7F, 0.7F, 0.7F, 1.0F);
        		renderblocks.renderStandardBlock(block, i, j, k);
    		}
    		

    		
//    		RenderManager.instance.renderEntity(null, 0);
    		
//    		renderblocks.renderS
//    		block.setBlockBounds(0.3F, 0.3F, 0.0F, 0.7F, 0.7F, 1.0F);
//    		renderblocks.renderStandardBlock(block, i, j, k);
    		
//    		block.setBlockBounds(0.0F, 0.3F, 0.3F, 1.0F, 0.7F, 0.7F);
//    		renderblocks.renderStandardBlock(block, i, j, k);
    		
    		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    		
    		return true;
    	}
    	
        return false;
    }
    
    public void RenderInvBlock(RenderBlocks renderblocks, Block block, int i, int j)
    {
    	if (block.getRenderType() == pipeBlock.modelID) {
    		renderblocks.renderBlockOnInventory(Block.blockSnow, 1);
//    		 Tessellator tessellator = Tessellator.instance;
//    		 
//    		block.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 1.0F);
//    		renderblocks.renderStandardBlock(block, i, j, 0);
    	}
    }
}
