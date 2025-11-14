package com.example.notepad.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class SanitizerService {
    private static final Safelist SAFE = Safelist.basic()
            .addTags("p","br","pre","code")
            .addAttributes("a","href","title","rel")
            .addProtocols("a","href","http","https","mailto");

    public String clean(String html) {
        return Jsoup.clean(html == null ? "" : html, SAFE);
    }
}