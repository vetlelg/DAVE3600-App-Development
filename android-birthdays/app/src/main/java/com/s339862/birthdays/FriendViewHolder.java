package com.s339862.birthdays;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

// This class is used to hold the Views for each item in the RecyclerView
public class FriendViewHolder extends ViewHolder {
    private final TextView nameTextView;
    private final TextView birthdayTextView;
    private final TextView phoneTextView;
    private final FloatingActionButton deleteButton;
    private final FloatingActionButton editButton;

    // Takes in a View that represents the layout for each item in the RecyclerView
    public FriendViewHolder(@NonNull View friendView) {
        super(friendView);

        // Gets the TextViews and Buttons from the view/layout
        nameTextView = friendView.findViewById(R.id.nameTextView);
        birthdayTextView = friendView.findViewById(R.id.birthdayTextView);
        phoneTextView = friendView.findViewById(R.id.phoneTextView);
        deleteButton = friendView.findViewById(R.id.deleteButton);
        editButton = friendView.findViewById(R.id.editButton);
    }

    // Binds the data for each friend to the View/layout
    public void bind(Friend friend, FriendAdapter.OnFriendClickListener listener) {
        String nameText = "Name: " + friend.getName();
        nameTextView.setText(nameText);
        String birthdayText = "Birthday: " + friend.getBirthday();
        birthdayTextView.setText(birthdayText);
        String phoneText = "Phone: " + friend.getPhone();
        phoneTextView.setText(phoneText);

        // Sets the click listeners for the delete and edit buttons
        deleteButton.setOnClickListener(v -> listener.onDeleteClick(friend));
        editButton.setOnClickListener(v -> listener.onEditClick(friend));
    }
}
