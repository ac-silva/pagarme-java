package pagarme.api.handler;

import pagarme.api.RestClient;
import pagarme.api.model.SplitRule;

public class SplitRuleHandler {
    private RestClient client;

    public SplitRuleHandler(RestClient client) {
        this.client = client;
    }

    public SplitRule getSplitRuleOfTransaction(Long id) {
        return client.get(id, "transactions", SplitRule.class);
    }
}
