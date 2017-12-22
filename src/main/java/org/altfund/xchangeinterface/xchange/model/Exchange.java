package org.altfund.xchangeinterface.xchange.model;

/**
 * altfund
 */
public enum Exchange {

  ANX("org.knowm.xchange.anx.v2.ANXExchange"),
  BITFINEX("org.knowm.xchange.bitfinex.v1.BitfinexExchange"),
  BITCOINDE("org.knowm.xchange.bitcoinde.BitcoindeExchange"),
  BITSO("org.knowm.xchange.bitso.BitsoExchange"),
  BITSTAMP("org.knowm.xchange.bitstamp.BitstampExchange"),
  BTCMARKETS("org.knowm.xchange.btcmarkets.BTCMarketsExchange"),
  CCEX("org.knowm.xchange.ccex.CCEXExchange"),
  COINFLOOR("org.knowm.xchange.coinfloor.CoinfloorExchange"),
  COINMATE("org.knowm.xchange.coinmate.CoinmateExchange"),
  CRYPTOFACILITIES("org.knowm.xchange.cryptofacilities.CryptoFacilitiesExchange"),

  DSX("org.knowm.xchange.dsx.DSXExchange"),
  INDEPENDENTRESERVE("org.knowm.xchange.independentreserve.IndependentReserveExchange"),
  BITCUREX("org.knowm.xchange.bitcurex.BitcurexExchange"),
  MERCADOBITCOIN("org.knowm.xchange.mercadobitcoin.MercadoBitcoinExchange"),
  BLOCKCHAIN("org.knowm.xchange.blockchain.BlockchainExchange"),
  BITCOINIUM("org.knowm.xchange.bitcoinium.BitcoiniumExchange"),
  BITMARKET("org.knowm.xchange.bitmarket.BitMarketExchange"),
  HITBTC("org.knowm.xchange.hitbtc.HitbtcExchange"),
  CRYPTOPIA("org.knowm.xchange.cryptopia.CryptopiaExchange"),
  CEXIO("org.knowm.xchange.cexio.CexIOExchange"),

  COINBASE("org.knowm.xchange.coinbase.CoinbaseExchange"),
  LIVECOIN("org.knowm.xchange.livecoin.LivecoinExchange"),
  BITTREX("org.knowm.xchange.bittrex.BittrexExchange"),
  BITBAY("org.knowm.xchange.bitbay.BitbayExchange"),
  GATECOIN("org.knowm.xchange.gatecoin.GatecoinExchange"),
  BLEUTRADE("org.knowm.xchange.bleutrade.BleutradeExchange"),
  PAYMIUM("org.knowm.xchange.paymium.PaymiumExchange"),
  CAMPBX("org.knowm.xchange.campbx.CampBXExchange"),
  VIRCUREX("org.knowm.xchange.vircurex.VircurexExchange"),

  VAULTORO("org.knowm.xchange.vaultoro.VaultoroExchange"),
  TRUEFX("org.knowm.xchange.truefx.TrueFxExchange"),
  QUADRIGACX("org.knowm.xchange.quadrigacx.QuadrigaCxExchange"),
  LUNO("org.knowm.xchange.luno.LunoExchange"),
  GEMINI("org.knowm.xchange.gemini.v1.GeminiExchange"),
  QUOINE("org.knowm.xchange.quoine.QuoineExchange"),
  ITBIT("org.knowm.xchange.itbit.v1.ItBitExchange"),
  CRYPTONIT("org.knowm.xchange.cryptonit.v2.CryptonitExchange"),
  BTCTRADE("org.knowm.xchange.btctrade.BTCTradeExchange"),
  BTCC("org.knowm.xchange.btcc.BTCCExchange"),
  BITCOINCORE("org.knowm.xchange.bitcoincore.BitcoinCoreWallet"),
  BITCOINCHARTS("org.knowm.xchange.bitcoincharts.BitcoinChartsExchange"),
  BITCOINAVERAGE("org.knowm.xchange.bitcoinaverage.BitcoinAverageExchange"),

  EMPOEX("org.knowm.xchange.empoex.EmpoExExchange"),
  GDAX("org.knowm.xchange.gdax.GDAXExchange"),
  KRAKEN("org.knowm.xchange.kraken.KrakenExchange"),
  OKCOIN("org.knowm.xchange.okcoin.OkCoinExchange"),
  LAKEBTC("org.knowm.xchange.lakebtc.LakeBTCExchange"),
  RIPPLE("org.knowm.xchange.ripple.RippleExchange"),
  TAURUS("org.knowm.xchange.taurus.TaurusExchange"),
  THEROCK("org.knowm.xchange.therock.TheRockExchange"),
  POLONIEX("org.knowm.xchange.poloniex.PoloniexExchange");

  private final String exchangeClassName;

  Exchange(String exchangeClassName) {
    this.exchangeClassName = exchangeClassName;
  }

  public String getExchangeClassName() {
    return exchangeClassName;
  }

}
