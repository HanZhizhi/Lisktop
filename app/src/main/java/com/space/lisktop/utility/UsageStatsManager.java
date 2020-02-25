package com.space.lisktop.utility;

import android.app.usage.ConfigurationStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;

import java.util.List;
import java.util.Map;

public final class UsageStatsManager {
    public static final int INTERVAL_BEST = 4; //根据提供的开始、结束时间决定时间间隔
    public static final int INTERVAL_DAILY = 0; //以天为时间间隔（最长7天）
    public static final int INTERVAL_MONTHLY = 2; //以月为时间间隔（最长6个月）
    public static final int INTERVAL_WEEKLY = 1; //以周为时间间隔（最长4个星期）
    public static final int INTERVAL_YEARLY = 3; //以年为时间间隔（最长2年）

    UsageStatsManager() {
        throw new RuntimeException("Stub!");
    }

    public List<UsageStats> queryUsageStats(int intervalType, long beginTime, long endTime) {
        throw new RuntimeException("Stub!");
    }

    public List<ConfigurationStats> queryConfigurations(int intervalType, long beginTime, long endTime) {
        throw new RuntimeException("Stub!");
    }

    public UsageEvents queryEvents(long beginTime, long endTime) {
        throw new RuntimeException("Stub!");
    }

    public Map<String, UsageStats> queryAndAggregateUsageStats(long beginTime, long endTime) {
        throw new RuntimeException("Stub!");
    }

    public boolean isAppInactive(String packageName) {
        throw new RuntimeException("Stub!");
    }
}