package com.example.liorkaramany.opticsdatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v7.app.AlertDialog;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DocumentsList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<Image> documents;
    DatabaseReference ref;
    StorageReference imgRef;
    ListView imgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_list);

        String id = getIntent().getStringExtra("id");

        ref = FirebaseDatabase.getInstance().getReference("images").child(id);
        imgRef = FirebaseStorage.getInstance().getReference("customers").child(id);

        documents = new ArrayList<>();
        imgList = (ListView) findViewById(R.id.imgList);

        imgList.setOnItemClickListener(this);
        imgList.setOnCreateContextMenuListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateList();
    }

    private void updateList() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                documents.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Image img = snapshot.getValue(Image.class);
                    documents.add(img);
                }
                ImageList adapter = new ImageList(DocumentsList.this, documents);
                imgList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final Image current = documents.get(position);

        final String url = current.getUrl();
        final String imgId = current.getId();

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.img_layout, null);
        adb.setView(dialogView);

        PhotoView document = dialogView.findViewById(R.id.document);

//Fit the image into the document ImageView.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final int size = (int) Math.ceil(Math.sqrt(width * height));
        Picasso.get()
                .load(url)
                .resize(size, size)
                .centerInside()
                .into(document);

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
                /*Intent t = new Intent(DocumentsList.this, Document.class);
                t.putExtra("url", url);
                t.putExtra("id", imgId);
                t.putExtra("sign", 1);
                startActivityForResult(t, 2);*/
                editDocument(current);
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(getString(R.string.options_settings));
        menu.add(getString(R.string.view_document));
        menu.add(getString(R.string.edit));
        menu.add(getString(R.string.delete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        final Image image = documents.get(index);
        String id = image.getId();
        String url = image.getUrl();

        String option = item.getTitle().toString();
        if (option.equals(getString(R.string.delete)))
            deleteDocument(image);
        else if (option.equals(getString(R.string.edit)))
            editDocument(image);

        return super.onContextItemSelected(item);
    }

    private void editDocument(Image image) {
        String customerId = getIntent().getStringExtra("id");
        String imgId = image.getId();
        String url = image.getUrl();
        Intent t = new Intent(this, Document.class);
        t.putExtra("id", customerId);
        t.putExtra("imgId", imgId);
        t.putExtra("url", url);
        t.putExtra("sign", 1);

        startActivityForResult(t, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (resultCode == RESULT_OK) {
                updateList();
                LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
                Snackbar snackbar = null;
                switch (requestCode) {
                    case 3:
                        snackbar = Snackbar.make(layout, R.string.doc_uploaded, Snackbar.LENGTH_LONG);
                        break;
                    case 2:
                        snackbar = Snackbar.make(layout, R.string.doc_edited, Snackbar.LENGTH_LONG);
                        break;
                }
                snackbar.show();
            }
        }
    }

    private void deleteDocument(Image image) {
        String id = image.getId();
        ref.child(id).removeValue();
        imgRef.child(id).delete();
        updateList();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        Snackbar snackbar = Snackbar.make(layout, R.string.doc_deleted, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void add(View view) {
        String customerId = getIntent().getStringExtra("id");
        Intent t = new Intent(this, Document.class);
        t.putExtra("id", customerId);
        t.putExtra("sign", 2);
        startActivityForResult(t, 3);
    }
}
