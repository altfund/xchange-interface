package org.altfund.xchangeinterface.restApi.balance;

import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
public class BalanceController {

    private static final String template = "Hello, %s!";

    @RequestMapping("/balance")
    public Balance balance(@RequestParam(value="name", defaultValue="World") String name) {
        return new Balance(String.format(template, name));
    }
}

