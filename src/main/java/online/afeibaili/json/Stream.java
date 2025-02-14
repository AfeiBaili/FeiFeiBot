package online.afeibaili.json;

import java.util.List;

public class Stream {
    String id;
    List<StreamChoice> choices;
    Integer created;
    String model;
    String object;
    String system_fingerprint;

    public Stream() {
    }

    public Stream(String id, List<StreamChoice> choices, Integer created, String model, String object, String system_fingerprint) {
        this.id = id;
        this.choices = choices;
        this.created = created;
        this.model = model;
        this.object = object;
        this.system_fingerprint = system_fingerprint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<StreamChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<StreamChoice> choices) {
        this.choices = choices;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getSystem_fingerprint() {
        return system_fingerprint;
    }

    public void setSystem_fingerprint(String system_fingerprint) {
        this.system_fingerprint = system_fingerprint;
    }

    @Override
    public String toString() {
        return "Stream{" +
                "id='" + id + '\'' +
                ", choices=" + choices +
                ", created=" + created +
                ", model='" + model + '\'' +
                ", object='" + object + '\'' +
                ", system_fingerprint='" + system_fingerprint + '\'' +
                '}';
    }
}
