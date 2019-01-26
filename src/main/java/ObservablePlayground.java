import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ObservablePlayground {

    public static void main(String[] args) {
        Observable<String> source = Observable.create(emitter -> {
            try {
                emitter.onNext("first");
                emitter.onNext("second");
                emitter.onNext("third");
                emitter.onComplete();
            }
            catch (Exception e) {
                emitter.onError(e);
            }
        });

        Observable<String> source2 = Observable.just("first", "second", "third");
        Observer<String> ob = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("subscribed!");
            }

            @Override
            public void onNext(String s) {
                System.out.println("now handled is: " + s);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("error:(");
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Completed!");
            }
        };

        source2.subscribe(ob);
    }
}
