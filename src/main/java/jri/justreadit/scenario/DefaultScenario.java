package jri.justreadit.scenario;

import jri.justreadit.JRIScene;
import x.XApp;
import x.XScenario;

public class DefaultScenario extends XScenario {
    // singleton pattern
    private static DefaultScenario mSingleton = null;

    public static DefaultScenario getSingleton() {
        assert (DefaultScenario.mSingleton != null);
        return mSingleton;
    }

    public static DefaultScenario createSingleton(XApp app) {
        assert (DefaultScenario.mSingleton == null);
        DefaultScenario.mSingleton = new DefaultScenario(app);
        return DefaultScenario.mSingleton;
    }

    private DefaultScenario(XApp app) {
        super(app);
    }

    @Override
    protected void addScenes() {
        this.addScene(ReadyScene.createSingleton(this));
    }

    public static class ReadyScene extends JRIScene {
        //  singleton
        private static ReadyScene mSingleton = null;

        public static ReadyScene getSingleton() {
            assert (ReadyScene.mSingleton != null);
            return ReadyScene.mSingleton;
        }

        public static ReadyScene createSingleton(XScenario scenario) {
            assert (ReadyScene.mSingleton == null);
            ReadyScene.mSingleton = new ReadyScene(scenario);
            return ReadyScene.mSingleton;
        }

        private ReadyScene(XScenario scenario) {
            super(scenario);
        }

        @Override
        public void getReady() {
//      this.mScenario.getApp().getPageControllerMgr().switchTo(PageController.PAGE_CONTROLLER_NAME);
        }

        @Override
        public void wrapUp() {
        }
    }
}
