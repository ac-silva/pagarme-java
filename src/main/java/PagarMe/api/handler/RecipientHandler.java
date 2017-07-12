package pagarme.api.handler;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pagarme.api.RestClient;
import pagarme.api.model.Recipient;

public class RecipientHandler {
    private RestClient client;

    public RecipientHandler(RestClient client) {
        this.client = client;
    }

    public Recipient createRecipient(Recipient recipient) throws JsonParseException, JsonMappingException, IOException {
        return client.create(recipient, Recipient.class);
    }

    public Recipient getRecipient(String id) {
        return client.get(id, Recipient.class);
    }

}
