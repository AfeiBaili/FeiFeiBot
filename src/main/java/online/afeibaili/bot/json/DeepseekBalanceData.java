package online.afeibaili.bot.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeepseekBalanceData {
    String currency;
    @JsonProperty("total_balance")
    String totalBalance;
    @JsonProperty("granted_balance")
    String grantedBalance;
    @JsonProperty("topped_up_balance")
    String toppedUpBalance;

    @Override
    public String toString() {
        return "货币类型：" + currency + '\n' +
                "可用余额：" + totalBalance + '\n' +
                "赠金余额：" + grantedBalance + '\n' +
                "充值余额" + toppedUpBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getGrantedBalance() {
        return grantedBalance;
    }

    public void setGrantedBalance(String grantedBalance) {
        this.grantedBalance = grantedBalance;
    }

    public String getToppedUpBalance() {
        return toppedUpBalance;
    }

    public void setToppedUpBalance(String toppedUpBalance) {
        this.toppedUpBalance = toppedUpBalance;
    }
}
