import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ObserversTest extends TestHelper {
    private static final CompositeDisposable DISPOSABLES = new CompositeDisposable();
    private static int b = 10;

    public static int getB() {
        return b;
    }

    public static void setB(int b) {
        ObserversTest.b = b;
    }

    @Test
    public void observer2_6() {
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
    public void observer2_7() {
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
    public void observer2_8() {
        Observable<String> observable = Observable.just("first", "second", "third");
//        Disposable subscribe = source2.subscribe(System.out::println, Throwable::printStackTrace, () -> System.out.println("Completed!"));


        Disposable subscribe = observable.subscribe(sout("Observer 1 "));
        Disposable subscribe2 = observable.subscribe(sout("Observer 2 "));

        Observable<String> sourceSecond = Observable.fromIterable(asList("11", "22", "33"));
        sourceSecond.subscribe(sout("Observer 1 "));
    }

    @Test
    public void observer2_9() {
        Observable<String> sourceSecond = Observable.fromIterable(asList("11", "22", "33"));
        ConnectableObservable<String> hot = sourceSecond.publish();

        Disposable subscribe = hot.subscribe(sout("Observer 1 "));
        Disposable subscribe2 = hot.subscribe(sout("Observer 2 "));


        hot.connect();
    }

    private Consumer<String> sout(String s) {
        return e -> System.out.println(s + e);
    }

    @Test
    public void observer2_10() {
//        Observable.interval(100, TimeUnit.MILLISECONDS).subscribe(sout());
//        try {
//            Thread.sleep(5000);
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Observable.range(1, 10).subscribe(sout());
        sleep(3000);
    }

    //for debugging!
    @Test
    public void error_factory() {
        Observable.error(new Exception("Crash!"))
                  .subscribe(sout(),
                             Throwable::printStackTrace,
                             () -> {
                                 System.out.println("done");
                             });
    }

    private Consumer<Object> sout() {
        return System.out::println;
    }

    @Test
    public void empty_never_factory() {
        Observable.empty();
        Observable.never();

//        Future<String> futureValue =...;
//        Observable.fromFuture(futureValue)
//                  .map(String::length)
//                  .subscribe(sout());
    }

    @Test
    public void without_defer_factory() {
        int a = 0, b = 4;
        Observable<Integer> source = Observable.range(a, b);
        source.subscribe(sout());
        b = 15;
        source.subscribe(sout());
    }

    @Test
    public void with_defer_factory() {
        int a = 0;
        Observable<Integer> source = Observable.defer(() -> Observable.range(a, getB()));
        source.subscribe(sout());
        setB(15);
        source.subscribe(sout());
    }

    @Test
    public void single_observable() {
        Observable.just("c", "a").first("default").subscribe(sout());
    }

    @Test
    public void maybe_observable() {
        MaybeObserver<String> maybeObserver = new MaybeObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable.just(maybeObserver).subscribe(sout());
    }

    @Test
    public void fromRunnable() {
        Completable.fromRunnable(() -> System.out.println("calling on complete"))
                   .doOnSubscribe(sout())
                   .subscribe(() -> System.out.println("Done!"));
    }

    @Test
    public void disposal_observable() {
        Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS);

        Observer<String> maybeObserver = new Observer<String>() {
            Disposable myDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                myDisposable = d;
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext " + s);
                if (s.trim().equals("5"))
                    myDisposable.dispose();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError " + e.getStackTrace());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete ");
            }
        };
        Disposable subscribe = source.subscribe(System.out::println);
        maybeObserver.onSubscribe(subscribe);
        sleep(10000);
    }

    @Test
    public void stopping() {
        Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS);
        Disposable disp = source.subscribe(sout());
        System.out.println(disp.isDisposed());
        sleep(5000);
        System.out.println(disp.isDisposed());
        disp.dispose();
        System.out.println(disp.isDisposed());
        sleep(5000);
        System.out.println(disp.isDisposed());
    }

    @Test
    public void composit_disposable() {
        Observable<Long> seconds = Observable.interval(1, TimeUnit.SECONDS);

        //subscribe and capture disposables
        Disposable disposable1 = seconds.subscribe(l -> System.out.println("Observer 1:" + l));
        Disposable disposable2 = seconds.subscribe(l -> System.out.println("Observer 2:" + l));

        DISPOSABLES.addAll(disposable1, disposable2);
        sleep(4000);
    }


    @Test
    public void XXX_XXX() {
    }
}
