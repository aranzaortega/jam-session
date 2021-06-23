package com.aios.jamsession.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aios.jamsession.R;
import com.aios.jamsession.models.Message;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.UserProvider;
import com.aios.jamsession.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.ViewHolder> {
    // Variables
    Context context;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    // Constructor for image
    public MessagesAdapter(FirestoreRecyclerOptions<Message> options, Context context){
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
    }

    /*Required methods from the Recycler Adapter*/

    // Set the content to the card view
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {
        // Get comment from database
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String messageId = document.getId();
        holder.textViewMessage.setText(message.getMessage());

        String relativeTime = RelativeTime.getTimeAgo(message.getTimestamp(), context);
        holder.textViewDateMessage.setText((relativeTime));

        if(message.getIdSender().equals(mAuthProvider.getUserId())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150, 0, 0, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,20,15,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.imageViewViewedMessage.setVisibility(View.VISIBLE);
            holder.textViewMessage.setTextColor(R.color.white);
            holder.textViewDateMessage.setTextColor(R.color.white);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0, 0, 150, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,20,-40,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_white));
            holder.imageViewViewedMessage.setVisibility(View.INVISIBLE);
            holder.textViewMessage.setTextColor(R.color.black);
            holder.textViewDateMessage.setTextColor(R.color.black);
        }
    }


    // Here is the instance of the view that we want to use
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewMessage;
        TextView textViewDateMessage;
        ImageView imageViewViewedMessage;
        LinearLayout linearLayoutMessage;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewDateMessage = view.findViewById(R.id.textViewDateMessage);
            imageViewViewedMessage = view.findViewById(R.id.imageViewViewedMessage);
            linearLayoutMessage = view.findViewById(R.id.linearLayoutMessage);
            viewHolder = view;
        }
    }
}
