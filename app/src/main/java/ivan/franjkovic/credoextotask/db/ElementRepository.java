package ivan.franjkovic.credoextotask.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ElementRepository {

    private ElementDao mElementDao;
    private LiveData<List<Element>> mAllElements;

    public ElementRepository(Application application) {
        ElementRoomDatabase db = ElementRoomDatabase.getDatabase(application);
        mElementDao = db.elementDao();
        mAllElements = mElementDao.getAllElements();

    }

    public LiveData<List<Element>> getAllElements() {
        return mAllElements;
    }

    public void insert(Element element) {
        ElementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mElementDao.insert(element);
        });
    }

    public void delete(Element element) {
        ElementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mElementDao.delete(element);
        });
    }

    public void update(int id, String newName, long newStart, long newEnd, String newTag) {
        ElementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mElementDao.update(id, newName, newStart, newEnd, newTag);
        });
    }
}
