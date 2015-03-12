package commonframework.src.commons.wedget;


public class BadgeView
	extends android.widget.TextView
	implements
		mono.android.IGCUserPeer
{
	static final String __md_methods;
	static {
		__md_methods = 
			"n_isShown:()Z:GetIsShownHandler\n" +
			"";
		mono.android.Runtime.register ("CommonFramework.Src.Commons.Wedget.BadgeView, CommonFramework, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", BadgeView.class, __md_methods);
	}


	public BadgeView (android.content.Context p0) throws java.lang.Throwable
	{
		super (p0);
		if (getClass () == BadgeView.class)
			mono.android.TypeManager.Activate ("CommonFramework.Src.Commons.Wedget.BadgeView, CommonFramework, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "Android.Content.Context, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065", this, new java.lang.Object[] { p0 });
	}


	public BadgeView (android.content.Context p0, android.util.AttributeSet p1) throws java.lang.Throwable
	{
		super (p0, p1);
		if (getClass () == BadgeView.class)
			mono.android.TypeManager.Activate ("CommonFramework.Src.Commons.Wedget.BadgeView, CommonFramework, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "Android.Content.Context, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065:Android.Util.IAttributeSet, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065", this, new java.lang.Object[] { p0, p1 });
	}


	public BadgeView (android.content.Context p0, android.util.AttributeSet p1, int p2) throws java.lang.Throwable
	{
		super (p0, p1, p2);
		if (getClass () == BadgeView.class)
			mono.android.TypeManager.Activate ("CommonFramework.Src.Commons.Wedget.BadgeView, CommonFramework, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "Android.Content.Context, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065:Android.Util.IAttributeSet, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065:System.Int32, mscorlib, Version=2.0.5.0, Culture=neutral, PublicKeyToken=7cec85d7bea7798e", this, new java.lang.Object[] { p0, p1, p2 });
	}


	public boolean isShown ()
	{
		return n_isShown ();
	}

	private native boolean n_isShown ();

	java.util.ArrayList refList;
	public void monodroidAddReference (java.lang.Object obj)
	{
		if (refList == null)
			refList = new java.util.ArrayList ();
		refList.add (obj);
	}

	public void monodroidClearReferences ()
	{
		if (refList != null)
			refList.clear ();
	}
}
