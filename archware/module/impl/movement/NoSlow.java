package archware.module.impl.movement;

import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.event.impl.EventUpdate;
import archware.event.impl.EventUseItem;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.utils.Wrapper;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP");
    private static final C07PacketPlayerDigging PLAYER_DIGGING = new C07PacketPlayerDigging(
            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);
    private static final C08PacketPlayerBlockPlacement BLOCK_PLACEMENT = new C08PacketPlayerBlockPlacement(
            new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f);

    public NoSlow() {
        super("NoSlow", "", 0, Category.MOVEMENT);
        addSettings(mode);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUseItem){
            event.setCancelled(true);
        }

        if(event instanceof EventMotion){

        }
        if(event instanceof EventUpdate){
            setDisplayName("NoSlow " + ChatFormatting.GRAY + mode.getSelected());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
