package bankdroid.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class TrackedActivity extends Activity
{
	public final static String ACTION_BROWSE = "Browse";
	public final static String ACTION_SEND = "Send";
	public final static String ACTION_CLICK = "Click";

	private static final String TRACKER_ID = "UA-12736006-3";

	private GoogleAnalyticsTracker tracker;
	private String pageId;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(TRACKER_ID, this);

		pageId = getClass().getName();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		tracker.trackPageView("/" + pageId);
	}

	public void trackPage( final String page )
	{
		tracker.trackPageView(page);
	}

	public void trackClickEvent( final String action, final String data )
	{
		tracker.trackEvent(pageId, action, data, 1);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		tracker.stop();
	}

	public void dispatch()
	{
		if ( tracker != null )
			tracker.dispatch();
	}

	public static void dispatch( final Context baseContext )
	{
		final GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start(TRACKER_ID, baseContext);
		tracker.dispatch();
		tracker.stop();
	}
}
