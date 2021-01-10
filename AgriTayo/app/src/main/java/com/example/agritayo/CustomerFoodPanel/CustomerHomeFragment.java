package com.example.agritayo.CustomerFoodPanel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import  com.example.agritayo.ChefFoodPanel.UpdateDishModel;
import  com.example.agritayo.Customer;

import  com.example.agritayo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CustomerHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private CustomerHomeAdapter adapter;
    String State, City, Sub;
    DatabaseReference dataaa, databaseReference;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customerhome, null);
        getActivity().setTitle("AgriTayo");
        setHasOptionsMenu(true);
        recyclerView = v.findViewById(R.id.recycle_menuu);
        recyclerView.setHasFixedSize(true);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move);
        recyclerView.startAnimation(animation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateDishModelList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.green);
        adapter = new CustomerHomeAdapter(getActivity(), updateDishModelList);
        recyclerView.setAdapter(adapter);
//        swipeRefreshLayout.setRefreshing(false);
        Log.e(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList);
        Log.e(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList.size());
        Log.i(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList.size());


        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                dataaa = FirebaseDatabase.getInstance().getReference("Customer").child(userid);
                dataaa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Customer cust = dataSnapshot.getValue(Customer.class);
                        State = cust.getState();
                        City = cust.getCity();
                        Sub = cust.getSuburban();
                        customermenu();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }


    @Override
    public void onRefresh() {

        customermenu();
    }

    private void customermenu() {

        swipeRefreshLayout.setRefreshing(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateDishModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        UpdateDishModel updateDishModel = snapshot.getValue(UpdateDishModel.class);
                        updateDishModelList.add(updateDishModel);

//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            UpdateDishModel updateDishModel = snapshot.getValue(UpdateDishModel.class);
//                            updateDishModelList.add(updateDishModel);
                        Log.e(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList);
                        Log.e(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList.size());
                        Log.i(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList.size());
                    }
//                }
//                adapter = new CustomerHomeAdapter(getActivity(), updateDishModelList);
//                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList);
                Log.e(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList.size());
                Log.i(TAG, "Sizeeeeeeeeeeeeeeeeeeeeeeeee" + updateDishModelList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });

    }

    private void search(final String searchtext) {

        ArrayList<UpdateDishModel> mylist = new ArrayList<>();
        for (UpdateDishModel object : updateDishModelList) {
            if (object.getDishes().toLowerCase().contains(searchtext.toLowerCase())) {
                mylist.add(object);
            }
        }
        adapter = new CustomerHomeAdapter(getActivity(), mylist);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.Searchdish);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search an Item");


    }
}
