package com.example.liorkaramany.opticsdatabase;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines an activity which displays all the documents of a customer and to maintain these documents.
 */
public class Main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * The list which contains all the customers.
     */
    ListView list;
    /**
     * A Spinner that shows the options to only show customers with a specific type of equipment.
     */
    Spinner options;
    /**
     * A TextView which shows how many customers there are in the database.
     */
    TextView count;

    /**
     * A Database reference to the branch containing the customers.
     */
    DatabaseReference ref;
    /**
     * A Database reference to the branch containing the customers' documents.
     */
    DatabaseReference imgRef;

    /**
     * An EditText which contains the first name of the customer to search.
     */
    EditText fnameSearch;

    /**
     * An EditText which contains the last name of the customer to search.
     */
    EditText lnameSearch;

    /**
     * An EditText which contains the personal ID of the customer to search.
     */
    EditText idSearch;

    /**
     * The list which contains all the customers.
     */
    List<Customer> customerList;

    /**
     * A value which tells the ListView what to sort the list by.
     */
    int option;

    /**
     * The list which contains all the customer's documents.
     */
    int optionSpinner;

    /**
     * The ConnectionReceiver listener that listens to the connectivity of the application.
     */
    ConnectionReceiver connectionReceiver;

    /**
     * Initializes the activity, the widgets, the reference and the connectionReceiver, shows a dialog containing information about the selected customer and automatically searches when information is entered in the fields.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        options = (Spinner) findViewById(R.id.options);
        count = (TextView) findViewById(R.id.count);

        fnameSearch = (EditText) findViewById(R.id.fnameSearch);
        lnameSearch = (EditText) findViewById(R.id.lnameSearch);
        idSearch = (EditText) findViewById(R.id.idSearch);

        fnameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lnameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        idSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        options.setAdapter(adapter);
        options.setOnItemSelectedListener(this);
        optionSpinner = 0;

        customerList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("customers");
        imgRef = FirebaseDatabase.getInstance().getReference("images");
        option = 2;

        list.setOnCreateContextMenuListener(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Customer customer = customerList.get(position);

                AlertDialog.Builder adb = new AlertDialog.Builder(Main.this);

                adb.setTitle(R.string.customer);

                LayoutInflater inflater = Main.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.customer_layout, null);
                adb.setView(dialogView);

                TextView fname = (TextView) dialogView.findViewById(R.id.fname);
                TextView lname = (TextView) dialogView.findViewById(R.id.lname);
                TextView customerID = (TextView) dialogView.findViewById(R.id.customerID);
                TextView address = (TextView) dialogView.findViewById(R.id.address);
                TextView city = (TextView) dialogView.findViewById(R.id.city);
                TextView phone = (TextView) dialogView.findViewById(R.id.phone);
                TextView mobile = (TextView) dialogView.findViewById(R.id.mobile);
                TextView opendate = (TextView) dialogView.findViewById(R.id.opendate);
                RadioButton glasses = (RadioButton) dialogView.findViewById(R.id.glasses);
                RadioButton lens = (RadioButton) dialogView.findViewById(R.id.lens);

                fname.setText("" + customer.getfName());
                lname.setText("" + customer.getlName());
                customerID.setText("" + customer.getCustomerID());
                address.setText("" + customer.getAddress());
                city.setText("" + customer.getCity());
                phone.setText("" + customer.getPhone());
                mobile.setText("" + customer.getMobile());
                opendate.setText("" + customer.getOpenDate());

                int typeID = customer.getTypeID();
                if (typeID >= 2)
                    lens.setChecked(true);
                if (typeID == 1 || typeID == 3)
                    glasses.setChecked(true);

                adb.setView(dialogView);

                adb.setPositiveButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editCustomer(customer);
                    }
                });
                adb.setNegativeButton(getString(R.string.view_document), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDocument(customer);
                    }
                });
                adb.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog ad = adb.create();
                ad.show();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // The request code used in ActivityCompat.requestPermissions()
            // and returned in the Activity's onRequestPermissionsResult()
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
            };

            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
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
     * Checks if the application has all the necessary permissions granted.
     *
     * @param context the context in which the permissions are asked.
     * @param permissions an array of all the requested permissions.
     * @return true if all the permissions are granted, and false otherwise.
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Passes the customer's information to the Input activity and starts it.
     *
     * @param customer the customer that will be edited.
     */
    public void editCustomer(Customer customer)
    {
        Intent t = new Intent(Main.this, Input.class);
        t.putExtra("sign", 1);

        t.putExtra("id", customer.getId());
        t.putExtra("fname", customer.getfName());
        t.putExtra("lname", customer.getlName());
        t.putExtra("customerID", customer.getCustomerID());
        t.putExtra("address", customer.getAddress());
        t.putExtra("city", customer.getCity());
        t.putExtra("phone", customer.getPhone());
        t.putExtra("mobile", customer.getMobile());
        t.putExtra("openDate", customer.getOpenDate());
        t.putExtra("typeID", customer.getTypeID());

        startActivityForResult(t, 1);
    }

    /**
     * Updates the list when the activity is started.
     */
    @Override
    protected void onStart() {
        super.onStart();

        sortList();
        /*ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sortList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    /**
     * Shows the options to view the selected customer, view the documents of a customer, edit him or delete him when the user long-presses on a customer in the list.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(getString(R.string.options_settings));
        menu.add(getString(R.string.view_document));
        menu.add(getString(R.string.edit_details));
        menu.add(getString(R.string.delete));
    }

    /**
     * Shows information about the customer, edits him, deletes him, or goes to the DocumentsList activity to show the user's documents, according to the user's selection.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        final Customer customer = customerList.get(index);

        String option = item.getTitle().toString();
        if (option.equals(getString(R.string.edit_details)))
        {
            editCustomer(customer);
        }
        else if (option.equals(getString(R.string.delete)))
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            adb.setTitle(R.string.warning);
            adb.setMessage(getString(R.string.delete_warning) + customer.getfName() + " " + customer.getlName() + "?");

            adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteCustomer(customer);
                }
            });
            adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog ad = adb.create();
            ad.show();
        }
        else if (option.equals(getString(R.string.view_document)))
        {
            showDocument(customer);
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Deletes the customer and its documents.
     *
     * @param customer the customer that will be deleted.
     */
    public void deleteCustomer(Customer customer)
    {
        String id = customer.getId();
        ref.child(id).removeValue();

        imgRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String url = ds.getValue(Image.class).getUrl();
                    StorageReference r = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    r.delete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgRef.child(id).removeValue();
        /*StorageReference r = FirebaseStorage.getInstance().getReference("customers").child(id);
        r.delete();*/
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        Snackbar snackbar = Snackbar.make(layout, R.string.customer_deleted, Snackbar.LENGTH_LONG);
        snackbar.show();
        //Toast.makeText(this, getString(R.string.customer_deleted), Toast.LENGTH_SHORT).show();
    }

    /**
     * Passes the customer's ID to DocumentsList activity and starts it.
     *
     * @param customer the customer that its documents will be shown.
     */
    public void showDocument(Customer customer)
    {
        final String id = customer.getId();

        Intent t = new Intent(this, DocumentsList.class);
        t.putExtra("id", id);
        startActivity(t);

        /*final List<Image> documents = new ArrayList<>();

        documents.clear();

        imgRef.child(id).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    Image image = customerSnapshot.getValue(Image.class);

                    documents.add(image);
                    /*final String url = image.getUrl();
                    String d = image.getOpenDate();
                    //date.setText(d);

                    //Fit the image into the document ImageView.
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    final Point point = new Point(width, height);

                    final int size = (int) Math.ceil(Math.sqrt(width * height));
                    Picasso.get()
                            .load(url)
                            .resize(size, size)
                            .centerInside()
                            .into(document);

                    //Picasso.get().load(url).into(document);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AlertDialog.Builder adb = new AlertDialog.Builder(Main.this);
        LayoutInflater inflater = Main.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_list, null);
        adb.setView(dialogView);

        ListView imgList = dialogView.findViewById(R.id.img_list);

        ImageList adapter = new ImageList(Main.this, documents);

        imgList.setAdapter(adapter);

        adb.setTitle(R.string.document);
        adb.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                *//*Intent t = new Intent(Main.this, Document.class);
                t.putExtra("url", url);
                t.putExtra("id", id);
                t.putExtra("sign", 1);
                startActivityForResult(t, 2);
            }
        });

        AlertDialog ad = adb.create();
        ad.show();*/
    }

    /**
     * Starts the Input activity.
     */
    public void add(View view) {
        Intent t = new Intent(this, Input.class);
        startActivityForResult(t, 3);
    }

    /**
     * Displays options when the user clicks the options button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(getString(R.string.credits_option));

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Starts the Credits activity after the user selects the option in the options menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals(getString(R.string.credits_option)))
        {
            Intent t = new Intent(this, Credits.class);
            startActivity(t);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Counts how many customers there are and displays this amount in a TextView.
     */
    public void count()
    {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long c = dataSnapshot.getChildrenCount();
                count.setText(getString(R.string.customers)+c);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Adds all the documents of the current customer to the list and displays them according to the way the user decided to sort them, and only displays the customers which meet the searching criteria.
     */
    public void sortList()
    {
        count();

        Query query;

        final String[] s = getSearchStringArray();

        switch (option) {
            case 0:
                query = ref.orderByChild("fName");
                break;

            case 1:
                query = ref.orderByChild("lName");
                break;

            /*case 2*/default:
                query = ref;
                break;
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                customerList.clear();
                for (DataSnapshot customerSnapshot :  dataSnapshot.getChildren())
                {
                    Customer customer = customerSnapshot.getValue(Customer.class);

                    if (searchCustomer(customer, s) && checkOption(customer))
                        customerList.add(customer);
                }

                CustomerList adapter = new CustomerList(Main.this, customerList);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Tells the application to sort the list by first names.
     */
    public void sortFName(View view) {
        option = 0;
        sortList();
    }

    /**
     * Tells the application to sort the list by last names.
     */
    public void sortLName(View view) {
        option = 1;
        sortList();
    }

    /**
     * Tells the application to sort the list by date of uploading.
     */
    public void sortDate(View view) {
        option = 2;
        sortList();
    }

    /**
     * Returns a string array that is used in the searching process.
     * @return an array which contains all the necessary information about the customer.
     */
    private String[] getSearchStringArray() {
        return new String[] {fnameSearch.getText().toString(), lnameSearch.getText().toString(), idSearch.getText().toString()};
    }

    /**
     * Tells if a customer meets the searching criteria.
     *
     * @param customer the customer that is checked.
     * @param s a string array which contain the searching criteria.
     * @return true if the customer meets the searching criteria, and false otherwise.
     */
    public boolean searchCustomer(Customer customer, String[] s)
    {
        String[] attributes = new String[3];
        attributes[0] = customer.getfName();
        attributes[1] = customer.getlName();
        attributes[2] = customer.getCustomerID();

        int i = 0;
        while(i < attributes.length)
        {
            if (!attributes[i].contains(s[i]))
                return false;
            i++;
        }
        return true;
    }

    /**
     * Checks whether a customer meets the type criteria.
     *
     * @param customer the customer which is checked
     * @return true if the customer meets the type criteria, and false otherwise.
     */
    public boolean checkOption(Customer customer)
    {
        if (optionSpinner == 0)
            return true;
        return optionSpinner == customer.getTypeID();
    }

    /**
     * Shows a message after a customer has been added or after a customer has been edited.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Snackbar snackbar;
            if (requestCode < 3) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
                snackbar = Snackbar.make(layout, R.string.customer_edited, Snackbar.LENGTH_LONG);
            }
            else {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
                snackbar = Snackbar.make(layout, R.string.customer_uploaded, Snackbar.LENGTH_LONG);
            }
            snackbar.show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //Listeners for the options spinner.
    /**
     * Get the selected type from the Spinner and update the list.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        optionSpinner = position;
        sortList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
