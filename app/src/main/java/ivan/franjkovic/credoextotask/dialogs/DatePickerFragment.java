package ivan.franjkovic.credoextotask.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import ivan.franjkovic.credoextotask.AddNewElementActivity;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnTimeDateSelectedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
        return dpd;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (getActivity() instanceof AddNewElementActivity) {
            AddNewElementActivity activity = (AddNewElementActivity) getActivity();
            activity.setDateText(dateFormat(i2, i1 + 1, i));
        } else {
            listener.onDateSelected(dateFormat(i2, i1 + 1, i));
        }
    }

    private String dateFormat(int day, int month, int year) {
        String d = "";
        String m = "";
        if (day < 10) {
            d = "0" + day;
        } else {
            d = day + "";
        }
        if (month < 10) {
            m = "0" + month;
        } else {
            m = month + "";
        }
        return d + "." + m + "." + year + ".";
    }

    public void setDateListener(OnTimeDateSelectedListener listener) {
        this.listener = listener;
    }

}
