package my_blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public HashMap<String,TransactionOutput> UTXOs=new HashMap<String,TransactionOutput>();
	public Wallet() {
		generateKeyPair();
	}
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// generate a KeyPair with keygenerater
			keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
	        	KeyPair keyPair = keyGen.generateKeyPair();
	        	// Set the public and private keys from the keyPair
	        	privateKey = keyPair.getPrivate();
	        	publicKey = keyPair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	public float checkBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: my_blockchain.UTXOs.entrySet()){
			TransactionOutput UTXO=item.getValue();
		
			if(UTXO.isMine(publicKey))
			{
				UTXOs.put(UTXO.id, UTXO);
				total+=UTXO.value;
			}
		}
		return total;
	}
	public Transaction sendFunds(PublicKey _reciver,float value) {
		if(checkBalance()<value) {
			System.out.println("Not enough funds");
			return null;
		}
		float total =0;
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
			TransactionOutput UTXO=item.getValue();
			TransactionInput i=new TransactionInput(UTXO.id);
			total+=UTXO.value;
			inputs.add(i);
			if(total>value)
				break;
		}
		Transaction trans=new Transaction(this.publicKey,_reciver,value,inputs);
		trans.generateSignature(privateKey);
		for(TransactionInput input: inputs){
			UTXOs.remove(input.transactionOutputId);
		}
		return trans;
		
	}
	
}
