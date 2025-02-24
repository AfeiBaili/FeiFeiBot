package online.afeibaili.command;

public final class Command {
    private final Method method;
    private final Integer level;

    private Command(Builder builder) {
        this.method = builder.method;
        this.level = builder.level;
    }

    public Method getMethod() {
        return method;
    }

    public Integer getLevel() {
        return level;
    }

    public static class Builder {
        private Integer level = 0;
        private Method method;

        public Builder() {
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder level(Integer level) {
            this.level = level;
            return this;
        }

        public Command build() {
            return new Command(this);
        }
    }
}
