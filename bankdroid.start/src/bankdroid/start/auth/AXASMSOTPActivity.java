package bankdroid.start.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.axa.AXASMSOTPValidationService;
import com.csaba.connector.axa.AXASelectAccountService;
import com.csaba.connector.axa.model.AXABank;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Customer;
import com.csaba.connector.model.Session;

public class AXASMSOTPActivity extends ServiceActivity
{
	private boolean showCode = false;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		setContentView(R.layout.auth_axa_smsotp);

		AuthUtil.setSelectedBank(this, AXABank.getInstance());

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		//load login ID from preferences
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if ( imm != null )
		{
			imm.showSoftInput(findViewById(R.id.smsotp), 0);
		}

		//check for clipboard content
		if ( showCode )
		{
			onPaste(null);
		}
		else
		{
			showCode = true;
			//code is only shown when user comes to screen 2nd time.
		}
	}

	public void onSMSKey( final View v )
	{
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SMSKEY_MARKET)));
	}

	public void onPaste( final View v )
	{
		final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		if ( clipboard.hasText() )
		{
			final String content = clipboard.getText().toString();
			if ( content.length() != 6 )
				Log.w(TAG, getString(R.string.axaInvalidOTP, content));
			else
			{
				( (EditText) findViewById(R.id.smsotp) ).setText(content);
				clipboard.setText(null);
			}

		}

	}

	public void onLogin( final View v )
	{
		final String smsotp = ( (EditText) findViewById(R.id.smsotp) ).getText().toString();

		final AXASMSOTPValidationService service = new AXASMSOTPValidationService();

		service.setSmsotp(smsotp);

		( new ServiceRunner(this, this, service, SessionManager.getInstance().getSession()) ).start();
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof AXASMSOTPValidationService )
		{
			//update name of customer
			final Session session = SessionManager.getInstance().getSession();
			if ( session != null )
			{
				try
				{
					final Customer customer = session.getCustomer();
					//get registry key
					int registryId = -1;
					if ( customer.isRemotePropertySet(RP_REGISTRY_ID) )
						registryId = (Integer) customer.getRemoteProperty(RP_REGISTRY_ID);
					final SecureRegistry registry = SecureRegistry.getInstance(this);
					if ( AuthUtil.isCustomerSaved(registry, registryId) )
					{
						boolean storePassword = false;
						if ( customer.isRemotePropertySet(RP_STORE_PASSWORD) )
							storePassword = (Boolean) customer.getRemoteProperty(RP_STORE_PASSWORD);

						//store to registry
						AuthUtil.storeCustomer(registry, registryId, customer, new String[] { RP_ACCOUNT_PIN },
								storePassword);
						registry.commit(this);
					}
				}
				catch ( final Exception e )
				{
					GUIUtil.fatalError(this, e);
				}
			}

			//start account select activity
			final Account[] accounts = ( (AXASMSOTPValidationService) service ).getAccounts();

			if ( accounts.length == 1 )
			{
				// auto select
				final Account account = accounts[0];
				final AXASelectAccountService sas = new AXASelectAccountService();
				sas.setAccount(account);
				( new ServiceRunner(this, this, sas, SessionManager.getInstance().getSession()) ).start();
			}
			else
			{
				//manual select
				final Intent intent = new Intent(this, AXAAccountActivity.class);
				intent.putExtra(EXTRA_ACCOUNT_LIST, accounts);
				startActivityForResult(intent, REQUEST_LOGIN);
			}
		}
		else if ( service instanceof AXASelectAccountService )
		{
			final int[] pinMask = ( (AXASelectAccountService) service ).getPinMask();
			final Intent intent = new Intent(this, AXAAccountPINActivity.class);
			intent.putExtra(EXTRA_PINMASK, pinMask);
			startActivityForResult(intent, REQUEST_LOGIN);
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( resultCode == RESULT_OK )
		{
			setResult(RESULT_OK);
			finish();
		}
		else
			super.onActivityResult(requestCode, resultCode, data);
	}

}
