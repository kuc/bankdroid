package bankdroid.start.auth;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import bankdroid.start.Eula;
import bankdroid.start.Eula.OnEulaAgreedTo;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.plugin.PluginManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.model.Customer;

/**
 * @author Gabe
 */
public class AuthStartActivity extends ServiceActivity implements OnClickListener, OnItemClickListener, OnEulaAgreedTo
{
	private final static int REQUEST_NEWUSER = 1001;

	private boolean onFirstDisplay = true;
	private boolean hasUsers = false;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setResult(RESULT_CANCELED);

		setSessionOriented(false);
		setShowHomeMenu(false);

		PluginManager.init();

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.authstart);

		final ListView list = (ListView) findViewById(R.id.userList);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);

		findViewById(R.id.newUser).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Log.d(TAG, "AuthStart resume");

		//clear notifications here
		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ACTIVE_SESSION);
		nm.cancel(NOTIFICATION_SESSION_TIMEOUT);
		nm.cancel(NOTIFICATION_SESSION_TIMEOUT_EXPIRED);

		hasUsers = false;
		try
		{
			final SecureRegistry registry = SecureRegistry.getInstance(this);

			final Customer[] customers = AuthUtil.restoreCustomers(registry);

			if ( customers != null && customers.length > 0 )
			{
				hasUsers = true;

				final ListView list = (ListView) findViewById(R.id.userList);
				list.setAdapter(new CustomerAdapter(customers));
			}
		}
		catch ( final Exception e )
		{
			GUIUtil.fatalError(this, e);
		}

		if ( Eula.show(this) )
		{ //eula was already accepted
			onEulaAgreedTo();
		}
	}

	private void createNewUser()
	{
		startActivityForResult(new Intent(this, AuthBankSelectActivity.class), REQUEST_NEWUSER);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.newUser )
		{
			createNewUser();
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == REQUEST_NEWUSER && resultCode == RESULT_OK )
		{
			Log.d(TAG, "AuthStart - finish it well.");
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onItemClick( final AdapterView<?> view, final View itemView, final int position, final long id )
	{
		if ( view.getId() == R.id.userList )
		{
			final Customer cust = ( (CustomerAdapter) view.getAdapter() ).getCustomer(position);

			openCustomer(cust);
		}

	}

	private void openCustomer( final Customer customer )
	{
		try
		{
			trackClickEvent(ACTION_CLICK, "openStoredCustomer");

			final Class<?> authActivityClass = PluginManager.getAuthActivityClass(customer.getBank());
			final Intent intent = new Intent(this, authActivityClass);
			intent.putExtra(EXTRA_CUSTOMER, customer);
			startActivityForResult(intent, REQUEST_NEWUSER);
		}
		catch ( final ClassNotFoundException e )
		{
			GUIUtil.fatalError(this, e);
		}
	}

	@Override
	public void onCreateContextMenu( final ContextMenu menu, final View v, final ContextMenuInfo menuInfo )
	{
		if ( v.getId() == R.id.userList )
		{
			final MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.userlistcontextmenu, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.deleteUser )
		{
			final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;
			final CustomerAdapter adapter = (CustomerAdapter) ( (ListView) findViewById(R.id.userList) ).getAdapter();

			final Customer customer = adapter.getCustomer((int) id);

			// delete user here
			try
			{
				final SecureRegistry registry = SecureRegistry.getInstance(this);
				AuthUtil.removeCustomer(registry, (Integer) customer.getRemoteProperty(RP_REGISTRY_ID));
				registry.commit(this);
				adapter.removeCustomer((int) id);

			}
			catch ( final Exception e )
			{
				GUIUtil.fatalError(this, e);
			}
		}
		else if ( item.getItemId() == R.id.toBrowser )
		{
			final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;
			final CustomerAdapter adapter = (CustomerAdapter) ( (ListView) findViewById(R.id.userList) ).getAdapter();

			final Customer customer = adapter.getCustomer((int) id);

			final Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(customer.getBank().getMobileBankURL()));
			startActivity(intent);
		}
		else if ( item.getItemId() == R.id.callBank )
		{
			final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;
			final CustomerAdapter adapter = (CustomerAdapter) ( (ListView) findViewById(R.id.userList) ).getAdapter();

			final Customer customer = adapter.getCustomer((int) id);

			final Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + customer.getBank().getCallCenterURL()));
			startActivity(intent);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onEulaAgreedTo()
	{
		if ( onFirstDisplay && !hasUsers )
		{
			onFirstDisplay = false;
			createNewUser();
		}
	}
}
