package online.afeibaili.bot.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {
    String model = "Kwai-Kolors/Kolors";
    String prompt;
    /**
     * 图片大小
     */
    @JsonProperty("image_size")
    String imageSize = ImageSize.DEFAULT.toString();
    /**
     * 数量
     */
    @JsonProperty("batch_size")
    Integer batchSize = 1;
    /**
     * 推理步骤数
     */
    @JsonProperty("num_inference_steps")
    Integer numInferenceSteps = 20;
    /**
     * 匹配程度
     */
    @JsonProperty("guidance_scale")
    Double guidanceScale = 7.5;

    public Image(String prompt) {
        this.prompt = prompt;
    }

    public Image(String prompt, Integer batchSize) {
        this.prompt = prompt;
        this.batchSize = batchSize;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getNumInferenceSteps() {
        return numInferenceSteps;
    }

    public void setNumInferenceSteps(Integer numInferenceSteps) {
        this.numInferenceSteps = numInferenceSteps;
    }

    public Double getGuidanceScale() {
        return guidanceScale;
    }

    public void setGuidanceScale(Double guidanceScale) {
        this.guidanceScale = guidanceScale;
    }

    public enum ImageSize {
        DEFAULT("1024x1024"),
        HEIGHT("960x1280"),
        SMALLEST("760x1024"),
        ;

        String size;

        ImageSize(String size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return this.size;
        }
    }
}
