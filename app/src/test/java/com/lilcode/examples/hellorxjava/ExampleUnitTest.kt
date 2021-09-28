package com.lilcode.examples.hellorxjava

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test

import org.junit.Assert.*
import org.reactivestreams.Publisher
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

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
}