package ru.mera.imozerov.mailcheckerapplication;

import ru.mera.imozerov.mailcheckerapplication.NewMailListener;

interface MailCheckerApi {
    void login(String username, String password);
    boolean isLoggedIn();
    void addListener(NewMailListener listener);
    void removeListener(NewMailListener listener);
}
