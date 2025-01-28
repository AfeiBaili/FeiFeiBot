package online.afeibaili.jsonmap;

import java.util.List;

public class RequestBody {
    String model = "gpt-3.5-turbo";
    List<Message> messages;
    Double temperature;

    public RequestBody() {
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
