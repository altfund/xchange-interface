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


exchanges = ['GDAX', 'KRAKEN', 'POLONIEX', 'BITFINEX']

def send(data, method, config):
    #r = json.loads(requests.get("http://localhost:9000/balance", params=json.dumps(data)).text)
    r = json.loads(requests.get(config.get('xi_url') + "/" + method, params=data).text)
    print (r)
    if r.get('ERROR', ''):
            print('error')

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

    # base 65 byte string is decoded to utf-8 encoded string for json serialization
    base64_cipher_string = base64_cipher_byte_string.decode('utf-8')

    encrypted_request = {"iv": init_vector,
            "encrypted_data": base64_cipher_string}
    return encrypted_request

def getCreds(exchange):
    exchange = exchange.upper()
    config = configparser.ConfigParser()
    config.read('config')
    creds = {
            "exchange": exchange,
            "key": config[exchange]['key'],
            "secret": config[exchange]['secret']
            }

    passphrase = config[exchange]['passphrase']

    if (passphrase != None):
        creds.update({"passphrase":  passphrase})

    return creds

def getConfig():
    config = configparser.ConfigParser()
    config.read('config')
    cfg = {
            "xi_url": config["settings"]['xi_url'],
            "aes_key": config["settings"]['aes_key']
            }
    return cfg

def requestBalance(exchange):
    config = getConfig()
    if exchange.lower() == 'all':
        for exchange in exchanges:
            creds = getCreds(exchange)
            send(encrypt(creds, config), "balance", config)
    else:
        creds = getCreds(exchange)
        send(encrypt(creds, config), "balance", config)

@task(help={'exchange': "give -e name of EXCHANGE or ALL for all exchanges"})
def balance(name, exchange):
    requestBalance(exchange)
