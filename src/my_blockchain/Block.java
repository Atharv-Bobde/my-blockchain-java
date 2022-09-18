package my_blockchain;

import java.util.ArrayList;
import java.util.Date;
//Block Structure
public class Block {
	public String hash;
	public String merkleRoot;
	public ArrayList<Transaction> transactions=new ArrayList<Transaction>();
	public String prevHash;
	public long timeStamp;
	public int nonce;
	//block constructor
	public Block(String prevHash)
	{
		this.prevHash=prevHash;
		this.timeStamp=new Date().getTime();//time stamp of block
		this.hash=createHash();
	}
	public String createHash() {
		return StringUtil.applySHA32(this.prevHash+Long.toString(this.timeStamp)+Integer.toString(this.nonce)+this.merkleRoot);
	}
	
	public void mineBlock(int difficulty)
	{
		merkleRoot=StringUtil.getMerkleRoot(transactions);
		String target=new String(new char[difficulty]).replace('\0','0');
		while(!(this.hash.substring(0,difficulty).equals(target))) {
			nonce++;
			this.hash=createHash();
		}
		System.out.println("Block Mined! : " + hash);
	}
	
	//adding transactions
	public boolean addtransaction(Transaction transaction)
	{
		if(transaction==null)
			return false;
		if(prevHash!="0")
		{
			if(!transaction.processTransaction())
			{
				System.out.println("transaction Failed");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Sucessfully added to block!");
		return true;
	}
}
