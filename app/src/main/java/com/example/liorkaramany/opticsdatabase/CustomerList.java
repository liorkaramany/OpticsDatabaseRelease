package com.example.liorkaramany.opticsdatabase;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;


/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines an ArrayAdapter that is linked to an activity with the ListView and to the customers list.
 */
public class CustomerList extends ArrayAdapter<Customer> {

    /**
     * The activity which contains the ListView.
     */
    private Activity context;
    /**
     * The list which contains the customers.
     */
    private List<Customer> customerList;

    /**
     * A constructor of the Image class.
     *
     * This function creates a CustomerList ArrayAdapter with all the given parameters.
     *
     * @param context The activity which contains the ListView.
     * @param customerList The list which contains the customers.
     * @return A reference to the created CustomerList ArrayAdapter.
     */
    public CustomerList(Activity context, List<Customer> customerList)
    {
        super(context, R.layout.list_layout, customerList);
        this.context = context;
        this.customerList = customerList;
    }

    /**
     * Sets up the view that will be displayed in each row of the ListView.
     *
     * @param position the index of the Customer in the list.
     * @return the view which the ListView displays.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView fname = (TextView) listViewItem.findViewById(R.id.fname);
        TextView lname = (TextView) listViewItem.findViewById(R.id.lname);
        TextView customerID = (TextView) listViewItem.findViewById(R.id.customerID);
        TextView phone = (TextView) listViewItem.findViewById(R.id.phone);
        TextView mobile = (TextView) listViewItem.findViewById(R.id.mobile);

        LinearLayout layout = (LinearLayout) listViewItem.findViewById(R.id.layout);

        if (position % 2 == 0)
            layout.setBackgroundColor(Color.parseColor("#d9e3f4"));
        else
            layout.setBackgroundColor(Color.parseColor("#ffffff"));

        RadioButton glasses = (RadioButton) listViewItem.findViewById(R.id.glasses);
        RadioButton lens = (RadioButton) listViewItem.findViewById(R.id.lens);

        Customer customer = customerList.get(position);

        if (customer != null) {

            fname.setText(customer.getfName());
            lname.setText("" + customer.getlName());
            customerID.setText("" + customer.getCustomerID());
            phone.setText("" + customer.getPhone());
            mobile.setText("" + customer.getMobile());

            int typeId = customer.getTypeID();
            if (typeId >= 2)
                lens.setChecked(true);
            if (typeId == 1 || typeId == 3)
                glasses.setChecked(true);

        }


        return listViewItem;
    }
}
