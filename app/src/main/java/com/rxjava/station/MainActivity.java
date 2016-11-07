package com.rxjava.station;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.rxjava.station.R.id.just_btn;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity, ";
    @BindView(just_btn)
    Button justBtn;
    @BindView(R.id.from_btn)
    Button fromBtn;
    @BindView(R.id.map_btn)
    Button mapBtn;
    @BindView(R.id.flatMap_btn)
    Button flatMapBtn;
    @BindView(R.id.composite)
    Button compositeBtn;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    private static String str = "Hello,Android,RxJava";


    static Observable<List<String>> query() {
        List<String> s = Arrays.asList(str);
        return Observable.just(s);
    }


    static Observable<String> addPre(String s) {
        return Observable.just("addPre_" + s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        justBtn.setOnClickListener(this);
        fromBtn.setOnClickListener(this);
        mapBtn.setOnClickListener(this);
        flatMapBtn.setOnClickListener(this);
        compositeBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.just_btn:
                just();
                break;
            case R.id.from_btn:
                from();
                break;
            case R.id.map_btn:
                map();
                break;
            case R.id.flatMap_btn:
                flatMap();
                break;
            case R.id.composite:
                composite();
                break;
            default:
                break;
        }
    }

    public void just() {
        Observable.just(str)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(TAG + "just ," + s);
                    }
                });
    }

    /**
     * from()接收一个集合作为输入，然后每次输出一个元素给subscriber.
     */
    public void from() {
        List<String> s = Arrays.asList("Hello", "Android", "RxJava");
        Observable.from(s).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(TAG + "form , " + s);
            }
        });
    }


    public void map() {
        //刚创建的Observable是String类型的
        Observable.just("Hello Map Operator")
                .map(new Func1<String, Integer>() { //new Fun1()中的第一个参数为输入数据类型，第二个为输出数据类型
                    @Override
                    public Integer call(String s) {
                        return 2015;//通过第一个map转成Integer
                    }
                }).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return String.valueOf(integer);//再通过第二个map转成String
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(TAG + "map, " + s);
            }
        });
    }

    /**
     * Observable.flatMap()接收一个Observable的输出作为输入，同时输出另外一个Observable。
     */
    public void flatMap() {
        query().flatMap(new Func1<List<String>, Observable<String>>() {
            @Override
            public Observable<String> call(List<String> strings) {
                return Observable.from(strings);
            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                //增加一个前缀
                return addPre(s);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(TAG + "flatMap ," + s);
            }
        });
    }

    /**
     * 多种操作符组合用法
     */
    public void composite() {
        query().flatMap(new Func1<List<String>, Observable<String>>() {
            @Override
            public Observable<String> call(List<String> strings) {
                return Observable.from(strings);
            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                return addPre(s);
            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                //包含a的留下
                return s.contains("a");
            }
        }).take(3)//最多只取3个
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        //onNext之前 输出一下
                        System.out.println(TAG + "doOnNext:  " + s);
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(TAG + s);
            }
        });
    }


}
