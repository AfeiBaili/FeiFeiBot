package online.afeibaili.bot.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class ImageResponse {
    Image[] images;
    Timing timings;
    Long seed;
    @JsonProperty("shared_id")
    String sharedId;
    Image[] data;
    Long created;

    @Override
    public String toString() {
        return "ImageResponse{" +
                "images=" + Arrays.toString(images) +
                ", timings=" + timings +
                ", seed=" + seed +
                ", sharedId='" + sharedId + '\'' +
                ", data=" + Arrays.toString(data) +
                ", created=" + created +
                '}';
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public Timing getTimings() {
        return timings;
    }

    public void setTimings(Timing timings) {
        this.timings = timings;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public String getSharedId() {
        return sharedId;
    }

    public void setSharedId(String sharedId) {
        this.sharedId = sharedId;
    }

    public Image[] getData() {
        return data;
    }

    public void setData(Image[] data) {
        this.data = data;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public static class Image {
        String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return url;
        }
    }

    public static class Timing {
        Double inference;

        public Double getInference() {
            return inference;
        }

        public void setInference(Double inference) {
            this.inference = inference;
        }

        @Override
        public String toString() {
            return "Timing{" +
                    "inference=" + inference +
                    '}';
        }
    }
}