package com.yp2019cl.testflowlayout;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import com.yp2019cl.flowlayout.FlowLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private FlowLayout mFlowLayout;

    private String [] mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlowLayout = (FlowLayout) findViewById(R.id.main_flowlayout);

        mDatas = new String[]{"QQ","视频","放开那三国","电子书","酒店","单机","小说","斗地主","优酷","网游","WIFI万能钥匙","播放器","捕鱼达人2","机票",
                "游戏","熊出没之熊大快跑","美图秀秀","浏览器","单机游戏","我的世界","电影电视","QQ空间","旅游","免费游戏","2048",
                "刀塔传奇","壁纸","节奏大师","锁屏","装机必备","天天动听","备份","网盘","海淘网","大众点评","爱奇艺视频",
                "腾讯手机管家","百度地图","猎豹清理大师","谷歌地图","hao123上网导航","京东","youni有你","万年历-农历黄历","支付宝钱包"};


        Random random = new Random();
        for (int i = 0; i < mDatas.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(mDatas[i]);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(18);
            tv.setGravity(Gravity.CENTER);

            tv.setClickable(true);

            int alpha = 255;
            int red = random.nextInt(200) + 30;
            int green = random.nextInt(200) + 30;
            int blue = random.nextInt(200) + 30;

            int rgb = Color.argb(alpha, red, green, blue);

            GradientDrawable normalDrawable = new GradientDrawable();
            normalDrawable.setShape(GradientDrawable.RECTANGLE);
            normalDrawable.setCornerRadius(5);
            normalDrawable.setColor(rgb);

            GradientDrawable selectDrawable = new GradientDrawable();
            selectDrawable.setShape(GradientDrawable.RECTANGLE);
            selectDrawable.setCornerRadius(5);
            selectDrawable.setColor(Color.GRAY);

            StateListDrawable selector = new StateListDrawable();
            selector.addState(new int[]{android.R.attr.state_pressed}, selectDrawable);
            selector.addState(new int[]{}, normalDrawable);

            tv.setBackgroundDrawable(selector);

            mFlowLayout.addView(tv);
        }
    }
}
