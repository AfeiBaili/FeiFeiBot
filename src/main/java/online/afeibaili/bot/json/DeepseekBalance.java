package online.afeibaili.bot.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeepseekBalance {
    @JsonProperty("is_available")
    Boolean isAvailable;
    @JsonProperty("balance_infos")
    DeepseekBalanceData[] balanceInfos;

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public DeepseekBalanceData[] getBalanceInfos() {
        return balanceInfos;
    }

    protected void setBalanceInfos(DeepseekBalanceData[] balanceInfos) {
        this.balanceInfos = balanceInfos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (DeepseekBalanceData balanceInfo : balanceInfos) {
            sb.append(balanceInfo.toString()).append("\n\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        return "是否有可用余额：" + (isAvailable ? "有" : "无") + "\n" +
                "余额列表：\n" + sb;
    }
}