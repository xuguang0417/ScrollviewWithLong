package com.github.xuguang0417.scrollviewwithlong;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.github.xuguang0417.scrollviewwithlong.scrollview.TopFloatScrollView;
import com.github.xuguang0417.scrollviewwithlong.util.DensityUtil;
import com.github.xuguang0417.scrollviewwithlong.util.LocalImageHolderView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @BindView(R.id.line_top)
    LinearLayout lineTop;
    @BindView(R.id.tabs_order)
    TabLayout tabsOrder;
    @BindView(R.id.text_hotel_introduction)
    TextView textHotelIntroduction;
    @BindView(R.id.line_hotel_introduction)
    LinearLayout lineHotelIntroduction;                 //酒店介绍
    @BindView(R.id.text_cost)
    TextView textCost;
    @BindView(R.id.line_cost)
    LinearLayout lineCost;                              //费用说明
    @BindView(R.id.text_reserve)
    TextView textReserve;
    @BindView(R.id.line_reserve)
    LinearLayout lineReserve;                           //预定须知
    @BindView(R.id.text_refund)
    TextView textRefund;
    @BindView(R.id.line_refund)
    LinearLayout lineRefund;                            //退款说明
    @BindView(R.id.text_prompt)
    TextView textPrompt;
    @BindView(R.id.line_prompt)
    LinearLayout linePrompt;
    @BindView(R.id.scroll_view)
    TopFloatScrollView scrollView;

    //页卡标题集合
    private ArrayList<String> mTitleList = new ArrayList<String>();
    //轮播图图集
    private ArrayList list = new ArrayList();


    //判断  粘性头部的距离测量方法  是不是加载一次
    private boolean isFirstOnMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initConvenientBanner();
        initTabLayout();
        setListener();
    }

    private void initView() {
        isFirstOnMeasure = true;
    }

    private void setListener() {
        //scrollView的滚动监听
        scrollView.setOnTopScrollListener(new TopFloatScrollView.OnTopScrollListener() {
            @Override
            public void onScrollY(int scrollY) {
                if (isFirstOnMeasure) {
                    //防止控件获取 粘性头部 距离顶部的距离
                    scrollView.onWindowFocusChanged(true);
                    isFirstOnMeasure = false;
                }

                if (scrollY < lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                        DensityUtil.dp2px(MainActivity.this, 10)) {
                    tabsOrder.setScrollPosition(0, 0, true);
                } else if (scrollY < lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                        lineCost.getHeight() + DensityUtil.dp2px(MainActivity.this, 20)) {
                    tabsOrder.setScrollPosition(1, 0, true);
                } else if (scrollY < lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                        lineCost.getHeight() + lineReserve.getHeight() +
                        DensityUtil.dp2px(MainActivity.this, 30)) {
                    tabsOrder.setScrollPosition(2, 0, true);
                } else if (scrollY < lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                        lineCost.getHeight() + lineReserve.getHeight() + lineRefund.getHeight() +
                        DensityUtil.dp2px(MainActivity.this, 30)) {
                    tabsOrder.setScrollPosition(3, 0, true);
                } else if (scrollY < lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                        lineCost.getHeight() + lineReserve.getHeight() + lineRefund.getHeight() +
                        linePrompt.getHeight() + DensityUtil.dp2px(MainActivity.this, 40)) {
                    tabsOrder.setScrollPosition(4, 0, true);
                }
            }
        });

        //标卡  监听            定位
        tabsOrder.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        scrollView.onScroll(0, lineTop.getHeight());
                        break;
                    case 1:
                        scrollView.onScroll(0, lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                                DensityUtil.dp2px(MainActivity.this, 10));
                        break;
                    case 2:
                        scrollView.onScroll(0, lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                                lineCost.getHeight() + DensityUtil.dp2px(MainActivity.this, 20));
                        break;
                    case 3:
                        scrollView.onScroll(0, lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                                lineCost.getHeight() + lineReserve.getHeight() +
                                DensityUtil.dp2px(MainActivity.this, 30));
                        break;
                    case 4:
                        scrollView.onScroll(0, lineTop.getHeight() + lineHotelIntroduction.getHeight() +
                                lineCost.getHeight() + lineReserve.getHeight() + lineRefund.getHeight() +
                                DensityUtil.dp2px(MainActivity.this, 30));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //必须加上
                tabsOrder.setScrollPosition(tab.getPosition(), 0F, true);
            }
        });
    }

    private void initConvenientBanner() {
        list.add(R.drawable.haizei);
        list.add(R.drawable.haizei);
        list.add(R.drawable.haizei);
        list.add(R.drawable.haizei);
        list.add(R.drawable.haizei);

        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new LocalImageHolderView();
            }
        }, list);
    }

    //初始化 TabLayout
    private void initTabLayout() {
        mTitleList.add("酒店介绍");
        mTitleList.add("费用说明");
        mTitleList.add("预定须知");
        mTitleList.add("退款说明");
        mTitleList.add("温馨提示");
        //设置tab模式，当前为系统默认模式
        tabsOrder.setTabMode(TabLayout.MODE_SCROLLABLE);
        //添加tab选项卡
        tabsOrder.addTab(tabsOrder.newTab().setText(mTitleList.get(0)));
        tabsOrder.addTab(tabsOrder.newTab().setText(mTitleList.get(1)));
        tabsOrder.addTab(tabsOrder.newTab().setText(mTitleList.get(2)));
        tabsOrder.addTab(tabsOrder.newTab().setText(mTitleList.get(3)));
        tabsOrder.addTab(tabsOrder.newTab().setText(mTitleList.get(4)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //开始自动翻页
        ViewGroup.LayoutParams params = convenientBanner.getLayoutParams();
        convenientBanner.setLayoutParams(params);
        convenientBanner.startTurning(2000);
    }
}





















