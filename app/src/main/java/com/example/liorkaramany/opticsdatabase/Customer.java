package com.example.liorkaramany.opticsdatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines a Customer that is stored in the database.
 */
public class Customer {

    /**
     * The ID of the customer that is used to identify him in the database.
     */
    public String id;
    /**
     * The first name of the customer.
     */
    public String fName;
    /**
     * The last name of the customer.
     */
    public String lName;
    /**
     * The personal ID of the customer.
     */
    public String customerID;
    /**
     * The address of the customer.
     */
    public String address;
    /**
     * The city of the customer.
     */
    public String city;
    /**
     * The phone number of the customer.
     */
    public String phone;
    /**
     * The mobile phone number of the customer.
     */
    public String mobile;
    /**
     * The date when the customer was uploaded to the database.
     */
    public String openDate;
    /**
     * The type of equipment that the customer bought (glasses or lens).
     */
    public int typeID;

    public Customer()
    {

    }

    /**
     * A constructor of the Customer class.
     *
     * This function creates a Customer with all the given parameters, and sets its registration date to the current date.
     *
     * @param id The ID of the customer that is used to identify him in the database.
     * @param fName The first name of the customer.
     * @param lName The last name of the customer.
     * @param customerID The personal ID of the customer.
     * @param address The address of the customer.
     * @param city The city of the customer.
     * @param phone The phone number of the customer.
     * @param mobile The mobile phone number of the customer.
     * @param typeID The type of equipment that the customer bought (glasses or lens).
     */
    public Customer(String id, String fName, String lName, String customerID, String address, String city, String phone, String mobile, int typeID) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.customerID = customerID;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.mobile = mobile;

        Calendar date = Calendar.getInstance();
        openDate = date.get(Calendar.DAY_OF_MONTH) + "/" + (date.get(Calendar.MONTH)+1) +"/" + date.get(Calendar.YEAR);

        this.typeID = typeID;
    }

    /**
     * A constructor of the Customer class.
     *
     * This function creates a Customer with all the given parameters.
     *
     * @param id The ID of the customer that is used to identify him in the database.
     * @param fName The first name of the customer.
     * @param lName The last name of the customer.
     * @param customerID The personal ID of the customer.
     * @param address The address of the customer.
     * @param city The city of the customer.
     * @param phone The phone number of the customer.
     * @param mobile The mobile phone number of the customer.
     * @param openDate The date when the customer was uploaded to the database.
     * @param typeID The type of equipment that the customer bought (glasses or lens).
     */
    public Customer(String id, String fName, String lName, String customerID, String address, String city, String phone, String mobile, String openDate, int typeID) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.customerID = customerID;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.mobile = mobile;
        this.openDate = openDate;
        this.typeID = typeID;
    }

    /**
     * Returns the customer's ID.
     *
     * @return The customer's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the customer's first name
     *
     * @return The customer's first name.
     */
    public String getfName() {
        return fName;
    }

    /**
     * Returns the customer's last name.
     *
     * @return The customer's last name.
     */
    public String getlName() {
        return lName;
    }

    /**
     * Returns the customer's personal ID.
     *
     * @return The customer's personal ID.
     */
    public String getCustomerID() {
        return customerID;
    }

    /**
     * Returns the customer's address.
     *
     * @return The customer's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the customer's city.
     *
     * @return The customer's city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the customer's phone number.
     *
     * @return The customer's phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the customer's mobile phone number.
     *
     * @return The customer's mobile phone number.
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Returns the customer's date of registration.
     *
     * @return The customer's date of registration.
     */
    public String getOpenDate() {
        return openDate;
    }

    /**
     * Returns the type of equipment that the customer bought.
     *
     * @return The type of equipment that the customer bought.
     */
    public int getTypeID() {
        return typeID;
    }

}
