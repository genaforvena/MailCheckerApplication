package ru.mera.imozerov.mailcheckerapplication;

import ru.mera.imozerov.mailcheckerapplication.NewMailListener;

interface MailCheckerApi {
    boolean isLoggedIn();
    void addNewMailListener(NewMailListener listener);
    void removeNewMailListener(NewMailListener listener);
}
