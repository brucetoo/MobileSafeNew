package com.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.itheima.mobilesafe.utils.MD5Utils;

public class HomeActivity extends Activity {

    private GridView list_home;
    private MyAdapter adapter;
    private SharedPreferences sp;

    private static String[] names = {
            "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"

    };

    private static int[] ids = {
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        list_home = (GridView) findViewById(R.id.list_home);
        adapter = new MyAdapter();
        list_home.setAdapter(adapter);
        list_home.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent;
                switch (position) {
                    case 8://进入设置中心
                        intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case 7://进入工具中心
                        intent = new Intent(HomeActivity.this, AToolsActivity.class);
                        startActivity(intent);
                        break;
                    case 1://进入通讯卫士
                        intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2://进入软件管理
                        intent = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 3://进入进程管理
                        intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 4://流量管理
                        intent = new Intent(HomeActivity.this, TrafficStatActivity.class);
                        startActivity(intent);
                        break;
                    case 5://手机杀毒
                        intent = new Intent(HomeActivity.this, AntVirusActivity.class);
                        startActivity(intent);
                        break;
                    case 6://缓存清理
                        intent = new Intent(HomeActivity.this, CleanCacheActivity.class);
                        startActivity(intent);
                        break;
                    case 0: //进入防盗设置
                        showLastFoundDialog();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void showLastFoundDialog() {

        //判断是否设置过密码
        if (isSetupPsd()) {
            showEnterDialog();
        } else {
            showSetupDialog();
        }

    }


    private EditText et_setup_pwd;
    private EditText et_setup_confirm;
    private Button ok;
    private Button cancle;
    private AlertDialog dialog;

    /**
     *  设置密码对话框
     */
    private void showSetupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //自定义一个带有输入框的对话框
        View view = View.inflate(this, R.layout.dialog_setup_password, null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
        ok = (Button) view.findViewById(R.id.ok);
        //取出密码
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = et_setup_pwd.getText().toString().trim();
                String confirm = et_setup_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.equals(confirm)) {

                    sp.edit().putString("password", MD5Utils.md5Password(password)).commit();
                    dialog.dismiss();

                    //跳转页面
                    Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        cancle = (Button) view.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //builder.setView(view);
        //返回dialog
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    /**
     * 显示 输入密码对话框
     */
    private void showEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //自定义一个带有输入框的对话框
        View view = View.inflate(this, R.layout.dialog_enter_password, null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_enter_pwd);
        ok = (Button) view.findViewById(R.id.ok);
        //取出密码
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = et_setup_pwd.getText().toString().trim();
                //密码匹配成功
                if (MD5Utils.md5Password(password).equals(sp.getString("password", null))) {

                    dialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                    startActivity(intent);
                    //  Toast.makeText(HomeActivity.this, "caomima", Toast.LENGTH_LONG).show();
                    //进入主页面
                } else { //匹配失败
                    Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        cancle = (Button) view.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        //����dialog
        dialog = builder.show();
        dialog.setView(view, 0, 0, 0, 0);
    }

    /**
     * 判断是否已经设置过密码
     *
     * @return
     */
    private boolean isSetupPsd() {

        String password = sp.getString("password", null);
        return !TextUtils.isEmpty(password);
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
            ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

            tv_item.setText(names[position]);
            iv_item.setImageResource(ids[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }


    }

}
