package com.yp2019cl.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class FlowLayout
        extends ViewGroup
{
    private static final String     TAG    = "FlowLayout";
    private              List<Line> mLines = new ArrayList<>();//布局管理的行
    private Line mCurrentLine;//记录当前行的

    private int mHorizontalSpace = 15;//水平的间隙
    private int mVerticalSpace   = 15;//垂直的间隙

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSpace(int horizontal, int vertical) {
        this.mHorizontalSpace = horizontal;
        this.mVerticalSpace = vertical;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        //清空 lines
        mLines.clear();
        mCurrentLine = null;

        int width = MeasureSpec.getSize(widthMeasureSpec);

        int childMaxWidth = width - getPaddingLeft() - getPaddingRight();

        //1.测量孩子
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            //测量孩子
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            //将孩子添加记录到行中
            if (mCurrentLine == null) {
                //说明一行都没有
                mCurrentLine = new Line(childMaxWidth, mHorizontalSpace);

                //将line添加到布局中
                mLines.add(mCurrentLine);

                //将child添加到line中
                mCurrentLine.addChild(child);
            } else {
                //判断line是否可以添加child
                if (mCurrentLine.canAdd(child)) {
                    //可以添加
                    mCurrentLine.addChild(child);
                } else {
                    //添加不进来,line放不下去
                    //换行
                    mCurrentLine = new Line(childMaxWidth, mHorizontalSpace);
                    //将line添加到布局中
                    mLines.add(mCurrentLine);
                    //将child添加到line中
                    mCurrentLine.addChild(child);
                }
            }
        }

        //2.设置自己的宽高
        int height = getPaddingTop() + getPaddingBottom();//所有的行的高度和 + 行和行间垂直的间隙和 + paddingTop+ paddingBottom TODO:

        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            height += line.mLineHeight;

            if (i != mLines.size() - 1) {
                height += mVerticalSpace;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //测量孩子
        //        int count = getChildCount();
        //        for (int i = 0; i < count; i++) {
        //            View child = getChildAt(i);
        //        }

        //测量line
        int top = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);

            line.layout(getPaddingLeft(), top);

            top += line.mLineHeight + mVerticalSpace;
        }

    }


    private class Line {
        //用来记录每行中的控件和相关的属性
        //1.属性:
        //2.方法
        //3.构造

        //用来记录行中的子控件的
        private List<View> mViews = new ArrayList<>();

        private int mLineUsedWidth;//行已经使用的宽度
        private int mLineMaxWidth;//行最大的宽度
        private int mLineHeight;//行的高度
        private int mSpace;//控件间的间隙

        public Line(int maxWidth, int space) {
            this.mLineMaxWidth = maxWidth;
            this.mSpace = space;
        }

        /**
         * 用来判断是否可以添加孩子
         * @param view
         * @return
         */
        public boolean canAdd(View view) {

            //如果一个孩子都没有时候，都可以加入
            if (mViews.size() == 0) {
                return true;
            }

            int childWidth = view.getMeasuredWidth();

            if (mLineUsedWidth + mSpace + childWidth <= mLineMaxWidth) {
                //可以添加
                return true;
            }
            return false;
        }


        /**
         * 添加子控件,调用addView方法前，先校验是否可以添加当前line中,通过canAdd
         * @param view
         */
        public void addChild(View view) {

            int childWidth  = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();

            if (mViews.size() == 0) {
                //如果一个孩子都没有
                //计算宽
                mLineUsedWidth = childWidth;

                //计算行的高度
                mLineHeight = childHeight;
            } else {
                //多个孩子时
                //计算宽
                mLineUsedWidth += childWidth + mSpace;

                //计算行的高度(就大不就小)
                mLineHeight = mLineHeight > childHeight
                              ? mLineHeight
                              : childHeight;
            }

            //记录views
            mViews.add(view);
        }

        /**
         * 布局的方法
         */
        public void layout(int left, int top) {

            //判断是否有多余的空间
            int extraWidth = mLineMaxWidth - mLineUsedWidth;
            //获得每个可以分到的平均值
            int avgWidth = (int) (extraWidth * 1f / mViews.size() + 0.5f);

            for (int i = 0; i < mViews.size(); i++) {
                View child      = mViews.get(i);
                int  childWidth = child.getMeasuredWidth();
                int  childHight = child.getMeasuredHeight();

                //先不去布局,先期望孩子的宽高
                if (avgWidth > 0) {
                    int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth + avgWidth,
                                                                     MeasureSpec.EXACTLY);
                    int childHeightSpec = MeasureSpec.makeMeasureSpec(childHight,
                                                                      MeasureSpec.EXACTLY);
                    child.measure(childWidthSpec, childHeightSpec);

                    //获取测量后新的值
                    childWidth = child.getMeasuredWidth();
                    childHight = child.getMeasuredHeight();
                }

                int l = left;
                int t = (int) (top + (mLineHeight - childHight) / 2f + 0.5f);
                int r = l + childWidth;
                int b = t + childHight;

                child.layout(l, t, r, b);

                //为下一个child记录left的值
                left += childWidth + mSpace;
            }

        }
    }

}
