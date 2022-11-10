package au.edu.jcu.cp3406.educationalapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void question_check() {
        GameActivity gameActivity = new GameActivity();
        assertEquals(gameActivity.answer, gameActivity.operand1 + gameActivity.operand2);
    }
}