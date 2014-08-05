package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.itheima.mobilesafe.db.dao.AntiVirusDao;
import com.itheima.mobilesafe.utils.MD5Utils;

import java.util.List;

/**
 * Created by Bruce
 * Data 2014/8/5
 * Time 9:16.
 */
public class AntVirusActivity extends Activity {
    private ImageView iv_scanning;        //雷达
    private ProgressBar pb;
    private PackageManager pm;
    private TextView tv_scan_status;      //文本提示
    private LinearLayout ll_scan_status; //扫描状态布局
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:  //扫描中
                    ScanInfo info = (ScanInfo) msg.obj;
                    tv_scan_status.setText("Scanning:"+info.name);
                    TextView textView = new TextView(AntVirusActivity.this);
                    if(info.isVirus){
                        textView.setText("Virus:"+info.name);
                        textView.setTextColor(Color.RED);
                        ll_scan_status.addView(textView,0);//每次放在最上面
                    }else{
                        textView.setText("Security:"+info.name);
                        textView.setTextColor(Color.GRAY);
                        ll_scan_status.addView(textView,0);//每次放在最上面
                    }
                    break;
                case 2:  //扫描结束
                    iv_scanning.clearAnimation(); //结束动画
                    iv_scanning.setVisibility(View.INVISIBLE);//设置不可见
                    tv_scan_status.setText("Scan Over..");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ant_virus);
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        RotateAnimation ra = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,
                                                 Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scanning.setAnimation(ra);
        ll_scan_status = (LinearLayout) findViewById(R.id.ll_scan_status);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        scanVirus();
    }

    /**
     * 扫描病毒
     */
    private void scanVirus() {
        pm = getPackageManager();
        //获取数据库数据在子线程中完成
        tv_scan_status.setText("Openning engine........");
        new Thread(){
            @Override
            public void run() {
                //获取安装包的信息
                List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                                                                                  + PackageManager.GET_SIGNATURES);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                pb.setMax(packageInfos.size());
                int process = 0;
                for(PackageInfo info : packageInfos){
                    //从包信息中获取md5 用singatures时 需要加上 flag PackageManager.GET_SIGNATURES
                    String Md5 = MD5Utils.md5Password(info.signatures[0].toCharsString());
//                    System.out.println("Md5 = " + Md5);
                    ScanInfo scanInfo = new ScanInfo();
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.packnage = info.packageName;
                    String result = AntiVirusDao.isVirus(Md5);
                    if(!TextUtils.isEmpty(result)){
                        //是病毒
                        scanInfo.desc = result;
                        scanInfo.isVirus = true;
                    }else{
                        //不是病毒
                        scanInfo.isVirus = false;
                        scanInfo.desc = null;
                    }
                    //发送消息到主线程
                    Message msg = Message.obtain();
                    msg.obj = scanInfo;
                    msg.what = 1;
                    handler.sendMessage(msg);

                    process++;
                    pb.setProgress(process);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Message msg = Message.obtain();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }.start();
    }

    class ScanInfo{
        String packnage;//包名
        String name;//应用名
        boolean isVirus;//病毒否？
        String desc;//描述
    }
}
