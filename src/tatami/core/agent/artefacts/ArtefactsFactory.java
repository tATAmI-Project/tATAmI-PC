package tatami.core.agent.artefacts;

public class ArtefactsFactory {
    private static ArtefactsFactory singleton = null;

    public static ArtefactsFactory getInst() {
        if (singleton == null)
            singleton = new ArtefactsFactory();
        return singleton;
    }
    
    public ArtefactInterface newInst(ArtefactCreationData artefactCreationData){
        if(artefactCreationData.equals("user-input")){
            return new UserArtefact(artefactCreationData);
        }
        return null;
    }
}
