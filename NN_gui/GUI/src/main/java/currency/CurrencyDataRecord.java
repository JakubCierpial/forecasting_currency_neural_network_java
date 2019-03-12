package currency;

import lombok.Data;

/**
 * Class represents one record of data in tuple format(currency price, time quote)
 * @author dev.jakub.cierpial@gmail.com
 */
@Data
public class CurrencyDataRecord
{
    private String value;
    private String date;

    public CurrencyDataRecord(String value, String date) {
        this.value = value;
        this.date = date;
    }
}