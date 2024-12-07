package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import jri.justreadit.pageController.SecondPageController;
import x.XApp;
import x.XCmdToChangeScene;
import x.XScenario;

public class SecondScenario extends XScenario {
    // singleton pattern
    private static SecondScenario mSingleton = null;

    public static SecondScenario getSingleton() {
        assert (SecondScenario.mSingleton != null);
        return mSingleton;
    }

    public static SecondScenario createSingleton(XApp app) {
        assert (SecondScenario.mSingleton == null);
        SecondScenario.mSingleton = new SecondScenario(app);
        return SecondScenario.mSingleton;
    }

    private SecondScenario(XApp app) {
        super(app);
    }

    @Override
    protected void addScenes() {
        this.addScene(SecondScene.createSingleton(this));
    }

    public void dispatchReturnButtonPress() {
        System.out.println("Button Pressed in FirstScenario");
        if (this.getApp().getScenarioMgr().getCurScene() == SecondScene.mSingleton) {
            SecondScene.mSingleton.onReturnButtonPress();
        }
    }

    public static class SecondScene extends JRIScene {
        // singleton
        private static SecondScene mSingleton = null;

        public static SecondScene getSingleton() {
            assert (SecondScene.mSingleton != null);
            return SecondScene.mSingleton;
        }

        public static SecondScene createSingleton(XScenario scenario) {
            assert (SecondScene.mSingleton == null);
            SecondScene.mSingleton = new SecondScene(scenario);
            return SecondScene.mSingleton;
        }

        public void onReturnButtonPress() {
            XCmdToChangeScene.execute(this.mScenario.getApp(), this.mReturnScene, null);
        }

        private SecondScene(XScenario scenario) {
            super(scenario);
        }

        @Override
        public void getReady() {
            this.mScenario.getApp().getPageControllerMgr().switchTo(SecondPageController.PAGE_CONTROLLER_NAME);
        }

        @Override
        public void wrapUp() {

        }
    }
}
