package ru.mera.imozerov.mailcheckerapplication;

import java.util.List;

import ru.mera.imozerov.mailcheckerapplication.NewMailListener;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;

interface MailCheckerApi {
    boolean isLoggedIn();
    void forceRefresh();
    List<Parselable> getAllEmails();
    void addNewMailListener(NewMailListener listener);
    void removeNewMailListener(NewMailListener listener);
}
