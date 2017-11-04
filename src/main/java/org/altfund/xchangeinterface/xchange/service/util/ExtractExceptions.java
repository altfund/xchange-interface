package org.altfund.xchangeinterface.xchange.service.util;

import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.altfund.xchangeinterface.xchange.model.OrderStatus;
import org.altfund.xchangeinterface.xchange.service.exceptions.XChangeServiceException;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.xchange.model.OrderStatusTypes;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.PLACED;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.PRICE_TOO_SMALL;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.PRICE_TOO_LARGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.INVALID_ARGUMENTS_VOLUME;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.ORDER_TOO_SMALL;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.ORDER_TOO_LARGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.INSUFFICIENT_FUNDS;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.GENERAL_EXCHANGE_ERROR;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NOT_AVAILABLE_FROM_EXCHANGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NOT_YET_IMPLEMENTED_FOR_EXCHANGE;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.NETWORK_ERROR;
import static org.altfund.xchangeinterface.xchange.model.OrderStatusTypes.UNKNOWN_ERROR;

public class ExtractExceptions {

    public static ObjectNode toJson(Exception e, JsonHelper jh) {
        ObjectNode errorMap = jh.getObjectNode();
        if (e instanceof IOException) {
            // Orders failed due to a network error can be retried.
            errorMap.put("ERROR", "Indication that a networking error occurred while fetching JSON data while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof ExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange reported some kind of error with the request or response while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof IllegalArgumentException) {
            errorMap.put("ERROR", "Illegal argument exception while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof NotAvailableFromExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange does not support the requested function or data while fetching requested data on exchange " );
            return errorMap;
        } else if (e instanceof NotYetImplementedForExchangeException) {
            errorMap.put("ERROR", "Indication that the exchange supports the requested function or data, but it has not yet been implemented while fetching requested data on exchange " );
            return errorMap;
        } else {
            errorMap.put("ERROR", "Unknown error while fetching requested data on exchange " );
            return errorMap;
        }
    }

    public static OrderStatus translate(OrderStatusTypes type, Exception e) {
        e.printStackTrace();
        String errorPhrase = getErrorPhrase(e);
        return new OrderStatus(type, errorPhrase);
    }

    public static OrderStatus translate(Exception e) {
        String errorPhrase = getErrorPhrase(e);

        if (errorPhrase.toLowerCase().contains("invalid arguments") && errorPhrase.toLowerCase().contains("volume")) {
            e.printStackTrace();
            return new OrderStatus(INVALID_ARGUMENTS_VOLUME, errorPhrase);
        }
        else if (errorPhrase.toLowerCase().contains("too small") && errorPhrase.toLowerCase().contains("size")) {
            e.printStackTrace();
            return new OrderStatus(ORDER_TOO_SMALL, errorPhrase);
        }
        else if (errorPhrase.toLowerCase().contains("too large")&& errorPhrase.toLowerCase().contains("size")) {
            e.printStackTrace();
            return new OrderStatus(ORDER_TOO_LARGE, errorPhrase);
        }
        else if (errorPhrase.toLowerCase().contains("too large")&& errorPhrase.toLowerCase().contains("price")) {
            e.printStackTrace();
            return new OrderStatus(PRICE_TOO_LARGE, errorPhrase);
        }
        else if (errorPhrase.toLowerCase().contains("too small")&& errorPhrase.toLowerCase().contains("price")) {
            e.printStackTrace();
            return new OrderStatus(PRICE_TOO_SMALL, errorPhrase);
        }
        else if (errorPhrase.toLowerCase().contains("insufficient funds")) {
            e.printStackTrace();
            return new OrderStatus(INSUFFICIENT_FUNDS, errorPhrase);
        }
        else if (e instanceof IOException) {
            e.printStackTrace();
            return new OrderStatus(NETWORK_ERROR, errorPhrase);
            //return OrderStatus.NETWORK_ERROR;
        } else if (e instanceof ExchangeException) {
            //e.printStackTrace();
            //return OrderStatus.GENERAL_EXCHANGE_ERROR;
            return new OrderStatus(GENERAL_EXCHANGE_ERROR, errorPhrase);
        } else if (e instanceof IllegalArgumentException) {
            //e.printStackTrace();
            //return OrderStatus.GENERAL_EXCHANGE_ERROR;
            return new OrderStatus(GENERAL_EXCHANGE_ERROR, errorPhrase);
        } else if (e instanceof NotAvailableFromExchangeException) {
            e.printStackTrace();
            //return OrderStatus.NOT_AVAILABLE_FROM_EXCHANGE;
            return new OrderStatus(NOT_AVAILABLE_FROM_EXCHANGE, errorPhrase);
        } else if (e instanceof NotYetImplementedForExchangeException) {
            e.printStackTrace();
            //return OrderStatus.NOT_YET_IMPLEMENTED_FOR_EXCHANGE;
            return new OrderStatus(NOT_YET_IMPLEMENTED_FOR_EXCHANGE, errorPhrase);
        } else {
            return new OrderStatus(UNKNOWN_ERROR, errorPhrase);
            //e.printStackTrace();
            //return OrderStatus.UNKNOWN_ERROR;
            //return new OrderStatus(UNKNOWN_ERROR, e);
        }
    }

    private static String getErrorPhrase(Exception e) {
        //return e.getClass().getCanonicalName() + " :: " + getCurrentStackTraceString(e);
        return e.getClass().getCanonicalName() + " :: " + e.getMessage().toString();
    }

    private static String getCurrentStackTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
