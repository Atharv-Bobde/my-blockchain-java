package my_blockchain;

import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey reciver;
	public String parentTransactionId;
	public float value;
	
	public TransactionOutput(PublicKey reciver,String parentTransactionId,float value)
	{
		this.reciver=reciver;
		this.parentTransactionId=parentTransactionId;
		this.value=value;
		this.id=StringUtil.applySHA32(parentTransactionId+StringUtil.getStringFromKey(reciver)+Float.toString(value));
		
	}
	
	public boolean isMine(PublicKey key)
	{
		return (reciver==key);
	}
	
}
