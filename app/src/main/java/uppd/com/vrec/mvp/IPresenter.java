package uppd.com.vrec.mvp;

/**
 * Created by o.rabinovych on 12/4/17.
 */

public interface IPresenter<T> {
    /**
     * Binds presenter with a view when resumed. The Presenter will perform initialization here.
     *
     * @param view the view associated with this presenter
     */
    void takeView(T view);

    /**
     * Drops the reference to the view when destroyed
     */
    void dropView();
}
