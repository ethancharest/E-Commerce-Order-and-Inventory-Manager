package ecommerce.service;

import ecommerce.model.Address;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class SimpleTaxCalc {

    private HashMap<String, Double> stateTaxRates;

    public SimpleTaxCalc() throws FileNotFoundException {
        stateTaxRates = new HashMap<>();
        File file = new File("ecommerce/data/state_tax_rates.csv");
        //data in csv from https://taxfoundation.org/data/all/state/sales-tax-rates/
        Scanner fileReader = new Scanner(file);
        fileReader.next(); //skip header
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            String[] parts = line.split(",");
            String state = parts[0];
            double rate = Double.parseDouble(parts[1]);
            stateTaxRates.put(state, rate);
        }
        fileReader.close();
    }

    public double calculateTax(Address adress, double amount) {
        Double rate = stateTaxRates.get(adress.getState());
        return amount * rate;
    }
}
