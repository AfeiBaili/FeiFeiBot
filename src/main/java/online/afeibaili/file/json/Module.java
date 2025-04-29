package online.afeibaili.file.json;

public class Module {
    boolean openMemoryName;

    public boolean isOpenMemoryName() {
        return openMemoryName;
    }

    public void setOpenMemoryName(boolean openMemoryName) {
        this.openMemoryName = openMemoryName;
    }

    @Override
    public String toString() {
        return "Module{" +
                "openMemoryName=" + openMemoryName +
                '}';
    }
}
