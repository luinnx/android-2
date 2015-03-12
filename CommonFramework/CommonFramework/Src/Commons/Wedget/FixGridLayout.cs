using Android.Content;
using Android.Graphics;
using Android.Util;
using Android.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Wedget
{
    class FixGridLayout : ViewGroup
    {
         
        private int mCellWidth;
	private int mCellHeight;

	public FixGridLayout(Context context):base(context) {
		    
	}

	public FixGridLayout(Context context, IAttributeSet attrs):base(context,attrs) {
		 
	}

	public FixGridLayout(Context context, IAttributeSet attrs, int defStyle):base(context,attrs,defStyle) {
		 
	}

	public void setmCellWidth(int w) {
		mCellWidth = w;
		RequestLayout();
	}

	public void setmCellHeight(int h) {
		mCellHeight = h;
		RequestLayout();
	}

	/**
	 * 控制子控件的换行
	 */
    protected override void OnLayout(bool changed, int l, int t, int r, int b)
    {
        
		int cellWidth = mCellWidth;
		int cellHeight = mCellHeight;
		int columns = (r - l) / cellWidth;
		if (columns < 0) {
			columns = 1;
		}
		int x = 0;
		int y = 0;
		int i = 0;
		int count = ChildCount;
		for (int j = 0; j < count; j++) {
		     View childView = GetChildAt(j);
			// 获取子控件Child的宽高
			int w = childView.MeasuredWidth;
			int h = childView.MeasuredHeight;
			// 计算子控件的顶点坐标
			int left = x + ((cellWidth - w) / 2);
			int top = y + ((cellHeight - h) / 2);
			// int left = x;
			// int top = y;
			// 布局子控件
			childView.Layout(left, top, left + w, top + h);

			if (i >= (columns - 1)) {
				i = 0;
				x = 0;
				y += cellHeight;
			} else {
				i++;
				x += cellWidth;

			}
		}
	}

	/**
	 * 计算控件及子控件所占区域
	 */
	 
	protected override void OnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 创建测量参数
		int cellWidthSpec = MeasureSpec.MakeMeasureSpec(mCellWidth, MeasureSpecMode.AtMost);
        int cellHeightSpec = MeasureSpec.MakeMeasureSpec(mCellHeight, MeasureSpecMode.AtMost);
		// 记录ViewGroup中Child的总个数
		int count = ChildCount;
          
		// 设置子空间Child的宽高
		for (int i = 0; i < count; i++) {
			View childView = GetChildAt(i);
			/*
			 * 090 This is called to find out how big a view should be. 091 The
			 * parent supplies constraint information in the width and height
			 * parameters. 092 The actual mesurement work of a view is performed
			 * in onMeasure(int, int), 093 called by this method. 094 Therefore,
			 * only onMeasure(int, int) can and must be overriden by subclasses.
			 * 095
			 */
			childView.Measure(cellWidthSpec, cellHeightSpec);
		}
		// 设置容器控件所占区域大小
		// 注意setMeasuredDimension和resolveSize的用法
		SetMeasuredDimension(ResolveSize(mCellWidth * count, widthMeasureSpec),
				ResolveSize(mCellHeight * count, heightMeasureSpec));
		// setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

		// 不需要调用父类的方法
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 为控件添加边框
	 */
	 
	protected override void DispatchDraw(Canvas canvas) {
		// 获取布局控件宽高
		int width = Width;
		int height = Height;
		// 创建画笔
		Paint mPaint = new Paint();
		// 设置画笔的各个属性
		mPaint.Color=new Color(0xFFFFFF);
        mPaint.SetStyle(Paint.Style.Stroke);
		mPaint.StrokeWidth=(10);
		mPaint.AntiAlias=(true);
		// 创建矩形框
		Rect mRect = new Rect(0, 0, width, height);
		// 绘制边框
		canvas.DrawRect(mRect, mPaint);
		// 最后必须调用父类的方法
		base.DispatchDraw(canvas);
	}
    }
}
