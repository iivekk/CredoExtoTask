package ivan.franjkovic.credoextotask.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ivan.franjkovic.credoextotask.db.Element;
import ivan.franjkovic.credoextotask.db.ElementRepository;

public class ElementViewModel extends AndroidViewModel {

    private ElementRepository mRepository;
    private LiveData<List<Element>> mAllElements;

    public ElementViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ElementRepository(application);
        mAllElements = mRepository.getAllElements();
    }

    public LiveData<List<Element>> getAllElements() {
        return mAllElements;
    }

    public void insert(Element element) {
        mRepository.insert(element);
    }

    public void delete(Element element) {
        mRepository.delete(element);
    }

    public void update(int id, String newName, long newStart, long newEnd, String newTag) {
        mRepository.update(id, newName, newStart, newEnd, newTag);
    }

}
