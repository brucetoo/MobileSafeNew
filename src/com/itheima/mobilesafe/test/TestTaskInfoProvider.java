package com.itheima.mobilesafe.test;

import android.test.AndroidTestCase;
import com.itheima.mobilesafe.domain.TaskInfo;
import com.itheima.mobilesafe.engine.TaskInfoProvider;

import java.util.List;

/**
 * Created by Bruce
 * Data 2014/7/30
 * Time 14:14.
 */
public class TestTaskInfoProvider extends AndroidTestCase {
    public void testTaskInfo() throws Exception{
        List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfo(getContext());
       for(TaskInfo info : taskInfos){
           System.out.println("info"+info.toString());
       }
    }
}
