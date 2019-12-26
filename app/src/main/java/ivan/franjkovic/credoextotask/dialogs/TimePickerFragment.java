package ivan.franjkovic.credoextotask.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import ivan.franjkovic.credoextotask.AddNewElementActivity;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimeDateSelectedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, hour, minute, true);
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if (getActivity() instanceof AddNewElementActivity) {
            AddNewElementActivity activity = (AddNewElementActivity) getActivity();
            activity.setTimeText(timeFormat(i, i1));
        } else {
            listener.onTimeSelected(timeFormat(i, i1));
        }
    }

    private String timeFormat(int hour, int minute) {
        String h = "";
        String m = "";
        if (hour < 10) {
            h = "0" + hour;
        } else {
            h = hour + "";
        }
        if (minute < 10) {
            m = "0" + minute;
        } else {
            m = minute + "";
        }
        return h + ":" + m;
    }

    public void setTimeListener(OnTimeDateSelectedListener listener) {
        this.listener = listener;
    }
}
