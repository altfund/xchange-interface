#!/bin/python3

import json
import re
import requests
import random
import base64
import string
import os
import configparser

from Crypto.Cipher import AES
from invoke import task

#balance, cancelorder, limitorder, openorders, orderbook, json, ticker, tradefees, tradehistory,

exchanges = ['GDAX', 'KRAKEN', 'POLONIEX', 'BITFINEX']
default_limit_ask = {"order_type":"ASK","order_specs":{"base_currency":"ETH","quote_currency":"BTC","volume":"0.1","price":"10000","test":False}}
default_limit_bid = {"order_type":"BID","order_specs":{"base_currency":"ETH","quote_currency":"BTC","volume":"0.01","price":"0.0001","test": False}}

def print_exchange_results(response, failure):
    failures = []
    for exchange in exchanges:
        if exchange.upper() + "ret" in response:
            print("+++++++++++++++++++++++++++++")
            print(exchange.upper())
            print("~~~~~~~~~~~~~~~~")
            print(response.get(exchange.upper()))
            if response.get(exchange.upper() + "ret"):
                print("SUCCESS")
            else:
                failures.append(exchange.upper())
                print("FAILURE")
            print("+++++++++++++++++++++++++++++")
    print(response)
    return failures


def print_report(response):
    print("==============================================================")
    print("START")
    print("==============================================================")
    if ('error' in response):
        failures = print_exchange_results(response, True)
        print("==============================================================")
        print("FAILURE(S): ", len(failures)/len(exchanges)*100,"%, :: ", len(failures),"/", len(exchanges))
        print(failures)
        print("==============================================================")
    else:
        print_exchange_results(response, False)
        print("==============================================================")
        print("SUCCESS")
        print("==============================================================")

def report(data, exchange, response):
    data = decrypt(data)
    if (data == None or 'ERROR' in data or 'exception' in data or 'error' in data or re.match(r'.*error.*', data.lower()) or re.match(r'.*redacted.*', data.lower())):
        response.update({"error": True})
        response.update({exchange.upper(): data})
        response.update({exchange.upper() + "ret": False})
    else:
        response.update({exchange.upper(): data})
        response.update({exchange.upper() + "ret": True})

def send(data, method, config):
    r = requests.get(config.get('xi_url') + "/" + method, params=data).text
    json_data = json.loads(json.dumps(r))
    return json_data

def decrypt(payload):
    config = getConfig()
    payload = json.loads(payload)
    init_vector = payload['iv']
    encrypted_data = payload['encrypted_data']
    init_vector = base64.b64decode(init_vector)
    encrypted_data = base64.b64decode(encrypted_data)


    encryption_suite = AES.new(config['aes_key'], AES.MODE_CFB, init_vector)

    plain_text = encryption_suite.decrypt(encrypted_data)
    plain_text = plain_text.decode('utf-8')

    return plain_text

def encrypt(data, config):
    #https://stackoverflow.com/questions/2257441/random-string-generation-with-upper-case-letters-and-digits-in-python
    init_vector = ''.join(random.SystemRandom().choice(string.ascii_letters + string.digits) for _ in range(16))

    #encryption_suite = AES.new(self.key, AES.MODE_CFB, init_vector, segment_size=128)
    encryption_suite = AES.new(config['aes_key'], AES.MODE_CFB, init_vector)
    json_data = json.dumps(data)

    # encrypt returns an encrypted byte string
    cipher_text = encryption_suite.encrypt(json_data)

    # encrypted byte string is base 64 encoded for message passing
    base64_cipher_byte_string = base64.b64encode(cipher_text)

    # base 64 byte string is decoded to utf-8 encoded string for json serialization
    base64_cipher_string = base64_cipher_byte_string.decode('utf-8')

    encrypted_request = {"iv": init_vector,
            "encrypted_data": base64_cipher_string}
    return encrypted_request

def getCreds(exchange):
    exchange = exchange.upper()
    config = configparser.ConfigParser()
    config.read('config')
    try:
        creds = {
                "exchange": exchange.lower(),
                "key": config[exchange]['key'],
                "secret": config[exchange]['secret']
                }
    except:
        raise ValueError('exchange ' + exchange.lower() + ' does not have credentials')

    try:
        passphrase = config[exchange]['passphrase']
        creds.update({"passphrase":  passphrase})
        return creds
    except:
        return creds

def getConfig():
    config = configparser.ConfigParser()
    config.read('config')
    cfg = {
            "xi_url": config["settings"]['xi_url'],
            "aes_key": config["settings"]['aes_key']
            }
    return cfg

def request(exchange, method):
    config = getConfig()
    response = {}
    if exchange.lower() == 'all':
        for an_exchange in exchanges:
            data = {"exchange": an_exchange.lower()}
            r = send(data, method, config)
            report(r, an_exchange.lower(), response)

    else:
        data = {"exchange": exchange.lower()}
        r = send(data, method, config)
        report(r, exchange.lower(), response)

    print_report(response)

