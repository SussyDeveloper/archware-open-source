package archware.module.impl.movement;

import archware.event.impl.*;
import archware.module.impl.other.LagDetector;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0BPacketAnimation;
import org.lwjgl.input.Keyboard;
import archware.Client;
import archware.event.Event;
import archware.module.Category;
import archware.module.Module;
import archware.module.impl.other.Bypass;
import archware.module.impl.render.HUD;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;
import archware.utils.movement.MoveUtils;
import archware.utils.timers.Stopwatch;

import java.awt.*;

public class Flight extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Motion", "Motion", "MushMC", "CraftPlay");
    NumberSetting speed = new NumberSetting("Speed", 1.5, 0.5, 4, 0.1, false);
    BoolSetting progressbar = new BoolSetting("Progress Bar", true);
    BoolSetting bobbing = new BoolSetting("Bobbing", false);
    public static BoolSetting noDamage = new BoolSetting("No Damage", true);
    private int stage;
    private double flightSpeed;
    Stopwatch timer = new Stopwatch();
    Stopwatch tumer = new Stopwatch();
    boolean canFly, pass;

    public Flight() {
        super("Flight", "", Keyboard.KEY_F, Category.MOVEMENT);
        addSettings(mode, speed, progressbar, bobbing, noDamage);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        pass = false;
        timer.reset();
        if(Client.moduleManager.getModuleByName("Speed").isEnabled()){
            Client.moduleManager.getModuleByName("Speed").toggle();
            NotificationManager.queue("Movement Check", "Disabling extra modules", NotificationType.WARNING, 800);
        }

        if(LagDetector.isCannotfly()){
            NotificationManager.queue("Lag Detector", "You cannot Fly due to problem with a lag", NotificationType.WARNING, 1000);
            toggle();
        }

        if(mode.is("MushMC")){
            if(!Client.moduleManager.getModuleByName("Bypass").isEnabled()){
                NotificationManager.queue("Disabling Flight", "Please turn on Bypass module and set to MushMC", NotificationType.ERROR, 3000);
                toggle();
            }else if(Client.moduleManager.getModuleByName("Bypass").isEnabled() && !Bypass.getMode().is("MushMC")){
                NotificationManager.queue("Disabling Flight", "Please set mode to MusHMC in Bypass module", NotificationType.ERROR, 3000);
                toggle();
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4.0001, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MoveUtils.setSpeed(0);
        stage = 0;
        timer.reset();
        canFly = false;
        pass = false;
        mc.timer.timerSpeed = 1F;
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUpdate){
            setDisplayName("Flight " + ChatFormatting.GRAY + mode.getSelected());
            if (bobbing.isEnable())
                mc.thePlayer.cameraYaw = 60 / 1000.0F;

            switch (mode.getSelected()){
                case "Motion": {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    if (!mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0;
                    }
                    MoveUtils.setSpeed(speed.getValue());
                    break;
                }
                case "MushMC":{
                    mc.thePlayer.motionY = 0;
                    if(stage == 0){
                        if(mc.thePlayer.onGround){
                            mc.thePlayer.jump();
                            canFly = true;
                            pass = true;
                        }else{
                            canFly = false;
                        }
                        if(!mc.thePlayer.onGround && canFly){
//                            mc.thePlayer.motionY = 0;
                            flightSpeed = 0.2F;
                        }

                        if(!canFly){
                            flightSpeed = 0;
                            NotificationManager.queue("Something went wrong with The Flight!", "Please try again.", NotificationType.ERROR, 3000);
                            toggle();
                        }
                    }
                    if(stage == 1 && pass){
                        flightSpeed = 0.5F;
                    }

                    if(stage == 2 && pass){
                        flightSpeed = 1.5F;
                    }
                    if(stage == 3 && pass){
                        flightSpeed = 2F;
                    }
                    if(stage == 4 && pass){
                        flightSpeed = speed.getValue();
                    }
                    if(timer.elapsed(1000)){
                        flightSpeed = speed.getValue() / 2;
                    }

                    if(timer.elapsed(3000L)) {
                        flightSpeed = 0;
                        NotificationManager.queue("AntiFly Kick", "Disabled Flight due to Time-UP", NotificationType.INFO, 1500);
                        toggle();
                    }
                    stage++;
                    break;
                }
            }
        }

        if(event instanceof Event2D){
            ScaledResolution resolution = new ScaledResolution(mc);
            float x = resolution.getScaledWidth() / 2.0F;
            float y = resolution.getScaledHeight() / 2.0F + 15;
            float width = 80.0F;
            float half = width / 2;
            float percentage = (float)(3000 - timer.getElapsedTime()) / 3000;
            int color = new Color(HUD.colorInstance).getRGB();
            if(progressbar.isEnable()) {
                Gui.drawRect(x - half - 0.5F, y - 2, x + half + 0.5F, y + 2, 0x78000000);
                Gui.getInstance().drawGradientRect(x - half, y - 1.5F, x - half + (width * percentage), y + 1.5F,
                        color, new Color(color).darker().getRGB());
                mc.fontRendererObj.drawStringWithShadow(timer.getElapsedTime() + "ms", x - mc.fontRendererObj.getStringWidth(timer.getElapsedTime() + "ms") / 2.0F, y - 10, -1);
            }
        }
        if(event instanceof EventPacket){
            EventPacket e = (EventPacket) event;
            if(e.isIncoming()){
                if(e.getPacket() instanceof S08PacketPlayerPosLook){
                    flightSpeed = 0;
                    NotificationManager.queue("Lag back", "Disabling Flight due to lag back", NotificationType.WARNING, 1500);
                    toggle();
                }
            }
        }
        if(event instanceof EventMove){
            EventMove e = (EventMove) event;
            if(mode.is("MushMC")){
                MoveUtils.setSpeed(e,flightSpeed);
            }
        }

        if(event instanceof EventMotion){
            if(event.isPre()){
                if(mode.is("CraftPlay")){
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    } else {
                        if (mc.thePlayer.fallDistance > 0) {

                            double v = 0;
                            v = -0.09800000190735147;

                            if (mc.thePlayer.motionY < v) {
                                mc.timer.timerSpeed = 1.3F;
                                mc.thePlayer.motionY = v;
                            }
                        }
                    }

                    if(tumer.elapsed(100)) {
                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX,mc.thePlayer.posY-0.1,mc.thePlayer.posZ);
                        tumer.reset();
                    }

                    MoveUtils.strafe();
                }
            }
        }
    }
}
