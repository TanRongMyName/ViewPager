package com.coffe.shentao.viewpagecircle_indication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*https://github.com/hackware1993/MagicIndicator
使用框架
ViewPager 指示器框架。是ViewPagerIndicator、TabLayout、PagerSlidingTabStrip的最佳替代品。
支持角标，更支持在非ViewPager场景下使用（使用hide()、show()切换Fragment
或使用setVisibility切换FrameLayout里的View等）
* */
public class ViewPagerActivity extends AppCompatActivity {
    private ViewPager pager;
    private List<View>pages;
    private MagicIndicator indicator;//导航蓝 实现
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        initView();
        initData();

    }

    private void initIndicator() {
        ScaleCircleNavigator navigator=new ScaleCircleNavigator(this);
        navigator.setCircleCount(pages.size());
        navigator.setNormalCircleColor(Color.DKGRAY);
        navigator.setSelectedCircleColor(Color.CYAN);
        navigator.setCircleClickListener(new ScaleCircleNavigator.OnCircleClickListener() {
            @Override
            public void onClick(int index) {
                pager.setCurrentItem(index);
            }
        });
        indicator.setNavigator(navigator);
    }

    public void initView(){
        pager=(ViewPager) findViewById(R.id.view_pager);
        indicator=(MagicIndicator) findViewById(R.id.bottom_indicator);
    }
    private void initData() {
        pages=new ArrayList<>();
        Field[] fields=R.drawable.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.getName().startsWith("double")) {
                    ImageView view = new ImageView(this);
//                    view.setImageResource(field.getInt(null));
//                    view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    //3D环绕使用  需要倒影
                    view.setImageBitmap(getReverseBitmapById(this, field.getInt(null), 0.5f));
                    pages.add(view);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        initIndicator();
        PagerAdapter adapter=new ViewAdapter(pages);
        pager.setAdapter(adapter);

        //pager.setPageTransformer(true, new ScalePageTransformer());
        //pager.setPageTransformer(true, new RotatePageTransformer());
        pager.setPageTransformer(true, new GalleryPageTransformer());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                indicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                indicator.onPageScrollStateChanged(state);
            }
        });

        ViewPagerHelper.bind(indicator,pager);
    }

    class ViewAdapter extends PagerAdapter{
        private List<View> datas;
        public ViewAdapter(List<View>list){
            datas=list;
        }
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view==o;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=datas.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(datas.get(position));
        }
    }
    /*viewpage 浮现的功能效果*/
    class ScalePageTransformer implements ViewPager.PageTransformer{
        private static final float MIN_SCALE=0.75f;

//        `page`表示 ViewPager 中的一页，`position`表示`page`当前的位置，
//         [-1, 0)表示屏幕左边的`page`（部分可见），[0, 0]表示屏幕上的`page`（完全可见），
//         (0, 1]表示屏幕右边的`page`（部分可见），具体看下图：
//        当`page`向左边滑动时，`position`从0向-1变化，当`position==-1`时完全不可见；
//        当`page`向右滑动时，`position`从0向1变化，当`position==1`时完全不可见。
        @Override
        public void transformPage(@NonNull View page, float position) {
            if(position<-1.0f) {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
            // slide left
            else if(position<=0.0f) {
                page.setAlpha(1.0f);
                page.setTranslationX(0.0f);
                page.setScaleX(1.0f);
                page.setScaleY(1.0f);
            }
            // slide right
            else if(position<=1.0f) {
                page.setAlpha(1.0f-position);
                page.setTranslationX(-page.getWidth()*position);
                float scale=MIN_SCALE+(1.0f-MIN_SCALE)*(1.0f-position);
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
            // out of right screen
            else {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }
    }

    class RotatePageTransformer implements ViewPager.PageTransformer{
        private static final float MAX_ROTATION=20.0f;
        @Override
        public void transformPage(@NonNull View page, float position) {
            if(position<-1)
                rotate(page, -MAX_ROTATION);
            else if(position<=1)
                rotate(page, MAX_ROTATION*position);
            else
                rotate(page, MAX_ROTATION);
        }
        private void rotate(View view, float rotation) {
            view.setPivotX(view.getWidth()*0.5f);
            view.setPivotY(view.getHeight());
            view.setRotation(rotation);
        }
    }
    /*3D 画廊效果*/
    public class GalleryPageTransformer implements ViewPager.PageTransformer {
        private static final float MAX_ROTATION=20.0f;
        private static final float MIN_SCALE=0.75f;
        private static final float MAX_TRANSLATE=20.0f;

        @Override
        public void transformPage(View page, float position) {
            if(position<-1) {
                page.setTranslationX(MAX_TRANSLATE);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
                page.setRotationY(-MAX_ROTATION);
            }
            else if(position<=0) {
                page.setTranslationX(-MAX_TRANSLATE*position);
                float scale=MIN_SCALE+(1-MIN_SCALE)*(1.0f+position);
                page.setScaleX(scale);
                page.setScaleY(scale);
                page.setRotationY(MAX_ROTATION*position);
            }
            else if(position<=1) {
                page.setTranslationX(-MAX_TRANSLATE*position);
                float scale=MIN_SCALE+(1-MIN_SCALE)*(1.0f-position);
                page.setScaleX(scale);
                page.setScaleY(scale);
                page.setRotationY(MAX_ROTATION*position);
            }
            else {
                page.setTranslationX(-MAX_TRANSLATE);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
                page.setRotationY(MAX_ROTATION);
            }
        }
    }

    //下面的方法负责生成有倒影的图片：  其中的`percent`参数指定倒影占原图的比例。
    public static Bitmap getReverseBitmapById(Context context, int resId, float percent) {
        // get the source bitmap
        Bitmap srcBitmap=BitmapFactory.decodeResource(context.getResources(), resId);
        // get the tow third segment of the reverse bitmap
        Matrix matrix=new Matrix();
        matrix.setScale(1, -1);
        Bitmap rvsBitmap=Bitmap.createBitmap(srcBitmap, 0, (int) (srcBitmap.getHeight()*(1-percent)),
                srcBitmap.getWidth(), (int) (srcBitmap.getHeight()*percent), matrix, false);
        // combine the source bitmap and the reverse bitmap
        Bitmap comBitmap=Bitmap.createBitmap(srcBitmap.getWidth(),
                srcBitmap.getHeight()+rvsBitmap.getHeight()+20, srcBitmap.getConfig());
        Canvas gCanvas=new Canvas(comBitmap);
        gCanvas.drawBitmap(srcBitmap, 0, 0, null);
        gCanvas.drawBitmap(rvsBitmap, 0, srcBitmap.getHeight()+20, null);
        Paint paint=new Paint();
        LinearGradient shader=new LinearGradient(0, srcBitmap.getHeight()+20, 0, comBitmap.getHeight(),
                Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0, srcBitmap.getHeight()+20, srcBitmap.getWidth(), comBitmap.getHeight(), paint);
        return comBitmap;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
//            View v = getWindow().getDecorView();
//            v.setSystemUiVisibility(View.GONE);
//        } else if (Build.VERSION.SDK_INT >= 19) {
//            Log.v("tanrong","VERSION.SDK_INT >= 19");
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }

        //显示虚拟按键
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            //低版本sdk
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }

}
