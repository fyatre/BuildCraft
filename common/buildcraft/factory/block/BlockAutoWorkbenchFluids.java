package buildcraft.factory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import buildcraft.factory.BCFactoryGuis;
import buildcraft.factory.tile.TileAutoWorkbenchFluids;
import buildcraft.lib.block.BlockBCTile_Neptune;

public class BlockAutoWorkbenchFluids extends BlockBCTile_Neptune {

    public BlockAutoWorkbenchFluids(Material material, String id) {
        super(material, id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAutoWorkbenchFluids();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BCFactoryGuis.AUTO_WORKBENCH_FLUIDS.openGUI(player, pos);
        }
        return true;
    }
}
