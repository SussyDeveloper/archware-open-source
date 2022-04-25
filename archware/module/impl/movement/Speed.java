package archware.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import archware.Client;
import archware.event.Event;
import archware.event.impl.EventMove;
import archware.event.impl.EventPacket;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;
import archware.utils.movement.MoveUtils;

public class Speed extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "MushMC", "MushMCLow","Verus", "NCP", "Vulcan");
    NumberSetting speed = new NumberSetting("Speed", 0.5, 0.1, 5.0, 0.1, false);
    double sped;

    public Speed() {
        super("Speed", "Run faster than normal player!", Keyboard.KEY_B, Category.MOVEMENT);
        addSettings(mode, speed);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(Client.moduleManager.getModuleByName("Flight").isEnabled()){
            Client.moduleManager.getModuleByName("Flight").toggle();
            NotificationManager.queue("Movement Check", "Disabling extra modules", NotificationType.WARNING, 800);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MoveUtils.setSpeed(0.2);
        mc.timer.timerSpeed = 1;
        if(mode.is("MushMCLow")){
            mc.gameSettings.viewBobbing = true;
        }
    }

    @Override
    public void onEvent(Event e) {
        super.onEvent(e);

        if(e instanceof EventUpdate){
            this.setDisplayName("Speed " + ChatFormatting.GRAY + mode.getSelected());

            switch (mode.getSelected()){
                case "Vulcan":{
                    if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
                        mc.thePlayer.jump();
                        MoveUtils.setSpeed(0.49F);
                        mc.timer.timerSpeed = 1.2f;
                    }

//                    MoveUtils.setSpeed(0.3);
//                    mc.timer.timerSpeed = 1.3f;
                    break;
                }
            }
        }

        if(e instanceof EventMove){
            EventMove event = (EventMove) e;
            switch (mode.getSelected()){
                case "Vanilla":{
                    if(MoveUtils.isMoving()){
                        MoveUtils.setSpeed(event,speed.getValue());
                        if(mc.thePlayer.onGround){
                            mc.thePlayer.jump();
                            event.y = 0.3532234;
                        }else {
                            MoveUtils.strafe();
                        }
                    }else{
                        MoveUtils.setSpeed(event, 0);
                    }
                    break;
                }
                case "NCP":{
                    if(MoveUtils.isMoving()){
                        if(mc.thePlayer.onGround){
                            mc.thePlayer.jump();
                            event.y = 0.3532234;
                        }else {
                            MoveUtils.strafe();
                        }
                    }else{
                        MoveUtils.strafe(0);
                    }
                    break;
                }

                case "MushMC":{
                    if (MoveUtils.isMoving()) {
                        MoveUtils.setSpeed(event,0.45);
                        if(mc.thePlayer.onGround){
                            mc.thePlayer.jump();
                            event.y = 0.423432;
                        }
                    }else{
                        MoveUtils.setSpeed(event, 0);
                    }
                break;
                }

                case "MushMCLow":{
                    if (MoveUtils.isMoving()) {
                        mc.gameSettings.viewBobbing = false;
                        MoveUtils.setSpeed(event,0.5F);
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY = 0.0;
                            event.y = 0.3272234;
                        } else {
                            MoveUtils.strafe();
                        }
                    } else {
                        MoveUtils.setSpeed(0);
                    }
                    break;
                }

            }
        }

        if(e instanceof EventPacket){
            EventPacket event = (EventPacket) e;
            if(e.isIncoming()){
                if(event.getPacket() instanceof S08PacketPlayerPosLook){
                    NotificationManager.queue("Lag back", "Disabling Speed due to lag back", NotificationType.WARNING, 1000);
                    toggle();
                }
            }
        }
    }

}
