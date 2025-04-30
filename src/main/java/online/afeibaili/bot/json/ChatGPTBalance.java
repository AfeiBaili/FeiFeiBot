package online.afeibaili.bot.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.DecimalFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTBalance {

    Integer id;
    String apiKey;
    Integer adminKeyId;
    Double balanceTotal;
    Double balanceUsed;
    Integer status;

    public ChatGPTBalance() {
    }

    public ChatGPTBalance(Integer id, String apiKey, Integer adminKeyId, Double balanceTotal, Double balanceUsed, Integer status) {
        this.id = id;
        this.apiKey = apiKey;
        this.adminKeyId = adminKeyId;
        this.balanceTotal = balanceTotal;
        this.balanceUsed = balanceUsed;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getAdminKeyId() {
        return adminKeyId;
    }

    public void setAdminKeyId(Integer adminKeyId) {
        this.adminKeyId = adminKeyId;
    }

    public Double getBalanceTotal() {
        return balanceTotal;
    }

    public void setBalanceTotal(Double balanceTotal) {
        this.balanceTotal = balanceTotal;
    }

    public Double getBalanceUsed() {
        return balanceUsed;
    }

    public void setBalanceUsed(Double balanceUsed) {
        this.balanceUsed = balanceUsed;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "当前额度：" + decimalFormat((this.balanceTotal - this.balanceUsed)) + "\n" +
                "使用额度：" + decimalFormat(this.balanceUsed) + "\n" +
                "总共额度：" + this.balanceTotal + "\n" +
                "Key: " + this.apiKey.substring(this.apiKey.length() - 10);
    }

    public String decimalFormat(Double value) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(value);
    }
}
