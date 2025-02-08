package ru.backend.academy.hometask4.util;


import com.ibm.icu.text.Transliterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransliterationUtil {

    private final Transliterator transliterator;
    public String transliterate(String input) {
        String latin = transliterator.transliterate(input);
        latin = latin.replaceAll(" ", "_");
        return latin;
    }

}
