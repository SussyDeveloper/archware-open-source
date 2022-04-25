package archware.command.impl;

import archware.Client;
import archware.command.Command;
import archware.module.impl.other.HackerDetector;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.EntityLivingBase;

public class Hackers extends Command {

    public Hackers() {
        super("Hackers", "It sends list of hackers", "", "hackers");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if(HackerDetector.hackers.isEmpty()){
            Client.message(ChatFormatting.RED + "There is no hacker detected by Hacker Detector!");
        }else{
            for(EntityLivingBase hacke : HackerDetector.hackers){
                Client.message(hacke.getName() + " is Hacker!");
            }
        }
    }
}
