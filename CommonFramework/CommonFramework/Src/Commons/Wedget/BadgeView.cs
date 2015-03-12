using Android.Content;
using Android.Graphics;
using Android.Graphics.Drawables;
using Android.Graphics.Drawables.Shapes;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Views.Animations;
using Android.Widget;
using Java.Lang;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CommonFramework.Src.Commons.Wedget
{
    class BadgeView:TextView
    {
        public  const int POSITION_TOP_LEFT = 1;
        public  const int POSITION_TOP_RIGHT = 2;
        public  const int POSITION_BOTTOM_LEFT = 3;
        public  const int POSITION_BOTTOM_RIGHT = 4;

        private  const int DEFAULT_MARGIN_DIP = 5;
        private  const int DEFAULT_LR_PADDING_DIP = 5;
        private  const int DEFAULT_CORNER_RADIUS_DIP = 8;
        private  const int DEFAULT_POSITION = POSITION_TOP_RIGHT;
       
        private static Animation fadeIn;
        private static Animation fadeOut;

        private Context context;
        private View target;

        private int badgePosition;
        private int badgeMargin;
        private int badgeColor;
        private bool isShown;
        private ShapeDrawable badgeBg;
        private int targetTabIndex;

        

        /**
         * Constructor -
         * 
         * create a new BadgeView instance attached to a target {@link android.view.View}.
         *
         * @param context context for this view.
         * @param target the View to attach the badge to.
         */
       
        public BadgeView(Context context, View target, View parent)
           : base(context, null, Android.Resource.Attribute.TextViewStyle)
        {
            init(context, target, 0, parent);

        }
        private void init(Context context, View target, int tabIndex) {
		
		this.context = context;
		this.target = target;
		this.targetTabIndex = tabIndex;
		
		// apply defaults
		badgePosition = DEFAULT_POSITION;
		badgeMargin = dipToPixels(DEFAULT_MARGIN_DIP);
		badgeColor = Color.Red;
		
		Typeface=(Typeface.DefaultBold);
		int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
		SetPadding(paddingPixels, 0, paddingPixels, 0);
		SetTextColor(Color.White);
		
		fadeIn = new AlphaAnimation(0, 1);
		fadeIn.Interpolator=(new DecelerateInterpolator());
		fadeIn.Duration=(200);

		fadeOut = new AlphaAnimation(1, 0);
		fadeOut.Interpolator=(new AccelerateInterpolator());
		fadeOut.Duration=(200);
		isShown = false;
		
		if (this.target != null) {
			applyTo(this.target);
		} else {
			show();
		}
		
	}
private void init(Context context, View target, int tabIndex,View parent) {
		
		this.context = context;
		this.target = target;
		this.targetTabIndex = tabIndex;
		
		// apply defaults
		badgePosition = DEFAULT_POSITION;
		badgeMargin = dipToPixels(DEFAULT_MARGIN_DIP);
		badgeColor = Color.Red;
		
		Typeface=(Typeface.DefaultBold);
		int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
		SetPadding(paddingPixels, 0, paddingPixels, 0);
        SetTextColor(Color.White);
		
		fadeIn = new AlphaAnimation(0, 1);
		fadeIn.Interpolator=(new DecelerateInterpolator());
		fadeIn.Duration=(200);

		fadeOut = new AlphaAnimation(1, 0);
		fadeOut.Interpolator=(new AccelerateInterpolator());
		fadeOut.Duration=(200);
		
		isShown = false;
		
		if (this.target != null) {
			applyTo(this.target,parent);
		} else {
			show();
		}
		
	}

	private void applyTo(View target) {

        Android.Views.ViewGroup.LayoutParams lp = target.LayoutParameters;
		Android.Views.IViewParent parent = target.Parent;
		FrameLayout container = new FrameLayout(context);
		
		if (target is TabWidget) {
			
			// set target to the relevant tab child container
			target = ((TabWidget) target).GetChildTabViewAt(targetTabIndex);
			this.target = target;
			
			((ViewGroup) target).AddView(container,
                    new Android.Views.ViewGroup.LayoutParams(Android.Views.ViewGroup.LayoutParams.FillParent, Android.Views.ViewGroup.LayoutParams.FillParent));
			
			this.Visibility=(ViewStates.Gone);
			container.AddView(this);
			
		} else {
			
			// TODO verify that parent is indeed a ViewGroup
			ViewGroup group = (ViewGroup) parent; 
			int index = group.IndexOfChild(target);
			
			group.RemoveView(target);
			group.AddView(container, index, lp);
			
			container.AddView(target);
	
			this.Visibility=(ViewStates.Gone);
			container.AddView(this);
			
			group.Invalidate();
			
		}
		
	}
private void applyTo(View target,View parent) {
		
		Android.Views.ViewGroup.LayoutParams lp = target.LayoutParameters;
		 
		FrameLayout container = new FrameLayout(context);
		
		if (target is TabWidget) {
			
			// set target to the relevant tab child container
			target = ((TabWidget) target).GetChildTabViewAt(targetTabIndex);
			this.target = target;
			
			((ViewGroup) target).AddView(container,
                    new Android.Views.ViewGroup.LayoutParams(Android.Views.ViewGroup.LayoutParams.FillParent, Android.Views.ViewGroup.LayoutParams.FillParent));

            this.Visibility = (ViewStates.Gone);
            container.AddView(this);
			
		} else {
			
			// TODO verify that parent is indeed a ViewGroup
			ViewGroup group = (ViewGroup) parent; 
			int index = group.IndexOfChild(target);
			if(index==-1){
				group.AddView(container);
			}
			else {
				group.RemoveView(target);
				group.AddView(container, index, lp);
			}
			
			
			
			container.AddView(target);
	
            this.Visibility=(ViewStates.Gone);
			container.AddView(this);
			
			group.Invalidate();
			
		}
		
	}
	
	/**
     * Make the badge visible in the UI.
     * 
     */
	public void show() {
		show(false, null);
	}
	
	/**
     * Make the badge visible in the UI.
     *
     * @param animate flag to apply the default fade-in animation.
     */
	public void show(bool animate) {
		show(animate, fadeIn);
	}
	
	/**
     * Make the badge visible in the UI.
     *
     * @param anim Animation to apply to the view when made visible.
     */
	public void show(Animation anim) {
		show(true, anim);
	}
	
	/**
     * Make the badge non-visible in the UI.
     * 
     */
	public void hide() {
		hide(false, null);
	}
	
	/**
     * Make the badge non-visible in the UI.
     *
     * @param animate flag to apply the default fade-out animation.
     */
	public void hide(bool animate) {
		hide(animate, fadeOut);
	}
	
	/**
     * Make the badge non-visible in the UI.
     *
     * @param anim Animation to apply to the view when made non-visible.
     */
	public void hide(Animation anim) {
		hide(true, anim);
	}
	
	/**
     * Toggle the badge visibility in the UI.
     * 
     */
	public void toggle() {
		toggle(false, null, null);
	}
	
	/**
     * Toggle the badge visibility in the UI.
     * 
     * @param animate flag to apply the default fade-in/out animation.
     */
	public void toggle(bool animate) {
		toggle(animate, fadeIn, fadeOut);
	}
	
	/**
     * Toggle the badge visibility in the UI.
     *
     * @param animIn Animation to apply to the view when made visible.
     * @param animOut Animation to apply to the view when made non-visible.
     */
	public void toggle(Animation animIn, Animation animOut) {
		toggle(true, animIn, animOut);
	}
	
	private void show(bool animate, Animation anim) {
		if (Background == null) {
			if (badgeBg == null) {
				badgeBg = getDefaultBackground();
			}
			SetBackgroundDrawable(badgeBg);
		}
		applyLayoutParams();
		
		if (animate) {
			this.StartAnimation(anim);
		}
		this.Visibility=(ViewStates.Visible);
		isShown = true;
	}
	
	private void hide(bool animate, Animation anim) {
        this.Visibility = (ViewStates.Gone);
		if (animate) {
			this.StartAnimation(anim);
		}
		isShown = false;
	}
	
	private void toggle(bool animate, Animation animIn, Animation animOut) {
		if (isShown) {
			hide(animate && (animOut != null), animOut);	
		} else {
			show(animate && (animIn != null), animIn);
		}
	}
	
	/**
     * Increment the numeric badge label. If the current badge label cannot be converted to
     * an integer value, its label will be set to "0".
     * 
     * @param offset the increment offset.
     */
	public int increment(int offset) {
		string txt = Text;
		int i;
		if (txt != null) {
			try {
				i = Int32.Parse(txt.ToString());
			} catch (NumberFormatException e) {
				i = 0;
			}
		} else {
			i = 0;
		}
		i = i + offset;
		Text=(i.ToString());
		return i;
	}
	
	/**
     * Decrement the numeric badge label. If the current badge label cannot be converted to
     * an integer value, its label will be set to "0".
     * 
     * @param offset the decrement offset.
     */
	public int decrement(int offset) {
		return increment(-offset);
	}
	
	private ShapeDrawable getDefaultBackground() {
		
		int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
		float[] outerR = new float[] {r, r, r, r, r, r, r, r};
        
		RoundRectShape rr = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(rr);
		drawable.Paint.Color=(new Color( badgeColor));
		
		return drawable;
		
	}
	
	private void applyLayoutParams() {

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WrapContent, FrameLayout.LayoutParams.WrapContent);
		
		switch (badgePosition) {
		case POSITION_TOP_LEFT:
                lp.Gravity = GravityFlags.Left | GravityFlags.Top;
			lp.SetMargins(badgeMargin, badgeMargin, 0, 0);
			break;
		case POSITION_TOP_RIGHT:
            lp.Gravity = GravityFlags.Right | GravityFlags.Top;
			lp.SetMargins(0, badgeMargin, 0, 0);
			break;
		case POSITION_BOTTOM_LEFT:
            lp.Gravity = GravityFlags.Left | GravityFlags.Bottom;
			lp.SetMargins(badgeMargin, 0, 0, badgeMargin);
			break;
		case POSITION_BOTTOM_RIGHT:
            lp.Gravity = GravityFlags.Right | GravityFlags.Bottom;
			lp.SetMargins(0, 0, badgeMargin, badgeMargin);
			break;
		default:
			break;
		}

        LayoutParameters = (lp);
		
	}

	/**
     * Returns the target View this badge has been attached to.
     * 
     */
	public View getTarget() {
		return target;
	}

	/**
     * Is this badge currently visible in the UI?
     * 
     */
	
	 
    public override bool IsShown
    {
        get
        {
            return isShown;
        }
    }
	/**
     * Returns the positioning of this badge.
     * 
     * one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT.
     * 
     */
	public int getBadgePosition() {
		return badgePosition;
	}

	/**
     * Set the positioning of this badge.
     * 
     * @param layoutPosition one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT.
     * 
     */
	public void setBadgePosition(int layoutPosition) {
		this.badgePosition = layoutPosition;
	}

	/**
     * Returns the horizontal/vertical margin from the target View that is applied to this badge.
     * 
     */
	public int getBadgeMargin() {
		return badgeMargin;
	}

	/**
     * Set the horizontal/vertical margin from the target View that is applied to this badge.
     * 
     * @param badgeMargin the margin in pixels.
     */
	public void setBadgeMargin(int badgeMargin) {
		this.badgeMargin = badgeMargin;
	}
	
	/**
     * Returns the color value of the badge background.
     * 
     */
	public int getBadgeBackgroundColor() {
		return badgeColor;
	}

	/**
     * Set the color value of the badge background.
     * 
     * @param badgeColor the badge background color.
     */
	public void setBadgeBackgroundColor(Color badgeColor) {
		this.badgeColor = badgeColor;
		badgeBg = getDefaultBackground();
	}
	
	private int dipToPixels(int dip) {

        Android.Content.Res.Resources r = Resources;
		float px = TypedValue.ApplyDimension(ComplexUnitType.Dip, dip, r.DisplayMetrics);
		return (int) px;
	}
    }
}
