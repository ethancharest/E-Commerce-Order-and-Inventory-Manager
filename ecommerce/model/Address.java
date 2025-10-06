package ecommerce.model;

public class Address {
    public String street, city, state, zipCode, country; //maybe add country, maybe not
    public Address (String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country; 
    }
    @Override public String toString() {return street + ", " + city + ", " + state + " " + zipCode + ", " + country;}
}
