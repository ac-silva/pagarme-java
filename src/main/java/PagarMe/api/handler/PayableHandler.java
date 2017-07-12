package pagarme.api.handler;

import java.util.List;

import pagarme.api.RestClient;
import pagarme.api.model.Payable;

public class PayableHandler {
    private RestClient client;

    public PayableHandler(RestClient client) {
        this.client = client;
    }

    public Payable getPayable(Long id) {
        return this.client.get(id, Payable.class);
    }

    public List<Payable> getAllPayables() {
        return this.client.getAll(Payable.class);
    }

}
