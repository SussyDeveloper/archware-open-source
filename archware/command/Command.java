package archware.command;

import java.util.Arrays;
import java.util.List;

public abstract class Command {

    String command, description, syntax;
    public List<String>aliases;

    public Command(String command, String description, String syntax, String...aliases){
        this.command = command;
        this.description = description;
        this.syntax = syntax;
        this.aliases = Arrays.asList(aliases);
    }

    public abstract void onCommand(String[] args, String command);


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
}
