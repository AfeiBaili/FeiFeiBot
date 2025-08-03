package online.afeibaili.file.json;

public class Module {
    boolean openMemoryName;
    boolean mcModSearch;
    boolean translation;
    PasswordBreak passwordBreakGame;

    public PasswordBreak getPasswordBreakGame() {
        return passwordBreakGame;
    }

    public void setPasswordBreakGame(PasswordBreak passwordBreakGame) {
        this.passwordBreakGame = passwordBreakGame;
    }

    public boolean isMcModSearch() {
        return mcModSearch;
    }

    public void setMcModSearch(boolean mcModSearch) {
        this.mcModSearch = mcModSearch;
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
