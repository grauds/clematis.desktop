package jworkspace.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import com.hyperrealm.kiwi.event.tree.KTreeModelListener;

/**
 * Definition node tests
 */
public class DefinitionNodeTest {

    public static final String APPLICATIONS = "Applications";
    public static final String PATH = "Applications/TestApps/network/mac";
    private final TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeEach
    public void before() throws IOException {
        testFolder.create();
        for (String s : Arrays.asList(APPLICATIONS, "Libraries", "JVMs", "Plugins")) {
            testFolder.newFolder(s);
        }
    }

    @Test
    public void moveNodeTest() throws IOException {

        List<DefinitionNode> topFolders = getTopFolders();
        List<DefinitionNode> applications = getApplications(topFolders);
        DefinitionNode applicationNode = applications.get(0);

        DefinitionNode appToMove = DefinitionNode.makeFolderNode(applicationNode,
            testFolder.newFolder(APPLICATIONS, "AppToMove")
        );

        DefinitionNode newParent = DefinitionNode.makeFolderNode(applicationNode,
            testFolder.newFolder(APPLICATIONS, "NewParent")
        );

        // events
        KTreeModelListener treeModelListener = mock(KTreeModelListener.class);
        appToMove.addHierarchicalAssociationListener(treeModelListener); // node removed event here
        newParent.addHierarchicalAssociationListener(treeModelListener); // node added event here

        appToMove.move(newParent);

        Assertions.assertEquals(newParent, appToMove.getParent());
        verify(treeModelListener, times(1)).nodesRemoved(any());
        verify(treeModelListener, times(1)).nodesAdded(any());
        verify(treeModelListener, times(0)).nodesChanged(any());
    }

    @Test
    public void findNodeTest() throws IOException {

        List<DefinitionNode> topFolders = getTopFolders();
        List<DefinitionNode> applications = getApplications(topFolders);
        DefinitionNode applicationNode = applications.get(0);

        DefinitionNode testApps = DefinitionNode.makeFolderNode(applicationNode,
            testFolder.newFolder(APPLICATIONS, "TestApps")
        );

        DefinitionNode mac = testApps
            .add(DefinitionNode.makeFolderNode(
                testFolder.newFolder(APPLICATIONS, "TestApps", "network"))
            )
            .add(DefinitionNode.makeFolderNode(
                testFolder.newFolder(APPLICATIONS, "TestApps", "network", "mac"))
            );

        Assertions.assertEquals(1, applications.size());
        Assertions.assertEquals(PATH, mac.getLinkString());

        Assertions.assertEquals(1, applicationNode.getChildren().size());
        Assertions.assertTrue(applicationNode.getFile().isDirectory());
        Assertions.assertEquals(1, Objects.requireNonNull(applicationNode.getFile().listFiles()).length);

        Assertions.assertEquals(1, testApps.getChildren().size());
        Assertions.assertTrue(testApps.getFile().isDirectory());
        Assertions.assertEquals(1, Objects.requireNonNull(testApps.getFile().listFiles()).length);

        DefinitionNode found = new DefinitionDataSource(applicationNode).findNode(PATH);

        Assertions.assertEquals(mac, found);

        List<String> names = Arrays.stream(mac.getLinkPath().getPath()).map((path) -> {
            if (path instanceof DefinitionNode) {
                return ((DefinitionNode) path).getNodeName();
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toList();

        Assertions.assertEquals(PATH, String.join("/", names));

        mac.delete();
        found = new DefinitionDataSource(applicationNode).findNode(PATH);
        Assertions.assertNull(found);
    }

    private static List<DefinitionNode> getApplications(List<DefinitionNode> topFolders) {
        return topFolders
            .stream().filter((node) -> node.getNodeName().equals(APPLICATIONS)).toList();
    }

    private List<DefinitionNode> getTopFolders() {
        return Arrays
            .stream(Objects.requireNonNull(testFolder.getRoot().listFiles()))
            .map(DefinitionNode::makeFolderNode).toList();
    }

    @AfterEach
    public void after() {
        testFolder.delete();
    }
}
