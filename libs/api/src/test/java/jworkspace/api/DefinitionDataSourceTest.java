package jworkspace.api;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import static jworkspace.api.DefinitionDataSource.EXPANDABLE_PROPERTY;
import static jworkspace.api.DefinitionDataSource.ICON_PROPERTY;
import static jworkspace.api.DefinitionDataSource.LABEL_PROPERTY;
import static jworkspace.api.DefinitionNode.CLOSED_ICON;
import static jworkspace.api.DefinitionNode.LEAF_ICON;
import static jworkspace.api.DefinitionNode.OPEN_ICON;

/**
 * Definition data source tests
 */
public class DefinitionDataSourceTest {

    public static final String APPLICATIONS = "Applications";

    private static final String NESTED_2_FOLDER = "Nested 2";

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeEach
    public void before() throws IOException {
        testFolder.create();
        File folder3 = testFolder.newFolder(APPLICATIONS, "Nested 1", NESTED_2_FOLDER, "Nested 3");
        File file = new File(folder3, "Test file 1");
        file.createNewFile();
    }

    @Test
    public void testDataSourceCreationAndSearch() {

        DefinitionDataSource definitionDataSource = new DefinitionDataSource(testFolder.getRoot());
        DefinitionNode nested2 = definitionDataSource.findNode(testFolder.getRoot().getName()
            + "/Applications/Nested 1/Nested 2"
        );

        // make sure we have found what we've been looking for
        Assertions.assertNotNull(nested2);
        Assertions.assertEquals(NESTED_2_FOLDER, nested2.getNodeName());
        Assertions.assertTrue(nested2.isExpandable());
    }

    @Test
    public void testDataSourceMethods() {

        DefinitionDataSource definitionDataSource = new DefinitionDataSource(testFolder.getRoot());
        DefinitionNode nested2 = definitionDataSource.findNode(testFolder.getRoot().getName()
            + "/Applications/Nested 1/Nested 2"
        );

        // test datasource methods
        Assertions.assertTrue(definitionDataSource.isExpandable(nested2));
        Assertions.assertEquals(CLOSED_ICON, definitionDataSource.getIcon(nested2, false));
        Assertions.assertEquals(OPEN_ICON, definitionDataSource.getIcon(nested2, true));
        Assertions.assertEquals(NESTED_2_FOLDER, definitionDataSource.getLabel(nested2));
    }

    @Test
    public void testDataSourcePropertiesMethods() {

        DefinitionDataSource definitionDataSource = new DefinitionDataSource(testFolder.getRoot());
        DefinitionNode nested2 = definitionDataSource.findNode(testFolder.getRoot().getName()
            + "/Applications/Nested 1/Nested 2"
        );

        // test datasource properties methods
        Assertions.assertEquals(LEAF_ICON, definitionDataSource.getValueForProperty(nested2, ICON_PROPERTY));
        Assertions.assertNotEquals(CLOSED_ICON, definitionDataSource.getValueForProperty(nested2, ICON_PROPERTY));
        Assertions.assertNotEquals(OPEN_ICON, definitionDataSource.getValueForProperty(nested2, ICON_PROPERTY));

        Assertions.assertEquals(Boolean.TRUE, definitionDataSource.getValueForProperty(nested2, EXPANDABLE_PROPERTY));
        Assertions.assertEquals(NESTED_2_FOLDER, definitionDataSource.getValueForProperty(nested2, LABEL_PROPERTY));
    }

    @Test
    public void testLinkString() {
        String link = testFolder.getRoot().getName() + "/Applications/Nested 1/Nested 2";
        DefinitionDataSource definitionDataSource = new DefinitionDataSource(testFolder.getRoot());
        DefinitionNode nested2 = definitionDataSource.findNode(link);
        Assertions.assertEquals(link, nested2.getLinkString());
    }

    @Test
    public void testDataSourceTestFileSearch() {
        DefinitionDataSource definitionDataSource = new DefinitionDataSource(testFolder.getRoot());
        DefinitionNode file = definitionDataSource.findNode(testFolder.getRoot().getName()
            + "/Applications/Nested 1/Nested 2/Nested 3/Test file 1"
        );
        Assertions.assertNotNull(file);
        Assertions.assertEquals(0, definitionDataSource.getChildren(file).length);
    }

    @Test
    public void testDataSourceAddNotSavedNode() {
        DefinitionDataSource definitionDataSource = new DefinitionDataSource(testFolder.getRoot());
        DefinitionNode nested3 = definitionDataSource.findNode(testFolder.getRoot().getName()
            + "/Applications/Nested 1/Nested 2/Nested 3"
        );
        Assertions.assertNotNull(nested3);
        DefinitionNode nested4 = DefinitionNode.makeFolderNode(nested3, new File("Nested 4"));
        DefinitionNode nested4found = definitionDataSource.findNode(testFolder.getRoot().getName()
            + "/Applications/Nested 1/Nested 2/Nested 3/Nested 4"
        );
        Assertions.assertNotNull(nested4found);
        Assertions.assertEquals(nested4, nested4found);
    }
}
