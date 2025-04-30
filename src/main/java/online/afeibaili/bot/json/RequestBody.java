package online.afeibaili.bot.json;

import java.util.List;

public class RequestBody {
    String model;
    List<Message> messages;
    Double temperature;
    Boolean stream = false;

    public RequestBody(String model, List<Message> messages, Double temperature, Boolean stream) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.stream = stream;
    }

    public RequestBody(String model, List<Message> messages, Boolean stream) {
        this.model = model;
        this.messages = messages;
        this.stream = stream;
    }

    public RequestBody(String model, List<Message> messages, Double temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }

    public RequestBody(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
