package org.altfund.xchangeinterface.restApi.balance;

import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.altfund.xchangeinterface.xchange.service.XChangeService;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
public class BalanceController {
    private final XChangeService xChangeService;

    public BalanceController(XChangeService xChangeService) {
        this.xChangeService = xChangeService;
    }

    @RequestMapping("/balance")
    public BalanceMap balance(@RequestParam Map<String, String> params) {
        return new BalanceMap(xChangeService.getExchangeBalances(params));
    }
}

