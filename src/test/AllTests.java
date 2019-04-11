package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ToolBoxTest.class, 
				TFIDFCalculatorTest.class, 
				TokeniserTest.class})

public class AllTests {

}
