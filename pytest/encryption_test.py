#!/bin/python3
import json
import requests
import random
import base64
import string
import os

from Crypto.Cipher import AES

class EncryptionTest:
    def __init__(self):
        self.exchange = os.environ["exchange"]
        self.aes_key = os.environ["AES_KEY"]
        self.xi_url = os.environ["XI_URL"]
        self.passphrase = os.environ["passphrase"]
        self.key = os.environ["key"]
        self.secret = os.environ["secret"]

    def request(self):
        payload = {"exchange": self.exchange, "key": self.key, "secret": self.secret}

        if self.passphrase:
            payload["passphrase"] = self.passphrase
        else:
            payload["passphrase"] = ""

        if self.key and self.secret:
            #https://stackoverflow.com/questions/2257441/random-string-generation-with-upper-case-letters-and-digits-in-python
            init_vector = ''.join(random.SystemRandom().choice(string.ascii_letters + string.digits) for _ in range(16))

            #encryption_suite = AES.new(self.key, AES.MODE_CFB, init_vector, segment_size=128)
            encryption_suite = AES.new(self.aes_key, AES.MODE_CFB, init_vector)
            json_data = json.dumps(payload)

            # encrypt returns an encrypted byte string
            cipher_text = encryption_suite.encrypt(json_data)

            # encrypted byte string is base 64 encoded for message passing
            base64_cipher_byte_string = base64.b64encode(cipher_text)

            # base 65 byte string is decoded to utf-8 encoded string for json serialization
            base64_cipher_string = base64_cipher_byte_string.decode('utf-8')

            data = {"iv": init_vector,
                    "encrypted_data": base64_cipher_string}

            #r = json.loads(requests.get("http://localhost:9000/balance", params=json.dumps(data)).text)
            r = json.loads(requests.get(self.xi_url + "/balance", params=data).text)
            print (r)
            if r.get('ERROR', ''):
                print('error')
        else:
            print('missing api key or secret')

if __name__ == "__main__":
     encryptionTest = EncryptionTest()
     encryptionTest.request()
