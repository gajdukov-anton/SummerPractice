package com.example.user.myapplication;

import com.example.user.myapplication.Activity.MainActivity;
import com.example.user.myapplication.Objects.Book;
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

    @Test
    public void bookClassTest() {
        Book book = new Book();

        assertEquals(false, book.checkForAllFieldsAreFilled());
        book.name = "Metro 2033";
        book.year = "1999";
        book.description = "It is a good book";
        assertEquals(false, book.checkForAllFieldsAreFilled());
        book.link = "some link";
        book.authors = "Glukhovsky";
        assertEquals(true, book.checkForAllFieldsAreFilled());

    }


}