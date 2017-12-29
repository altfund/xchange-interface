package org.altfund.xchangeinterface.xchange.service.util;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.altfund.xchangeinterface.xchange.model.ExchangeCredentials;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import java.math.BigDecimal;

@Slf4j
public class ExchangeScale {

    public BigDecimal getMinimumAmount(CurrencyPair cp, org.knowm.xchange.Exchange exchange) {
        //Optional<org.knowm.xchange.Exchange> exchange =
        //    Optional.ofNullable(exchangeCredsMap.get(exchangeCredentials));

        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.getExchangeMetaData());

        Optional<CurrencyPairMetaData> exCurPairMetaData =
            Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));

        if (exCurPairMetaData.isPresent() && exCurPairMetaData.get().getMinimumAmount() != null) {
            log.debug("found xchange scale in the currency pair meta data minimum amt.");
            log.debug("setting min amt: {}", exCurPairMetaData.get().getMinimumAmount());
            return exCurPairMetaData.get().getMinimumAmount();
        }
        return new BigDecimal("-1");
    }

    public int getQuoteScale(CurrencyPair cp, org.knowm.xchange.Exchange exchange) {
        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.getExchangeMetaData());

        Optional<Integer> exQuoteCurScale = null;
        Optional<CurrencyMetaData> exQuoteCurMd = null;
        Optional<CurrencyPairMetaData> exCurPairMetaData = null;

        if (exchangeMetaData.isPresent()) {
            exCurPairMetaData = Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));
            exQuoteCurMd = Optional.ofNullable(exchangeMetaData.get().getCurrencies().get(cp.counter));
        }

        if (exQuoteCurMd != null && exQuoteCurMd.isPresent()) {
            exQuoteCurScale = Optional.ofNullable(exQuoteCurMd.get().getScale());
        }

        if (exCurPairMetaData != null && exCurPairMetaData.isPresent() && exCurPairMetaData.get().getMinimumAmount() != null) {
            log.debug("found xchange scale in the currency pair meta data minimum amt.");
            log.debug("setting quote scale: {}", exCurPairMetaData.get().getMinimumAmount().scale());
            return exCurPairMetaData.get().getMinimumAmount().scale();
        } else if (exQuoteCurScale !=  null && exQuoteCurScale.isPresent()){
            log.debug("inferred xchange scale from the quote currency.");
            log.debug("setting quote scale: {}", exQuoteCurScale.get());
            return exQuoteCurScale.get();
        }
        log.debug("Randomly setting quote scale to 5.");
        log.debug("setting quote scale: {}", 5);
        return 5;
    }

    /*
    private int getScale() {
        TODO make getBaseScale and getQuoteScale same method AND figure out
        which approximation for scale is better?
    }
    */
    public int getBaseScale(CurrencyPair cp, org.knowm.xchange.Exchange exchange) {

        Optional<ExchangeMetaData> exchangeMetaData =
            Optional.ofNullable(exchange.getExchangeMetaData());

        Optional<Integer> exBaseCurScale = null;
        Optional<CurrencyMetaData> exBaseCurMd = null;
        Optional<CurrencyPairMetaData> exCurPairMetaData = null;

        if (exchangeMetaData.isPresent()) {
            exCurPairMetaData = Optional.ofNullable(exchangeMetaData.get().getCurrencyPairs().get(cp));
            exBaseCurMd = Optional.ofNullable(exchangeMetaData.get().getCurrencies().get(cp.base));
        }

        if (exBaseCurMd != null && exBaseCurMd.isPresent()) {
            exBaseCurScale = Optional.ofNullable(exBaseCurMd.get().getScale());
        }

        if (exCurPairMetaData != null && exCurPairMetaData.isPresent() && exCurPairMetaData.get().getPriceScale() != null) {
            log.debug("found xchange scale in the currency pair meta data price scale.");
            log.debug("setting base scale: {}", exCurPairMetaData.get().getPriceScale());
            return exCurPairMetaData.get().getPriceScale();
        } else if (exBaseCurScale != null && exBaseCurScale.isPresent()){
            log.debug("inferred xchange scale from the base currency.");
            log.debug("setting base scale: {}", exBaseCurScale.get());
            return exBaseCurScale.get();
        }
        log.debug("Randomly setting base scale to 5.");
        log.debug("setting base scale: {}", 5);
        return 5;
    }
}
