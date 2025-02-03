package online.afeibaili.other;

import online.afeibaili.bot.ChatGPT;

public enum Model {
    A("gpt-3.5-turbo", 0.0035, 0.0105),
    B("gpt-3.5-turbo-1106", 0.007, 0.014),
    C("gpt-3.5-turbo-0125", 0.0035, 0.0105),
    D("gpt-3.5-turbo-16k", 0.021, 0.028),
    E("gpt-3.5-turbo-instruct", 0.0105, 0.014),
    F("o1-mini", 0.021, 0.084),
    G("o1-preview", 0.105, 0.42),
    H("gpt-4", 0.21, 0.42),
    I("gpt-4o", 0.0175, 0.07),
    J("gpt-4o-2024-05-13", 0.035, 0.105),
    K("gpt-4o-2024-08-06", 0.0175, 0.07),
    L("gpt-4o-2024-11-20", 0.0175, 0.07),
    M("chatgpt-4o-latest", 0.035, 0.105),
    N("gpt-4o-mini", 0.00105, 0.0042),
    O("gpt-4-0613", 0.21, 0.42),
    P("gpt-4-turbo-preview", 0.07, 0.21),
    Q("gpt-4-0125-preview", 0.07, 0.21),
    R("gpt-4-1106-preview", 0.07, 0.21),
    S("gpt-4-vision-preview", 0.07, 0.21),
    T("gpt-4-turbo", 0.07, 0.21),
    U("gpt-4-turbo-2024-04-09", 0.07, 0.21),
    V("gpt-3.5-turbo-ca", 0.001, 0.003),
    W("gpt-4-ca", 0.12, 0.24),
    X("gpt-4-turbo-ca", 0.04, 0.12),
    Y("gpt-4o-ca", 0.01, 0.04),
    Z("chatgpt-4o-latest-ca", 0.02, 0.06),
    AA("o1-mini-ca", 0.012, 0.048),
    AB("o1-preview-ca", 0.06, 0.24),
    AC("claude-3-5-sonnet-20240620", 0.015, 0.075),
    AD("claude-3-5-sonnet-20241022", 0.015, 0.075),
    AE("claude-3-5-haiku-20241022", 0.005, 0.025),
    AF("gemini-1.5-flash-latest", 0.0006, 0.0024),
    AG("gemini-1.5-pro-latest", 0.01, 0.04),
    AH("gemini-exp-1206", 0.01, 0.04),
    AI("gemini-2.0-flash-exp", 0.01, 0.04);

    final String model;
    final Double input;
    final Double output;

    Model(String model, Double input, Double output) {
        this.model = model;
        this.input = input;
        this.output = output;
        ChatGPT.MODELS.add(this);
    }

    public String getModel() {
        return model;
    }

    public Double getInput() {
        return input;
    }

    public Double getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "model " + model + ", input " + input + ", output " + output;
    }
}
