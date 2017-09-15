#!/bin/python3

import json
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
    if ('ERROR' in data or 'exception' in data or 'error' in data):
        response.update({"error": True})
        response.update({exchange.upper(): data})
        response.update({exchange.upper() + "ret": False})
    else:
        response.update({exchange.upper(): data})
        response.update({exchange.upper() + "ret": True})

def send(data, method, config):
    #r = json.loads(requests.get("http://localhost:9000/balance", params=json.dumps(data)).text)
    r = json.loads(requests.get(config.get('xi_url') + "/" + method, params=data).text)
    #print (r)
    #if r.get('ERROR', ''):
    #    print('error')
    return r

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

@task
def tradehistory(name, exchange):
    #requestBalance(exchange)
    print("Not yet implemented")

@task
def tradefees(name, exchange):
    request(exchange, 'tradefees')

@task
def ticker(name, exchange):
    request(exchange, 'ticker')

@task
def orderbook(name, exchange):
    request(exchange, 'orderbook')

@task
def openorders(name, exchange):
    #requestBalance(exchange)
    print("Not yet implemented")

@task
def jsonendpoint(name, exchange):
    request(exchange, 'json')

@task
def limitorder(name, exchange):
    #requestBalance(exchange)
    print("Not yet implemented")

@task
def currency(name, exchange):
    request(exchange, 'currency')

@task
def cancelorder(name, exchange):
    #requestBalance(exchange)
    print("Not yet implemented")

@task
def errorendpoint(name, exchange):
    #requestBalance(exchange)
    #print("Not yet implemented")
    request(exchange, 'error')

@task
def ls(name):
    for x in exchanges:
        print(x)
