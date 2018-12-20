package com.coffe.shentao.viewpagecircle_indication;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean mIsChanged = false;
    private int mCurrentPagePosition = FIRST_ITEM_INDEX;
    private static final int POINT_LENGTH = 3;
    private static final int FIRST_ITEM_INDEX = 0;
    int item=-1;
    Handler handler;
    List<Fragment> list=new ArrayList<>();
    ViewPager viewpager ;
    private CircleIndicator circleIndicator;
    private SimpleIndicator indicator;

    List<String> titles=new ArrayList<>();
    ArrayList<TextView> views=new ArrayList<>();
    String[]holdays=new String[]{"万圣节","圣诞节","清明节","重阳节"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        indicator=findViewById(R.id.id_indicator);
        circleIndicator=findViewById(R.id.indicator);
        viewpager=findViewById(R.id.viewpager);
    }

    private void initData() {
        for (int i = 0; i < holdays.length; i++) {
            titles.add(holdays[i]);
            TextView tv = new TextView(this);
            tv.setText("pager"+i);
            tv.setTextSize(18);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            views.add(tv);
        }


        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
            }
        },3000);
        list.add(new OneFragment());
        list.add(new TwoFragment());
        list.add(new ThreeFragment());
        list.add(new FourFragment());
        viewpager.setAdapter(pagerAdapter);
       circleIndicator.setViewPager(viewpager);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pPosition) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewpager.setCurrentItem((viewpager.getCurrentItem() + 1) % list.size());
                    }
                }, 4000);

//                mIsChanged = true;
//                if (pPosition > POINT_LENGTH) {// 末位之后，跳转到首位（1）
//                    mCurrentPagePosition = FIRST_ITEM_INDEX;
//                } else if (pPosition < FIRST_ITEM_INDEX) {// 首位之前，跳转到末尾（N）
//                    mCurrentPagePosition = POINT_LENGTH;
//                } else {
//                    mCurrentPagePosition = pPosition;
//                }


            }

            @Override
            public void onPageScrollStateChanged(int pState) {
//                if (ViewPager.SCROLL_STATE_IDLE == pState) {
//                    if (mIsChanged) {
//                        mIsChanged = false;
//                        viewpager.setCurrentItem(mCurrentPagePosition, false);
//                    }
//                }
                switch (pState){
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.removeCallbacksAndMessages(null);
                        item = viewpager.getCurrentItem();
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        if(item == viewpager.getCurrentItem()){
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewpager.setCurrentItem((viewpager.getCurrentItem() + 1) % list.size());
                                }
                            }, 4000);
                        }

                        item = -1;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;

                }

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

        indicator.setTabItemTitles(titles);
        indicator.setViewPager(viewpager, 0);

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
        handler.removeCallbacksAndMessages(null);
        handler=null;
    }
}
