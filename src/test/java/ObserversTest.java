import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ObserversTest {
    static int b = 10;

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

    @Test
    public void basicObserver_2_9() {
        Observable<String> sourceSecond = Observable.fromIterable(asList("11", "22", "33"));
        ConnectableObservable<String> hot = sourceSecond.publish();

        Disposable subscribe = hot.subscribe(e -> System.out.println("Observer 1 " + e));
        Disposable subscribe2 = hot.subscribe(e -> System.out.println("Observer 2 " + e));


        hot.connect();
    }

    @Test
    public void basicObserver_2_10() {
//        Observable.interval(100, TimeUnit.MILLISECONDS).subscribe(System.out::println);
//        try {
//            Thread.sleep(5000);
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Observable.range(1, 10).subscribe(System.out::println);
        try {
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //for debugging!
    @Test
    public void basicObserver_error_factory() {
        Observable.error(new Exception("Crash!"))
                  .subscribe(System.out::println,
                             Throwable::printStackTrace,
                             () -> {
                                 System.out.println("done");
                             });
    }

    @Test
    public void basicObserver_empty_never_factory() {
        Observable.empty();
        Observable.never();

//        Future<String> futureValue =...;
//        Observable.fromFuture(futureValue)
//                  .map(String::length)
//                  .subscribe(System.out::println);
    }

    @Test
    public void basicObserver_without_defer_factory() {
        int a = 0, b = 4;
        Observable<Integer> source = Observable.range(a, b);
        source.subscribe(System.out::println);
        b = 15;
        source.subscribe(System.out::println);
    }

    @Test
    public void basicObserver_with_defer_factory() {
        int a = 0;
        Observable<Integer> source = Observable.defer(() -> Observable.range(a, b));
        source.subscribe(System.out::println);
        b = 15;
        source.subscribe(System.out::println);
    }

    @Test
    public void basicObserver_XXX_XXX() {
    }
}
