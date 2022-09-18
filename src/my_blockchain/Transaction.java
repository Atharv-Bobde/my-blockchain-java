package my_blockchain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
	public PublicKey sender;
	public PublicKey reciver;
	public String transactionId;
	public float value;
	public byte[] signature;
	private static int sequence=0;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	//constructor
	public Transaction(PublicKey from,PublicKey to,float value,ArrayList<TransactionInput> inputs) {
		this.sender=from;
		this.reciver=to;
		this.value=value;
		this.inputs=inputs;
	}
	//processing Transaction
	public boolean processTransaction() {
		
		//checkSignature
		if(!verifySignature())
		{
			System.out.println("Failed to verify trasaction 's signature");
			return false;
		}
		//get transaction inputs(unspent)
		for(TransactionInput i:inputs)
		{
			i.UTXO=my_blockchain.UTXOs.get(i.transactionOutputId);
		}
		
		//check if transaction valid
		if(getInputValues()<this.value)
		{
			System.out.println("Insufficient Inputs");
			return false;
		}
		
		//generate transaction outputs
		float diff=getInputValues()-value;
		this.transactionId=calculateHash();
		TransactionOutput o1= new TransactionOutput(this.reciver,this.transactionId,this.value);
		TransactionOutput o2= new TransactionOutput(this.sender,this.transactionId,diff);
		outputs.add(o1);
		outputs.add(o2);
		
		//add outputs to unspent list
		for(TransactionOutput o:outputs)
		{
			my_blockchain.UTXOs.put(o.id,o);
		}
		
		//remove used inputs from unspent list
		for(TransactionInput i:inputs)
		{
			if(i.UTXO==null)continue;
			my_blockchain.UTXOs.remove(i.UTXO.id);
		}
			
		return true;
	}
	//calculate transactionID
	public  String calculateHash() {
		sequence++;
		return StringUtil.applySHA32(StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciver)+Float.toString(value)+Integer.toString(sequence));
	}
	//signature
	public void generateSignature(PrivateKey privateKey) {
		this.signature=StringUtil.applyECDSASig(privateKey,StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciver)+Float.toString(value));
	}
	//verifying transaction
	public boolean verifySignature() {
		return StringUtil.verifyECDSASig(sender,StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciver)+Float.toString(value),signature);
	}
	//get input values
	public float getInputValues() {
		float value=0;
		for(TransactionInput i:inputs)
		{
			if(i.UTXO!=null) //if transaction found add value
			{
				value+=i.UTXO.value;
			}
		}
		return value;
	}
	//get output values
	public float getOutputValues() {
		float value=0;
		for(TransactionOutput o:outputs)
		{
			value+=o.value;
		}
		return value;
	}
	
}
