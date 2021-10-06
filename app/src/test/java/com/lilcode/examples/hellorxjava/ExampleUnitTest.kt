package com.lilcode.examples.hellorxjava

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test

import org.junit.Assert.*
import org.reactivestreams.Publisher
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test // 명령형 프로그래밍 (반응형 프로그래밍과의 차이를 알아보기 위한 simple example)
    fun imperative_programming() {
        val items = ArrayList<Int>()
        items.add(1)
        items.add(2)
        items.add(3)
        items.add(4)

        // 짝수만 출력
        for (item in items) {
            if (item % 2 == 0) {
                println(item)
            }
        }

        items.add(5)
        items.add(6)
        items.add(7)
        items.add(8)

        /*
        2
        4
         */
    }

    @Test
    fun reactive_programming() {
        val items = PublishSubject.create<Int>() // 데이터 스트림 생성

        items.onNext(1)
        items.onNext(2)
        items.onNext(3)
        items.onNext(4)

        items.filter { it % 2 == 0 }
            .subscribe(System.out::println) // 짝수만 출력하는 데이터 스트림으로 변경한 뒤 구독

        items.onNext(5)
        items.onNext(6)
        items.onNext(7)
        items.onNext(8)

        /*
        6
        8
         */
    }

    @Test
    fun create_ex1() {
        val source = Observable.create<String> { emitter ->
            emitter.onNext("Hello")
            emitter.onNext("World")
            emitter.onError(Throwable())
            emitter.onComplete()
            emitter.onNext("!")
        }

        // Consumer 를 통해 구독
        source.subscribe(System.out::println, Consumer { println("Error!!") })

        /*
        Hello
        World
        Error!!
         */
    }

    @Test
    fun just_ex1() {
        val source = Observable.just("Hello", "World")
        source.subscribe(System.out::println)

        // 빈 Observable
        val source2 = Observable.empty<String>()
        source2.subscribe(System.out::println)
    }

    @Test
    fun convert_ex1() {
        println("########## fromArray ##########")
        val itemArray = arrayOf("A", "B", "C")
        val source = Observable.fromArray(itemArray)
        source.subscribe(System.out::println)


    }

    @Test
    fun convert_ex2() {
        println("########## fromIterable ##########")
        val arrayList = ArrayList<String>()
        arrayList.add("A")
        arrayList.add("B")
        arrayList.add("C")

        val source = Observable.fromIterable(arrayList)
        source.subscribe(System.out::println)
    }

    @Test
    fun convert_ex3() {
        println("########## fromFuture ##########")
        val future: Future<String> = Executors.newSingleThreadExecutor()
            .submit<String> {
                Thread.sleep(5000)
                return@submit "Hello World!"
            }


        val source = Observable.fromFuture(future)
        source.subscribe(System.out::println) // 블로킹 되어 기다림
    }

    @Test
    fun convert_ex4() { // import org.reactivestreams.Publisher
        println("########## fromPublisher ##########")
        val publisher = Publisher<String> { subscriber ->
            subscriber.onNext("A")
            subscriber.onNext("B")
            subscriber.onNext("C")
            subscriber.onComplete()
        }

        val source = Observable.fromPublisher(publisher)
        source.subscribe(System.out::println)
    }

    @Test
    fun convert_ex5() {
        val callable = Callable {
            "Hello World"
        }

        val source = Observable.fromCallable(callable)
        source.subscribe(System.out::println)
    }

    @Test
    fun single_ex() {
        Single.just("Hello World")
            .subscribe(System.out::println)
    }

    @Test
    fun single_ex2() {
        Single.create<String> { emitter -> emitter.onSuccess("Hello") }
            .subscribe(System.out::println)
    }

    @Test
    fun single_ex3() {
        // Observable -> Single
        val src = Observable.just(1, 2, 3)

        val singleSrc1 = src.all { i -> i > 0 }
        val singleSrc2 = src.first(-1)
        val singleSrc3 = src.toList()
    }

    @Test
    fun single_ex4() {
        // Single -> Observable
        val singleSrc = Single.just("Hello World")
        val observableSrc = singleSrc.toObservable()
    }

    @Test
    fun maybe_ex1() {
        Maybe.create<Int> { emitter ->
            emitter.onSuccess(100)
            emitter.onComplete() // 무시됨
        }
            .doOnSuccess { item -> println("doOnSuccess1") }
            .doOnComplete { println("doOnComplete1") }
            .subscribe(System.out::println)

        Maybe.create<Any> { emitter -> emitter.onComplete() }
            .doOnSuccess { item -> println("doOnSuccess2") }
            .doOnComplete { println("doOnComplete2") }
            .subscribe(System.out::println)

        /*
        doOnSuccess1
        100
        doOnComplete2
         */
    }

    @Test
    fun maybe_ex2() {
        val src1 = Observable.just(1, 2, 3)
        val srcMaybe1 = src1.firstElement()
        srcMaybe1.subscribe(System.out::println)

        val src2 = Observable.empty<Any>()
        val srcMaybe2 = src2.firstElement()
        srcMaybe2.subscribe(System.out::println, { throwable -> }, { println("onComplete!") })

        /*
        1
        onComplete!
         */
    }

    @Test
    fun completable_ex1() {
        Completable.create { emitter ->
            // do something here
            emitter.onComplete()
        }
            .subscribe { println("completed1") }

        Completable.fromRunnable {
            // do something here
        }
            .subscribe { println("completed2") }

        /*
        completed1
        completed2
         */
    }

    @Test
    fun coldObservable_ex1() {
        val src = Observable.interval(1, TimeUnit.SECONDS)
            .apply {
                subscribe { value -> println("#1: $value") }
                Thread.sleep(3000)
                subscribe { value -> println("#2: $value") } // 처음 발행된 아이템 부터 구독됨
                Thread.sleep(3000)
            }

        /*
        #1: 0
        #1: 1
        #1: 2
        #1: 3
        #2: 0
        #1: 4
        #2: 1
        #1: 5
        #2: 2
         */
    }

    @Test
    fun publish_connect_ex() {
        val src = Observable.interval(1, TimeUnit.SECONDS)
            .publish()
        src.connect() // connect() 해야 비로소 데이터를 발행
        src.subscribe { value -> println("#1 : $value") }
        Thread.sleep(3000)
        src.subscribe { value -> println("#2 : $value") }
        Thread.sleep(3000)

        /*
        #1 : 0
        #1 : 1
        #1 : 2
        #2 : 2 // 동일 시간 아이템 을 발행 받음 (0~1 는 수신 받지 못함)
        #1 : 3
        #2 : 3
        #1 : 4
        #2 : 4
        #1 : 5
        #2 : 5
         */
    }

    @Test
    fun autoConnect_ex() {
        val src = Observable.interval(100, TimeUnit.MILLISECONDS)
            .publish()
            .autoConnect(2)
        src.subscribe { i -> println("A: $i") }
        src.subscribe { i -> println("B: $i") }
        Thread.sleep(500)

        /*
        A: 0
        B: 0
        A: 1
        B: 1
        A: 2
        B: 2
        A: 3
        B: 3
         */
    }

    @Test
    fun disposable_ex() {
        // 유한한 아이템을 발행하는 경우
        val source = Observable.just("A", "B", "C")
        val disposable = source.subscribe { o -> println(o) }

        // 무한한 아이템을 발행하는 경우 (메모리 릭 방지를 위해 추가 처리해줄 필요가 있을 수 있다.)
        val source2 = Observable.interval(1000, TimeUnit.MILLISECONDS)
        val disposable2 = source2.subscribe(System.out::println)
        thread(start = true) {
            try {
                Thread.sleep(3500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            disposable2.dispose() // 아이템 발행 중단 및 모든 리소스 폐기
        }
    }

    @Test
    fun compositeDisposable_ex() {
        val source = Observable.interval(1000, TimeUnit.MILLISECONDS)
        val d1 = source.subscribe(System.out::println)
        val d2 = source.subscribe(System.out::println)
        val d3 = source.subscribe(System.out::println)
        val cd = CompositeDisposable()
        cd.add(d1)
        cd.add(d2)
        cd.add(d3)
        // cd.addAll(d1,d2,d3)기
        Thread.sleep(3000)
        cd.dispose() // 특정 시점에 전부 폐기

        /*
        0
        0
        0
        1
        1
        1
        2
        2
         */
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        return format.format(date)
    }

    @Test
    fun defer_ex1() {
        val justSrc = Observable.just(System.currentTimeMillis())
        val deferSrc = Observable.defer { Observable.just(System.currentTimeMillis()) }

        println("#1 now = ${convertLongToTime(System.currentTimeMillis())}")

        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        println("#2 now = ${convertLongToTime(System.currentTimeMillis())}")

        justSrc.subscribe{ time ->
            println("#1 time = ${convertLongToTime(time)}")
        }

        deferSrc.subscribe { time ->
            println("#2 time = ${convertLongToTime(time)}")
        }

        /*
        #1 now = 2021.10.06 17:26:00
        #2 now = 2021.10.06 17:26:05
        #1 time = 2021.10.06 17:26:00
        #2 time = 2021.10.06 17:26:05
         */
    }
}