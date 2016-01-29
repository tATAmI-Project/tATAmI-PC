package tatami.websocket;

import java.io.Serializable;
import java.util.ArrayList;

import net.xqhs.util.logging.Logger;

public class InputComplexMessageTokenizer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int BATCH_SIZE = 30;
	
	ArrayList<String> packs;
	
	int index = 0;
	
	int mCount = 0;
	
	public InputComplexMessageTokenizer(byte[] rawObject, Logger log){
		String rawString  = getByteArrayAsString(rawObject);
		packs = new ArrayList<String>();
		System.out.println(rawString);
		System.out.println("Input raw: " + rawString.length());
		
		while(rawString.length() > 0){
			//A - source, B - target
			packs.add(rawString.substring(0, Math.min(BATCH_SIZE, rawString.length())));
			if(rawString.length() <= BATCH_SIZE)
				break;
			rawString = rawString.substring(BATCH_SIZE);
		}
		
		mCount = packs.size();
		log.trace("Message splited into ", mCount);
		
	}
	
	private String getByteAsString(byte x){
		String out = String.valueOf(String.valueOf(x).length()) + String.valueOf(x);
		return out;
	}
	
	private String getByteArrayAsString(byte[] raw){
		String out = "";
		for(int i = 0; i < raw.length; ++i){
			out += getByteAsString(raw[i]);
		}
		return out;
	}
	
	public String getNextPackage(){
		String res = packs.get(index);
		if(index == 200)
			System.out.println("??????????????????????????????????");
		res = index + "|" + mCount + "|" + res;
		index++;
		return res;
	}
	
	public boolean hasMorePackages(){
		return index < mCount;
	}
	
	public void clear(){
		if(packs != null)
			packs.clear();
		index = 0;
		mCount = 0;
	}
}
