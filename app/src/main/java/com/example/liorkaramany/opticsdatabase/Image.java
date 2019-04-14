package com.example.liorkaramany.opticsdatabase;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines an Image that is stored in the database.
 */
public class Image implements Serializable {
    /**
     * The ID of the image that is used to identify it in the database.
     */
    public String id;
    /**
     * The URL of the image.
     */
    public String url;
    /**
     * The date when the image was stored in the database.
     */
    public String openDate;

    public Image()
    {

    }

    /**
     * A constructor of the Image class.
     *
     * This function creates an Image with all the given parameters, and sets its registration date to the current date.
     *
     * @param id The ID of the image that is used to identify it in the database.
     * @param url The URL of the image.
     */
    public Image(String id, String url)
    {
        this.id = id;
        this.url = url;

        Calendar date = Calendar.getInstance();
        openDate = date.get(Calendar.DAY_OF_MONTH) + "/" + (date.get(Calendar.MONTH)+1) +"/" + date.get(Calendar.YEAR);
    }

    /**
     * Returns the image's ID.
     *
     * @return The image's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the image's URL.
     *
     * @return The image's URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns The image's date of storage.
     *
     * @return The image's date of storage.
     */
    public String getOpenDate() {
        return openDate;
    }
}
