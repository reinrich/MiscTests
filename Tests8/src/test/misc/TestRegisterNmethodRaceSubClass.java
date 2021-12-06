package test.misc;

public class TestRegisterNmethodRaceSubClass extends TestRegisterNmethodRace {
    public TestRegisterNmethodRaceSubClass(int val) {
        super(val);
    }

    @Override
    public int getInitVal() {
        return super.getInitVal()+1;
    }
}
