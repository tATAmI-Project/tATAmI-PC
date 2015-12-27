package tatami.websocket;

import java.util.ArrayList;

public class OutputComplexMessageTokenizer {

	int mReceivedCount = 0;
	
	ArrayList<String> buffer;
	
	int mSize = 0;
	
	public OutputComplexMessageTokenizer(){
		buffer = new ArrayList<String>();
	}
	
	public void addNewMessage(String segment){
		
		int index = Integer.parseInt(segment.substring(0, segment.indexOf("|")));
		
		segment = segment.substring(segment.indexOf("|") + 1);
		
		int size = Integer.parseInt(segment.substring(0, segment.indexOf("|")));
		
		segment = segment.substring(segment.indexOf("|") + 1);
		
		if(buffer.size() <= 0){
			for(int i = 0; i < size; ++i){
				buffer.add("");
			}
		}
		
		buffer.set(index, segment);
		mSize = size;
		
		mReceivedCount++;
	}
	
	public byte fromString(String x){
		return Byte.parseByte(x);
	}
	
	
	public boolean allMessagereceived(){
		if (mSize == 0)
			return false;
		
		return mReceivedCount == mSize;
	}
	
	public byte[] returnObj(){
		String finalBuffer = new String();
		
		for(int i = 0; i < buffer.size(); ++i){
			finalBuffer += buffer.get(i);
		}
		
		System.out.println(finalBuffer);
		System.out.println(finalBuffer.length());
		
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		while(finalBuffer.length() > 0){
			int byteSize = Integer.valueOf(finalBuffer.substring(0, 1));
			
			byte b = Byte.valueOf(finalBuffer.substring(1, 1 + byteSize));
			
			bytes.add(b);
		}
		

		
		byte[] result = new byte[bytes.size()];
		
		for(int i = 0; i < bytes.size(); ++i){
			result[i] = bytes.get(i);
		}
		
		
		return result;
		
	}
}
