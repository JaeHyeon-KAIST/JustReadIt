package jri.justreadit;

import jri.justreadit.scenario.BookNoteScenario;
import jri.justreadit.scenario.FirstScenario;
import jri.justreadit.scenario.SecondScenario;
import x.XScenarioMgr;

public class JRIScenarioMgr extends XScenarioMgr {
    public JRIScenarioMgr(JRIApp jri) {
        super(jri);
    }

    @Override
    protected void addScenarios() {
        this.addScenario(FirstScenario.createSingleton(this.mApp));
        this.addScenario(SecondScenario.createSingleton(this.mApp));
        this.addScenario(BookNoteScenario.createSingleton(this.mApp));
    }

    @Override
    protected void setInitCurScene() {
        this.setCurScene(FirstScenario.FirstScene.getSingleton());
    }
}
