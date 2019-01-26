import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ObserversTest {

    @Test
    public void basicObserver_2_6() {
        Observable<String> observable = Observable.create(emitter -> {
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

        Observer<String> observer = new Observer<String>() {
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

        observable.subscribe(observer);
    }

    @Test
    public void basicObserver_2_7() {
        Observer<String> observer = new Observer<String>() {
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

        Observable<String> observable = Observable.just("first", "second", "third");
        Disposable subscribe = observable.subscribe(System.out::println, Throwable::printStackTrace, () -> System.out.println("Completed!"));
        observer.onSubscribe(subscribe);
    }

    @Test
    public void basicObserver_2_8() {
        Observable<String> observable = Observable.just("first", "second", "third");
//        Disposable subscribe = source2.subscribe(System.out::println, Throwable::printStackTrace, () -> System.out.println("Completed!"));


        Disposable subscribe = observable.subscribe(e -> System.out.println("Observer 1 " + e));
        Disposable subscribe2 = observable.subscribe(e -> System.out.println("Observer 2 " + e));

        Observable<String> sourceSecond = Observable.fromIterable(asList("11", "22", "33"));
        sourceSecond.subscribe(e -> System.out.println("Observer 1 " + e));
    }
}
