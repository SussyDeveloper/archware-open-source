package archware.command.impl;

import archware.Client;
import archware.command.Command;
import archware.module.Module;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", "It toggles modules by Name.", "toggle <name>", "t");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if(args.length > 0){
            String foundName = args[0];

            boolean foundModule = false;

            for(Module module : Client.moduleManager.getModules()){
                if(module.getName().equalsIgnoreCase(foundName)){
                    module.toggle();
                    foundModule = true;
                    break;
                }
            }

            if(!foundModule){
                NotificationManager.queue("Something wrong", "Could not find module.", NotificationType.ERROR, 3000);
            }
        }
    }
}
