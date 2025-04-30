package online.afeibaili.bot.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamChoice {
    Integer index;
    Delta delta;
    String finish_reason;
    Boolean logprobs;


    public StreamChoice() {
    }

    public StreamChoice(Integer index, Delta delta, String finish_reason, Boolean logprobs) {
        this.index = index;
        this.delta = delta;
        this.finish_reason = finish_reason;
        this.logprobs = logprobs;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Delta getDelta() {
        return delta;
    }

    public void setDelta(Delta delta) {
        this.delta = delta;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }

    public Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Boolean logprobs) {
        this.logprobs = logprobs;
    }

    @Override
    public String toString() {
        return "StreamChoice{" +
                "index=" + index +
                ", delta=" + delta +
                ", finish_reason='" + finish_reason + '\'' +
                ", logprobs=" + logprobs +
                '}';
    }
}
