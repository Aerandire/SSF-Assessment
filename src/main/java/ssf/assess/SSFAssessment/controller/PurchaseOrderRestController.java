package ssf.assess.SSFAssessment.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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


@RestController
@RequestMapping(path="/api")
public class PurchaseOrderRestController {
    
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

            if (jsonArray != null) {      
                //Iterating JSON array  
                for (int i=0;i<jsonArray.size();i++){               
                    //Adding each element of JSON array into ArrayList  
                    items.add(jsonArray.getJsonObject(i).getString("item"));  
                }   
            } 
            //Checking that items are collected
            System.out.printf(">>>>>> Test Item: %s\n", items);

            String name = req.getString("name");
            builder.add("name", name);

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            JsonObject result = Json.createObjectBuilder()
                .add("error", ex.getMessage())
                .build();
            return ResponseEntity.status(400).body(result.toString());
        } 
        return ResponseEntity.ok(builder.build().toString());
    }


}
