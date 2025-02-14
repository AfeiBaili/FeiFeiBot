package online.afeibaili.other;

import net.mamoe.mirai.event.events.GroupMessageEvent;

@FunctionalInterface
public interface Method {
    String get(String[] param, GroupMessageEvent messageEvent);
}
