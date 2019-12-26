package ivan.franjkovic.credoextotask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
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

public class MainActivity extends AppCompatActivity implements OnElementListChangedListener, OnItemClickListener
        , OnElementUpdateListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private ElementListAdapter mAdapter;
    private ElementViewModel viewModel;
    private ItemTouchHelper itemTouchHelper;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static final String LIST_OF_SORTED_DATA_ID = "list_sorted_data_id";
    private static final String PREFERENCE_FILE = "preference_file";

    private static final int NEW_ELEMENT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mSharedPreferences = this.getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mAdapter = new ElementListAdapter(this, this, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                } else {
                    mAdapter.setElements(elements);
                }

                //test - for drawing lines

                /*Set<String> unique = new HashSet<String>();
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

                Toast.makeText(MainActivity.this, same + " - " + sameStrings.size(), Toast.LENGTH_SHORT).show();*/


            }
        });
    }

    @OnClick(R.id.fab)
    public void addNewElement() {
        startActivityForResult(new Intent(this, AddNewElementActivity.class), NEW_ELEMENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_ELEMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            Element e = data.getParcelableExtra(AddNewElementActivity.ELEMENT_OBJECT);
            if (e != null) {
                viewModel.insert(e);
            } else {
                Toast.makeText(this, "null object", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSwipeListChanged(List<Integer> ids) {
        saveToSharedPreferences(stringOfIntegerList(ids));
    }

    @Override
    public void onElementRemove(Element element) {
        viewModel.delete(element);
        refreshIdList(element.getId());
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

    // test for drawing lines
    /*private List<String> returnPositions(List<Element> elements, List<String> sameStrings) {
        List<String> positions = new ArrayList<>();
        for (String s : sameStrings) {
            List<Integer> integers = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getTag().equals(s)) {
                    integers.add(i);
                }
            }
            Collections.sort(integers);
            positions.add(new String(integers.get(0) + "x" + integers.get(integers.size() - 1)));
        }
        return positions;
    }*/

}
