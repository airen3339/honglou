package com.hlsp.video.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hlsp.video.R;
import com.jack.mc.cyg.cygptr.PtrFrameLayout;
import com.jack.mc.cyg.cygptr.PtrUIHandler;
import com.jack.mc.cyg.cygptr.indicator.PtrIndicator;
import com.jack.mc.cyg.cygptr.indicator.PtrTensionIndicator;

/**
 * Created by hackest on 2018/4/10.
 */

public class MyCustomHeader extends FrameLayout implements PtrUIHandler {
    ImageView iv_refresh;
    ProgressBar progressbar_pull;
    TextView tv_pull_title;
    private Matrix mMatrix = new Matrix();

    private PtrFrameLayout mPtrFrameLayout;
    private PtrTensionIndicator mPtrTensionIndicator;
    private Handler mHandler = new Handler();

    private Animation mScale;

    private Runnable mPullRRunnable = new Runnable() {
        @Override
        public void run() {
            progressbar_pull.setVisibility(GONE);
            tv_pull_title.setVisibility(VISIBLE);
            tv_pull_title.startAnimation(mScale);
        }
    };

    public MyCustomHeader(@NonNull Context context) {
        this(context, null);
    }

    public MyCustomHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCustomHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.header_layout, this);
        iv_refresh = view.findViewById(R.id.iv_refresh);
        progressbar_pull = view.findViewById(R.id.progressbar_pull);
        tv_pull_title = view.findViewById(R.id.tv_pull_title);
        progressbar_pull.setVisibility(GONE);
        tv_pull_title.setVisibility(GONE);

        mScale = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1);
        mScale.setDuration(200);


    }

    /**
     * ???????????? : Content??????????????????Header??????????????? View???
     *
     * @param frame
     */
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        iv_refresh.setVisibility(GONE);
        progressbar_pull.setVisibility(GONE);
        tv_pull_title.setVisibility(GONE);
        tv_pull_title.clearAnimation();
    }

    /**
     * ???????????? : Header ????????????????????????
     *
     * @param frame
     */
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mHandler.removeCallbacks(mPullRRunnable);
        tv_pull_title.clearAnimation();
        pullStep0(0.0f);
    }

    /**
     * ???????????? : Header ?????????????????????????????????
     *
     * @param frame
     */
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mPtrFrameLayout.setEnabled(false);

        iv_refresh.setVisibility(GONE);
        tv_pull_title.setVisibility(GONE);
        progressbar_pull.setVisibility(VISIBLE);
        mHandler.postDelayed(mPullRRunnable, 500);
    }


    /**
     * ???????????? : Header ?????????????????????????????????
     *
     * @param frame
     */
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        iv_refresh.setVisibility(GONE);
        progressbar_pull.setVisibility(GONE);
        tv_pull_title.setVisibility(VISIBLE);

        mPtrFrameLayout.setEnabled(true);

    }


    /**
     * isUnderTouch: ???????????????true???false???
     * status: ?????????1:init???2:prepare???3:loading???4???complete???
     */
    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
                                   PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();//?????????????????????
        final int currentPos = ptrIndicator.getCurrentPosY();//????????????
        final int lastPos = ptrIndicator.getLastPosY();//???????????????

        if (isUnderTouch) {//???????????????????????????????????????????????????????????????
            if (lastPos < currentPos && currentPos < mOffsetToRefresh) {//??????2
                float scale = lastPos * 5 / 4 / (float) mOffsetToRefresh;
                if (scale > 1.0f) {
                    scale = 1.0f;
                }
                pullStep0(scale);
            } else {
                float scale = lastPos / (float) mOffsetToRefresh;
                if (scale > 1.0f) {
                    scale = 1.0f;
                }
                pullStep0(scale);

            }
        }


    }

    private void pullStep0(float scale) {
        iv_refresh.setVisibility(VISIBLE);
        progressbar_pull.setVisibility(GONE);
        tv_pull_title.setVisibility(GONE);

        scaleImage(scale);
    }


    private void scaleImage(float scale) {
        mMatrix.setScale(scale, scale, iv_refresh.getWidth() / 2, iv_refresh.getHeight() / 2);
        iv_refresh.setImageMatrix(mMatrix);
    }


    public void setUp(PtrFrameLayout ptrFrameLayout) {
        mPtrFrameLayout = ptrFrameLayout;
        mPtrTensionIndicator = new PtrTensionIndicator();
        mPtrFrameLayout.setPtrIndicator(mPtrTensionIndicator);
    }


    public TextView getTvtitle() {
        return tv_pull_title;
    }
}

