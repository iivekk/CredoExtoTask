package ivan.franjkovic.credoextotask.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "element_table")
public class Element implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "element_id")
    private int id;
    @ColumnInfo(name = "element_name")
    private String name;
    @ColumnInfo(name = "element_start")
    private long start;
    @ColumnInfo(name = "element_end")
    private long end;
    @ColumnInfo(name = "element_tag")
    private String tag;

    public Element(String name, long start, long end, String tag) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.name);
        parcel.writeLong(this.start);
        parcel.writeLong(this.end);
        parcel.writeString(this.tag);
    }

    public static final Parcelable.Creator<Element> CREATOR = new Parcelable.Creator<Element>() {
        @Override
        public Element createFromParcel(Parcel parcel) {
            return new Element(parcel);
        }

        @Override
        public Element[] newArray(int i) {
            return new Element[i];
        }
    };

    private Element(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        start = parcel.readLong();
        end = parcel.readLong();
        tag = parcel.readString();
    }
}
