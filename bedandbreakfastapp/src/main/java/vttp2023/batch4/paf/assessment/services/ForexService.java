package vttp2023.batch4.paf.assessment.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ForexService {

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.frankfurter.app/latest";
        String responseBody = restTemplate.getForObject(url, String.class);

        float rate_FROM = parseExchangeRate(responseBody, from.toUpperCase());
        float rate_TO = parseExchangeRate(responseBody, to.toUpperCase());
        float amount_SGD = (amount / rate_FROM) * rate_TO;

        return amount_SGD;
    }

	private float parseExchangeRate(String responseBody, String currency) {
        String rate = "\"" + currency + "\":";
        int index = responseBody.indexOf(rate);
        if (index == -1) {
            return -1000f;
        }
        index += rate.length();
        int lastIndex = responseBody.indexOf(",", index);
        String rateAmount = responseBody.substring(index, lastIndex);

        return Float.parseFloat(rateAmount);
    }

}
