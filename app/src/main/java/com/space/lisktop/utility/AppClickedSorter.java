package com.space.lisktop.utility;

import android.os.Bundle;
import android.util.Log;

import com.space.lisktop.obj.AppInfo;
import java.util.ArrayList;

/**
 * 处理点击应用后的排序操作，包括：
 * 1.Lru排序：点击的应用置顶
 * 2.冒泡法排序：点击的应用上升一位
 * 3.次数限制排序：按照应用级别设定次数，次数多的在前，达到限制自动隐藏
 */

public class AppClickedSorter {
    // 对外方法，传入排序方法、应用列表及点击位置
    public static void Sort(int sortingMethod, ArrayList<AppInfo> list1,int whichApp){
        switch (sortingMethod){
            case 0:
                LruSorter(list1,whichApp);
                break;
            case 1:
                PopSorter(list1,whichApp);
                break;
            case 2:
                RestrictSorter(list1,whichApp);
                break;
        }
    }

    // 最近最新使用，将点击的应用从原位置删除并添加到开头
    private static void LruSorter(ArrayList<AppInfo> list, int which){
        AppInfo aIfo=list.get(which);
        Log.i("lru before:",list.get(0).getAppName());
        list.remove(list.get(which));
        list.add(0,aIfo);
        Log.i("lru after:",list.get(0).getAppName());
    }

    private static void PopSorter(ArrayList<AppInfo> list, int which){
        if(which>0){
            AppInfo aIfo=list.get(which);
            list.remove(list.get(which));
            list.add(which-1,aIfo);
        }
    }

    private static void RestrictSorter(ArrayList<AppInfo> list, int which){
        AppInfo aIfo=list.get(which);
    }
}
