package shakrlabs.shotshakr;


public class ShotPicker
	extends android.app.Activity
	implements
		mono.android.IGCUserPeer
{
	static final String __md_methods;
	static {
		__md_methods = 
			"n_onCreate:(Landroid/os/Bundle;)V:GetOnCreate_Landroid_os_Bundle_Handler\n" +
			"n_onResume:()V:GetOnResumeHandler\n" +
			"";
		mono.android.Runtime.register ("ShakrLabs.ShotShakr.ShotPicker, ShakrLabs.ShotShakr, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", ShotPicker.class, __md_methods);
	}


	public ShotPicker ()
	{
		super ();
		if (getClass () == ShotPicker.class)
			mono.android.TypeManager.Activate ("ShakrLabs.ShotShakr.ShotPicker, ShakrLabs.ShotShakr, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "", this, new java.lang.Object[] {  });
	}


	public void onCreate (android.os.Bundle p0)
	{
		n_onCreate (p0);
	}

	private native void n_onCreate (android.os.Bundle p0);


	public void onResume ()
	{
		n_onResume ();
	}

	private native void n_onResume ();

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
