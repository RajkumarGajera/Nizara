package onealldigital.nizara.in.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import onealldigital.nizara.in.R;
import onealldigital.nizara.in.adapter.TrackerAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.CurrentOrder;
import onealldigital.nizara.in.model.OrderTracker;
import onealldigital.nizara.in.model.filterDate;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CurrentOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView nodata;
    private Session session;
    private Activity activity;
    private View root;
    private ArrayList<OrderTracker> orderTrackerArrayList;
    private ArrayList<CurrentOrder> currentOrderArrayList;
    private TrackerAdapter trackerAdapter;
    private SwipeRefreshLayout swipeLayout;
    private int offset = 0;
    private int total = 0;
    private NestedScrollView scrollView;
    private ShimmerFrameLayout mShimmerViewContainer;
//    int Years,months,days;
    String Start_date;
    int Start_day;
    int Start_month;
    int Start_year;
    String End_date;
    int End_day;
    int End_month;
    int End_year;
    filterDate filterdates=new filterDate();
    ArrayList<filterDate> list;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String User_name;

    public CurrentOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_current_order, container, false);
        setHasOptionsMenu(true);

        activity = getActivity();
        session = new Session(activity);
        User_name=session.getData(Constant.NAME);
        recyclerView = root.findViewById(R.id.recyclerView);
        scrollView = root.findViewById(R.id.scrollView);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        nodata = root.findViewById(R.id.nodata);

        swipeLayout = root.findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeLayout.setOnRefreshListener(() -> {
            offset = 0;
            swipeLayout.setRefreshing(false);
            getAllOrders();
        });


        getAllOrders();

        return root;
    }

    void getAllOrders() {
        recyclerView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        orderTrackerArrayList = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();

        myRef.child("Filterdate").child(session.getData(Constant.NAME)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    filterdates = dataSnapshot.getValue(filterDate.class);
                    End_date=filterdates.getEnd_date();
                    Start_date=filterdates.getStart_date();
                    System.out.println(End_date);
                    System.out.println(Start_date);
                    String Start[] =Start_date.split("-");
                    Start_day= Integer.parseInt(Start[0]);
                    Start_month= Integer.parseInt(Start[1]);
                    Start_year= Integer.parseInt(Start[2]);
                    String End[] = End_date.split("-");
                    End_day= Integer.parseInt(End[0]);
                    End_month= Integer.parseInt(End[1]);
                    End_year= Integer.parseInt(End[2]);
                    System.out.println("vjcgnrckxjmxihmgjhjxnmdhfncgmxdhfxkh,fzmxdvk");
                    System.out.println(Start_day+"==="+Start_month+"==="+Start_year);
                    System.out.println(End_day+"==="+End_month+"==="+End_year);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ORDERS, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        System.out.println("====product  " + response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));

//                            largeLog("CurrentOrderFrag", response);

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    OrderTracker orderTracker = new OrderTracker();
                                    orderTracker = ApiConfig.OrderTracker(jsonObject);
//                                    orderTracker.setActiveStatus(jsonObject.getString(Constant.ACTIVE_STATUS));
                                    //
                                    //
//                                    System.out.println(jsonObject.getString(orderTracker.getItemsList().toString()));
                                    orderTrackerArrayList.add(orderTracker);
                                    for(int k=0;k<orderTracker.getItemsList().size();k++){


                                        String hi = orderTracker.getItemsList().get(k).activeStatus;
//                                    System.out.println(hi);
                                        if(hi.equals("awaiting payment") || hi.equals("received") || hi.equals("processed") || hi.equals("shipped")){
//                                            orderTrackerArrayList.remove(orderTracker);
                                        }else{
                                            orderTrackerArrayList.remove(orderTracker);
                                        }
                                    }

                                    for(int k=0;k<orderTracker.getItemsList().size();k++) {

                                        String hi = orderTracker.getItemsList().get(k).activeStatusDate;
                                        String fir[] = hi.split(" ");
                                        String sp[] = fir[0].split("-");
                                        int year = Integer.parseInt(sp[0]);
                                        int month = Integer.parseInt(sp[1]);
                                        int day = Integer.parseInt(sp[2]);
                                        System.out.println(day+"==="+month+"==="+year);
                                        System.out.println(Start_day+"==="+Start_month+"==="+Start_year);
                                        System.out.println(End_day+"==="+End_month+"==="+End_year);
                                        if (Start_date == null && End_date == null) {

                                        } else {
                                            if (Start_year <= year && End_year >= year) {
                                                if (Start_month <= month || End_month >= month) {
                                                    if (Start_day <= day && End_day >= day) {
                                                        System.out.println("Hello");
                                                    } else {
                                                        orderTrackerArrayList.remove(orderTracker);
                                                    }
                                                } else {
                                                    orderTrackerArrayList.remove(orderTracker);
                                                }
                                            } else {
                                                orderTrackerArrayList.remove(orderTracker);
                                            }
                                        }
                                    }

                                }
                            }
                            if (offset == 0) {
                                trackerAdapter = new TrackerAdapter(getContext(), activity, orderTrackerArrayList);
                                recyclerView.setAdapter(trackerAdapter);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    private boolean isLoadMore;

                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (orderTrackerArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == orderTrackerArrayList.size() - 1) {
                                                        //bottom of list!
                                                        orderTrackerArrayList.add(null);
                                                        trackerAdapter.notifyItemInserted(orderTrackerArrayList.size() - 1);

                                                        offset += Constant.LOAD_ITEM_LIMIT;
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_ORDERS, Constant.GetVal);
                                                        params.put(Constant.USER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.OFFSET, "" + offset);
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        // System.out.println("====product  " + response);
                                                                        JSONObject objectbject1 = new JSONObject(response);
                                                                        if (!objectbject1.getBoolean(Constant.ERROR)) {

                                                                            session.setData(Constant.TOTAL, objectbject1.getString(Constant.TOTAL));

                                                                            orderTrackerArrayList.remove(orderTrackerArrayList.size() - 1);
                                                                            trackerAdapter.notifyItemRemoved(orderTrackerArrayList.size());

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                                                                if (jsonObject1 != null) {
                                                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                                                    OrderTracker orderTracker =new OrderTracker();
                                                                                    orderTracker = ApiConfig.OrderTracker(jsonObject);
//                                                                                    System.out.println(jsonObject.getString(Constant.ACTIVE_STATUS));
                                                                                    orderTrackerArrayList.add(orderTracker);
                                                                                }
                                                                            }
                                                                            trackerAdapter.notifyDataSetChanged();
                                                                            trackerAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        mShimmerViewContainer.stopShimmer();
                                                                        mShimmerViewContainer.setVisibility(View.GONE);
                                                                        recyclerView.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.ORDERPROCESS_URL, params, false);

                                                    }
                                                    isLoadMore = true;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                }
                System.out.println(Calendar.DAY_OF_WEEK_IN_MONTH+1);
                System.out.println(Calendar.DAY_OF_MONTH+1);
                Start_date="1-1-1900";
                End_date=String.valueOf((Calendar.DAY_OF_WEEK_IN_MONTH+1)+"-"+(Calendar.DAY_OF_MONTH+1)+"-"+"2022");
                filterDate filterDate= new filterDate(Start_date,End_date);
                myRef.child("Filterdate").child(User_name).child(User_name).setValue(filterDate);
            }
        }, activity, Constant.ORDERPROCESS_URL, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }
}