package tatami.core.platforms;

import tatami.simulation.PlatformLoader;
import tatami.websocket.WebSocketMessagingPlatform;

public class PlatformFactory {
    public enum PlatformType{WEBSOCKET};
    
    private static PlatformFactory singleton = null;
    
    public static PlatformFactory getInst(){
        if(singleton == null)
            singleton = new PlatformFactory();
        return singleton;
    }
    
    public PlatformLoader newInst(String platformName){
        if(platformName.equals("websocket"))
            return new WebSocketMessagingPlatform();
        
        return null;
    }
}
