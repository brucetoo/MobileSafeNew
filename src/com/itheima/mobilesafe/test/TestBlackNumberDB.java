package com.itheima.mobilesafe.test;

import android.test.AndroidTestCase;
import com.itheima.mobilesafe.db.dao.BlackNumberDB;
import com.itheima.mobilesafe.db.dao.BlackNumberDBOpenHelper;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by Bruce
 * Data 2014/7/24
 * Time 15:58.
 * 测试数据库是否创建成功
 */
public class TestBlackNumberDB extends AndroidTestCase {
     public void testCreateDB() throws Exception{
         BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
         //如果可写的话说明数据库创建成功了
         helper.getWritableDatabase();
    }

     public void testAdd() throws Exception{
         BlackNumberDB dao = new BlackNumberDB(getContext());
         long basenumber = 13500000000l;
         Random random = new Random();
         for (int i = 0; i < 100; i++) {
             dao.insert(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
         }
     }

    public void testFindAll() throws Exception{
        BlackNumberDB dao = new BlackNumberDB(getContext());
        List<BlackNumberInfo> infos = dao.findAll();
        for(BlackNumberInfo info:infos){
            System.out.println(info.toString());
        }
    }

    public void testDele() throws Exception{
        BlackNumberDB db = new BlackNumberDB(getContext());
        db.delete("111");
    }

    public void testUpdate() throws Exception{
        BlackNumberDB db = new BlackNumberDB(getContext());
        db.update("111", "2");
    }

    public void testFind() throws Exception{
        BlackNumberDB db = new BlackNumberDB(getContext());
        boolean res = db.find("111");
      //  assertEquals(true,res);
    }

}
