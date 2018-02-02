package com.example.pc.khaledwaleedshopping.Support.design;

/**
 * Created by ahmed on 3/25/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.example.pc.khaledwaleedshopping.R;

public class CustomeDialogMassege extends Dialog {


    public CustomeDialogMassege(Context context, String text) {
        super(context);
        this.setContentView(R.layout.dialog_messege);
        CustomeTextView customeTextView = (CustomeTextView) this.findViewById(R.id.text);
        customeTextView.setText(text);
        CustomeButton customeButton = (CustomeButton) this.findViewById(R.id.button);
        customeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomeDialogMassege.this.cancel();
            }
        });
        this.show();
    }


}
