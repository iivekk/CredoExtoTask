package ivan.franjkovic.credoextotask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ivan.franjkovic.credoextotask.adapter.ElementListAdapter;
import ivan.franjkovic.credoextotask.adapter.OnElementListChangedListener;
import ivan.franjkovic.credoextotask.adapter.OnItemClickListener;
import ivan.franjkovic.credoextotask.adapter.SimpleItemTouchHelperAdapter;
import ivan.franjkovic.credoextotask.db.Element;
import ivan.franjkovic.credoextotask.db.ElementViewModel;
import ivan.franjkovic.credoextotask.dialogs.OnElementUpdateListener;
import ivan.franjkovic.credoextotask.dialogs.UpdateElementDialog;

import static ivan.franjkovic.credoextotask.tools.Const.INTENT_ACTION_DRAW;
import static ivan.franjkovic.credoextotask.tools.Const.ITEM_HEIGHT;
import static ivan.franjkovic.credoextotask.tools.Const.LIST_OF_SORTED_DATA_ID;
import static ivan.franjkovic.credoextotask.tools.Const.NEW_ELEMENT_REQUEST_CODE;
import static ivan.franjkovic.credoextotask.tools.Const.PREFERENCE_FILE;

public class MainActivity extends AppCompatActivity implements OnElementListChangedListener, OnItemClickListener
        , OnElementUpdateListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.test_image)
    ImageView image;

    @BindView(R.id.main_activity)
    ConstraintLayout layout;

    private ElementListAdapter mAdapter;
    private ElementViewModel viewModel;
    private ItemTouchHelper itemTouchHelper;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private BroadcastReceiver receiver;

    private List<Element> elementList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mSharedPreferences = this.getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mAdapter = new ElementListAdapter(this, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = (layout.getHeight() - 4) / 8;
                mEditor.putInt(ITEM_HEIGHT, height).apply();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperAdapter(mAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        viewModel = new ViewModelProvider(this).get(ElementViewModel.class);
        viewModel.getAllElements().observe(this, new Observer<List<Element>>() {
            @Override
            public void onChanged(List<Element> elements) {
                List<Element> sortedList = returnSortedList(elements, getFromSharedPreferences());
                if (sortedList.size() == elements.size()) {
                    mAdapter.setElements(sortedList);
                    elementList = sortedList;
                } else {
                    mAdapter.setElements(elements);
                    elementList = elements;
                }

                sendBroadcast(new Intent(INTENT_ACTION_DRAW));
            }
        });
    }

    @OnClick(R.id.fab)
    public void addNewActivity() {
        startActivityForResult(new Intent(this, AddNewElementActivity.class), NEW_ELEMENT_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReceieve();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_ELEMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            Element e = data.getParcelableExtra(AddNewElementActivity.ELEMENT_OBJECT);
            if (e != null) {
                viewModel.insert(e);
                elementList.add(e);
            } else {
                Toast.makeText(this, "null object", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSwipeListChanged(List<Integer> ids, List<Element> elements) {
        saveToSharedPreferences(stringOfIntegerList(ids));
        elementList = elements;
        sendBroadcast(new Intent(INTENT_ACTION_DRAW));
    }

    @Override
    public void onElementRemove(Element element, List<Element> elements) {
        viewModel.delete(element);
        refreshIdList(element.getId());
        elementList = elements;
    }

    @Override
    public void onItemClick(int position, String name, String time, String date, String tag) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("data", new String[]{String.valueOf(position), name, time, date, tag});
        UpdateElementDialog dialog = new UpdateElementDialog();
        dialog.setOnUpdateListener(MainActivity.this);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Update element");
    }

    @Override
    public void onUpdate(int elementId, String newName, long newTime, long newDate, String newTag) {
        viewModel.update(elementId, newName, newTime, newDate, newTag);
    }

    // return String value of Integer list with delimiter 'x'
    private String stringOfIntegerList(List<Integer> integers) {
        List<String> list = new ArrayList<>();
        for (Integer i : integers) {
            list.add(i.toString());
        }
        return TextUtils.join("x", list);
    }

    // return sorted list of Element by id list
    private List<Element> returnSortedList(List<Element> elements, String idList) {
        List<Element> sortedList = new ArrayList<>();
        String[] ids = null;
        if (idList != null) {
            ids = idList.split("x");
        }
        for (int i = 0; i < elements.size(); i++) {
            if (ids != null && i < ids.length) {
                if (isInteger(ids[i])) {
                    int j = Integer.valueOf(ids[i]);
                    for (Element e : elements) {
                        if (e.getId() == j) {
                            sortedList.add(e);
                        }
                    }
                }
            } else {
                sortedList.add(elements.get(i));
            }
        }
        return sortedList;
    }

    // checks if the String is an Integer value
    private boolean isInteger(String verifiedValue) {
        try {
            int j = Integer.parseInt(verifiedValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // refresh the IdList and saves to SharedPreferences
    private void refreshIdList(int id) {
        String oldIds = getFromSharedPreferences();
        if (oldIds != null) {
            String[] strings = oldIds.split("x");
            List<Integer> list = convertStringArrayToIntegerList(strings);
            removeFromList(list, id);
            saveToSharedPreferences(stringOfIntegerList(list));
        }
    }

    private void saveToSharedPreferences(String s) {
        mEditor.putString(LIST_OF_SORTED_DATA_ID, s);
        mEditor.apply();
    }

    private String getFromSharedPreferences() {
        return mSharedPreferences.getString(LIST_OF_SORTED_DATA_ID, null);
    }

    private List<Integer> convertStringArrayToIntegerList(String[] s) {
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            if (isInteger(s[i])) {
                integerList.add(Integer.valueOf(s[i]));
            }
        }
        return integerList;
    }

    private void removeFromList(List<Integer> list, int integer) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == integer) {
                list.remove(i);
            }
        }
    }

    private void getReceieve() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int width = recyclerView.getWidth();
                int range = recyclerView.computeVerticalScrollRange();
                drawLine(width, range);

            }
        };
        registerReceiver(receiver, new IntentFilter(INTENT_ACTION_DRAW));
    }

    private void unRegReceiver() {
        unregisterReceiver(receiver);
    }

    private List<String> sameStrings(List<Element> elements) {

        Set<String> unique = new HashSet<String>();
        for (Element e : elements) {
            if (!e.getTag().equals("")) {
                unique.add(e.getTag());
            }
        }

        List<String> sameStrings = new ArrayList<>(); // tags with duplicates
        int same = 0; // number of lines
        for (String s : unique) {
            int counter = 0;
            for (Element e : elements) {
                if (e.getTag().equals(s)) {
                    counter++;
                }
            }
            if (counter > 1) {
                same++;
                sameStrings.add(s);
            }
        }

        return sameStrings;
    }

    private List<String> returnPositions(List<Element> elements, List<String> sameStrings) {
        List<String> positions = new ArrayList<>();
        for (String s : sameStrings) {
            List<Integer> integers = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getTag().equals(s)) {
                    integers.add(i + 1);
                }
            }
            Collections.sort(integers);
            positions.add(new String(integers.get(0) + "x" + integers.get(integers.size() - 1)));
        }
        return positions;
    }

    private void drawLine(int w, int h) {
        List<String> sameStrings = sameStrings(elementList);
        List<String> positions = returnPositions(elementList, sameStrings);

        if (!(w > 0)) {
            w = 1;
        }
        if (!(h > 0)) {
            h = 1;
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);

        if (elementList.size() > 0) {
            int listSize = elementList.size();
            int item_height = h / listSize;
            int strokeWidth = 4;
            int offSet = item_height / 2;
            int space = 4;
            int line_x = w - offSet;

            for (int i = 0; i < positions.size(); i++) {
                String[] p = positions.get(i).split("x");
                int y_start = Integer.valueOf(p[0]) * item_height - offSet;
                int y_end = Integer.valueOf(p[1]) * item_height - offSet;
                canvas.drawLine(line_x, y_start, line_x, y_end, paint);
                line_x -= (strokeWidth + space);
            }
        }

        image.setImageBitmap(bitmap);
    }

}
