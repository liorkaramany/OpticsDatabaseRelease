package com.example.liorkaramany.opticsdatabase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines an ArrayAdapter that is linked to an activity with the ListView and to the images list.
 */
public class ImageList extends ArrayAdapter<Image> {

    /**
     * The activity which contains the ListView.
     */
    private Activity context;
    /**
     * The list which contains the images.
     */
    private List<Image> imageList;

    /**
     * A constructor of the Image class.
     *
     * This function creates an ImageList ArrayAdapter with all the given parameters.
     *
     * @param context The activity which contains the ListView.
     * @param imageList The list which contains the images.
     * @return A reference to the created ImageList ArrayAdapter.
     */
    public ImageList(Activity context, List<Image> imageList)
    {
        super(context, R.layout.img_layout, imageList);
        this.context = context;
        this.imageList = imageList;
    }

    /**
     * Sets up the view that will be displayed in each row of the ListView.
     *
     * @param position the index of the Image in the list.
     * @return the view which the ListView displays.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.small_img_list, null, true);

        ImageView document = (ImageView) listViewItem.findViewById(R.id.document);

        TextView date = (TextView) listViewItem.findViewById(R.id.date);

        LinearLayout layout = (LinearLayout) listViewItem.findViewById(R.id.layout);

        if (position % 2 == 0)
            layout.setBackgroundColor(Color.parseColor("#d9e3f4"));
        else
            layout.setBackgroundColor(Color.parseColor("#ffffff"));

        Image image = imageList.get(position);

        if (image != null) {
            date.setText(image.getOpenDate());

            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            final Point point = new Point(width, height);

            final int size = (int) Math.ceil(Math.sqrt(width * height));
            Picasso.get()
                    .load(image.getUrl())
                    .resize(size, size)
                    .centerInside()
                    .into(document);
        }

        return listViewItem;
    }
}
