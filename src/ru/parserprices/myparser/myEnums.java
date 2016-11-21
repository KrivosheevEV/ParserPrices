package ru.parserprices.myparser;

enum addTo {
    logFile, Console, LogFileAndConsole
}
enum OS{
    Windows, Linux;
}

enum shopNames{
    empty,
    DNS,
    CITILINK,
    DOMO,
    CORPCENTRE,
    FENIXCOMP,
    AVITO,
    GIS,
    CORNKZ,
    LUCKSHOP,
    YOUTUBE,
    CERAMTRADE,
    FINDEMAILS
}

enum shopCities{
    empty,
    aznakaevo,
    bugulma,
    buzuluk,
    volsk,
    dimitrovgrad,
    zainsk,
    leninogorsk,
    novokuybishevsk,
    samara,
    syzran,
    chapaevsk,
    chistopol,
    tolyatti,
    nikolsk,
    barysh,
    ershov,
    sanktpeterburg,     // 812
    moscow,             // 495
    novosibirsk,        // 8383
    ekaterinburg,       // 8343
    nizhniynovgorod,    // 8831
    kazan,              // 8843
    chelyabinsk,        // 8351 -
    omsk,               // 83812
    rostovnadonu,       // 8863 -
    ufa,                // 8347 -
    krasnoyarsk,        // 8391 -
    perm,               // 8342 -
    voronezh,           // 8473 -
    volgograd           // 8844
}

enum shopCityCodes{
    empty,
    _855592,
    _85594,
    _35342,
    _84593,
    _84235,
    _85558,
    _85595,
    _84635,
    _846,
    _8464,
    _84639,
    _84342,
    _8482,
    _84165,
    _84253,
    _84564,
    _812,
    _495,
    _8383,
    _8343,
    _8831,
    _8843,
    _8351,
    _83812,
    _8863,
    _8347,
    _8391,
    _8342,
    _8473,
    _8844
}

enum extensionForExport{
    empty, txt, xml, xls, xlsx
}

public class myEnums {
}
