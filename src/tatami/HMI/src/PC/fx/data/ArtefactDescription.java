package tatami.HMI.src.PC.fx.data;

import tatami.HMI.src.PC.fx.Tree.ITreeNode;

public class ArtefactDescription implements ITreeNode {
    String mName;

    public static class AllArtefactDescription implements ITreeNode {

        public String toString() {
            return "Artefacts";
        }
    }

    public ArtefactDescription(String name) {
        mName = name;
    }

    public String toString() {
        return mName;
    }
}
