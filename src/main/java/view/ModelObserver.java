package view;

import model.Model;

public interface ModelObserver {
    void modelUpdated(Model model);
}
