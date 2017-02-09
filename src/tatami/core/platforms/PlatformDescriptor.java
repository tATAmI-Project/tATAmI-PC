package tatami.core.platforms;

import java.util.HashMap;
import java.util.Iterator;

import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.util.platformUtils.PlatformUtils;

public class PlatformDescriptor {
    HashMap<String, String> mConfig;
    public PlatformDescriptor(XMLNode platformNode){
        mConfig = new HashMap<String, String>();
        Iterator<XMLNode> paramsIt = platformNode.getNodeIterator("parameter");
        
        while (paramsIt.hasNext()) {
            XMLNode node = paramsIt.next();
            mConfig.put(node.getAttributeValue("name"), node.getAttributeValue("value").toString());
        }
    }
    
    public String getName(){
        return mConfig.get("name");
    }
    
    public String getValue(String key){
        return mConfig.get(key);
    }
    
}
