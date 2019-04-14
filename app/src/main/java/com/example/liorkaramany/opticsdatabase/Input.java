package com.example.liorkaramany.opticsdatabase;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines an activity which lets the user enter the credentials of a new customer, or edit the credentials of an existing customer.
 */
public class Input extends AppCompatActivity {

    /**
     * An EditText which contains the first name of the customer.
     */
    EditText fname;
    /**
     * An EditText which contains the last name of the customer.
     */
    EditText lname;
    /**
     * An EditText which contains the personal ID of the customer.
     */
    EditText customerID;
    /**
     * An EditText which contains the address of the customer.
     */
    EditText address;
    /**
     * An EditText which contains the city of the customer.
     */
    EditText city;
    /**
     * An EditText which contains the phone number of the customer.
     */
    EditText phone;
    /**
     * An EditText which contains the mobile phone number of the customer.
     */
    EditText mobile;
    /**
     * A RadioGroup which contains the glasses and lens RadioButtons.
     */
    RadioGroup options;
    /**
     * A RadioButton of the glasses.
     */
    RadioButton glasses;
    /**
     * A RadioButton of the lens.
     */
    RadioButton lens;

    /**
     * A Database reference to the customers branch if the user is currently editing an existing customer.
     */
    DatabaseReference dref;

    /**
     * The right button in the activity.
     */
    Button rightBtn;

    /**
     * The type of equipment that an existing customer bought.
     */
    int typeID;
    /**
     * The ID of an existing customer.
     */
    String id/*, url*/;
    /**
     * A sign which tells whether a user is creating a new customer or editing an existing one.
     * 0 = creating, 1 = editing.
     */
    int sign;

    //ArrayList<Image> imageList;

    /**
     * The ConnectionReceiver listener that listens to the connectivity of the application.
     */
    ConnectionReceiver connectionReceiver;

    /**
     * Initializes the activity, the widgets, the reference and the connectionReceiver, gets the information passed from the previous activity and sets them inside the appropriate variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        customerID = (EditText) findViewById(R.id.customerID);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        phone = (EditText) findViewById(R.id.phone);
        mobile = (EditText) findViewById(R.id.mobile);

        options = (RadioGroup) findViewById(R.id.options);
        glasses = (RadioButton) findViewById(R.id.glasses);
        lens = (RadioButton) findViewById(R.id.lens);


        rightBtn = (Button) findViewById(R.id.rightBtn);

        dref = FirebaseDatabase.getInstance().getReference("customers");

        Intent gt = getIntent();
        typeID = gt.getIntExtra("typeID", -1);
        sign = gt.getIntExtra("sign", 0);

        if (sign == 1)
        {
            id = gt.getStringExtra("id");

            fname.setText("" + gt.getStringExtra("fname"));
            lname.setText("" + gt.getStringExtra("lname"));
            customerID.setText("" + gt.getStringExtra("customerID"));
            address.setText("" + gt.getStringExtra("address"));
            city.setText("" + gt.getStringExtra("city"));
            phone.setText("" + gt.getStringExtra("phone"));
            mobile.setText("" + gt.getStringExtra("mobile"));

            rightBtn.setText(getString(R.string.save));

            if (typeID >= 2)
                lens.setChecked(true);
            if (typeID == 1 || typeID == 3)
                glasses.setChecked(true);

            //url = gt.getStringExtra("url");
        }

        connectionReceiver = new ConnectionReceiver();
    }

    /**
     * Registers the receiver when the application resumes the activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(connectionReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Unregisters the receiver when the application stops the activity.
     */
    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(connectionReceiver);
    }

    /**
     * Goes back to the previous activity.
     */
    public void back(View view) {
        finish();
    }

    /**
     * Checks if all the necessary information was entered, and if the user is creating a new customer, it passes this information to the next Activity, otherwise it updates the existing customer.
     */
    public void upload(View view) {
        String fn = fname.getText().toString();
        String ln = lname.getText().toString();
        String cID = customerID.getText().toString();
        String a = address.getText().toString();
        String c = city.getText().toString();
        String p = phone.getText().toString();
        String m = mobile.getText().toString();

        if (fn.isEmpty() || ln.isEmpty() || cID.isEmpty() /*|| a.isEmpty() || c.isEmpty()*/ || p.isEmpty()/* || m.isEmpty()*/
                || options.getCheckedRadioButtonId() == -1)
            Toast.makeText(this, getString(R.string.no_enough_info), Toast.LENGTH_SHORT).show();
        else
        {
            int typeID = 0;
            if (glasses.isChecked() && lens.isChecked())
                typeID = 3;
            else if (glasses.isChecked())
                typeID = 1;
            else if (lens.isChecked())
                typeID = 2;

            if (sign == 1) {
                String openDate = getIntent().getStringExtra("openDate");
                Customer customer = new Customer(id, fn, ln, cID, a, c, p, m, openDate, typeID);
                dref.child(id).setValue(customer);

                Intent gt = getIntent();
                setResult(RESULT_OK, gt);
                finish();
                //Toast.makeText(this, getString(R.string.customer_edited), Toast.LENGTH_SHORT).show();
            }
            else {

                Intent t = new Intent(this, Document.class);
                t.putExtra("fname", fn);
                t.putExtra("lname", ln);
                t.putExtra("customerID", cID);
                t.putExtra("address", a);
                t.putExtra("city", c);
                t.putExtra("phone", p);
                t.putExtra("mobile", m);

                /*if (sign == 0)
                    t.putExtra("url", "");
                else
                    t.putExtra("url", url);*/

                t.putExtra("typeID", typeID);
                //t.putExtra("sign", 1);

                startActivityForResult(t, 0);
            }
        }
    }

    /**
     * Goes back to the previous activity when the application returns from the next activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null)
        {
            Intent gt = getIntent();
            setResult(RESULT_OK, gt);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
