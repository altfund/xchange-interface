package org.altfund.xchangeinterface.xchange.service.util;

import org.altfund.xchangeinterface.xchange.service.util.ExtractExceptions;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.currency.Currency;
import java.util.NoSuchElementException;
import org.knowm.xchange.dto.account.Wallet;
import java.util.Optional;

@Slf4j
public class ExtractBalances {

    public static ObjectNode toJson(
            Map<String, Wallet> wallets,
            String exchange,
            JsonHelper jh) throws XChangeServiceException {

        ObjectNode innerJson = jh.getObjectNode();
        ObjectNode outerJson = jh.getObjectNode();
        ObjectNode errorMap = jh.getObjectNode();
        ObjectNode json;
        Optional<String> walletString;
        Optional<String> walletId;
        Optional<String> walletName;
        Optional<Wallet> wallet;
        String key;
        Optional<Currency> currency;
        Optional<Balance> balance;
        //Optional<String> walletValue;
        String currencyCode;
        String balanceAvailable;

        try {
            for (Map.Entry<String, Wallet> entry : wallets.entrySet()) {
                key = "";
                try {
                    wallet = Optional.ofNullable(entry.getValue());
                    walletName = Optional.ofNullable(wallet.get().getName());
                    walletString = Optional.ofNullable(entry.getKey());
                    walletId = Optional.ofNullable(wallet.get().getId());

                    key = walletName.orElse(
                            walletString.orElse(
                                walletId.orElse("wallet")
                                )
                            );
                    currencyCode = "";
                    balanceAvailable = "";
                    if (wallet.isPresent()){
                        for (Map.Entry<Currency, Balance> balanceEntry : wallet.get().getBalances().entrySet()) {
                            currencyCode = "";
                            currency = Optional.ofNullable(balanceEntry.getKey());
                            balance = Optional.ofNullable(balanceEntry.getValue());
                            json = getWalletBalances(currency, balance, jh);
                            if (currency.isPresent())
                                currencyCode = currency.get().getCurrencyCode();
                            innerJson.put(currencyCode, json);


                            log.debug("a balance " + json.toString());
                        }//end loop for balances of currency
                        outerJson.put(key, innerJson);
                    } else {
                        errorMap.put("ERROR", "no wallets");
                        return errorMap;
                    }//no loop needed for balances
                    outerJson.put(key, innerJson);
                } catch(NoSuchElementException e) {
                    log.error("No balances found for wallet ", key);
                    errorMap.put("ERROR", exchange + "Falied to retrieve contents of wallets in exchange");
                    return errorMap;
                }
            } //end wallet loop
            log.info("Processed exchange currency {} successfully.", exchange);
        } catch (RuntimeException re) {
            log.error("Non-retryable error occurred while processing exchange {}.",
                    exchange);
            errorMap.put("ERROR", exchange + "Falied to retrieve contents of exchange");
            return errorMap;
        }
        return outerJson;

    }

    private static ObjectNode getWalletBalances(Optional<Currency> currency, Optional<Balance> balance, JsonHelper jh){
        String currencyCode = "";
        ObjectNode json = jh.getObjectNode();
        ObjectNode outerJson = jh.getObjectNode();
        if (balance.isPresent()) {
            json.put("available", balance.get().getAvailable());
            json.put("availableForWithdraw", balance.get().getAvailableForWithdrawal());
            json.put("borrowed", balance.get().getBorrowed());
            json.put("depositing", balance.get().getDepositing());
            json.put("frozen", balance.get().getFrozen());
            json.put("loaned", balance.get().getLoaned());
            json.put("total", balance.get().getTotal());
            json.put("withdrawing", balance.get().getWithdrawing());
        }
        return json;
    }

}
