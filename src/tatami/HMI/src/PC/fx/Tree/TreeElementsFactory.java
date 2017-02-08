package tatami.HMI.src.PC.fx.Tree;

import java.io.File;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tatami.HMI.src.PC.fx.MenuItemsController;
import tatami.HMI.src.PC.fx.data.AgentDescription;
import tatami.HMI.src.PC.fx.data.ContainerDescription;
import tatami.HMI.src.PC.fx.data.PlatformDescription;
import tatami.HMI.src.PC.fx.data.ProjectDescription;

public class TreeElementsFactory extends TreeTableCell<ITreeNode, String> {
     
    private static final ImageView PROJECT_ICON = new ImageView(new File("res/icons/project-icon.png").toURI().toString());
    private static final ImageView PLATFORM_ICON = new ImageView(new File("res/icons/platform-icon.png").toURI().toString());
    private static final ImageView ALL_PLATFORMS_ICON = new ImageView(new File("res/icons/all-platforms-icon.png").toURI().toString());
    private static final ImageView CONTAINER_ICON = new ImageView(new File("res/icons/container-icon.png").toURI().toString());
    private static final ImageView ALL_CONTAINERS_ICON = new ImageView(new File("res/icons/all-containers-icon.png").toURI().toString());
    private static final ImageView AGENT_ICON = new ImageView(new File("res/icons/person-2x.png").toURI().toString());
    
    MenuItemsController mParent;
    
    static ContextMenu platformDescriptionContextMenu;
    
    private static HashMap<NodeType, ImageView> mIcons;
    
    public enum NodeType{PROJECT_NODE, ALL_PLATFORMS_NODE, PLATFORM_NODE, CONTAINER_NODE, ALL_CONTAINERS_NODE, AGENT_NODE, EMPTY_NODE}
    
    public TreeElementsFactory(MenuItemsController parent) {
        mParent = parent;
        if (platformDescriptionContextMenu == null) {
            platformDescriptionContextMenu = new ContextMenu();
        }
        if (mIcons == null) {
            mIcons = new HashMap<NodeType, ImageView>();
            mIcons.put(NodeType.PROJECT_NODE, PROJECT_ICON);
            mIcons.put(NodeType.PLATFORM_NODE, PLATFORM_ICON);
            mIcons.put(NodeType.ALL_PLATFORMS_NODE, ALL_PLATFORMS_ICON);
            mIcons.put(NodeType.CONTAINER_NODE, CONTAINER_ICON);
            mIcons.put(NodeType.ALL_CONTAINERS_NODE, ALL_CONTAINERS_ICON);
            mIcons.put(NodeType.AGENT_NODE, AGENT_ICON);
        }
    }
    
    private void initPlatformContextMenu(){
        platformDescriptionContextMenu = new ContextMenu();
        platformDescriptionContextMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    mParent.onStartPlatform(getTreeTableRow().getItem().toString());
                    System.out.println("consuming right release button in cm filter");
                    event.consume();
                    platformDescriptionContextMenu.hide();
                }
            }
        });
        
        MenuItem menuItem1 = new MenuItem("Start Platform");
        MenuItem menuItem2 = new MenuItem("StopPlatform");

        platformDescriptionContextMenu.getItems().addAll(menuItem1, menuItem2);
    }
    
    private NodeType getType(ITreeNode node){
        if(node instanceof ProjectDescription)
            return NodeType.PROJECT_NODE;
        if(node instanceof PlatformDescription)
            return NodeType.PLATFORM_NODE;
        if(node instanceof PlatformDescription.AllPlatformsDescription)
            return NodeType.ALL_PLATFORMS_NODE;
        if(node instanceof ContainerDescription)
            return NodeType.CONTAINER_NODE;
        if(node instanceof ContainerDescription.AllContainersDescription)
            return NodeType.ALL_CONTAINERS_NODE;
        if(node instanceof AgentDescription)
            return NodeType.AGENT_NODE;
        
        return NodeType.EMPTY_NODE;
    }
    
    private boolean isType(NodeType node){
        return node == getType(getTreeTableRow().getItem());
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        if(isType(NodeType.PLATFORM_NODE)){
            initPlatformContextMenu();
        }
        super.updateItem(item, empty);
        setText(empty ? null : item);
        setGraphic(empty ? null : mIcons.get(getType(getTreeTableRow().getItem())));
        setContextMenu(platformDescriptionContextMenu);
    }
}
