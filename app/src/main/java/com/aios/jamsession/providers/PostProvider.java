package com.aios.jamsession.providers;

import com.aios.jamsession.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostsByGenreAndTimestamp(String genre) {
        return mCollection.whereEqualTo("genre", genre).orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostsByUser(String id){
        return mCollection.whereEqualTo("iduser", id);
    }

    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }

    public Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }

}
