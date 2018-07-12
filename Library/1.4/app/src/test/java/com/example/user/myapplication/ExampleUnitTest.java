package com.example.user.myapplication;

import com.example.user.myapplication.Activity.MainActivity;
import com.example.user.myapplication.Objects.Card;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        MainActivity activity = new MainActivity();
        List<Card> list = new ArrayList<>();
        
        activity.clearCards(list);
        assertEquals(0, list.size());

        list.add(new Card("LOL", "LOL", R.drawable.emma));
        list.add(new Card("LOL", "LOL", R.drawable.emma));
        list.add(new Card("LOL", "LOL", R.drawable.emma));

        activity.clearCards(list);
        assertEquals(0, list.size());

        list.add(new Card("LOL", "LOL", R.drawable.emma));
        assertEquals(1, list.size());
    }
}