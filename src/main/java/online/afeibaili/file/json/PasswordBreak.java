package online.afeibaili.file.json;

/**
 * 密文破译的JSON配置类
 *
 * @author AfeiBaili
 * @version 2025/7/30 14:48
 */

public class PasswordBreak {
    boolean isOpen;
    String fontPath;

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
