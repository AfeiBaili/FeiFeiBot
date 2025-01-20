package online.afeibaili.chat.jsonmap;

import java.util.List;

public class RequestBody {
    String model = "gpt-3.5-turbo";
    List<Message> messages;

    public RequestBody() {
    }

    public RequestBody(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
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
