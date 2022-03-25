package ssf.assess.SSFAssessment.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import ssf.assess.SSFAssessment.model.Quotation;
import ssf.assess.SSFAssessment.services.QuotationService;


@RestController
@RequestMapping(path="/api")
public class PurchaseOrderRestController {

    @Autowired
    private QuotationService quoteSvc;

    
    @PostMapping(path="/po", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postPO(@RequestBody String payload){

        JsonObjectBuilder builder;

        System.out.printf(">>>>>> payload: %s\n", payload);

        try (InputStream is = new ByteArrayInputStream(payload.getBytes())) {
            JsonReader r = Json.createReader(is);
            JsonObject req = r.readObject();
            builder = Json.createObjectBuilder();

            JsonArray jsonArray = req.getJsonArray("lineItems");
            System.out.printf(">>>>>> jsonArray: %s\n", jsonArray.toString());
            
            List<String> items = new ArrayList<>();
            List<Integer> itemQty = new ArrayList<>(); 

            if (jsonArray != null) {      
                //Iterating JSON array  
                for (int i=0;i<jsonArray.size();i++){               
                    //Adding each element of JSON array into ArrayList  
                    items.add(jsonArray.getJsonObject(i).getString("item"));
                    itemQty.add(jsonArray.getJsonObject(i).getInt("quantity"));
                }   
            } 
            //Checking that items are collected
            System.out.printf(">>>>>> Test Item: %s\n", items);

            Optional<Quotation> opt = quoteSvc.getQuotations(items);
            Quotation quotes = opt.get();

            String invoiceId = quotes.getQuoteId();
            List<Float> quoteP = new ArrayList<>();
            
            for (int i=0;i<items.size();i++){               
                //Adding each element of JSON array into ArrayList  
                quoteP.add(quotes.getQuotation(items.get(i)));         
            }

            Float total = 0f;

            for (int i=0;i<items.size();i++){               
                Float price = quoteP.get(i);
                Integer qty = itemQty.get(i);
                
                Float cost = price * qty;

                total += cost;              
            }
            System.out.printf(">>>>>> Total Cost: %s\n", total);

            String name = req.getString("name");
            builder.add("invoiceId", invoiceId);
            builder.add("name", name);
            builder.add("total",total);

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            JsonObject result = Json.createObjectBuilder()
                .add("","")
                .build();
            return ResponseEntity.status(400).body(result.toString());
        }     

        return ResponseEntity.ok(builder.build().toString());
    }


}
