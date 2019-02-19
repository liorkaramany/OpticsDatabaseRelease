package com.example.liorkaramany.opticsdatabase;

import java.io.Serializable;
import java.util.Calendar;

public class Image implements Serializable {
    public String id;
    public String url;
    public String openDate;

    public Image()
    {

    }

    public Image(String id, String url)
    {
        this.id = id;
        this.url = url;

        Calendar date = Calendar.getInstance();
        openDate = date.get(Calendar.DAY_OF_MONTH) + "/" + (date.get(Calendar.MONTH)+1) +"/" + date.get(Calendar.YEAR);
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getOpenDate() {
        return openDate;
    }
}
