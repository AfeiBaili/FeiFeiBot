package online.afeibaili.chat.jsonmap;

import java.util.List;

public class ResponseBody {
    String id;
    List<Choice> choices;
    String object;
    Integer created;
    Usage usage;
    String model;
    String system_fingerprint;

    public ResponseBody(String id, List<Choice> choices, String object, Integer created, Usage usage, String model, String system_fingerprint) {
        this.id = id;
        this.choices = choices;
        this.object = object;
        this.created = created;
        this.usage = usage;
        this.model = model;
        this.system_fingerprint = system_fingerprint;
    }

    public ResponseBody() {
    }

    public String getSystem_fingerprint() {
        return system_fingerprint;
    }

    public void setSystem_fingerprint(String system_fingerprint) {
        this.system_fingerprint = system_fingerprint;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}
