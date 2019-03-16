package gcewing.sg.features.gdo;

import gcewing.sg.SGCraft;
import gcewing.sg.network.GuiNetworkHandler;
import gcewing.sg.tileentity.SGBaseTE;
import gcewing.sg.util.GateUtil;
import gcewing.sg.util.SGState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nullable;

public class GdoItem extends Item {

    public GdoItem() {}

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        if (!worldIn.isRemote) {
            TileEntity localGateTE = GateUtil.locateLocalGate(worldIn, new BlockPos(player.posX, player.posY, player.posZ), 6, true);
            if (localGateTE instanceof SGBaseTE) {
                SGBaseTE localGate = (SGBaseTE) localGateTE;

                boolean canAccessLocal = localGate.allowAccessToIrisController(player.getName());
                boolean canAccessRemote = true;
                if (localGate.isConnected() && localGate.state == SGState.Connected) {
                    SGBaseTE remoteGate = localGate.getConnectedStargateTE();
                    canAccessRemote = remoteGate.allowAccessToIrisController(player.getName());
                }

                if (SGCraft.hasPermission(player, "sgcraft.gui.gdo")) {
                    GuiNetworkHandler.openGuiAtClient(localGate, player, 2, SGCraft.hasPermission(player, "sgcraft.admin"), canAccessLocal, canAccessRemote);
                } else {
                    player.sendMessage(new TextComponentString("Insufficient permissions.  Requires 'sgcraft.gui.gdo."));
                    return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(handIn));
                }

                return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(handIn));  //Both Server & Client expect a returned value.
            } else {
                player.sendMessage(new TextComponentString("Could not detect Stargate near current position."));
                return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(handIn));
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(handIn));  //Both Server & Client expect a returned value.
    }
}
