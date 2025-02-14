package online.afeibaili.other;

import com.sun.management.OperatingSystemMXBean;
import online.afeibaili.FeiFeiBot;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

public class Memory {
    public static String getMemory() {
        OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalMemorySize = bean.getTotalMemorySize();
        long freeMemorySize = bean.getFreeMemorySize();
        return new DecimalFormat("0.00").format(100 - ((float) freeMemorySize / totalMemorySize) * 100);
    }
}
