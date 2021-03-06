package bankdroid.start;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import bankdroid.util.TrackedActivity;

/**
 * @author Gabe
 *  
 *  FIXME handle rotaion
 *  FIXME prepare FHB plugin
 *  
 *  TODO PIN based security store
 *  TODO PIN based panic functionality
 *  TODO the details of AbstractRemoteObject should contain properties in sequence of adding of property
 *
 *	XXX set language option in preferences
 *  XXX shortcut widget for the various services - direct access to the service. These widgets should go through the mainactivity.
 *
 *  XXX add Citibank plugin
 *  XXX add K&H plugin
 */

public class AboutActivity extends TrackedActivity implements OnClickListener, Codes
{

	private static final String BANKDROID_TEXT = "BankDroid";

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getContentLayoutId());

		initEventHandlers();

		findViewById(R.id.twitterIcon).setOnClickListener(this);
		findViewById(R.id.gmailIcon).setOnClickListener(this);
		findViewById(R.id.facebookIcon).setOnClickListener(this);
		findViewById(R.id.url2).setOnClickListener(this);

		//set version number
		try
		{
			final PackageManager manager = getPackageManager();
			final PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			final String versionName = info.versionName;
			( (TextView) findViewById(R.id.versionId) ).setText(versionName);
		}
		catch ( final NameNotFoundException e )
		{
			Log.e(TAG, "Error getting package name.", e);
		}
	}

	protected void initEventHandlers()
	{
		final Button productInfo = (Button) findViewById(R.id.productInfoButton);
		productInfo.setOnClickListener(this);
		final Button donate = (Button) findViewById(R.id.donateButton);
		donate.setOnClickListener(this);

		//Set links in description0 
		final TextView desc0 = (TextView) findViewById(R.id.description);
		final String text0 = desc0.getText().toString();
		final SpannableString f0 = new SpannableString(text0);

		final int x0 = text0.indexOf(BANKDROID_TEXT);
		final int y0 = x0 + BANKDROID_TEXT.length();

		f0.setSpan(new ClickSpan(new SpanClickListener()
		{
			@Override
			public void onSpanClicked( final View source, final int spanId )
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PROJECT_HOME)));
			}
		}, 1), x0, y0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		desc0.setText(f0);

		final MovementMethod m0 = desc0.getMovementMethod();
		if ( ( m0 == null ) || !( m0 instanceof LinkMovementMethod ) )
		{
			if ( desc0.getLinksClickable() )
			{
				desc0.setMovementMethod(LinkMovementMethod.getInstance());
			}
		}
	}

	protected int getContentLayoutId()
	{
		return R.layout.about;
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.productInfoButton )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.aboutUrl2)));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.donateButton )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.donateURL)));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.twitterIcon )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_URL));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.gmailIcon )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GMAIL_URL));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.facebookIcon )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.url2 )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(( (TextView) v ).getText().toString()));
			startActivity(viewIntent);
		}
	}

	static class ClickSpan extends ClickableSpan
	{
		SpanClickListener listener;
		int spanId;

		public ClickSpan( final SpanClickListener listener, final int spanId )
		{
			this.listener = listener;
			this.spanId = spanId;
		}

		@Override
		public void onClick( final View widget )
		{
			listener.onSpanClicked(widget, spanId);
		}
	}

	static interface SpanClickListener
	{
		void onSpanClicked( View source, int spanId );
	}

}
