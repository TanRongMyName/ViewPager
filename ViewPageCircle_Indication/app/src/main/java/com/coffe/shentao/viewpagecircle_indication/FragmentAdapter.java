package com.coffe.shentao.viewpagecircle_indication;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends AppCompatActivity {
    List<Fragment> list=new ArrayList<>();
    ViewPager viewpager ;
    private CircleIndicator circleIndicator;
    List<String> titles=new ArrayList<>();
    private MagicIndicator indicator;
    String[]holdays=new String[]{"万圣节","圣诞节","清明节","重阳节"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_adapter);
        initView();
        initData();
    }


    private void initView() {

        circleIndicator=findViewById(R.id.indicator);
        viewpager=findViewById(R.id.viewpager);
    }

    private void initData() {
        for (int i = 0; i < holdays.length; i++) {
            titles.add(holdays[i]);
//            TextView tv = new TextView(this);
//            tv.setText("pager"+i);
//            tv.setTextSize(18);
//            tv.setGravity(Gravity.CENTER);
//            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            views.add(tv);
        }
        list.add(new OneFragment());
        list.add(new TwoFragment());
        list.add(new ThreeFragment());
        list.add(new FourFragment());
        viewpager.setAdapter(pagerAdapter);
        circleIndicator.setViewPager(viewpager);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int pPosition) {
                indicator.onPageSelected(pPosition);
            }

            @Override
            public void onPageScrollStateChanged(int pState) {
                indicator.onPageScrollStateChanged(pState);
            }
        });
        //修改切换的时间
        Field mScroller;
        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(this);
            scroller.setmDuration(3000);
            mScroller.set(viewpager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.v("TanRong","出错了：NoSuchFieldException"+e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.v("TanRong","出错了：IllegalAccessException"+e.getMessage());
        }
       initIndicator();


    }
    private void initIndicator() {
        indicator=(MagicIndicator) findViewById(R.id.top_indicator);
        CommonNavigator navigator=new CommonNavigator(this);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                SimplePagerTitleView titleView=new SimplePagerTitleView(context);
                titleView.setText(titles.get(i));
                titleView.setTextSize(18);
                titleView.setNormalColor(Color.LTGRAY);
                titleView.setSelectedColor(Color.WHITE);
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewpager.setCurrentItem(i);
                    }
                });
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                BezierPagerIndicator pagerIndicator=new BezierPagerIndicator(context);
                pagerIndicator.setColors(Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.GREEN);
                return pagerIndicator;
            }
        });
        indicator.setNavigator(navigator);
    }
    FragmentPagerAdapter pagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int i) {
            return list.get(i);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
