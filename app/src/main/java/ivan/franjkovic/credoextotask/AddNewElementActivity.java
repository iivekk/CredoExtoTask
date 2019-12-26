package ivan.franjkovic.credoextotask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ivan.franjkovic.credoextotask.db.Element;
import ivan.franjkovic.credoextotask.dialogs.DatePickerFragment;
import ivan.franjkovic.credoextotask.dialogs.TimePickerFragment;

public class AddNewElementActivity extends AppCompatActivity {

    public static final String ELEMENT_OBJECT = "element_object";

    @BindView(R.id.et_name)
    EditText etName;

    @BindView(R.id.et_start)
    EditText etStart;

    @BindView(R.id.et_end)
    EditText etEnd;

    @BindView(R.id.et_tag)
    EditText etTag;

    @OnClick(R.id.btn_add)
    public void btnAdd() {
        Intent i = new Intent();
        String name = etName.getText().toString();
        long start = convertTimeStringToLong(etStart.getText().toString());
        long end = convertDateStringToLong(etEnd.getText().toString());
        String tag = etTag.getText().toString();
        i.putExtra(ELEMENT_OBJECT, new Element(name, start, end, tag));
        setResult(RESULT_OK, i);
        finish();
    }

    @OnClick(R.id.btn_cancel)
    public void btnCancel() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_element);

        ButterKnife.bind(this);

        etStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    TimePickerFragment tpf = new TimePickerFragment();
                    tpf.show(getSupportFragmentManager(), "Time picker");
                }
            }
        });

        etEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    DatePickerFragment dpf = new DatePickerFragment();
                    dpf.show(getSupportFragmentManager(), "Date picker");
                }
            }
        });
    }

    public void setTimeText(String text) {
        etStart.setText(text);
        etEnd.requestFocus();
    }

    public void setDateText(String text) {
        etEnd.setText(text);
        etTag.requestFocus();
    }

    public static long convertDateStringToLong(String s) {
        try {
            Date date = new SimpleDateFormat("dd.MM.yyyy.").parse(s);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static long convertTimeStringToLong(String s) {
        try {
            Date date = new SimpleDateFormat("HH:mm").parse(s);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }
}
