package org.altfund.xchangeinterface.restApi.currency;

import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.currency.Currency;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */

@Slf4j
@RestController
public class CurrencyController {

    private static final String template = "Exchange, %s!";
    private final XChangeService xChangeService;

    //private XChangeService xChangeService;

    //public CurrencyController currencyController(XChangeService xChangeService) {
    //    this.xChangeService = xChangeService;
    //}

    public CurrencyController(XChangeService xChangeService) {
        this.xChangeService = xChangeService;
    }
    @RequestMapping("/currency")
    public CurrencyMap currencyMap(@RequestParam(value="exchange") String exchange) {
        return new CurrencyMap(xChangeService.getExchangeCurrencies(exchange));
    }
}

