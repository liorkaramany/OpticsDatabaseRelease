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

public class CustomerList extends ArrayAdapter<Customer> {

    private Activity context;
    private List<Customer> customerList;

    public CustomerList(Activity context, List<Customer> customerList)
    {
        super(context, R.layout.list_layout, customerList);
        this.context = context;
        this.customerList = customerList;
    }

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
