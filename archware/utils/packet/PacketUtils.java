package archware.utils.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtils {
    private static final Minecraft mc;

    public static void sendPacket(final Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(final Packet packet){
        mc.getNetHandler().addToSilentQueue(packet);
    }

//    public static void sendPacketSilent(final Packet packet) {
//        mc.thePlayer.sendQueue.addToSilentQueue(packet);
//    }

//    public static void sendC04(final double x, final double y, final double z, final boolean ground, final boolean silent) {
//        if (silent) {
//            sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
//        }
//        else {
//            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
//        }
//    }

    static {
        mc = Minecraft.getMinecraft();
    }

}
