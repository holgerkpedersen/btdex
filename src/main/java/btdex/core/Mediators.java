package btdex.core;

import burst.kit.crypto.BurstCrypto;
import burst.kit.entity.BurstID;
import burst.kit.entity.BurstValue;

import java.util.Random;

public class Mediators {
    private BurstID[] mediators;
    private BurstValue[] mediatorBalances;
    
    private static final BurstValue MIN_TRT = BurstValue.fromPlanck(10000L * 1000000L);

    public Mediators(Boolean testnet) {
        String[] mediators = (testnet) ? Constants.MEDIATORS_TESTNET : Constants.MEDIATORS;

        this.mediators = convertStringToBurstID(mediators);
        mediatorBalances = new BurstValue[mediators.length];
    }

    private BurstID[] convertStringToBurstID (String[] md) {
        BurstCrypto BC = BurstCrypto.getInstance();
        int mediatorsCount = md.length;
        BurstID[] converted = new BurstID[mediatorsCount];
        for (int i = 0; i < mediatorsCount; i++) {
            converted[i] = BC.rsDecode(md[i]);
        }
        return converted;
    }

    public BurstID[] getMediators() {
        return mediators;
    }

    public BurstID[] getTwoRandomMediators() {
    	Globals g = Globals.getInstance();
        Random rand = new Random();
        BurstID[] randomMediators = new BurstID[2];
        
        randomMediators[0] = mediators[rand.nextInt(mediators.length)];
        while(randomMediators[0].getSignedLongId() == g.getAddress().getSignedLongId()) {
            // make sure we don't mediate our own contract
            randomMediators[1] = mediators[rand.nextInt(mediators.length)];
        }
        randomMediators[1] = mediators[rand.nextInt(mediators.length)];
        while(randomMediators[1] == randomMediators[0] ||
        		randomMediators[1].getSignedLongId() == g.getAddress().getSignedLongId()) {
            // make sure we have 2 different mediators and that we do not mediate our own contract
            randomMediators[1] = mediators[rand.nextInt(mediators.length)];
        }
        return randomMediators;
    }

    private boolean isMediatorAccepted(ContractState contract, long mediator) {
    	if(contract.getCreator().getSignedLongId() == mediator)
    		return false;
    	
    	for (int i = 0; i < mediators.length; i++) {
    		if(mediators[i].getSignedLongId() == mediator && mediatorBalances[i]!=null && mediatorBalances[i].compareTo(MIN_TRT) >= 0)
    			return true;
		}
        return false;
    }
    
    public void setMediatorBalance(int i, BurstValue value) {
    	mediatorBalances[i] = value;
    }
    
    public boolean isMediator(long id) {
    	for (BurstID m : mediators) {
            if(m.getSignedLongId() == id)
                return true;
        }
        return false;
    }
    
    public boolean areMediatorsAccepted(ContractState contract) {
        return isMediatorAccepted(contract, contract.getMediator1())
        		&& isMediatorAccepted(contract, contract.getMediator2());
    }
}
