package online.afeibaili.file.json;

public class Bot {
    long qq;
    String name;

    @Override
    public String toString() {
        return "Bot{" +
                "qq=" + qq +
                ", name='" + name + '\'' +
                '}';
    }

    public long getQq() {
        return qq;
    }

    public void setQq(long qq) {
        this.qq = qq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
