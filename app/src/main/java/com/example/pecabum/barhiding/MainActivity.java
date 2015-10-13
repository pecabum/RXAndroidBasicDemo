package com.example.pecabum.barhiding;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Observable<OnTextChangeEvent> userNameText =
//                WidgetObservable.text((EditText) findViewById(R.id.etUsername));
//        userNameText.filter(new Func1<OnTextChangeEvent, Boolean>() {
//            @Override
//            public Boolean call(OnTextChangeEvent onTextChangeEvent) {
//                return onTextChangeEvent.text().toString().length() > 5;
//            }
//        }).subscribe(new Action1<OnTextChangeEvent>() {
//            @Override
//            public void call(OnTextChangeEvent onTextChangeEvent) {
//                Log.d("[Rx]", onTextChangeEvent.text().toString());
//            }
//        });

        final Pattern emailPattern = Pattern.compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"); // ..... [1]

        final EditText unameEdit = (EditText) findViewById(R.id.etUsername);
        final EditText emailEdit = (EditText) findViewById(R.id.etEmail);
        final Button regButton = (Button) findViewById(R.id.btnRegister);
        final Observable<Boolean> emailValid = WidgetObservable.text(emailEdit)
                .map(new Func1<OnTextChangeEvent, Boolean>() {
                    @Override
                    public Boolean call(OnTextChangeEvent onTextChangeEvent) {
                        return emailPattern.matcher(onTextChangeEvent.text().toString()).matches();
                    }
                });
        emailValid
                .distinctUntilChanged()
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.d("DoOnNext", "Email validation: " + aBoolean);
                    }
                })
                .map(new Func1<Boolean, Integer>() {
                    @Override
                    public Integer call(Boolean s) {
                        return s ? Color.BLACK : Color.RED;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer color) {
                        emailEdit.setTextColor(color);
                    }
                });

        final Observable<Boolean> unameValid = WidgetObservable.text(unameEdit)
                .map(new Func1<OnTextChangeEvent, Boolean>() {
                    @Override
                    public Boolean call(OnTextChangeEvent onTextChangeEvent) {
                        return onTextChangeEvent.text().toString().length() > 6;
                    }
                });

        unameValid
                .map(new Func1<Boolean, Integer>() {
                    @Override
                    public Integer call(Boolean s) {
                        return s ? Color.BLACK : Color.RED;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer color) {
                        unameEdit.setTextColor(color);
                    }
                });


        Observable
                .combineLatest(emailValid, unameValid, new Func2<Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean, Boolean aBoolean2) {
                        return aBoolean && aBoolean2;
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        regButton.setEnabled(aBoolean);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
