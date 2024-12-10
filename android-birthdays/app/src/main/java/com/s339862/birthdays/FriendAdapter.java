package com.s339862.birthdays;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.annotation.NonNull;
import java.util.List;

// Adapter for the friends RecyclerView
// It binds the data for each friend to each item in the RecyclerView
public class FriendAdapter extends Adapter<FriendViewHolder> {
    // The list of friends is passed to the adapter when it's created in MainActivity
    // It's retrieved from the database in MainActivity and passed to the adapter constructor
    private final List<Friend> friends;

    // The listener is used to handle clicks on the delete and edit buttons in each item
    private final OnFriendClickListener listener;

    // When the adapter is created in MainActivity, the list of friends and the listener are passed to it
    // The listener is an interface that MainActivity implements
    public FriendAdapter(List<Friend> friends, OnFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    // The listener interface has two methods: onDeleteClick and onEditClick
    // It's implemented in MainActivity when the adapter is created
    public interface OnFriendClickListener {
        void onDeleteClick(Friend friend);
        void onEditClick(Friend friend);
    }

    /*
    This is called when a new item is created in the RecyclerView
    It uses the friend.xml layout to create the View for each item
    It takes in two parameters: the parent ViewGroup and the viewType
    The ViewGroup is the RecyclerView itself
    The viewType is used when there are multiple types of items in the RecyclerView
    In this case, there's only one type of item, so the viewType is not used
    */
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates a layout inflater from the RecycleView/ViewGroup parent context
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // The inflater is used to create the View from the friend.xml layout
        View view = inflater.inflate(R.layout.friend, parent, false);
        return new FriendViewHolder(view);
    }

    // The purpose of this method is to bind the data for each friend
    // to each ViewHolder in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        // Gets the friend at the current position in the list
        Friend friend = friends.get(position);
        // Binds the friend data to the ViewHolder
        holder.bind(friend, listener);
    }

    // This method must be implemented
    // Because it's an abstract method in the Adapter class
    @Override
    public int getItemCount() {
        return friends.size();
    }




}
