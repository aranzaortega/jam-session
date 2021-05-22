package com.aios.jamsession.providers;

import com.aios.jamsession.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public Task<Void> save(Post post){
        return mCollection.document().set(post);
    }

    public Query getAll() {
        return mCollection.orderBy("title", Query.Direction.DESCENDING);
    }
}
