package bankdroid.start.auth;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bankdroid.start.R;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.model.Customer;

public class CustomerAdapter extends BaseAdapter
{
	private final Customer[] customers;

	public CustomerAdapter( final Customer[] customers )
	{

		this.customers = customers;
	}

	@Override
	public int getCount()
	{
		return customers.length;
	}

	@Override
	public Object getItem( final int position )
	{
		return customers[position];
	}

	public Customer getCustomer( final int position )
	{
		return customers[position];
	}

	public void removeCustomer( final int position )
	{
		final Customer[] customers = new Customer[this.customers.length - 1];
		for ( int i = 0; i < customers.length; i++ )
		{
			if ( i < position )
				customers[i] = this.customers[i];
			else
				customers[i] = this.customers[i + 1];
		}
		notifyDataSetChanged();
	}

	@Override
	public long getItemId( final int position )
	{
		return position;
	}

	@Override
	public View getView( final int position, final View contentView, final ViewGroup parent )
	{
		View view = contentView;
		if ( view == null )
		{
			view = View.inflate(parent.getContext(), R.layout.customeritem, null);
		}

		final Customer customer = customers[position];
		( (TextView) view.findViewById(R.id.customerName) ).setText(customer.getName());
		( (TextView) view.findViewById(R.id.customerLoginId) ).setText(customer.getBank().getName());
		( (ImageView) view.findViewById(R.id.bankLogo) ).setImageDrawable(PluginManager.getIconDrawable(customer
				.getBank().getLargeIcon()));

		return view;
	}

}
