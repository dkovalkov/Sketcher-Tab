package org.sketchertab;

import java.util.ArrayList;
import java.util.List;

/**
 * Paint history. Undo system inspired by Pinta graphical editor.
 */
public class DocumentHistory {
    private static final DocumentHistory INSTANCE = new DocumentHistory();

    private List<HistoryItem> historyList = new ArrayList<HistoryItem>();
    private int historyPointer = -1;

    private DocumentHistory() {
    }

    public static DocumentHistory getInstance() {
        return INSTANCE;
    }

    public void pushNewItem(HistoryItem historyItem) {
//      remove redos
        historyList.add(historyItem);
        historyPointer = historyList.size() - 1;
    }

    public void undo() {
        if (historyPointer < 0)
            return;

        HistoryItem historyItem = historyList.get(historyPointer);
        historyItem.undo();
        historyItem.setState(HistoryItem.HistoryItemState.REDO);
        historyPointer -= 1;
    }

    public void redo() {
        if (historyPointer >= historyList.size() - 1)
            return;

        historyPointer += 1;
        HistoryItem historyItem = historyList.get(historyPointer);
        historyItem.redo();
        historyItem.setState(HistoryItem.HistoryItemState.UNDO);
    }

    public void clear() {
        historyList.clear();
        historyPointer = -1;
    }
}
