package online.afeibaili.jsonmap;

public class Choice {
    Integer index;
    Message message;
    String finish_reason;
    Boolean logprobs;

    public Choice(Integer index, Message message, String finish_reason, Boolean logprobs) {
        this.index = index;
        this.message = message;
        this.finish_reason = finish_reason;
        this.logprobs = logprobs;
    }


    public Choice() {
    }

    public Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
}
