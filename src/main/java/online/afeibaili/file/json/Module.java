package online.afeibaili.file.json;

public class Module {
    boolean openMemoryName;
    boolean enableMinecraft;
    //    boolean enableTerraria;
    boolean translation;
    PasswordBreak passwordBreakGame;
    boolean echoCave;
    UploadFile uploadFile;
    boolean openJoinLeaveMessage;
    boolean putChat;

    public boolean isPutChat() {
        return putChat;
    }

    public void setPutChat(boolean putChat) {
        this.putChat = putChat;
    }

    public boolean isOpenJoinLeaveMessage() {
        return openJoinLeaveMessage;
    }

    public void setOpenJoinLeaveMessage(boolean openJoinLeaveMessage) {
        this.openJoinLeaveMessage = openJoinLeaveMessage;
    }

    public UploadFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public boolean isEchoCave() {
        return echoCave;
    }

    public boolean isEnableMinecraft() {
        return enableMinecraft;
    }

    public void setEnableMinecraft(boolean enableMinecraft) {
        this.enableMinecraft = enableMinecraft;
    }

    public void setEchoCave(boolean echoCave) {
        this.echoCave = echoCave;
    }

    public PasswordBreak getPasswordBreakGame() {
        return passwordBreakGame;
    }

    public void setPasswordBreakGame(PasswordBreak passwordBreakGame) {
        this.passwordBreakGame = passwordBreakGame;
    }

    public boolean isTranslation() {
        return translation;
    }

    public void setTranslation(boolean translation) {
        this.translation = translation;
    }

    public boolean isOpenMemoryName() {
        return openMemoryName;
    }

    public void setOpenMemoryName(boolean openMemoryName) {
        this.openMemoryName = openMemoryName;
    }
}
