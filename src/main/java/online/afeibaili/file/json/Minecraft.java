package online.afeibaili.file.json;

/**
 * # 我的世界配置文件
 *
 * @author AfeiBaili
 * @version 2026/8/24 11:40
 */

public class Minecraft {
    boolean isOpen;
    String mcmodCookies;

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getMcmodCookies() {
        return mcmodCookies;
    }

    public void setMcmodCookies(String mcmodCookies) {
        this.mcmodCookies = mcmodCookies;
    }
}
