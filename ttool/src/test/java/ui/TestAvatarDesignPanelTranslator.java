package ui;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarConstant;
import avatartranslator.AvatarElement;
import avatartranslator.AvatarSpecification;

public class TestAvatarDesignPanelTranslator extends AbstractUITest {

  private static final String TAB_MAIN_DESIGN = "MainDesign";

  private static final String DIAG_WALLET = "Wallet";
  private static final String DIAG_COFFEE_MACHINE = "CoffeeMachine";
  private static final String DIAG_TEA_BUTTON = "TeaButton";
  private static final String DIAG_COFFEE_BUTTON = "CoffeeButton";

  private static final String TAB_AVATAR_DESIGN = "AVATAR Design";

  private static final String DIAG_PAGE_LOADER = "PageLoader";

  private static final String TAB_DESIGN_LOSSY = "Design_lossy";

  private static final String DIAG_ON_BOARD_COMMUNICATION = "OnBoardCommunication";

  private static final String TAB_DESIGN = "Design";

  private static final String DIAG_ALICE = "Alice";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    RESOURCES_DIR = getBaseResourcesDir() + "ui/diagram2avatar/";
    INPUT_DIR = RESOURCES_DIR + "input/";
    EXPECTED_CODE_DIR = RESOURCES_DIR + "expected/";
    ACTUAL_CODE_DIR = RESOURCES_DIR + "actual/";
  }

  public TestAvatarDesignPanelTranslator() {
    super();
    System.out.println("Test before super");
  }

  private AvatarSpecification translateDiagramToAvatar(final String diagramName) throws MalformedModelingException {
    final AvatarDesignPanel panel = (AvatarDesignPanel) mainGUI.getTURTLEPanel(diagramName);

    // Ensure the two static boolean constants are always instantiated before
    // resetting the static ID counter
    final Object dummy = AvatarConstant.TRUE;

    // Reset the ID to ensure IDs are the same as those of the expected
    // specifications
    AvatarElement.resetID();

    final AvatarDesignPanelTranslator translator = new AvatarDesignPanelTranslator(panel);

    final AvatarSpecification spec = translator
        .generateAvatarSpecification(panel.getAvatarBDPanel().getFullStateMachineOwnerList());

    if (!translator.getErrors().isEmpty()) {
      throw new MalformedModelingException();
    }

    return spec;
  }

  private void checkResult(final AvatarElement avatarElement, final String fileName) {
    checkResult(avatarElement.toString(), fileName);
  }

  private void testTranslateStateMachineDiagramToAvatarDisable(final String modelName, final String tabName,
      final String blockName, final String testName, final int... composToDisable) throws MalformedModelingException {
    openModel(modelName);

    final AvatarDesignPanel panel = (AvatarDesignPanel) mainGUI.getTURTLEPanel(tabName);

    final TDiagramPanel stateMachineDiagramPanel = findDiagramPanel(panel.getAvatarBDPanel(), blockName);

    /*
     * for ( final int compoId : composToDisable ) { final TGComponent
     * compoToDisable = findDiagComponent( compoId, stateMachineDiagramPanel );
     * compoToDisable.setEnabled( false ); }
     */

    final AvatarSpecification spec = translateDiagramToAvatar(tabName);
    final AvatarBlock block = spec.getBlockWithName(blockName);

    final String expectedSubDirName = composToDisable.length == 0 ? "" : "components_disabling";

    final String fileNameSuffix = composToDisable.length == 0 ? "" : "_Disable_";

    // checkResult( block.getStateMachine(), expectedSubDirName + File.separatorChar
    // + blockName + fileNameSuffix + testName );
  }

  private void testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(final String tabName,
      final String blockName, final String testName, final int... composToDisable) throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarDisable("CoffeeMachine_Avatar", tabName, blockName, testName,
        composToDisable);
  }

  private void testTranslateStateMachineDiagramToAvatarCoffeeMachine(final String tabName, final String blockName)
      throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(tabName, blockName, "", new int[0]);
  }

  private void testTranslateStateMachineDiagramToAvatarEbookReaderDisable(final String tabName, final String blockName,
      final String testName, final int... composToDisable) throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarDisable("ebook_reader_01", tabName, blockName, testName, composToDisable);
  }

  private void testTranslateStateMachineDiagramToAvatarFutureAirNavigationSystemDisable(final String tabName,
      final String blockName, final String testName, final int... composToDisable) throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarDisable("FutureAirNavigationSystem", tabName, blockName, testName,
        composToDisable);
  }

  private void testTranslateStateMachineDiagramToAvatarExampleFunctionCall(final String tabName, final String blockName,
      final String testName, final int... composToDisable) throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarDisable("exampleFunctionCall", tabName, blockName, testName,
        composToDisable);
  }

  // Translation from diagram to Avatar
  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineWallet() throws
   * MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachine( TAB_MAIN_DESIGN,
   * DIAG_WALLET ); }
   */

  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineCoffeeMachine() throws
   * MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachine( TAB_MAIN_DESIGN,
   * DIAG_COFFEE_MACHINE ); }
   */

  @Test
  public void testTranslateStateMachineDiagramToAvatarCoffeeMachineTeaButton() throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarCoffeeMachine(TAB_MAIN_DESIGN, DIAG_TEA_BUTTON);
  }

  @Test
  public void testTranslateStateMachineDiagramToAvatarCoffeeMachineCoffeeButton() throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarCoffeeMachine(TAB_MAIN_DESIGN, DIAG_COFFEE_BUTTON);
  }

  // Components disabling tests

  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarDisableExpireTimer() throws
   * MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarFutureAirNavigationSystemDisable(
   * TAB_DESIGN_LOSSY, DIAG_ON_BOARD_COMMUNICATION, "ExpireTimer", 3802 ); }
   */

  @Test
  public void testTranslateStateMachineDiagramToAvatarDisableLibraryFunctionCall() throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarExampleFunctionCall(TAB_DESIGN, DIAG_ALICE, "LibraryFunctionCall", 218);
  }

  /*
   * @Test public void testTranslateStateMachineDiagramToAvatarDisableRandom()
   * throws MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarEbookReaderDisable(
   * TAB_AVATAR_DESIGN, DIAG_PAGE_LOADER, "Random", 794 ); }
   */

  /*
   * @Test public void testTranslateToAvatarCoffeeMachineDisableReceiveSignal()
   * throws MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_WALLET, "ReceiveSignal", 820 ); }
   */

  /*
   * @Test public void testTranslateStateMachineDiagramToAvatarDisableResetTimer()
   * throws MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarFutureAirNavigationSystemDisable(
   * TAB_DESIGN_LOSSY, DIAG_ON_BOARD_COMMUNICATION, "ResetTimer", 3780 ); }
   */

  /*
   * @Test public void testTranslateToAvatarCoffeeMachineDisableSendSignal()
   * throws MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_WALLET, "SendSignal", 809 ); }
   */

  /*
   * @Test public void testTranslateStateMachineDiagramToAvatarDisableSetTimer()
   * throws MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarFutureAirNavigationSystemDisable(
   * TAB_DESIGN_LOSSY, DIAG_ON_BOARD_COMMUNICATION, "SetTimer", 3791 ); }
   */

  /*
   * @Test public void testTranslateStateMachineDiagramToAvatarDisableState()
   * throws MalformedModelingException {
   * 
   * // Testing disabling of states with the same name
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_WALLET, "State", 943 );
   * 
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_WALLET, "State", 902 );
   * 
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_WALLET, "State", 861 ); }
   */

  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarDisableCompositeState() throws
   * MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_COFFEE_MACHINE, "CompositeState", 1407 ); }
   */

  @Test
  public void testTranslateStateMachineDiagramToAvatarDisableTransitionInfoGuard() throws MalformedModelingException {
    testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(TAB_MAIN_DESIGN, DIAG_WALLET, "TransitionInfoGuard",
        789);
  }

  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarDisableTransitionInfoAfterActions()
   * throws MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_WALLET, "TransitionInfoAfterActions", 782 ); }
   */

  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarDisableTransitionInfoComputeFor()
   * throws MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_COFFEE_MACHINE, "TransitionInfoComputeFor", 985 ); }
   */

  // Tests for disabling several elements

  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarDisableCompoInOutTransitions() throws
   * MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_COFFEE_MACHINE, "CompoInOutTransitions", new int[]{
   * 1366, 964, 957 } ); }
   */

  /*
   * @Test public void
   * testTranslateStateMachineDiagramToAvatarDisableComponentBranch() throws
   * MalformedModelingException {
   * testTranslateStateMachineDiagramToAvatarCoffeeMachineDisable(
   * TAB_MAIN_DESIGN, DIAG_COFFEE_MACHINE, "ComponentBranch", new int[]{ 1072,
   * 1124, 1165, 1061 } ); }
   */
}