def requestLimitOrder(exchange, ordertype):
    order = ""
    limitorder = ""
    if ordertype.lower() == 'ask':
        order = 'ask'
        limitorder = default_limit_ask
    elif ordertype.lower() == 'bid':
        order = 'bid'
        limitorder = default_limit_bid
    else:
        order = ""

    if order == "":
        print("Must set order type to ASK or BID");
    else:
        config = getConfig()
        response = {}
        if exchange.lower() == 'all':
            for exchange in exchanges:
                creds = getCreds(exchange)
                limitorder.update({"exchange_credentials":creds})
                r = send(encrypt(limitorder, config), "limitorder", config)
                report(r, exchange.lower(), response)
        else:
            creds = getCreds(exchange)
            limitorder.update({"exchange_credentials":creds})
            r = send(encrypt(limitorder, config), "limitorder", config)
            report(r, exchange.lower(), response)
        print_report(response)

def requestOpenOrders(exchange):
    config = getConfig()
    response = {}
    temp = {}
    #orders_req = {}
    #temp.update({"base_currency": "ETH"});
    #temp.update({"quote_currency": "BTC"});
    #orders_req.update({"open_order_params": temp});
    if exchange.lower() == 'all':
        for exchange in exchanges:
            creds = getCreds(exchange)
            #orders_req.update({"exchange_credentials": creds});
            r = send(encrypt(creds, config), "openorders", config)
            report(r, exchange.lower(), response)
    else:
        creds = getCreds(exchange)
        #orders_req.update({"exchange_credentials": creds});
        r = send(encrypt(creds, config), "openorders", config)
        report(r, exchange.lower(), response)
    print_report(response)

def requestTradeHistory(exchange):
    config = getConfig()
    response = {}
    temp = {}
    history_req = {}
    #temp.update({"currency_pair": "ETH/BTC"});
    temp.update({"page_length": "10"});
    history_req.update({"trade_params": temp});
    if exchange.lower() == 'all':
        for exchange in exchanges:
            creds = getCreds(exchange)
            history_req.update({"exchange_credentials": creds});
            r = send(encrypt(history_req, config), "tradehistory", config)
            report(r, exchange.lower(), response)
    else:
        creds = getCreds(exchange)
        history_req.update({"exchange_credentials": creds});
        r = send(encrypt(history_req, config), "tradehistory", config)
        report(r, exchange.lower(), response)
    print_report(response)

def cancelLimitOrder(exchange, order_id):
    config = getConfig()
    response = {}
    order_to_cancel = {}
    if exchange.lower() == 'all':
        for exchange in exchanges:
            creds = getCreds(exchange)
            order_to_cancel.update({"exchange_credentials": creds});
            order_to_cancel.update({"order_id": order_id});
            r = send(encrypt(order_to_cancel, config), "cancelorder", config)
            report(r, exchange.lower(), response)
    else:
        creds = getCreds(exchange)
        order_to_cancel.update({"exchange_credentials": creds});
        order_to_cancel.update({"order_id": order_id});
        r = send(encrypt(order_to_cancel, config), "cancelorder", config)
        report(r, exchange.lower(), response)
    print_report(response)

def requestBalance(exchange):
    config = getConfig()
    response = {}
    if exchange.lower() == 'all':
        for exchange in exchanges:
            creds = getCreds(exchange)
            r = send(encrypt(creds, config), "balance", config)
            report(r, exchange.lower(), response)
    else:
        creds = getCreds(exchange)
        r = send(encrypt(creds, config), "balance", config)
        report(r, exchange.lower(), response)
    print_report(response)

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def balance(name, exchange):
    requestBalance(exchange)

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def tradehistory(name, exchange):
    requestTradeHistory(exchange)

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def openorders(name, exchange):
    requestOpenOrders(exchange)

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def tradefees(name, exchange):
    request(exchange, 'tradefees')

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def ticker(name, exchange):
    request(exchange, 'ticker')

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def orderbook(name, exchange):
    request(exchange, 'orderbook')

def jsonendpoint(name):
    request(exchanqe, 'json')

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges", "ordertype": "give -o ASK or BID"})
def limitorder(name, exchange, ordertype):
    requestLimitOrder(exchange, ordertype)

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def currency(name, exchange):
    request(exchange, 'currency')

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges", "order_id": "give -o id of order to cancel"})
def cancelorder(name, exchange, order_id):
    cancelLimitOrder(exchange, order_id)

@task
def errorendpoint(name, exchange):
    request(exchange, 'error')

@task
def ls(name):
    for x in exchanges:
        print(x)
