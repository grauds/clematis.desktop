package jworkspace.ui;

/**
 * @author Anton Troshin
 */
public class ChildTestShell extends TestShell {


    private TestShell testShell = new TestShell();

    public TestShell getTestShell() {
        return testShell;
    }

    public void setTestShell(TestShell testShell) {
        this.testShell = testShell;
    }
}
