package org.sketchertab;

import java.util.ArrayList;
import java.util.List;

/**
 * Paint history. Undo system inspired by Pinta graphical editor.
 * todo: Keep undo level under memory footprint but at least 12 step.
 */
public class DocumentHistory {
    private static final DocumentHistory INSTANCE = new DocumentHistory();
    private static final int UNDO_LIMIT = 12;

    private List<HistoryItem> historyList = new ArrayList<HistoryItem>();
    private int historyPointer = -1;
    private boolean canUndo = false;
    private boolean canRedo = false;

    private DocumentHistory() {
    }

    public static DocumentHistory getInstance() {
        return INSTANCE;
    }

    //    Memory limit for undo operations about 40 MB, Acer Iconia A500
    public void pushNewItem(HistoryItem historyItem) {
        //Remove all old redos starting from the end of the list
        for (int i = historyList.size() - 1; i >= 0; i--) {
            HistoryItem item = historyList.get(i);
            if (item.getState() == HistoryItem.HistoryItemState.REDO) {
                historyList.remove(i);
            } else if (item.getState() == HistoryItem.HistoryItemState.UNDO)
                break;
        }
        if (UNDO_LIMIT == historyList.size()) {
            historyList.remove(0);
        }
        historyList.add(historyItem);
        historyPointer = historyList.size() - 1;

        if (historyList.size() > 0)
            canUndo = true;

        canRedo = false;
    }

    public void undo() {
        if (historyPointer < 0)
            return;

        HistoryItem historyItem = historyList.get(historyPointer);
        historyItem.undo();
        historyItem.setState(HistoryItem.HistoryItemState.REDO);
        historyPointer -= 1;

        if (historyPointer < 0)
            canUndo = false;

        canRedo = true;
    }

    public void redo() {
        if (historyPointer >= historyList.size() - 1)
            return;

        historyPointer += 1;
        HistoryItem historyItem = historyList.get(historyPointer);
        historyItem.redo();
        historyItem.setState(HistoryItem.HistoryItemState.UNDO);

        if (historyPointer == historyList.size() - 1)
            canRedo = false;

        if (historyList.size() > 1)
            canUndo = true;
    }

    public void clear() {
        historyList.clear();
        historyPointer = -1;
        canRedo = false;
        canUndo = false;
    }

    public boolean canUndo() {
        return canUndo;
    }

    public boolean canRedo() {
        return canRedo;
    }
}
