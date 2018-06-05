package com.example.hj.mysearchview;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    boolean isExpand= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //设置全屏透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup rootView = (ViewGroup) ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
            ViewCompat.setFitsSystemWindows(rootView,false);
            rootView.setClipToPadding(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //设置toolbar初始透明度为0
        toolbar.getBackground().mutate().setAlpha(0);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.e("ScrollView",scrollView.getScrollY()+"");
                //改变toolbar的透明度
                changeToolbarAlpha();
                //滚动距离>=大图高度-toolbar高度 即toolbar完全盖住大图的时候 且不是伸展状态 进行伸展操作
                if (scrollView.getScrollY()>=imgBack.getHeight()-toolbar.getHeight() && !isExpand){
                    expand();
                    isExpand= true;
                } //滚动距离<=0时 即滚动到顶部时  且当前伸展状态 进行收缩操作
                else if (scrollView.getScrollY()<=0 && isExpand){
                    reduce();
                    isExpand= false;
                }
            }
        });
    }

    /**
     * 收缩时的布局
     */
    private void reduce() {
        etSearch.setHint("搜索");
        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) rlSearch.getLayoutParams();
        layoutParams.width= dip2px(80);
        layoutParams.setMargins(10, 10, 10, 10);
        rlSearch.setLayoutParams(layoutParams);
        //开始动画
        beginTransition(rlSearch);
    }

    /**
     *   伸展时的布局
     */
    private void expand() {
        etSearch.requestFocus();
        etSearch.setHint("搜索简书的内容和朋友");
        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) rlSearch.getLayoutParams();
        layoutParams.width= layoutParams.MATCH_PARENT;
        layoutParams.setMargins(10,10, 10, 10);
        rlSearch.setLayoutParams(layoutParams);
        //开始动画
        beginTransition(rlSearch);
    }

    private void beginTransition(ViewGroup viewGroup) {
        TransitionSet mSet = new AutoTransition();
        mSet.setDuration(300);
        TransitionManager.beginDelayedTransition(viewGroup, mSet);
    }

    private void changeToolbarAlpha() {
        int scrollY= scrollView.getScrollY();
        //快速下拉
        if (scrollY<0){
            toolbar.getBackground().mutate().setAlpha(0);
            return;
        }
        //计算当前透明度比率
        float radio= Math.min(1,scrollY/(imgBack.getHeight()-toolbar.getHeight()*1f));
        //设置透明度
        toolbar.getBackground().mutate().setAlpha( (int)(radio * 0xFF));
    }

    private int dip2px(float dpVale) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }
}
