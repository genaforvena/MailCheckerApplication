package ru.mera.imozerov.mailcheckerapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;

/**
 * Created by imozerov on 04.01.2015.
 */
public class EmailListAdapter extends ArrayAdapter<Email> {
    private static final String TAG = EmailListAdapter.class.getName();

    public EmailListAdapter(Context context, int resource, List<Email> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int aPosition, View aConvertView, ViewGroup aParent) {
        Email email = getItem(aPosition);

        if (aConvertView == null) {
            aConvertView = LayoutInflater.from(getContext()).inflate(R.layout.email_row, aParent, false);
        }

        TextView senderTextView = (TextView) aConvertView.findViewById(R.id.email_row_sender);
        TextView sentTimeTextView = (TextView) aConvertView.findViewById(R.id.email_row_sent_time);
        TextView subjectTextView = (TextView) aConvertView.findViewById(R.id.email_row_subject);

        senderTextView.setText(email.getSenderEmail());
        sentTimeTextView.setText(email.getSentDate().toString());
        subjectTextView.setText(email.getSubject());
        return aConvertView;
    }

}
