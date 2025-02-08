package ru.backend.academy.hometask4.util;

import com.ibm.icu.text.Transliterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TransliterationUtilTest {

    String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";
    private TransliterationUtil transliterationUtil;

    @BeforeEach
    public void setUp() {
        transliterationUtil = new TransliterationUtil(Transliterator.getInstance(CYRILLIC_TO_LATIN));
    }

    @Test
    public void transliterate_latin_title_then_return_category_with_correct_url() {
        String title = "Test Title";
        String expectedUrl = "Test_Title";
        assertEquals(expectedUrl, transliterationUtil.transliterate(title));
    }

    @Test
    public void transliterate_cyrillic_title_then_return_category_with_correct_url() {
        String title = "кухонная техника";
        String expectedUrl = "kukhonnaya_tekhnika";
        assertEquals(expectedUrl, transliterationUtil.transliterate(title));
    }


}