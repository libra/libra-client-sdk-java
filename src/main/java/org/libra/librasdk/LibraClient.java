package org.libra.librasdk;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import org.libra.librasdk.dto.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LibraClient implements Client {

    private JSONRPCClient jsonrpcClient;

    public LibraClient(LibraNetwork libraNetwork) {
        jsonrpcClient = new JSONRPCClient(libraNetwork.url);
    }

    public List<Transaction> getTransactions(int fromVersion, int limit, boolean includeEvents) throws JSONRPC2Error, JSONRPC2SessionException {
        List<Object> params = new ArrayList<>();
        params.add(fromVersion);
        params.add(limit);
        params.add(includeEvents);

        String transactionJson = jsonrpcClient.call(Method.get_transactions, params);
        // get the type of a list of LibraTransaction to ease deserialization
        Type listType = new TypeToken<List<Transaction>>() {
        }.getType();

        List<Transaction> libraTransactions = new Gson().fromJson(transactionJson, listType);

        return libraTransactions;
    }

    public Account getAccount(String address) throws JSONRPC2Error, JSONRPC2SessionException {
        List<Object> params = new ArrayList<>();
        params.add(address);
        return executeCall(params, Method.get_account, Account.class);
    }

    private <T> T executeCall(List<Object> params, Method method, Class<T> responseType) throws JSONRPC2Error, JSONRPC2SessionException {
        String response = jsonrpcClient.call(method, params);
        T result = null;

        if (responseType != null) {
            result = new Gson().fromJson(response, responseType);
        }
        return result;
    }

    public Metadata getMetadata() throws JSONRPC2Error, JSONRPC2SessionException {
        return executeCall(new ArrayList<>(), Method.get_metadata, Metadata.class);
    }

    public Metadata getMetadata(long version) throws JSONRPC2Error, JSONRPC2SessionException {
        List<Object> params = new ArrayList<>();
        params.add(version);

        return executeCall(params, Method.get_metadata, Metadata.class);
    }

    public Currency[] getCurrencies() throws JSONRPC2Error, JSONRPC2SessionException {
        return executeCall(new ArrayList<>(), Method.get_currencies, Currency[].class);
    }

    public Transaction getAccountTransaction(String address, long sequence,
                                             boolean includeEvents) throws JSONRPC2Error,
            JSONRPC2SessionException {
        List<Object> params = new ArrayList<>();
        params.add(address);
        params.add(sequence);
        params.add(includeEvents);

        return executeCall(params, Method.get_account_transaction, Transaction.class);
    }

    public void submit(String data) throws JSONRPC2Error, JSONRPC2SessionException {
        List<Object> params = new ArrayList<>();
        params.add(data);

        executeCall(params, Method.submit, null);
    }

    public Transaction waitForTransaction(String address, long sequence, boolean includeEvents,
                                          long timeoutMillis) throws InterruptedException,
            JSONRPC2Error, JSONRPC2SessionException {
        for (long millis = 0, step = 100; millis < timeoutMillis; millis += step) {
            Transaction transaction = this.getAccountTransaction(address, sequence, includeEvents);
            if (transaction != null) {
                return transaction;
            }
            Thread.sleep(step);
        }

        return null;
    }

    public List<Event> getEvents(String eventsKey, long start, long limit) throws JSONRPC2Error,
            JSONRPC2SessionException {
        List<Object> params = new ArrayList<>();
        params.add(eventsKey);
        params.add(start);
        params.add(limit);

        Type listType = new TypeToken<List<Event>>() {
        }.getType();

        String eventsJson = jsonrpcClient.call(Method.get_events, params);
        List<Event> libraEvents = new Gson().fromJson(eventsJson, listType);

        return libraEvents;
    }
}
