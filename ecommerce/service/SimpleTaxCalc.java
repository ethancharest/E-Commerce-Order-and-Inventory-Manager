package ecommerce.service;

import ecommerce.model.Address;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * SimpleTaxCalc provides basic sales tax calculation based on U.S. state rates
 * Uses shipping address to determine applicable tax rate Reads a CSV file of
 * state tax rates at startup and uses it to calculate tax on order amounts
 */
public class SimpleTaxCalc {

    //Map of U.S state abbreviation --> tax rate
    private final HashMap<String, Double> stateTaxRates;

    /**
     * Constructs a SimpleTaxCalc and loads state tax rates from CSV file
     *
     * @throws FileNotFoundException if the tax rates file cannot be found
     */
    public SimpleTaxCalc() throws FileNotFoundException {
        stateTaxRates = new HashMap<>();
        File file = new File("ecommerce/data/state_tax_rates.csv");
        //data in csv from https://taxfoundation.org/data/all/state/sales-tax-rates/
        Scanner fileReader = new Scanner(file);

        fileReader.nextLine(); //skip header

        //parse each line into state --> rate entry
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            String[] parts = line.split(",");
            String state = parts[0];
            String stateCode = parts[1];
            double rate = Double.parseDouble(parts[2]);
            stateTaxRates.put(state, rate);
            stateTaxRates.put(stateCode, rate);
        }
        fileReader.close();
    }

    public boolean validateStateCode(String stateCode) {
        return stateTaxRates.containsKey(stateCode);
    }

    /**
     * Calculates the sales tax for a given address and amount
     *
     * @param address The shipping address to determine tax rate
     * @param amount The amount to calculate tax on
     * @return The calculated tax amount, or 0.0 if no rate found
     *
     * If a state code is missing or has no rate, returns 0.0 tax rather than
     * throwing an exception.
     */
    public double calculateTax(Address address, double amount) {
        try {
            Double rate = stateTaxRates.get(address.getState());
            return amount * rate;
        } catch (Exception e) {
            return 0.0;
        }

    }
}
