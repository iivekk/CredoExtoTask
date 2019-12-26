package ivan.franjkovic.credoextotask.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivan.franjkovic.credoextotask.AddNewElementActivity;
import ivan.franjkovic.credoextotask.R;

public class UpdateElementDialog extends DialogFragment implements OnTimeDateSelectedListener {

    private OnElementUpdateListener listener;

    @BindView(R.id.et_update_name)
    EditText etUpdateName;

    @BindView(R.id.et_update_time)
    EditText etUpdateTime;

    @BindView(R.id.et_update_date)
    EditText etUpdateDate;

    @BindView(R.id.et_update_tag)
    EditText etUpdateTag;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.update_dialog_fragment, null);
        ButterKnife.bind(this, v);
        builder.setView(v);

        String[] data = getArguments().getStringArray("data");
        int elementId = Integer.valueOf(data[0]);
        setView(etUpdateName, data[1]);
        setView(etUpdateTime, data[2]);
        setView(etUpdateDate, data[3]);
        setView(etUpdateTag, data[4]);

        etUpdateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    TimePickerFragment tpf = new TimePickerFragment();
                    tpf.setTimeListener(UpdateElementDialog.this);
                    tpf.show(getParentFragmentManager(), "Time picker");
                }
            }
        });

        etUpdateDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    DatePickerFragment dpf = new DatePickerFragment();
                    dpf.setDateListener(UpdateElementDialog.this);
                    dpf.show(getParentFragmentManager(), "Date picker");
                }
            }
        });

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = etUpdateName.getText().toString();
                long time = AddNewElementActivity.convertTimeStringToLong(etUpdateTime.getText().toString());
                long date = AddNewElementActivity.convertDateStringToLong(etUpdateDate.getText().toString());
                String tag = etUpdateTag.getText().toString();
                listener.onUpdate(elementId, name, time, date, tag);
                dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void setView(EditText et, String text) {
        et.setText(text);
        et.setSelectAllOnFocus(true);
    }

    public void setTimeText(String text) {
        etUpdateTime.setText(text);
    }

    public void setDateText(String text) {
        etUpdateDate.setText(text);
    }

    @Override
    public void onTimeSelected(String time) {
        setTimeText(time);
    }

    @Override
    public void onDateSelected(String date) {
        setDateText(date);
    }

    public void setOnUpdateListener(OnElementUpdateListener listener) {
        this.listener = listener;
    }
}
