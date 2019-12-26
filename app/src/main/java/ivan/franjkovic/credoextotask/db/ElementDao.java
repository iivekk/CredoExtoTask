package ivan.franjkovic.credoextotask.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Element element);

    @Delete
    void delete(Element element);

    @Query("UPDATE element_table SET element_name = :newName, element_start = :newStart" +
            ", element_end = :newEnd, element_tag = :newTag WHERE element_id = :id")
    void update(int id, String newName, long newStart, long newEnd, String newTag);

    @Query("SELECT * FROM element_table")
    LiveData<List<Element>> getAllElements();

}
