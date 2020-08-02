package btdex.markets;

import java.util.HashMap;

//import btdex.core.Globals;
import btdex.locale.Translation;

public class MarketBSV extends MarketCrypto {

	public String toString() {
		return "BSV";
	}
	
	@Override
	public long getID() {
		return MARKET_BSV;
	}
	
	@Override
	public void validate(HashMap<String, String> fields) throws Exception {
		super.validate(fields);
		
		String addr = fields.get(ADDRESS);
		
		if(!addr.startsWith("1"))
			throw new Exception(Translation.tr("mkt_invalid_address", addr, toString()));
		else if(!BTCAddrValidator.validate(addr, BTCAddrValidator.BTC_HEADERS))
			throw new Exception(Translation.tr("mkt_invalid_address", addr, toString()));
	}

}
