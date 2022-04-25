package archware.utils;

import archware.utils.font.FontManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Timer;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class Wrapper {

    public static boolean authorized;

    private static final FontManager titlefont = new FontManager("cfont", Font.PLAIN,18,1,1);
    private static final FontManager infofont = new FontManager("cfont", Font.PLAIN,14,1,1);

    public static EntityRenderer getEntityRenderer() {
        return getMinecraft().entityRenderer;
    }

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getPlayer() {
        return getMinecraft().thePlayer;
    }

    public static WorldClient getWorld() {
        return getMinecraft().theWorld;
    }

    public static PlayerControllerMP getPlayerController() {
        return getMinecraft().playerController;
    }

    public static NetHandlerPlayClient getNetHandler() {
        return getMinecraft().getNetHandler();
    }

    public static GameSettings getGameSettings() {
        return getMinecraft().gameSettings;
    }

    public static boolean isInThirdPerson() {
        return getGameSettings().thirdPersonView != 0;
    }

    public static ItemStack getStackInSlot(int index) {
        return getPlayer().inventoryContainer.getSlot(index).getStack();
    }

    public static Timer getTimer() {
        return getMinecraft().timer;
    }

    public static Block getBlock(BlockPos pos) {
        return getMinecraft().theWorld.getBlockState(pos).getBlock();
    }


    public static GuiScreen getCurrentScreen() {
        return getMinecraft().currentScreen;
    }

    public static List<Entity> getLoadedEntities() {
        return getWorld().getLoadedEntityList();
    }

    public static List<EntityLivingBase> getLivingEntities() {
        return getWorld().getLoadedEntityList()
                .stream()
                .filter(e -> e instanceof EntityLivingBase)
                .map(e -> (EntityLivingBase) e)
                .collect(Collectors.toList());
    }

    public static List<EntityPlayer> getLoadedPlayers() {
        return getWorld().playerEntities;
    }

    public static void forEachInventorySlot(int begin, int end, SlotConsumer consumer) {
        for (int i = begin; i < end; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null)
                consumer.accept(i, stack);
        }
    }

//    public static List<EntityPlayer> getLoadedPlayersNoNPCs() {
//        if (ModuleManager.getInstance(AntiBot.class).isEnabled()) {
//            List<EntityPlayer> loadedPlayers = new ArrayList<>();
//
//            for (EntityPlayer player : getLoadedPlayers())
//                if (!AntiBot.BOTS.contains(player))
//                    loadedPlayers.add(player);
//
//            return loadedPlayers;
//        } else {
//            return getLoadedPlayers();
//        }
//    }


    public static void sendPacket(Packet<?> packet) {
        getNetHandler().sendPacket(packet);
    }

    public static void sendPacketDirect(Packet<?> packet) {
        getNetHandler().getNetworkManager().sendPacket(packet);
    }

    @FunctionalInterface
    public interface SlotConsumer {
        void accept(int slot, ItemStack stack);
    }

    public static FontManager getTitlefont() {
        return titlefont;
    }

    public static FontManager getInfofont() {
        return infofont;
    }

}
