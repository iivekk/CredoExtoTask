package ivan.franjkovic.credoextotask.adapter;

import java.util.List;

import ivan.franjkovic.credoextotask.db.Element;

public interface OnElementListChangedListener {

    void onSwipeListChanged(List<Integer> ids, List<Element> elements);

    void onElementRemove(Element element, List<Element> elements);
}
