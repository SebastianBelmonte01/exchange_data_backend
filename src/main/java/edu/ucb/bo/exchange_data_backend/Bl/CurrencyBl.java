package edu.ucb.bo.exchange_data_backend.Bl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucb.bo.exchange_data_backend.Dto.CurrencyDto;
import edu.ucb.bo.exchange_data_backend.Dto.ResponseDto;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;


@Service
public class CurrencyBl {
    @Value("${currency.url}")
    private String url;
    @Value("${currency.apiKey}")
    private String apiKey;

    public ResponseDto exchange (String to, String from, BigDecimal amount){
        Logger logger = Logger.getLogger(CurrencyBl.class.getName());
        logger.info("Entering to Bussiness Logic");
        if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("An amount cannot be a negative value");
        }
        OkHttpClient client = new OkHttpClient();
        logger.info("Calling the currency API");

        Request request = new Request.Builder()
                .url(url+"?from="+from+"&to="+to+"&amount="+amount)
                .addHeader("apiKey", apiKey)
                .build();
        Call call = client.newCall(request);
        logger.info("Getting the response from the currency API");
        try {
            Response response = call.execute();
            String res = response.body().string();
            if(response.isSuccessful()){
                return responseDto(res);
            }
            throw new RuntimeException("Error calling the currency API");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public ResponseDto responseDto (String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto res = new ResponseDto();
        res.setCode("200");
        System.out.println("Response");
        System.out.println(response);
        res.setResponse(objectMapper.readValue(response, CurrencyDto.class));
        return res;
    }


}
