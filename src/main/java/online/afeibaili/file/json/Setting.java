package online.afeibaili.file.json;

public class Setting {
    String currentBot;
    String startMessage;
    String commandPrefix;
    Integer maxChatLength;
    Boolean printNotFoundCommand;

    public Boolean getPrintNotFoundCommand() {
        return printNotFoundCommand;
    }

    public void setPrintNotFoundCommand(Boolean printNotFoundCommand) {
        this.printNotFoundCommand = printNotFoundCommand;
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

    public String getCurrentBot() {
        return currentBot;
    }

    public void setCurrentBot(String currentBot) {
        this.currentBot = currentBot;
    }

    public Integer getMaxChatLength() {
        return maxChatLength;
    }

    public void setMaxChatLength(Integer maxChatLength) {
        this.maxChatLength = maxChatLength;
    }
}
