package ssf.assess.SSFAssessment.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import ssf.assess.SSFAssessment.model.Quotation;

@Service
public class QuotationService {

    private static final String URL = "https://quotation.chuklee.com/";
    
    public Optional<Quotation> getQuotations(List<String> items) throws IOException{

        String itemURL = UriComponentsBuilder.fromUriString(URL)
                            .path("/quotation")
                            .toUriString();

        JsonArrayBuilder jsonArray = Json.createArrayBuilder();

        items.stream()
            .forEach((String i) -> {
                jsonArray.add(i);
            });
        
        JsonArray itemsArray = jsonArray.build();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = null; 
        RequestEntity<String> req = RequestEntity.post(itemURL)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON)
                                                .body(itemsArray.toString());
        System.out.printf(">>>>>> JSON ARRAY: %s\n", itemsArray);
                                   


        resp = template.exchange(req, String.class);
        System.out.printf(">>>>>> Return: %s\n", resp.getBody());
        Quotation q = new Quotation();
        
        try (InputStream is = new ByteArrayInputStream(resp.getBody().getBytes())) {
            JsonReader r = Json.createReader(is);
            JsonObject res = r.readObject();

            JsonArray returnQuote = res.getJsonArray("quotations");
            System.out.printf(">>>>>> Return Array: %s\n", returnQuote.toString());

            String quoteID = res.getString("quoteId");
            System.out.printf(">>>>>> QuoteID: %s\n", quoteID);
            q.setQuoteId(quoteID);

            if (jsonArray != null) {      
                //Iterating JSON array  
                for (int i=0;i<returnQuote.size();i++){               
                    //Adding each element of JSON array into ArrayList 
                    String item = returnQuote.getJsonObject(i).getString("item");
                    String unitP = returnQuote.getJsonObject(i).get("unitPrice").toString();
                    Float unitPf = Float.parseFloat(unitP);
                    System.out.printf(">>>>>> price: %s\n", unitPf);
                    q.addQuotation(item, unitPf);  
                }   
            } 

        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }   
        
        return Optional.of(q);
    }
}
