package my_blockchain;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.*;


public class my_blockchain {
	public static ArrayList<Block> blockchain=new ArrayList<Block>();
	public static int difficulty=3;
	public static HashMap<String,TransactionOutput> UTXOs=new HashMap<String,TransactionOutput>();
	public static Transaction genesisTransaction;
	public static boolean isChainValid() {
		
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		for(int i=1;i<blockchain.size();i++) {
			String hashTarget = new String(new char[difficulty]).replace('\0', '0');
			
			if(!(blockchain.get(i).hash.equals(blockchain.get(i).createHash()))) {
				System.out.println("Hashes not equal data has changed");
				return false;
			}//check if current hash is equal to previous
			if(!(blockchain.get(i).prevHash.equals(blockchain.get(i-1).hash)))
			{
				System.out.println("Previous Hash not equal");
				return false;
			}	
				//check if previous hash is still same
			if(!(blockchain.get(i).hash.substring(0,difficulty).equals(hashTarget))) {
				System.out.println("block not mined");
				return false;//check if block is mined
			}
			
			
			for(Transaction j:blockchain.get(i).transactions)
			{
				if(!j.verifySignature())
				{
					System.out.println("Signature on Transaction "+j.transactionId+" is invalid");
					return false;
				}
				if(j.getInputValues()!=j.getOutputValues())
				{
					System.out.println("Inputs not equal to outputs in trasaction: "+j.transactionId);
					return false;
				}
				//check the inputs provided are valid
				for(TransactionInput input:j.inputs)
				{
					TransactionOutput tempOutput=tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput==null)
					{
						System.out.println("Referenced input in Transaction "+j.transactionId+" is missing or invalid");
						return false;
						//useful when someone tries to provide invalid inputs to transaction or change the provided inputs
					}
					if(tempOutput.value!=input.UTXO.value) {
						System.out.println("Input Invalid");//useful if someone tries to change the output/input value
						return false;
					}
					tempUTXOs.remove(input.transactionOutputId);//remove all input transactions from unspent
					
				}
				
				for(TransactionOutput o:j.outputs)
				{
					tempUTXOs.put(o.id,o);
					
				}
			
			}
		}
		return true;
	}

	public static void main(String[] args) {
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		Wallet walletA=new Wallet();
		Wallet walletB=new Wallet();
		Wallet coinBase=new Wallet();
		
//		System.out.println("Public key wallet A: "+StringUtil.getStringFromKey(walletA.publicKey));
		
		//creating genesis block start
		genesisTransaction=new Transaction(coinBase.publicKey,walletA.publicKey,100f,null);
		genesisTransaction.generateSignature(coinBase.privateKey);
		genesisTransaction.transactionId="0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciver,genesisTransaction.transactionId,genesisTransaction.value));
		UTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0) );
		
		System.out.println("Creating and mining genesis block...");
		Block genesisBlock=new Block("0");
		genesisBlock.addtransaction(genesisTransaction);
		
		addBlock(genesisBlock);		
		//creating genesis block end
		
		//testing
		System.out.println("Wallet A balance: "+walletA.checkBalance());
		
		//transaction 1 start
		System.out.println("Wallet A sending 20 to Wallet B..");
		Transaction t1=walletA.sendFunds(walletB.publicKey, 20f);
		Block block1=new Block(genesisBlock.hash);
		block1.addtransaction(t1);
		//transaction 1 end
		
		
		System.out.println("wallet A balance: "+walletA.checkBalance());
		System.out.println("Wallet B balance: "+walletB.checkBalance());
		
//		transaction 2 start
		Transaction t2=walletA.sendFunds(walletB.publicKey, 20f);
		block1.addtransaction(t2);
		addBlock(block1);
//		transaction 2 end
		
		//results
		System.out.println("wallet A balance: "+walletA.checkBalance());
		System.out.println("Wallet B balance: "+walletB.checkBalance());
		block1.transactions.get(0).value=9;
		System.out.println("Blockchain valid: "+ isChainValid());
//		System.out.println("wallet A balance: "+walletA.checkBalance());
//		System.out.println("Wallet B balance: "+walletB.checkBalance());
//		blockchain.add(new Block("first block","0"));
//		blockchain.get(blockchain.size()-1).mineBlock(difficulty);
//
//		blockchain.add(new Block("second block",blockchain.get(blockchain.size()-1).hash));
//		blockchain.get(blockchain.size()-1).mineBlock(difficulty);
//		
//		blockchain.add(new Block("third block",blockchain.get(blockchain.size()-1).hash));
//		blockchain.get(blockchain.size()-1).mineBlock(difficulty);   
//		
//		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);		
//		
//		System.out.println(blockchainJson);
//		System.out.print("chain valid: "+isChainValid());
		
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}



}
