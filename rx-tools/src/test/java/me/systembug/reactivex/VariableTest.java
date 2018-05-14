package me.systembug.reactivex;

import org.junit.Test;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import me.systembug.reactivex.Variable;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class VariableTest {
    @Test
    public void intValueChange() {


        Variable<Integer> at = new Variable<Integer>(10);

        at
                .asObservable()
                .subscribe(value -> {
                    assertEquals(5, value.intValue());
                });

        at.set(5);
    }

    @Test
    public void stringValueChange() {


        Variable<String> at = new Variable<String>("A");

        at
                .asObservable()
                .subscribe(value -> {
                    assertEquals("B", value.toString());
                });

        at.set("B");
    }
}