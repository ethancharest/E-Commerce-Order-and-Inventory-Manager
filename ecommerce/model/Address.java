package ecommerce.model;

public class Address {

    /*
     * This class stores the necessary information for a U.S. based shipping address
     */

    private String street;
    private String city;
    private String state;
    private String zipCode;

    /**
     * @param street The street and house number
     * @param city The city or town
     * @param state Two-letter U.S. state code ("PA")
     * @param zipCode Five-digit postal code
     *
     * U.S only, no need for other countries for this project
     */
    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;

    }

    /*
     * Returns the U.S. state code for this address
     * Used for tax calculation
     */
    public String getState() {
        return state;
    }

    // Simple toString to return the Address, used for displaying address
    // information
    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " " + zipCode;
    }
}
