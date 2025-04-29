package online.afeibaili.file.json;

public class Setting {
    String atByTargetBot;
    String startMessage;
    String commandPrefix;

    @Override
    public String toString() {
        return "Setting{" +
                "atByTargetBot='" + atByTargetBot + '\'' +
                ", startMessage='" + startMessage + '\'' +
                '}';
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public void setStartMessage(String startMessage) {
        this.startMessage = startMessage;
    }

    public String getAtByTargetBot() {
        return atByTargetBot;
    }

    public void setAtByTargetBot(String atByTargetBot) {
        this.atByTargetBot = atByTargetBot;
    }
}
