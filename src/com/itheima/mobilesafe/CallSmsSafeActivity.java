package com.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.itheima.mobilesafe.db.dao.BlackNumberDB;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

import java.util.List;

/**
 * Created by Bruce
 * Data 2014/7/24
 * Time 15:26.
 */
public class CallSmsSafeActivity extends Activity {
    private ListView lv_callsms_safe;

    private List<BlackNumberInfo> info;

    private SmsSafeAdapter adapter;
    private BlackNumberDB db;
    private LinearLayout ll_loading;
    private int offset = 0;
    private int maxnum = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsms);
        lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
        db = new BlackNumberDB(this);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        fillData();

        //listView 注册一个滚动监听事件
        lv_callsms_safe.setOnScrollListener(new AbsListView.OnScrollListener() {
            //当滚动的状态变化的时候调用
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: //空闲状态
                        //判断ListView滚动的位置
                        //获取最后一个可视化的item
                       int lastPosition =  lv_callsms_safe.getLastVisiblePosition();
                        //如果恰巧等于加载的数目
                        if(info.size() == lastPosition+1){
                        //列表被移动到最后一个位置，加载更多数据
                            offset+=maxnum;
                            fillData();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: //惯性滚动状态
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //触摸滚动状态
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                super.run();
                //info 为空时 为其赋值 ，不为空时在其后面添加参数
                if(info == null) {
                    info = db.findPart(offset, maxnum);
                }else{
                    info.addAll(db.findPart(offset, maxnum));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        //如果适配器为空则创建适配器，不为空则只需要更新适配器就行
                        if(adapter ==null) {
                            adapter = new SmsSafeAdapter();
                            lv_callsms_safe.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    private class SmsSafeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return info.size();
        }

        @Override
        public Object getItem(int position) {
            return info.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view;
            //1.减少内存中创建view的次数，对convertView的复用
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.list_item_smssafe, null);
                Log.i("创建一个新对象", position + "");
                //减少子孩子的查询次数，在view创建的时候就把对应的对象保存起来，实际是查询在内存的地址
                holder = new ViewHolder();
                holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_mode);
                holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                view.setTag(holder);//以holder为标签保存对应的对象
            } else {
                view = convertView;
                Log.i("复用隐藏的View对象couvertView", position + "");
                holder = (ViewHolder) view.getTag(); //取出对应对象的hodler
            }

            holder.tv_number.setText(info.get(position).number);
            String mode = info.get(position).mode;
            if ("1".equals(mode)) {
                holder.tv_mode.setText("拦截电话");
            } else if ("2".equals(mode)) {
                holder.tv_mode.setText("拦截短信");
            } else {
                holder.tv_mode.setText("拦截电话和短信");
            }

           final int pos = position;
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.delete(info.get(pos).number);
                    info.remove(pos);
                    adapter.notifyDataSetChanged();
                }
            });

            return view;
        }
    }

    /**
     * View 对象的容器
     * 记录对象的内存地址
     * 相当于一个记事本
     */
    class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }


    private EditText et_black_number;
    private CheckBox cb_phone;
    private CheckBox cb_message;
    private Button ok;
    private Button cancle;

    public void addBlackNumber(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        et_black_number = (EditText) contentView.findViewById(R.id.et_black_number);
        cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
        cb_message = (CheckBox) contentView.findViewById(R.id.cb_message);
        ok = (Button) contentView.findViewById(R.id.ok);
        cancle = (Button) contentView.findViewById(R.id.cancle);

        dialog.setView(contentView);
        dialog.show();

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = et_black_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(CallSmsSafeActivity.this, "号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode;
                if (cb_message.isChecked() && cb_phone.isChecked()) {
                    mode = "3";
                } else if (cb_phone.isChecked()) {
                    mode = "1";
                } else if (cb_message.isChecked()) {
                    mode = "2";
                } else {
                    Toast.makeText(CallSmsSafeActivity.this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.insert(number,mode);
                //将数据添加到集合
                BlackNumberInfo info1 = new BlackNumberInfo();
                info1.mode = mode;
                info1.number =number;
                info.add(0,info1); //在listView的第一个位置添加
                //通知更新
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }
}
