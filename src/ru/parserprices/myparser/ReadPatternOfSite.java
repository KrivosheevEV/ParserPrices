package ru.parserprices.myparser;

import ru.parserprices.myparser.interfaces.ReadPatternOfSite_Intarface;

/**
 * Created by vnc on 5/21/16.
 */
public class ReadPatternOfSite implements ReadPatternOfSite_Intarface{

    private String siteAdress;
    private String nameParserOfSite;

    public static void main2(String[] args) {

    }

    @Override
    public void setSiteAdress(String siteAdress) {
        this.siteAdress = siteAdress;
    }

    @Override
    public void setNameParserOfSite(String nameParserOfSite) {
        this.nameParserOfSite = nameParserOfSite;

    }
}
