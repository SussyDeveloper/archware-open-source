package archware;

import archware.command.CommandManager;
import archware.event.impl.EventChat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import archware.event.Event;
import archware.module.ModuleManager;
import archware.ui.clickgui.ClickGUI;

public class Client {

    private static Client instance = new Client();
    public static ModuleManager moduleManager;
    public static ClickGUI clickGUI;
    public static CommandManager commandManager;
    static Minecraft mc = Minecraft.getMinecraft();
    public static String name = "Archware";
    public static String build = "Development";
    public static String version = "210422";


    public static void onStart(){
        System.out.println("Starting The Client...");
        moduleManager = new ModuleManager();
        clickGUI = new ClickGUI();
        commandManager = new CommandManager();
    }

    public static void onShutdown(){

    }


    public static Client getInstance() {
        return instance;
    }

    public static boolean canPass;
    public static void onEvent(Event event) {
        moduleManager.getModules().forEach(module -> {
            canPass = module.isEnabled();
            if (canPass) module.onEvent(event);
        });
        if(event instanceof EventChat){
            commandManager.handleChat((EventChat)event);
        }
    }

    public static void message(String message){
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static String getName() {
        return name;
    }

    public static String getBuild() {
        return build;
    }

    public static String getVersion() {
        return version;
    }
}
