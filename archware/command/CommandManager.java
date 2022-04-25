package archware.command;

import archware.command.impl.Hackers;
import archware.command.impl.Toggle;
import archware.event.impl.EventChat;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandManager {

    CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<>();
    public String prefix = ".";

    public CommandManager(){
        commands.add(new Toggle());
        commands.add(new Hackers());
    }

    public CopyOnWriteArrayList<Command> getCommands() {
        return commands;
    }

    public void handleChat(EventChat event){
        String message = event.getMessage();

        if(!message.startsWith(prefix))
            return;

        event.setCancelled(true);

        message = message.substring(prefix.length());


        if(message.split(" ").length > 0){
            String commandName = message.split(" ")[0];

            for(Command c : commands){
                if(c.aliases.contains(commandName) || c.command.equalsIgnoreCase(commandName)){
                    c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
                }
            }
        }
    }
}
