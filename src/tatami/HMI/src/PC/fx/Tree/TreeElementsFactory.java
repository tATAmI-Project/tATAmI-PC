package tatami.HMI.src.PC.fx.Tree;

import java.io.File;
import java.util.HashMap;

import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;
import tatami.HMI.src.PC.fx.data.PlatformDescription;
import tatami.HMI.src.PC.fx.data.ProjectData;

public class TreeElementsFactory extends TreeTableCell<ITreeNode, String> {
     
    private static final ImageView PROJECT_ICON = new ImageView(new File("res/icons/project-icon.png").toURI().toString());
    private static final ImageView PLATFORM_ICON = new ImageView(new File("res/icons/platform-icon.png").toURI().toString());
    private static final ImageView ALL_PLATFORMS_ICON = new ImageView(new File("res/icons/all-platforms-icon.png").toURI().toString());
    
    private static HashMap<NodeType, ImageView> mIcons;
    
    public enum NodeType{PROJECT_NODE, ALL_PLATFORMS_NODE, PLATFORM_NODE, EMPTY_NODE}
    
    public TreeElementsFactory(){
        mIcons = new HashMap<NodeType, ImageView>();
        mIcons.put(NodeType.PROJECT_NODE, PROJECT_ICON);
        mIcons.put(NodeType.PLATFORM_NODE, PLATFORM_ICON);
        mIcons.put(NodeType.ALL_PLATFORMS_NODE, ALL_PLATFORMS_ICON);
    }
    
    private NodeType getType(ITreeNode node){
        if(node instanceof ProjectData)
            return NodeType.PROJECT_NODE;
        if(node instanceof PlatformDescription)
            return NodeType.PLATFORM_NODE;
        if(node instanceof PlatformDescription.AllPlatformsDescription)
            return NodeType.ALL_PLATFORMS_NODE;
        return NodeType.EMPTY_NODE;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? null : item);
        setGraphic(empty ? null : mIcons.get(getType(getTreeTableRow().getItem())));
    }
}
